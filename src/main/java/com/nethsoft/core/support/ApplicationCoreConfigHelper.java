package com.nethsoft.core.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import com.nethsoft.core.util.ObjectUtil;

public class ApplicationCoreConfigHelper {
	private static Properties cacheProperties = null;
	public static File getFile(){
		File file = null;
		try {
			file = ApplicationContextHelper.getInstance().getApplicationContext().getResource("classpath:core-config.properties").getFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	/**
	 * 获取所有配置项
	 * @return
	 */
	public static Properties getProperties(){
		Properties prop = new Properties();
		try {
			File file = getFile();
			InputStream is = new FileInputStream(file);
			prop.load(is);
			is.close();
			
			cacheProperties = prop;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	/**
	 * 获取配置值
	 * @param key
	 * @return
	 */
	public static String getProperty(String key){
		if(ObjectUtil.isNull(cacheProperties))
			getProperties();
		
		return cacheProperties.getProperty(key);
	}
	
	/**
	 * 获取配置组
	 * 匹配前缀， 如：mail.
	 * @param group
	 * @return
	 */
	public static Properties getPropertyGroup(String group){
		Properties allProp = getProperties();
		Properties prop = new Properties();
		Enumeration<?> keys = allProp.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement().toString();
			if(key.startsWith(group)){
				prop.put(key, allProp.get(key));
			}
		}
		return prop;
	}
}
