package com.nethsoft.core.plugin;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.nethsoft.core.plugin.annotation.Plugin;
import com.nethsoft.core.plugin.annotation.PluginAfterAdvice;
import com.nethsoft.core.plugin.annotation.PluginBeforeAdvice;
import com.nethsoft.core.support.ApplicationContextHelper;
import com.nethsoft.core.util.ClassUtil;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;

/**
 * 插件注册类
 * @author zengchao
 *
 */
public class PluginRegister {
	 
	private Logger logger = Logger.getLogger(this.getClass());
	public void init() {
		Set<Class<?>> classes = ClassUtil.getClassesByAnnotation("com.nethsoft.plugins",Plugin.class);
		Iterator<Class<?>> iter = classes.iterator();
		while(iter.hasNext()){
			Class<?> cls = iter.next();
			Plugin plu = cls.getAnnotation(Plugin.class);
			if(!plu.autoloading())//判断插件系统启动时是否加载
				continue;
			register(cls);
		}
	}
	
	/**
	 * 获取目录中的所有插件
	 * 包含系统加载和系统未加载的
	 * @return
	 */
	public Set<Class<?>> getAllPluginClasses(){
		Set<Class<?>> classes = ClassUtil.getClassesByAnnotation("com.nethsoft.plugins",Plugin.class);
		return classes;
	}
	
	/**
	 * 注销
	 */
	public void destory(){
		Map<String, IPlugin> map = PluginPool.getAllPlugins();
		Iterator<String> iterName = map.keySet().iterator();
		while(iterName.hasNext()){
			unregister(map.get(iterName.next()));
		}
	}
	
	/**
	 * 插件加载器
	 * @param cls
	 * @return
	 */
	public boolean register(Class<?> cls){
		Plugin plu = cls.getAnnotation(Plugin.class);
		if(ObjectUtil.isNull(plu.value()) || StringUtil.isEmpty(plu.value())){
			logger.info("插件未加载:插件名称不可为空");
			return false;
		}
		//判断插件池中是否有相同的插件,如已存在，不重新加载
		if(ObjectUtil.isNotNull(PluginPool.getPluginBean(plu.value()))){
			logger.info("["+plu.value()+"] 插件系统中已存在，请勿重复注册!");
			return false;
		}
		boolean isPlugin = false;
		for(Class<?> classType : cls.getInterfaces()){
			if(classType == IPlugin.class){
				isPlugin = true;
				break;
			}
		}
		if(!isPlugin){
			logger.info(plu.value()+"插件未加载:必须实现"+IPlugin.class.getName()+"接口!");
			return false;
		}
		IPlugin plugin = PluginLoader.load(plu.value(), cls, plu.params());
		if(ObjectUtil.isNotNull(plugin)){
			PluginPool.addPluginBean(plu.value(), plugin);
		}else{
			logger.info(plu.value()+"插件未加载:执行初始化方法返回false!");
			return false;
		}
		PluginBeforeAdvice beforeAdvice = cls.getAnnotation(PluginBeforeAdvice.class);
		//拦截规则  - before
		if(ObjectUtil.isNotNull(beforeAdvice)){
			if(!StringUtil.isEmpty(beforeAdvice.expression())){
				PluginAdvice pAdvice = ApplicationContextHelper.getInstance().getBean(PluginAdvice.class);
				pAdvice.addBeforeAdvices(plugin, beforeAdvice.expression());
			}
		}
		PluginAfterAdvice afterAdvice = cls.getAnnotation(PluginAfterAdvice.class);
		//拦截规则  - after
		if(ObjectUtil.isNotNull(afterAdvice)){
			if(!StringUtil.isEmpty(afterAdvice.expression())){
				PluginAdvice pAdvice = ApplicationContextHelper.getInstance().getBean(PluginAdvice.class);
				pAdvice.addAfterAdvices(plugin, afterAdvice.expression());
			}
		}
		logger.debug("Plugin(name:"+plu.value()+",class:"+cls.getName()+") Initialized!");
		return true;
	}
	
	/**
	 * 插件解注册
	 * @param cls
	 * @return
	 */
	public boolean unregister(IPlugin plugin){
		if(ObjectUtil.isNotNull(plugin)){
			Plugin plu = plugin.getClass().getAnnotation(Plugin.class);
			plugin.destory(plu.params());
			//移除拦截
			PluginAdvice pAdvice = ApplicationContextHelper.getInstance().getBean(PluginAdvice.class);
			pAdvice.removeAfterAdvices(plugin);
			pAdvice.removeBeforeAdvices(plugin);
			//从插件池中移除
			PluginPool.getAllPlugins().remove(plu.value());
		}
		return true;
	}
	
	/**
	 * 插件解注册
	 * @param cls
	 * @return
	 */
	public boolean unregister(Class<?> cls){
		if(ObjectUtil.isNotNull(cls)){
			try {
				Plugin plu = cls.getAnnotation(Plugin.class);
				IPlugin plugin = (IPlugin) cls.newInstance();
				plugin.destory(plu.params());
				PluginPool.getAllPlugins().remove(plu.value());//从线程池中移除
			} catch (InstantiationException e) {
				logger.error(e);
				return false;
			} catch (IllegalAccessException e) {
				logger.error(e);
				return false;
			} catch (Exception e){
				logger.error(e);
				return false;
			}
		}
		return true;
	}
	
}
