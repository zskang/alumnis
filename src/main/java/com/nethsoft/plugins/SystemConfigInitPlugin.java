package com.nethsoft.plugins;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import com.nethsoft.core.plugin.IPlugin;
import com.nethsoft.core.plugin.annotation.Plugin;
import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.web.entity.system.Config;
import com.nethsoft.web.service.system.ConfigService;

@Plugin(value="系统参数初始化插件", autoloading=true)
public class SystemConfigInitPlugin implements IPlugin{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ConfigService configService;
	@Override
	public boolean init(String[] args) {
		//设置项目目录
		try {
			String path = this.getClass().getResource("/").toURI().getPath();
			
			ApplicationCommon.CLASSPATH = path;
			ApplicationCommon.WEBAPPS_PATH = path.replace("/WEB-INF/classes/", "");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		//设置资源路径
		Properties props = ApplicationCoreConfigHelper.getPropertyGroup("system.");
		ApplicationCommon.APP = props.getProperty("system.app");
		ApplicationCommon.RESPATH = props.getProperty("system.respath");
		List<Config> configs = configService.list(Restrictions.eq("enabled", true));
		ApplicationCommon.SYSTEMCONFIGS.clear();
		for(Config config : configs){
			ApplicationCommon.SYSTEMCONFIGS.put(config.getK(), config.getV());
		}
		logger.info("系统配置参数加载完成！");
		return true;
	}

	@Override
	public void destory(String[] args) {
		
	}

	@Override
	public void before(Method method, Object[] params, Object obj) {
		
	}

	@Override
	public void afterReturning(Object returnValue, Method method,
			Object[] params, Object obj) {
		
	}
	
	
}
