package com.nethsoft.core.web.support;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.ObjectUtil;

import net.sf.json.JSONObject;

/**
 * 异步响应类型
 * @author zengchao
 *
 */
public  class WebResult {
	private static Logger logger = Logger.getLogger(WebResult.class);
	/**
	 * 没有任何改变
	 * @return
	 */
	public static JSONObject NoChange(){
		JSONObject obj = new JSONObject();
		obj.element("NoChanges", true);
		return obj;
	}
	/**
	 * 需要验证密码
	 * @return
	 */
	public static JSONObject NeedVerifyPassword(){
		JSONObject obj = new JSONObject();
		obj.element("NeedVerifyPassword", true);
		return obj;
	}
	/**
	 * 操作成功
	 * @return
	 */
	public static JSONObject success(){
		JSONObject obj = new JSONObject();
		obj.element("success", true);
		return obj;
	}
	/**
	 * 操作失败
	 * @param 错误提示 msg
	 * @return
	 */
	public static JSONObject error(String msg){
		JSONObject obj = new JSONObject();
		obj.element("success", false);
		obj.element("error", msg);
		return obj;
	}
	
	/**
	 * 操作失败
	 * @param 异常
	 * @return
	 */
	public static JSONObject error(Exception e){
		logger.debug(e.getCause().getClass()+"<-->"+e.getCause().getLocalizedMessage());
		Map<String, String> exMap = ApplicationCommon.DATADICT.getMap("sys_exception");
		if(ObjectUtil.isNotNull(exMap)){
			String exp = exMap.get(e.getCause().getClass().getName());
			if(StringUtil.isBlank(exp)){
				exp = e.toString();
			}
			return error(exp);
		}
		return error(e.toString());
	}
}

