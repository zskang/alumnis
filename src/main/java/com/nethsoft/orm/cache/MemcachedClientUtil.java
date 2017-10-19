package com.nethsoft.orm.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.ObjectUtil;

public final class MemcachedClientUtil {
	private static MemCachedClient cachedClient = null;
	private static boolean available = false;//是否可用
	
	/**
	 * 初始化
	 */
	private static void init(){
		if(ObjectUtil.isNull(cachedClient)){
			Properties prop = getProp();
			
			cachedClient = new MemCachedClient();
			//获取连接池实例
			SockIOPool pool = SockIOPool.getInstance();
			
			//服务器列表 及权重
			String[] servers = {prop.getProperty("cache.memcached.url","cache.nethsoft.com")+":"+prop.getProperty("port", "11211")};
			Integer[] weights = {Integer.parseInt(prop.getProperty("cache.memcached.weight", "3"))};
			
			// 设置服务器信息
			pool.setServers(servers);
			pool.setWeights(weights);
			
			// 设置初始化连接数、最小连接数、最大连接数、最大处理时间
			pool.setInitConn(Integer.parseInt(prop.getProperty("cache.memcached.initConn", "10")));
			pool.setMinConn(Integer.parseInt(prop.getProperty("cache.memcached.minConn", "10")));
			pool.setMaxConn(Integer.parseInt(prop.getProperty("cache.memcached.maxConn", "10")));
			pool.setMaxIdle(Integer.parseInt(prop.getProperty("cache.memcached.maxIdle", "3600000")));
			
			//设置连接池守护线程的睡眠时间
			pool.setMaintSleep(Integer.parseInt(prop.getProperty("cache.memcached.maintSleep", "60")));
			
			//设置TCP参数，连接超时
			pool.setNagle(false);
			pool.setSocketTO(60);
			pool.setSocketConnectTO(0);
			
			// 初始化连接池
			pool.initialize();
			
		}
	}
	
	/**
	 * 获取Memcached客户端
	 * @return
	 */
	public static MemCachedClient getClient(){
		init();
		return cachedClient;
	}
	/**
	 * 获取配置
	 * @return
	 */
	protected static Properties getProp(){
		return ApplicationCoreConfigHelper.getPropertyGroup("cache.memcached.");
	}
	
	/**
	 * 保存配置
	 * @param prop
	 */
	protected static void saveProp(Properties prop){
		try {
			Properties allProp = ApplicationCoreConfigHelper.getProperties();
			Enumeration<?> keys = prop.keys();
			while(keys.hasMoreElements()){
				Object key = keys.nextElement();
				allProp.put(key, prop.get(key));
			}
			File file = ApplicationCoreConfigHelper.getFile();
			OutputStream out = new FileOutputStream(file);
			allProp.store(out, "");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试参数是否可用
	 * @param prop
	 * @return
	 */
	protected static boolean testProp(Properties prop){
		try {
			MemCachedClient cc = new MemCachedClient();
			//获取连接池实例
			SockIOPool pool = SockIOPool.getInstance();
			
			//服务器列表 及权重
			String[] servers = {prop.getProperty("url","cache.nethsoft.com")+":"+prop.getProperty("port", "11211")};
			Integer[] weights = {Integer.parseInt(prop.getProperty("weight", "3"))};
			
			// 设置服务器信息
			pool.setServers(servers);
			pool.setWeights(weights);
			
			// 设置初始化连接数、最小连接数、最大连接数、最大处理时间
			pool.setInitConn(Integer.parseInt(prop.getProperty("initConn", "10")));
			pool.setMinConn(Integer.parseInt(prop.getProperty("minConn", "10")));
			pool.setMaxConn(Integer.parseInt(prop.getProperty("maxConn", "10")));
			pool.setMaxIdle(Integer.parseInt(prop.getProperty("maxIdle", "3600000")));
			
			//设置连接池守护线程的睡眠时间
			pool.setMaintSleep(Integer.parseInt(prop.getProperty("maintSleep", "60")));
			
			//设置TCP参数，连接超时
			pool.setNagle(false);
			pool.setSocketTO(60);
			pool.setSocketConnectTO(0);
			
			// 初始化连接池
			pool.initialize();
			
			return cc.set("test", true);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return false;
	}
	/**
	 * 判断缓存 是否可用
	 * @return
	 */
	public static boolean isAvailable(){
		if(available) {
			if(ObjectUtil.isNull(cachedClient.get("refreshing")))
				return true;
			else
				return !(Boolean) cachedClient.get("refreshing");
		}
		return available;
	}
	/**
	 * 设置服务是否可用
	 * @param a
	 */
	public static void setAvailable(boolean a){
		available = a;
	}
	/**
	 * 心跳测试
	 * @return
	 */
	public static boolean test(){
		init();
		return cachedClient.set("test", true);
	}
	
	/**
	 * 重新初始化
	 * @return
	 */
	public static boolean rebulidClient(){
		setAvailable(false);
		cachedClient = null;
		init();
		return true;
	}
	/**
	 * 设置正在处于刷新缓存状态
	 * 路由发现处于刷新缓存状态时，调用数据库数据，保持服务不中断
	 * @return
	 */
	public static boolean setRefreshing(boolean c){
		return cachedClient.set("refreshing", c);
	}
	/**
	 * 判断是否处于刷新缓存状态，或者缓存服务器不可用状态
	 * @return
	 */
	public static boolean isRefreshing(){
		if(!isAvailable()){//如果缓存服务器不可用，标记成刷新状态，调用数据库数据
			return true;
		}else{
			return (Boolean) cachedClient.get("refreshing");
		}
	}
}
