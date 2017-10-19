package com.nethsoft.core.plugin;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;

import com.nethsoft.core.support.ApplicationContextHelper;
import com.nethsoft.core.util.ObjectUtil;

/**
 * 插件加载器
 * @author zengchao
 *
 */
public class PluginLoader{
	private static Logger logger = Logger.getLogger(PluginLoader.class);
	/**
	 * 
	 * @param 插件名称
	 * @param 插件类
	 * @param 初始化参数
	 * @return
	 */
	public static IPlugin load(String name,Class<?> clazz,String[] args){
		IPlugin plugin = null;
		try {
			plugin = (IPlugin) ApplicationContextHelper.getInstance().getApplicationContext().getAutowireCapableBeanFactory().createBean(clazz);
		} catch (BeansException e) {
			e.printStackTrace();
			logger.error("Plugin Unable to create bean:"+clazz.getName());
			logger.error("Plugin:"+e.getMessage());
		} catch (IllegalStateException e) {
			e.printStackTrace();
			logger.error("Plugin:"+e.getMessage());
		}
		if(ObjectUtil.isNotNull(plugin))
			if(plugin.init(args)){
				return plugin;
			}else{
				return null;
			}
		else
			return null;
	}
}
