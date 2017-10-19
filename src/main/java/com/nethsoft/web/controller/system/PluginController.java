package com.nethsoft.web.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.plugin.PluginPool;
import com.nethsoft.core.plugin.PluginRegister;
import com.nethsoft.core.plugin.annotation.Plugin;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.service.system.LogService;

@Controller
@RequestMapping("/system/plugin")
public class PluginController extends BaseController<Object>{
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model){
		//获取插件列表
		List<Plugin> list = new ArrayList<Plugin>();
		//存放插件名和插件类名
		Map<String,String> mapPlugin = new HashMap<String, String>();
		//获取系统插件目录下的所有插件
		PluginRegister pr = new PluginRegister();
		Set<Class<?>> plugins = pr.getAllPluginClasses();
		
		Iterator<Class<?>> iter = plugins.iterator();
		while(iter.hasNext()){
			Class<?> cls = iter.next();
			Plugin plu = cls.getAnnotation(Plugin.class);
			list.add(plu);
			mapPlugin.put(plu.value(), cls.getName());
		}
		model.addAttribute("list", list);
		model.addAttribute("PluginPool", PluginPool.getAllPlugins());
		model.addAttribute("mapPlugin", mapPlugin);
		return "system/plugin/index";
	}
	
	/**
	 * 装载插件
	 * @param cls
	 * @return
	 */
	@RequestMapping(value="/register", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject register(String cls){
		try {
			PluginRegister pr = new PluginRegister();
			if(pr.register(Class.forName(cls))){
				logService.info("插件管理", cls+"插件装载成功！", getCurrentUser().getUserName(), getRequestAddr());
				return WebResult.success();
			}else{
				logService.error("插件管理", cls+"插件装载失败！", getCurrentUser().getUserName(), getRequestAddr());
				return WebResult.error("插件装载失败！");
			}
		} catch (Exception e) {
			logger.error(e);
			logService.error("插件管理", cls+"插件装载失败！", getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.error(e);
		}
	}
	
	/**
	 * 卸载插件
	 * @param cls
	 * @return
	 */
	@RequestMapping(value="/unregister", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject unregister(String name){
		try {
			PluginRegister pr = new PluginRegister();
			pr.unregister(PluginPool.getPluginBean(name));
			logService.info("插件管理", name+"插件卸载成功！", getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			logService.error("插件管理", name+"插件卸载失败！", getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.error(e);
		}
	}
}
