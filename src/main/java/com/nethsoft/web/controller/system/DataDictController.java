package com.nethsoft.web.controller.system;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.DataDict;
import com.nethsoft.web.entity.system.DataDictItem;
import com.nethsoft.web.service.system.DataDictItemService;
import com.nethsoft.web.service.system.DataDictService;
import com.nethsoft.core.util.ObjectUtil;

@Controller
@RequestMapping("/system/datadict")
public class DataDictController extends BaseController<DataDict>{
	private Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "system/datadict/";
	@Autowired
	private DataDictService ddService;
	@Autowired
	private DataDictItemService ddiService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		return TPL + "index";
	}
	
	/**
	 * 字典分类
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/category", method = { RequestMethod.GET })
	public String category(Model model) {
		List<DataDict> list = ddService.listForEntity("select id, category from DataDict where category is not null and category != '' group by category order by createTime asc");
		model.addAttribute("list", list);
		return TPL + "category";
	}
	
	@RequestMapping(value="/tree", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONArray tree(){
		JSONArray data = new JSONArray();
		
		int count = ddService.count();
		if(count == 0){
			JSONObject node = new JSONObject();
			
			node.element("id", "-1");
			node.element("text", "无数据字典");
			node.element("icon", "fa fa-tags");
			
			data.add(node);
		}else{
			List<DataDict> list = ddService.listForEntity("select id, category from DataDict group by category order by createTime asc");
			//获取分类
			for(DataDict dd : list){
				JSONObject node = new JSONObject();
				
				if(StringUtil.isBlank(dd.getCategory())){
					node.element("id", "-1");
					node.element("text", "未分类");
					node.element("icon", "fa fa-folder");
				}else{
					node.element("id", dd.getCategory());
					node.element("text", dd.getCategory());
					node.element("icon", "fa fa-folder");
					
				}
				node.element("children", true);
				data.add(node);
			}
		}
		return data;
	}
	
	@RequestMapping(value="/tree/{category}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONArray tree(@PathVariable String category){
		JSONArray data = new JSONArray();
		List<DataDict> list = null;
		if("-1".equals(category))
			list = ddService.listForEntity("select id,name,remarks from DataDict where category is null or category='' group by name order by createTime asc");
		else
			list = ddService.listForEntity("select id,name,remarks from DataDict where category=? group by name order by createTime asc", category);
			
		if(list.size() == 0){
			JSONObject node = new JSONObject();
			
			node.element("id", "-1");
			node.element("text", "无数据字典");
			node.element("icon", "fa fa-tags");
			
			data.add(node);
		}
		for(DataDict dd : list){
			JSONObject node = new JSONObject();
			
			node.element("id", dd.getId());
			String text = dd.getRemarks();
			if(!StringUtil.isEmpty(dd.getRemarks()))
				text += " ["+dd.getName()+"]";
			node.element("text", text);
			node.element("icon", "fa fa-tags");
			
			data.add(node);
		}
		return data;
	}
	
	
	/**
	 * 数据字典子项
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/item/{id}", method={RequestMethod.GET, RequestMethod.POST})
	public String item(Model model, @PathVariable String id){
		if(!StringUtil.isEmpty(id)){
			if(!id.equals("-1")){
				List<DataDictItem> list = ddiService.list(Restrictions.eq("dataDict.id", id), Order.asc("location"));
				model.addAttribute("list", list);
			}
		}
		return TPL + "item";
	}
	
	/**
	 * 数据字典子项(JSON)
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/item/data/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public List<DataDictItem> item_data(Model model, @PathVariable String id){
		List<DataDictItem> list = new ArrayList<DataDictItem>();
		if(!StringUtil.isEmpty(id)){
			if(!id.equals("-1")){
				list = ddiService.listForEntity("select id,k,v from DataDictItem where dataDict.id=? order by location asc", id);
			}
		}
		return list;
	}
	/**
	 * 修改字典名称和描述
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}", method={RequestMethod.GET, RequestMethod.POST})
	public String edit(Model model, @PathVariable String id){
		if(!StringUtil.isEmpty(id)){
			DataDict dd = ddService.getById(id);
			model.addAttribute("dd", dd);
			List<DataDict> list = ddService.listForEntity("select id, category from DataDict where category is not null and category != '' group by category");
			model.addAttribute("list", list);
		}
		return TPL + "edit";
	}
	/**
	 * 编辑
	 * @param dataDict
	 * @return
	 */
	@RequestMapping(value="/edit/do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject item(DataDict dataDict){
		try {
			if(StringUtil.isEmpty(dataDict.getName()))
				return WebResult.error("请填写字典名称！");
			DataDict eqDataDict = ddService.getById(dataDict.getId());
			if(ObjectUtil.isNull(eqDataDict))
				return WebResult.error("数据字典不存在!");
			//判断是否重复
			if(ddService.count(Restrictions.eq("name", dataDict.getName()), Restrictions.not(Restrictions.eq("id", dataDict.getId()))) > 0)
				return WebResult.error("'"+dataDict.getName()+"' 已存在!");
			
			eqDataDict.setCategory(dataDict.getCategory());
			eqDataDict.setName(dataDict.getName());
			eqDataDict.setRemarks(dataDict.getRemarks());
			ddService.merge(eqDataDict);
			
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	/**
	 * 保存数据字典
	 * @param name
	 * @param key
	 * @param value
	 * @return
	 */
	@RequestMapping(value="/save", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject save(DataDict datadict){
		try {
			if(StringUtil.isEmpty(datadict.getName()))
				return WebResult.error("请填写字典名称！");
			//判断是否重复
			if(ddService.count(Restrictions.eq("name", datadict.getName())) > 0)
				return WebResult.error("'"+datadict.getName()+"' 已存在!");
			datadict.setCreateUser(getCurrentUser().getUserName());
			datadict.setCreateTime(getCurrentTime());
			ddService.save(datadict);
			
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 保存数据字典项
	 * @param name
	 * @param key
	 * @param value
	 * @return
	 */
	@RequestMapping(value="/saveItem", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject saveItem(String id, @RequestParam(value="key",required=false)String[] key, @RequestParam(value="value",required=false)String[] value){
		try {
			if(StringUtil.isEmpty(id))
				return WebResult.error("非法操作！");
			DataDict dd = ddService.getById(id);
			if(ObjectUtil.isNull(dd))
				return WebResult.error("数据字典不存在！");
			//更新数据字典Item
			ddiService.updateItem(id, key, value, dd);
			
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 删除数据字典
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delete/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject delete(@PathVariable String id){
		try {
			ddService.delete(id);
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	/**
	 * 删除分类下的所有数据字典
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delete/catalog/{catalog}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject deleteCatalog(@PathVariable String catalog){
		try {
			ddService.executeHQL("delete from DataDictItem where dataDict.id in (select id from DataDict where category = ?)", catalog);
			ddService.executeHQL("delete from DataDict where category = ?", catalog);
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 数据字典同步
	 * @return
	 */
	@RequestMapping(value="/sync", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject sync(){
		try {
			boolean success = getDataDict().sync();//同步数据字典
			if(success)
				return WebResult.success();
			return WebResult.error("同步失败!");
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
}
