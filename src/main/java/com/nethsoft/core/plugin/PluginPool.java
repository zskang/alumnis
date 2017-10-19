package com.nethsoft.core.plugin;

import java.util.HashMap;
import java.util.Map;

public class PluginPool {
	private PluginPool(){
		
	}
	private static Map<String,IPlugin> mapPlugins = new HashMap<String, IPlugin>();
	
	/**
	 * 获取所有已加载的插件
	 * @return
	 */
	public static Map<String,IPlugin> getAllPlugins(){
		return mapPlugins;
	}
	/**
	 * 根据插件名称获取插件
	 * @param name
	 * @return
	 */
	public static IPlugin getPluginBean(String name){
		return mapPlugins.get(name);
	}
	
	/**
	 * 根据插件名称获取插件
	 * @param name
	 * @return
	 */
	public static IPlugin getPluginBean(Class<?> clazz){
		IPlugin plugin = null;
		for(IPlugin p : mapPlugins.values()){
			if(p.getClass().equals(clazz)){
				plugin = p;
				break;
			}
		}
		return plugin;
	}
	/**
	 * 增加插件
	 * @param name
	 * @param plugin
	 */
	public static void addPluginBean(String name,IPlugin plugin){
		mapPlugins.put(name, plugin);
	}
}
