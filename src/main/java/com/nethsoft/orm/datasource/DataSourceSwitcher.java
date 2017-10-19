package com.nethsoft.orm.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.nethsoft.core.support.ApplicationContextHelper;
import com.nethsoft.core.util.ObjectUtil;

/**
 * 多数据源转换类
 * @author ZENGCHAO
 *
 */
public class DataSourceSwitcher implements DataSource{
	private final Logger logger = Logger.getLogger(this.getClass());
	private DataSource dataSource = null;//当前数据源
	private DataSource defaultDataSource = null;//默认数据源
	private String dataSourceName = "masterDataSource";
	public DataSourceSwitcher(){
		
	}
	public DataSourceSwitcher(String dataSourceName){
		this.dataSource = getDataSource(dataSourceName);
		this.dataSourceName = dataSourceName;
	}
	
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public void setDataSource(DataSource dataSource){
		if(ObjectUtil.isNotNull(dataSource)){
			this.dataSource = dataSource;
			if(ObjectUtil.isNull(defaultDataSource)){
				this.defaultDataSource = dataSource;
			}
		}
	}
	/**
	 * 设置数据源
	 * @param dataSourceName
	 */
	public void setDataSource(String dataSourceName){
		setDataSource(getDataSource(dataSourceName));
		this.dataSourceName = dataSourceName;
	}
	
	public DataSource getDataSource(){
		return this.dataSource;
	}
	/**
	 * 从Spring配置文件中动态读取数据源
	 * @param 数据源Bean的ID
	 * @return
	 */
	public DataSource getDataSource(String dataSourceName){
		return ApplicationContextHelper.getInstance().getBean(dataSourceName);
	}
	
	/**
	 * 获取所有数据源
	 * @return
	 */
	public List<String> getAllDataSources(){
		List<String> dataSources = new ArrayList<String>();
		
		dataSources.add("masterDataSource");
		dataSources.add("slaveDataSource");
		
		return dataSources;
	}
	/**
	 * 还原为默认数据源
	 */
	public void restoreDefaultDataSource(){
		logger.debug("设置数据源为初始数据源!");
		this.dataSource = this.defaultDataSource;
	}
	/**
	 * 切换至主服务器
	 * 主要负责写入更新操作
	 */
	public boolean switchToMaster(){
		DataSource ds = getDataSource("masterDataSource");
		if(ObjectUtil.isNotNull(ds)){
			//TODO 测试时关闭主数据源切换
			setDataSource(ds);
			return true;
		}else{
			restoreDefaultDataSource();
			logger.debug("未找到masterDataSource数据源配置,使用默认数据源");
			return false;
		}
	}
	/**
	 * 切换至从服务器
	 * 主要负责读取操作
	 */
	public boolean switchToSlave(){
		DataSource ds = getDataSource("slaveDataSource");
		if(ObjectUtil.isNotNull(ds)){
			//TODO 测试时关闭从数据源切换
			 setDataSource(ds);
			return true;
		}else{
			restoreDefaultDataSource();
			logger.debug("未找到slaveDataSource数据源配置，使用默认数据源");
			return false;
		}
	}
	
	public PrintWriter getLogWriter() throws SQLException { 
		return getDataSource().getLogWriter();
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		getDataSource().setLogWriter(out);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		getDataSource().setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return getDataSource().getLoginTimeout();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return getDataSource().unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return getDataSource().isWrapperFor(iface);
	}

	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}
	public Connection getConnection(String username, String password)
			throws SQLException {
		return getDataSource().getConnection(username, password);
	}
	
	public java.util.logging.Logger getParentLogger()
			throws SQLFeatureNotSupportedException {
		return java.util.logging.Logger.getLogger("DataSourceSwitcher");
	}
	
	
}
