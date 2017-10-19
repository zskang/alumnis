package com.nethsoft.orm.support;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据字典默认实现类
 * @author zengchao
 *
 */
public class DefaultDataDictProvider implements IDataDict{
	private Map<String,Object> data = null;
	private boolean isLoaded = false;
	
	public Object get(String key) {
		if(!isLoaded)
			this.sync();
		return data.get(key);
	}

	public synchronized boolean sync() {
		data = new LinkedHashMap<String, Object>();
		isLoaded = true;
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<String> getList(String key) {
		if(!isLoaded)
			this.sync();
		return (List<String>) data.get(key);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getMap(String key) {
		if(!isLoaded)
			this.sync();
		return (Map<String, String>) data.get(key);
	}

	public void put(String key, Object data) {
		if(!isLoaded)
			this.sync();
	}
}
