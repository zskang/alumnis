package com.nethsoft.orm.support;

import java.util.List;
import java.util.Map;

import com.nethsoft.orm.cache.ICacheProvider;

public interface IDataDict extends ICacheProvider{
	Object get(String key);
	
	List<String> getList(String key);
	
	Map<String,String> getMap(String key);
	
	void put(String key, Object data);
	
}
