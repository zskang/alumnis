package com.nethsoft.web.controller.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.web.service.system.ResourceService;

/**
 * 资源菜单管理
 * @author zengc
 *
 */
@Controller
@RequestMapping("/system/res")
public class ResourceController extends BaseController<Resource>{
	private final Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "/system/res/";
	@Autowired
	private ResourceService resService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model, String status){
		String hql = "select id, name, icon, url, parentId, enabled, hasChildren, beta, display from Resource where parentId='-1' and enabled = true order by location asc";
		if(ObjectUtil.isNotNull(status) && !StringUtil.isBlank(status)){
			hql = "select id, name, icon, url, parentId, enabled, hasChildren from Resource where parentId='-1' order by location asc";
			model.addAttribute("status", status);
		}
		List<Resource> list = resService.listForEntity(hql);
		model.addAttribute("list", list);
		model.addAttribute("count", resService.count());
		
		logService.info("菜单管理", "访问菜单列表", getCurrentUser().getUserName(), getRequestAddr());
		return "system/res/index";
	}
	/**
	 * 首页TreeTable子菜单
	 * @param model
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/child/{pid}", method={RequestMethod.GET, RequestMethod.POST})
	public String children(Model model, @PathVariable String pid){
		List<Resource> list = resService.listForEntity("select id, name, icon, url, parentId, enabled, hasChildren, beta, display,newWindow from Resource where parentId='"+pid+"' order by location asc");
		model.addAttribute("list", list);
		return "system/res/children";
	}
	
	/**
	 * 新增菜单
	 * @param model
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="add", method={RequestMethod.GET, RequestMethod.POST})
	public String add(Model model,String pid){
		if(ObjectUtil.isNotNull(pid)){
			Resource parentRes = resService.getByHql("select id,name,icon from Resource where id='"+pid+"'");
			if(ObjectUtil.isNotNull(parentRes)){
				model.addAttribute("parentRes", parentRes);
			}
		}
		model.addAttribute("noRes", resService.count()<=0);
		return "system/res/add";
	}
	
	/**
	 * 执行新增菜单
	 * @param model
	 * @param res
	 * @return
	 */
	@RequestMapping(value="/add/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(Model model, Resource res){
		try {
			if(StringUtil.isBlank(res.getParentId()))
				res.setParentId("-1");
			res.setCreateTime(getCurrentTime());
			
			//计算菜单位置
			resService.saveAndUpdateParent(res);
			
			logService.info("菜单管理", String.format("新增菜单：[Id:%s, Name:%s, ParentId:%s]", res.getId(), res.getName(), res.getParentId()), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 菜单树
	 * @return
	 */
	@RequestMapping(value="/tree", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONArray tree(){
		List<Resource> list = resService.listForEntity("select id, name, icon, parentId, hasChildren, descript from Resource where parentId='-1' order by location");
		JSONArray data = new JSONArray();
		for(Resource res : list){
			JSONObject node = new JSONObject();
			
			node.element("id", res.getId());
			node.element("text", res.getName());
			node.element("icon", res.getIcon());
			node.element("data", res.getDescript());
			node.element("children", res.isHasChildren());
			
			data.add(node);
		}
		return data;
	}
	
	/**
	 * 获取才担保子节点
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/tree/{pid}", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONArray tree(@PathVariable String pid){
		List<Resource> list = resService.listForEntity("select id, name, icon, parentId, hasChildren, descript from Resource where parentId='"+pid+"' order by location");
		JSONArray data = new JSONArray();
		for(Resource res : list){
			JSONObject node = new JSONObject();
			
			node.element("id", res.getId());
			node.element("text", res.getName());
			node.element("icon", res.getIcon());
			node.element("data", res.getDescript());
			node.element("children", res.isHasChildren());
			
			data.add(node);
		}
		return data;
	}
	
	/**
	 * 资源详情
	 * @param model
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/detail/{id}", method={RequestMethod.GET, RequestMethod.POST})
	public String detail(Model model, @PathVariable String id){
		Resource res = resService.getById(id);
		if(ObjectUtil.isNull(res))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		if(!res.getParentId().equals("-1")){
			Resource parentRes = resService.getByHql("select name from Resource where id='"+res.getParentId()+"'");
			if(ObjectUtil.isNotNull(parentRes)){
				model.addAttribute("parentMenu", parentRes.getName());
			}
		}
		model.addAttribute("res", res);
		return "system/res/detail";
	}
	
	/**
	 * 删除
	 * @param model
	 * @param res
	 * @return
	 */
	@RequestMapping(value="/delete/{id}", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject delete(Model model, @PathVariable String id){
		try {
			resService.deleteAndRelated(id);
			logService.info("菜单管理", String.format("删除角色：[Id:%s]", id), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 资源详情
	 * @param model
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/sort", method={RequestMethod.GET, RequestMethod.POST})
	public String sort(Model model){
		List<Resource> list = resService.listForEntity("select id,name,icon,parentId,hasChildren from Resource order by location asc");
		
		List<Resource> firstRes = new ArrayList<Resource>();
		Map<String,List<Resource>> secondRes = new HashMap<String, List<Resource>>();
		Map<String,List<Resource>> thirdRes = new HashMap<String, List<Resource>>();
		for(Resource res : list){
			if(res.getParentId().equals("-1")){//筛选一级菜单
				firstRes.add(res);
				if(res.isHasChildren()){//寻找二级菜单
					List<Resource> childRes = getChildRes(list, res.getId());
					secondRes.put(res.getId(), childRes);
					for(Resource child : childRes){
						if(child.isHasChildren()){//寻找三级菜单
							thirdRes.put(child.getId(), getChildRes(list, child.getId()));
						}
					}
				}
			}
		}
		model.addAttribute("firstRes", firstRes);
		model.addAttribute("secondRes", secondRes);
		model.addAttribute("thirdRes", thirdRes);
		
		return "system/res/sort";
	}
	
	@RequestMapping(value="/doSort", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doSort(Model model, String data){
		try {
			resService.executeSort(data);
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	/**
	 * 获取子菜单
	 * @param list
	 * @param pid
	 * @return
	 */
	public List<Resource> getChildRes(List<Resource> list, String pid){
		List<Resource> childRes = new ArrayList<Resource>();
		for(Resource res : list){
			if(res.getParentId().equals(pid)){
				childRes.add(res);
			}
		}
		return childRes;
	}
	
	@RequestMapping(value="/edit/{id}", method={RequestMethod.GET, RequestMethod.POST})
	public String edit(Model model, @PathVariable String id){
		Resource res = resService.getById(id);
		if(ObjectUtil.isNull(res))
			return ControllerCommon.UNAUTHORIZED_ACCESS;//非法访问
		model.addAttribute("res", res);
		//获取资源的上级菜单名称
		if(res.getParentId().equals("-1")){
			model.addAttribute("parentName", "顶级菜单");
		}else{
			String name = resService.getByHql("select name from Resource where id='"+res.getParentId()+"'").getName();
			model.addAttribute("parentName", name);
		}
		return "system/res/edit";
	}
	
	/**
	 * 修改URL
	 * @param pk
	 * @param value
	 * @return
	 */
	@RequestMapping(value="/edit/url", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject editUrl(String pk, String value){
		try {
			Resource eqRes = resService.getById(pk);
			String oldUrl = eqRes.getUrl();
			if(ObjectUtil.isNull(eqRes))
				return WebResult.error("此菜单不存在！");
			eqRes.setUrl(value);
			
			resService.merge(eqRes);
			
			logService.info("菜单管理", String.format("编辑菜单Url：[Id:%s, OldUrl:%s, NewUrl:%s]", eqRes.getId(),oldUrl, value), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 修改名称
	 * @param pk
	 * @param url
	 * @return
	 */
	@RequestMapping(value="/edit/name", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject editName(String pk, String value){
		try {
			Resource eqRes = resService.getById(pk);
			String oldName = eqRes.getName();
			if(ObjectUtil.isNull(eqRes))
				return WebResult.error("此菜单不存在！");
			eqRes.setName(value);
			
			resService.merge(eqRes);
			
			logService.info("菜单管理", String.format("编辑菜单名称：[Id:%s, OldName:%s, NewName:%s]", eqRes.getId(),oldName, value), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	@RequestMapping(value="/edit/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(Resource res){
		try {
			Resource eqRes = resService.getById(res.getId());
			if(ObjectUtil.isNull(eqRes))
				return WebResult.error("此菜单不存在！");
			eqRes.setName(res.getName());
			eqRes.setIcon(res.getIcon());
			eqRes.setUrl(res.getUrl());
			eqRes.setDescript(res.getDescript());
			eqRes.setEnabled(res.isEnabled());
			eqRes.setParentId(res.getParentId());
			eqRes.setDisplay(res.isDisplay());
			eqRes.setBeta(res.isBeta());
			eqRes.setNewWindow(res.isNewWindow());
			
			resService.merge(eqRes);
			//更新父级菜单为拥有下级子菜单
			if(!eqRes.getParentId().equals("-1"))
				resService.executeHQL("update Resource set hasChildren=true where id='"+eqRes.getParentId()+"'");
			if(resService.count(Restrictions.eq("parentId", eqRes.getId())) > 0){
				resService.executeHQL("update Resource set hasChildren=true where id='"+eqRes.getId()+"'");
			}
			logService.info("菜单管理", String.format("编辑菜单：[Id:%s, Name:%s, ParentId:%s]", res.getId(), res.getName(), res.getParentId()), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 启用
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/enable/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject enable(@PathVariable String id){
		try {
			resService.executeHQL("update Resource set enabled=true where id='"+id+"'");
			logService.info("菜单管理", String.format("启用菜单：[Id:%s]", id), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 禁用
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/disable/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject disable(@PathVariable String id){
		try {
			resService.executeHQL("update Resource set enabled=false where id='"+id+"'");
			logService.info("菜单管理", String.format("禁用菜单：[Id:%s]", id), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 选择图标
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/icon", method={RequestMethod.GET, RequestMethod.POST})
	public String icon(Model model){
		try {
			List<String> fa = new ArrayList<>();
			List<String> ti = new ArrayList<>();
			List<String> gly = new ArrayList<>();
			File faFile = new File(ApplicationCommon.WEBAPPS_PATH+"/resources/framework/css/icon/icon-fa.css");
			BufferedReader br = new BufferedReader(new FileReader(faFile));
			while(br.ready()){
				fa.add(br.readLine());
			}
			br.close();
			File tiFile = new File(ApplicationCommon.WEBAPPS_PATH+"/resources/framework/css/icon/icon-ti.css");
			br = new BufferedReader(new FileReader(tiFile));
			while(br.ready()){
				ti.add(br.readLine());
			}
			br.close();
			File glyFile = new File(ApplicationCommon.WEBAPPS_PATH+"/resources/framework/css/icon/icon-gly.css");
			br = new BufferedReader(new FileReader(glyFile));
			while(br.ready()){
				gly.add(br.readLine());
			}
			br.close();
			
			model.addAttribute("fa", fa);
			model.addAttribute("ti", ti);
			model.addAttribute("gly", gly);
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
		}
		
		return TPL + "icon";
	}
	
}
