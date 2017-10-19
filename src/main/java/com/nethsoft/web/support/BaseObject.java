package com.nethsoft.web.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.web.support.WebContextHelper;
import com.nethsoft.orm.cache.ICacheProvider;
import com.nethsoft.orm.support.IDataDict;
import com.nethsoft.web.entity.system.Role;
import com.nethsoft.web.entity.system.Unit;
import com.nethsoft.web.entity.system.User;

/**
 * web包中所有类的父类
 * @author zengchao
 *
 */
public class BaseObject{
	/**
	 * 当前用户是否是超级管理员
	 * @return
	 */
	protected boolean isSuperadmin(){
		boolean isSuperadmin = false;
		User currentUser = getCurrentUser();
		if(ObjectUtil.isNotNull(currentUser)){
			if(currentUser.getUserName().equals(ApplicationCommon.SYSTEM_ADMIN)){
				isSuperadmin = true;
			}else{
				isSuperadmin = false;
			}
		}
		return isSuperadmin;
	}
	/**
	 * 当前用户是否是系统管理员
	 * 权限低于超级管理员
	 * @return
	 */
	@Deprecated
	protected boolean isSystemadmin(){
		boolean isSystemadmin = false;
		User currentUser = getCurrentUser();
		if(ObjectUtil.isNotNull(currentUser)){
			if(isSuperadmin()){//如果是超级管理员，也就拥有了系统管理员的角色
				isSystemadmin = true;
			}else{
				//判断角色列表中是否拥有系统管理员角色
				for(Role role : currentUser.getRoles()){
					if(role.getName().equals(getSystemConfig("SystemRole"))){
						isSystemadmin = true;
						break;
					}
				}
			}
		}
		return isSystemadmin;
	}
	/**
	 * 判断当前用户所在单位是否是系统单位
	 * 系统单位拥有全部权限
	 * @return
	 */
	protected boolean isSystemUnit(){
		boolean isSystemUnit = false;
		User currentUser = getCurrentUser();
		if(ObjectUtil.isNotNull(currentUser)){
			List<Unit> units = currentUser.getUnits();
			if(ObjectUtil.isNotNull(units)){
				for(Unit unit : units){
					if(unit.isSystem()){
						isSystemUnit = true;
						break;
					}
				}
			}
		}
		return isSystemUnit;
	}
	/**
	 * 获取数据字典
	 * @return
	 */
	protected IDataDict getDataDict(){
		return ApplicationCommon.DATADICT;
	}
	/**
	 * 获取系统缓存提供者
	 * @return
	 */
	protected ICacheProvider getApplicationCache(){
		return ApplicationCommon.CACHEPROVIDER;
	}
	
	/**
	 * 获取系统配置信息
	 * @param key
	 * @return
	 */
	protected String getSystemConfig(String key){
		return ApplicationCommon.SYSTEMCONFIGS.get(key);
	}
	
	/**
	 * 获取当前登录用户
	 * 不存在返回Null
	 * @return
	 */
	protected User getCurrentUser(){
		return WebContextHelper.getSession()==null?null:(User)WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
	}
	/**
	 * 获取当前用户所在全部机构的全部ID
	 * @return ID字符串 
	 */
	protected String getCurrentUserUnitIds(){
		StringBuffer sbUnitId = new StringBuffer();
		User currentUser = getCurrentUser();
		if(ObjectUtil.isNotNull(currentUser)){
			List<Unit> units = currentUser.getUnits();
			if(ObjectUtil.isNotNull(units)){
				for(Unit unit : units){
					sbUnitId.append(",'"+unit.getId()+"'");
				}
			}
		}
		if(sbUnitId.length()>0)
			sbUnitId = new StringBuffer(sbUnitId.substring(1));
		return sbUnitId.toString();
	}
	/**
	 * 获取当前用户所在全部机构的全部ID
	 * @return ID集合
	 */
	protected List<String> getCurrentUserUnitIdList(){
		List<String> lstUnitIds = new ArrayList<String>();
		User currentUser = getCurrentUser();
		if(ObjectUtil.isNotNull(currentUser)){
			List<Unit> units = currentUser.getUnits();
			if(ObjectUtil.isNotNull(units)){
				for(Unit unit : units){
					lstUnitIds.add(unit.getId());
				}
			}
		}
		return lstUnitIds;
	}
	/**
	 * 获取请求对象
	 * @return
	 */
	protected HttpServletRequest getRequest(){
		return WebContextHelper.getRequest()==null?null:WebContextHelper.getRequest();
	}
	/**
	 * 获取当前时间 格式为 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	protected String getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	/**
	 * 获取当前时间 格式为 pattern
	 * @param pattern 时间格式
	 * @return
	 */
	protected String getCurrentTime(String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}
	/**
	 * 获取访问着的IP地址
	 * @return
	 */
	protected String getRequestAddr(){
		String ip = getRequest().getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = getRequest().getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = getRequest().getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = getRequest().getRemoteAddr();
	    }
	    if("0:0:0:0:0:0:0:1".equals(ip))
	    	ip = "localhost";
	    if("127.0.0.1".equals(ip))
	    	ip = "localhost";
	    return ip;
	}
	/**
	 * 判断集合中是否确定的属性及属性值
	 * @param array
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean hasPropertyValueInArray(Object array,Object propertyName,Object propertyValue){
		if(array instanceof JSONArray){
			JSONArray jArray = (JSONArray) array;
			int len = jArray.size();
			for(int i=0;i<len;i++){
				JSONObject jObj = jArray.getJSONObject(i);
				if(propertyValue.equals(jObj.get(propertyName))){
					return true;
				}
			}
		}else if(array instanceof List<?>){
			List<JSONObject> jList = (List<JSONObject>) array;
			for(JSONObject jObj : jList){
				if(propertyValue.equals(jObj.get(propertyName))){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 对比验证码
	 * @param token
	 * @return
	 */
	public boolean VerificationCode(String token){
		HttpSession session = WebContextHelper.getSession();
		Object _token = session.getAttribute(ApplicationCommon.VERYFICATION_CODE);
		if(ObjectUtil.isNotNull(_token)){
			if(token.equals(_token.toString()))
				return true;
			else
				return false;
		}
		return false;
	}
}
