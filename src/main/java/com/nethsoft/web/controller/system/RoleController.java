package com.nethsoft.web.controller.system;

import java.util.ArrayList;
import java.util.List;

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

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.Button;
import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.entity.system.Role;
import com.nethsoft.web.service.system.ButtonService;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.web.service.system.ResourceService;
import com.nethsoft.web.service.system.RoleService;
import com.nethsoft.core.util.StringUtil;

/**
 * 角色管理
 * @author zengchao
 *
 */
@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController<Role>{
	private final Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private RoleService roleService;
	@Autowired
	private ButtonService btnService;
	@Autowired
	private ResourceService resService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model){
		List<Role> roleList = roleService.list_noLazy(Restrictions.in("unit", getCurrentUser().getUnits()));
		model.addAttribute("roleList", roleList);
		
		logService.info("角色管理", "访问角色列表", getCurrentUser().getUserName(), getRequestAddr());
		return "system/role/index";
	}
	
	/**
	 * 机构下属角色
	 * @param model
	 * @param unitId
	 * @return
	 */
	@RequestMapping(value="/unitrole/{unitId}", method={RequestMethod.GET, RequestMethod.POST})
	public String unitrole(Model model, @PathVariable String unitId){
		List<Role> roleList = roleService.list(Restrictions.eq("unit.id", unitId));
		model.addAttribute("roleList", roleList);
		
		return "system/role/unitrole";
	}
	
	/**
	 * 新增角色
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET, RequestMethod.POST})
	public String add(Model model){
		return "system/role/add";
	}
	
	/**
	 * 执行新增角色
	 * @param role
	 * @param resourceIds
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value="/add/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(Role role, String resourceIds, String userIds) {
		try {
			//if(roleService.count(Restrictions.eq("name", role.getName()))>0)
				//return WebResult.error("角色名已存在！");
			if(StringUtil.isBlank(resourceIds) && StringUtil.isBlank(userIds)){
				if(StringUtil.isBlank(role.getName()))
					return WebResult.error("角色名不能为空!");
				role.setCreateTime(getCurrentTime());
				roleService.save(role);
			}else{
				if(!resourceIds.startsWith("["))
					resourceIds = "["+resourceIds+"]";
				roleService.save(role, resourceIds, userIds);
			}
			logService.info("角色管理", String.format("新增角色：[Id:%s, Name:%s]", role.getId(), role.getName()), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
		
	}
	
	/**
	 * 获取资源+按钮
	 */
	@RequestMapping(value="/resbtn", method={RequestMethod.GET,RequestMethod.POST})
	public String resBtn(Model model, String ids){
		//获取选中菜单
		List<Resource> resList = resService.list(Restrictions.in("id", ids.split(",")));
		//获取按钮列表
		List<Button> btnList = btnService.list(Restrictions.eq("enabled", true));
		
		model.addAttribute("resList", resList);
		model.addAttribute("btnList", btnList);
		return "system/role/resBtn";
	}
	
	/**
	 * 修改基本信息
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}", method={RequestMethod.GET})
	public String edit(Model model, @PathVariable String id){
		Role role = roleService.getById_noLazy(id);
		if(ObjectUtil.isNull(role))
			return "_error/500";
		model.addAttribute("role", role);
		return "system/role/edit";
	}
	
	@RequestMapping(value="/edit/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(Role role){
		try {
			//if(roleService.count(Restrictions.eq("name", role.getName()), Restrictions.not(Restrictions.eq("id", role.getId())))>0)
				//return WebResult.error("角色名已存在！");
			Role eqRole = roleService.getById(role.getId());
			if(ObjectUtil.isNull(eqRole))
				return WebResult.error("角色不存在！");
			eqRole.setName(role.getName());
			eqRole.setDescript(role.getDescript());
			eqRole.setEnabled(role.isEnabled());
			
			roleService.merge(eqRole);
			
			logService.info("角色管理", String.format("编辑角色：[Id:%s, Name:%s]", role.getId(), role.getName()), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 修改权限
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/right/{id}", method={RequestMethod.GET})
	public String editRight(Model model, @PathVariable String id){
		Role role = roleService.getById_noLazyRes(id);
		if(ObjectUtil.isNull(role))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		JSONArray array = new JSONArray();
		for(Resource res : role.getResources()){
			array.add(res.getId());
		}
		model.addAttribute("data", array);
		model.addAttribute("role", role);
		return "system/role/editRight";
	}
	
	@RequestMapping(value="/edit/doRight", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEditRight(Role role, String resourceIds){
		try {
			Role eqRole = roleService.getById(role.getId());
			if(ObjectUtil.isNull(eqRole))
				return WebResult.error("非法操作");
			if(!resourceIds.startsWith("["))
				resourceIds = "["+resourceIds+"]";
			//修改角色权限
			roleService.executeEditRoleRes(role.getId(), resourceIds);
			logService.info("角色管理", String.format("编辑角色权限：[Id:%s, Name:%s]", role.getId(), role.getName()), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	@RequestMapping(value="/delete/{id}", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject delete(@PathVariable String id){
		try {
			roleService.deleteAsRelated(id);
			logService.info("角色管理", String.format("删除角色：[Id:%s]", id), getCurrentUser().getUserName(), getRequestAddr());
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
			roleService.executeHQL("update Role set enabled=true where id='"+id+"'", getCurrentUser().getUserName(), getRequestAddr());
			logService.info("角色管理", String.format("启用角色：[Id:%s]", id), getCurrentUser().getUserName(), getRequestAddr());
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
			roleService.executeHQL("update Role set enabled=false where id='"+id+"'");
			logService.info("角色管理", String.format("禁用角色：[Id:%s]", id), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 查看下属用户
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/detail/user/{id}", method={RequestMethod.GET})
	public String detail_info(Model model, @PathVariable String id){
		Role role = roleService.getById_noLazyUser(id);
		if(ObjectUtil.isNull(role))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		//获取下属用户
		model.addAttribute("role", role);
		return "system/role/detail_user";
	}
	
	/**
	 * 查看权限
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/detail/right/{id}", method={RequestMethod.GET})
	public String detail_right(Model model, @PathVariable String id){
		Role role = roleService.getById_noLazyRes(id);
		if(ObjectUtil.isNull(role))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		//获取用户菜单资源
		List<Resource> resList = role.getResources();
		List<Resource> firstResList = new ArrayList<Resource>();
		JSONArray jsonResList = new JSONArray();
		for(Resource res : resList){
			if(res.getParentId().equals("-1")){
				firstResList.add(res);
			}
			//防止由于roles延迟加载导致出错
			res.setRoles(null);
			JSONObject jsonRes = JSONObject.fromObject(res);
			jsonResList.add(jsonRes);
		}
		model.addAttribute("allRes", jsonResList.toString());
		model.addAttribute("resList", firstResList);
		model.addAttribute("role", role);
		return "system/role/detail_right";
	}
	
}
