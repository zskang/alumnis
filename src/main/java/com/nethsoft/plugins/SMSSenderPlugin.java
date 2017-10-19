package com.nethsoft.plugins;

import java.lang.reflect.Method;

import com.nethsoft.core.plugin.IPlugin;
import com.nethsoft.core.plugin.annotation.Plugin;

@Plugin(value="短信发送插件", autoloading=false)
public class SMSSenderPlugin implements IPlugin{

	public boolean init(String[] args) {
		return false;
	}

	public void destory(String[] args) {
		
	}

	public void before(Method method, Object[] params, Object obj) {
		
	}

	public void afterReturning(Object returnValue, Method method,
			Object[] params, Object obj) {
		
	}

}
