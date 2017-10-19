package com.nethsoft.core.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.nethsoft.orm.cache.DefaultCacheProvider;
import com.nethsoft.orm.cache.ICacheProvider;
import com.nethsoft.orm.support.DefaultDataDictProvider;
import com.nethsoft.orm.support.IDataDict;

/**
 * 系统全局变量配置类
 * @author ZENGCHAO
 *
 */
public final class ApplicationCommon implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4723677111800151534L;
	/**
	 * 项目BasePath
	 */
	public static String BASEPATH = "";
	/**
	 * 项目资源路径
	 */
	public static String RESPATH = "";
	/**
	 * 项目类根目录
	 */
	public static String CLASSPATH = "";
	/**
	 * web项目根目录
	 */
	public static String WEBAPPS_PATH = "";
	/**
	 * 当前App名称
	 */
	public static String APP = "";
	
	/**
	 * 后台用户session
	 */
	public final static String SESSION_ADMIN = "ADMINSESSION";
	
	/**
	 * 前台用户session
	 */
	public final static String SESSION_USER = "USERSESSION";
	/**
	 * 系统超级管理员用户名
	 * 不可删除该用户
	 */
	public final static String SYSTEM_ADMIN = "superadmin";
	/**
	 * 加密Key
	 * 没有此Key，无法从密文获取明文密码
	 */
	public final static String DEC_KEY = "zc@zframework";
	
	/**
	 * 登录验证次数
	 * 存储在Session中的名称
	 */
	public final static String LOGIN_DENIED_NUMBER = "LoginDeniedNumber";
	/**
	 * 是否验证通过
	 * 用于执行关键性操作且需要验证用户密码
	 * 存储在Session中的名称
	 */
	public final static String ALLOW_ACCESS = "AllowAccess";
	/**
	 * 记录验证通过的时间
	 * 用于下次验证时进行时间比较
	 * 存储在Session中的名称
	 */
	public final static String ALLOW_ACCESS_TIMESPAN = "AllowAccess_TIMESPAN";
	/**
	 * 验证码
	 * 存储在Session中的名称
	 */
	public final static String VERYFICATION_CODE = "VERYFICATION_CODE";
	
	/**
	 * 国际化标记
	 */
	public final static String LOCALE_REQUEST = "locale";
	/**
	 * 国际化Session中标记
	 */
	public final static String LOCALE_SESSION = "lang.locale";
	/**
	 * 系统登录的用户名集合
	 */
	public static Set<String> LOGIN_USERS = new HashSet<String>();
	/**
	 * 记录已经登录的用户
	 */
	public static Map<String,HttpSession> LOGIN_SESSIONS = new HashMap<String, HttpSession>();
	/**
	 * 系统公用型数据，存储在数据库中
	 */
	public static Map<String,String> SYSTEMCONFIGS = new HashMap<String, String>();
	/**
	 * 数据字典
	 */
	public static IDataDict DATADICT = ApplicationContextHelper.getInstance().createBean(DefaultDataDictProvider.class);
	/**
	 * 存放缓存
	 */
	public static ICacheProvider CACHEPROVIDER = ApplicationContextHelper.getInstance().createBean(DefaultCacheProvider.class);
}