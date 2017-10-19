package com.nethsoft.orm.cache;

import java.util.HashMap;
import java.util.Map;


public class DefaultCacheProvider implements ICacheProvider{
	private Map<String,Object> data = new HashMap<String, Object>();
	public Object get(String key) {
		return data.get(key);
	}
	public void put(String key, Object value) {
		data.put(key, value);
	}

	public boolean sync() {
		return false;
	}
}
