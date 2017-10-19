package com.nethsoft.web.service.system;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.hibernate.SQLQuery;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.web.service.BaseService;

@Service
public class DataBaseService extends BaseService<Object>{
	
	/**
	 * 执行SQL语句
	 * @param sql
	 * @return
	 */
	public List<?> executeQuery(String sql){
		SQLQuery query = getBaseDao().getSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setMaxResults(20);
		return query.list();
	}

	/**
	 * 获取数据库 Catalog
	 * @return
	 */
	public List<String> getCatalogs() {
		final List<String> catalogs = new ArrayList<String>();
		this.getBaseDao().getSession().doWork(new Work() {
			@Override
			public void execute(Connection conn) throws SQLException {
				ResultSet rs;
				DatabaseMetaData meta;
				
				meta = conn.getMetaData();
				if(meta.getDatabaseProductName().equals("Oracle")){
					catalogs.add(meta.getUserName());
				}else{
					rs = meta.getCatalogs();
					while(rs.next()){
						catalogs.add(rs.getString(1));
					}
				}
			}
		});
		return catalogs;
	}
	
	/**
	 * 获取数据库表
	 * @param catalog
	 * @return
	 */
	public List<String> getTables(final String catalog) {
		final List<String> tableNames = new ArrayList<String>();
		this.getBaseDao().getSession().doWork(new Work() {
			@Override
			public void execute(Connection conn) throws SQLException {
				ResultSet rs;
				DatabaseMetaData meta;
				
				meta = conn.getMetaData();
				if(meta.getDatabaseProductName().equals("Oracle")){
					rs = meta.getTables(null,catalog, "%", new String[]{"TABLE"});
				}else{
					rs = meta.getTables(catalog, "%", "%", new String[]{"TABLE"});
				}

				while(rs.next()){
					tableNames.add(rs.getString(3));
				}
			}
		});
		return tableNames;
	}
	/**
	 * 获取外键
	 * @param catalog
	 * @param table
	 * @return Map<表名,列名>
	 */
	public Map<String, String> getTableForget(final String catalog, final String table){
		final Map<String, String> map = new HashMap<>();
		this.baseDao.getSession().doWork(new Work() {
			
			@Override
			public void execute(Connection conn) throws SQLException {
				DatabaseMetaData databaseMetaData = conn.getMetaData();
				//获取外键信息
				ResultSet rs_for = databaseMetaData.getImportedKeys(catalog, catalog, table);
				while(rs_for.next()){
					map.put(rs_for.getString(8), rs_for.getString(3));
				}
			}
		});
		return map;
	}
	/**
	 * 获取表字段名
	 * @param catalog
	 * @param table
	 * @return
	 */
	public Map<String, String> getTableColumnsName(final String catalog, final String table){
		final String sql = "select * from "+table;
		final Map<String, String> columns = new LinkedHashMap<String, String>();
		this.baseDao.getSession().doWork(new Work() {
			@Override
			public void execute(Connection conn) throws SQLException {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				ResultSetMetaData metaData = rs.getMetaData();
				int count = metaData.getColumnCount();
				for(int i=1;i<=count;i++){
					String columnName = metaData.getColumnName(i);//名称
					columns.put(columnName, columnName);
				}
				
				//获取列备注信息
				DatabaseMetaData databaseMetaData = conn.getMetaData();
				ResultSet rs_col = databaseMetaData.getColumns(catalog, "%", table, "%");
				while(rs_col.next()){
					String name = rs_col.getString("COLUMN_NAME");
					String remarks = rs_col.getString("REMARKS");
					if(!StringUtil.isEmpty(remarks))
						columns.put(name, remarks);
				}
				rs.close();
				stmt.close();
			}
		});
		
		return columns;
	}
	/**
	 * 获取表字段
	 * @param catalog
	 * @param table
	 */
	public Map<String, Map<String, Object>> getTableColumnsFull(final String catalog, final String table, final JSONObject mapping) {
		final String sql = "select * from "+table;
		final Map<String, Map<String, Object>> mapInfo = new LinkedHashMap<>();
		this.baseDao.getSession().doWork(new Work() {
			
			@Override
			public void execute(Connection conn) throws SQLException {
				DatabaseMetaData databaseMetaData = conn.getMetaData();
				//获取主键信息
				List<String> primaryKeys = new ArrayList<>();
				ResultSet rs_key = databaseMetaData.getPrimaryKeys(catalog, catalog, table);
				while(rs_key.next()){
					primaryKeys.add(rs_key.getString(4));
				}
				rs_key.close();
				
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				ResultSetMetaData metaData = rs.getMetaData();
				int count = metaData.getColumnCount();
				for(int i=1;i<=count;i++){
					Map<String, Object> map = new LinkedHashMap<>();
					String columnName = metaData.getColumnName(i);//名称
					String columnType = metaData.getColumnClassName(i);//对应Java类型
					if(columnType == null){
						String columnTypeName = metaData.getColumnTypeName(i);
						if(columnTypeName.equals("BINARY_DOUBLE")){//此处的判断有待增加
							columnType = "java.lang.Double";
						}
					}else if(columnType.equals("java.lang.Double")){
						columnType = "java.math.BigDecimal";
					}else if(columnType.equals("java.lang.Long")){
						columnType = "java.math.BigDecimal";
					}else if(columnType.equals("java.sql.Timestamp")){
						columnType = "java.util.Date";
					}else if(columnType.equals("java.sql.Date")){
						columnType = "java.util.Date";
					}else if(columnType.equals("java.sql.Time")){
						columnType = "java.util.Date";
					}else if(columnType.equals("java.lang.Short")){
						columnType = "java.lang.Integer";
					}else if(columnType.equals("[B")){
						columnType = "java.lang.String";
					}else if(columnType.equals("java.lang.Boolean")){
						columnType = "boolean";
					}
					boolean nullable = metaData.isNullable(i)==ResultSetMetaData.columnNullable?true:false;//是否可以为null
					boolean primary = primaryKeys.contains(columnName);
					
					String name = StringUtil.firstUpperCase(columnName);
					String lowerName = StringUtil.firstLowerCase(name);
					map.put("name", name);
					map.put("lowerName", lowerName);
					map.put("displayName", mapping.getString(columnName));
					map.put("type", columnType.replaceAll("java.lang.", ""));
					map.put("nullable", nullable);
					map.put("primary", primary);
					
					mapInfo.put(columnName, map);
				}
				rs.close();
				stmt.close();
			}
		});
		return mapInfo;
	}

}
