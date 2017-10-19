package com.nethsoft.web.support;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nethsoft.core.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class AppResult { 
	public static JSONObject success(){
		JSONObject obj = new JSONObject();
		obj.element("result", "success");
		obj.element("message","操作成功");
		return obj;
	}
	
	
	public static JSONObject success(String msg){
		JSONObject obj = new JSONObject();
		obj.element("result", "success");
		obj.element("message",msg);
		return obj;
	}

	
	
	public static JSONObject error() {
		JSONObject obj = new JSONObject();
		obj.element("result", "fail");
		obj.element("message","操作失败");
		return obj;
	}
	
	public static JSONObject error(String msg) {
		JSONObject obj = new JSONObject();
		obj.element("result", "fail");
		obj.element("message",msg);
		return obj;
	}
	
}
