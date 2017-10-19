package com.nethsoft.web.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.orm.dao.BaseHibernateDao;
import com.nethsoft.orm.query.Condition;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.orm.query.QueryParams;
import com.nethsoft.orm.support.DBType;
import com.nethsoft.web.support.BaseObject;
@Service
public class BaseService<M> extends BaseObject{
	@Autowired
	protected BaseHibernateDao baseDao;
	private final Class<M> entityClass; 
	
	/**
	 * 切换数据源
	 * @param dataSourceName
	 */
	public void setDataSource(String dataSourceName){
		baseDao.setDataSource(dataSourceName);
	}
	/**
	 * 恢复为默认数据源
	 * 在同一个方法中，如果存在以下情况，则需要调用该方法恢复为默认数据源
	 * 1. 切换完数据库之后需要操作默认数据库时
	 */
	public void restoreDefaultDataSource(){
		baseDao.restoreDefaultDataSource();
	}
	/**
	 * 获取BaseDao
	 * @return
	 */
	public BaseHibernateDao getBaseDao(){
		return this.baseDao;
	}
	
	public DBType getDBType(){
		SessionFactoryImplementor sfi = ((SessionImpl) getBaseDao().getSession()).getFactory();
		String dbType = sfi.getDialect().getClass().getName().toLowerCase();
		if(dbType.indexOf("oracle")>=0)
			return DBType.Oracle;
		else if(dbType.indexOf("mysql")>=0)
			return DBType.Mysql;
		
		return DBType.Oracle;
	}
	/**
	 * 获取<M>的Class对象
	 */
	@SuppressWarnings("unchecked") 
	public BaseService(){
		Class<?> c = this.getClass();
		Type t = c.getGenericSuperclass();
		if(t instanceof ParameterizedType){
			Type[] p = ((ParameterizedType)t).getActualTypeArguments();
			this.entityClass = (Class<M>)p[0];
		}else{
			this.entityClass = null;
		}
	}
	/**
	 * 根据Id获取<M>
	 * @param id
	 * @return
	 */
	public M getById(Serializable id){
		return baseDao.get(entityClass, id);
	}
	/**
	 * 根据多个ID获取多个<M>
	 * @param ids
	 * @return
	 */
	public List<M> getByIds(Serializable[] ids){
		return baseDao.list(entityClass, Restrictions.in("id", ids));
	}
	/**
	 * 根据Name获取<M>
	 * @param name
	 * @return
	 */
	public M getByName(String name){
		return baseDao.getBy(entityClass, Restrictions.eq("name", name));
	}
	/**
	 * 根据属性名获取<M>
	 * @param propName
	 * @param propValue
	 * @return
	 */
	public M getByProperties(String propName,Object propValue){
		return baseDao.getBy(entityClass, Restrictions.eq(propName, propValue));
	}
	/**
	 * 根据多个的查询条件查询单个对象
	 * @param criterions
	 * @return
	 */
	public M get(Criterion...criterions){
		return baseDao.getBy(entityClass, criterions);
	}
	
	public M getByHql(String hql){
		return baseDao.getByHQL(entityClass, hql);
	}
	
	public M getByHql(String hql, Object...params){
		return baseDao.getByHQL(entityClass, hql, params);
	}
	/**
	 * 根据条件获取对象
	 * 如果有多条符合数据，则只返回首条数据
	 * @param <M>
	 * @param clazz
	 * @param criterions
	 * @return
	 */
	public M getBy(Condition condition){
		return baseDao.getBy(entityClass, condition);
	}
	/**
	 * 获取单个字段的值
	 * @param field
	 * @return
	 */
	public Object getField(String field){
		Object value = null;
		return value;
	}
	/**
	 * 分页获取<M>
	 * @param pageBean
	 * @return
	 */
	public List<M> listByPage(PageBean pageBean){
		setTotalCount(pageBean);
		return baseDao.list(entityClass, pageBean);
	}
	/**
	 * 获取全部数据
	 * @return
	 */
	public List<M> list(){
		return baseDao.list(entityClass);
	}
	
	/**
	 * 根据封装类查询
	 * @param queryParams
	 * @return
	 */
	public List<M> list(QueryParams queryParams){
		return baseDao.list(entityClass, queryParams);
	}
	/**
	 * 根据查询条件获取集合
	 * @param criterions
	 * @return
	 */
	public List<M> list(Criterion...criterions){
		return baseDao.list(entityClass, criterions);
	}
	/**
	 * 按照排序规则获取集合
	 * @param 排序条件
	 * @return
	 */
	public List<M> list(Order...orders){
		return baseDao.list(entityClass,orders);
	}
	/**
	 * 获取集合
	 * @param criterion
	 * @param order
	 * @return
	 */
	public List<M> list(Criterion criterion,Order order){
		return baseDao.list(entityClass, criterion, order);
	}
	/**
	 * 
	 * @param criterions
	 * @param orders
	 * @return
	 */
	public List<M> list(Criterion[] criterions,Order...orders){
		return baseDao.list(entityClass, criterions, orders);
	}
	/**
	 * 根据HQL查询
	 * @param hql
	 * @return
	 */
	public List<M> list(String hql){
		return baseDao.list(entityClass,hql);
	}
	
	/**
	 * 分页查询
	 * @param hql
	 * @return
	 */
	public List<M> list(PageBean pageBean){
		setTotalCount(pageBean);
		return baseDao.list(entityClass, pageBean);
	}
	/**
	 * 分页查询字段
	 * @param hql
	 * @return
	 */
	public List<M> list(PageBean pageBean, String[] colums){
		setTotalCount(pageBean);
		return baseDao.listPartColumns(entityClass, pageBean, colums);
	}
	
	/**
	 * 自定实体查询
	 * @param hql
	 * @return
	 */
	public List<M> listForEntity(String hql) {
		return this.baseDao.listForEntity(this.entityClass, hql);
	}
	/**
	 * 自定实体查询
	 * @param hql
	 * @return
	 */
	public List<M> listForEntity(String hql, Object...params) {
		return this.baseDao.listForEntity(this.entityClass, hql, params);
	}
	/**
	 * 自定实体查询
	 * @param hql
	 * @return
	 */
	public List<M> listByPageForEntity(String hql, PageBean pageBean) {
		setTotalCount(pageBean, hql);
		return this.baseDao.listForEntity(this.entityClass, hql, pageBean);
	}
	
	/**
	 * 获取集合
	 * @param clazz
	 * @param 需要查询字段
	 * @return
	 */
	public List<M> list(Condition conditon){
		return baseDao.list(entityClass, conditon);
	}
	
	/**
	 * 获取集合 分页
	 * @param clazz
	 * @param 需要查询字段
	 * @return
	 */
	public List<M> list(Condition condition, PageBean pageBean){
		setTotalCount(pageBean, condition);
		return baseDao.list(entityClass, condition, pageBean);
	}
	
	/**
	 * 分组查询
	 * @param projs
	 * @return
	 */
	public List<M> group(Projection...projs){
		QueryParams queryParams = new QueryParams();
		for(Projection proj : projs)
			queryParams.addProjection(proj);
		
		return this.baseDao.list(entityClass, queryParams);
	}
	/**
	 *保存<M>
	 * @param m
	 */
	public void save(M m){
		baseDao.save(m);
	}
	
	/**
	 * 批量保存
	 * @param mList
	 */
	public void save(Collection<M> mList){
		baseDao.save(mList);
	}
	
	/**
	 * 保存或更新
	 * @param m
	 */
	public void saveOrUpdate(M m){
		baseDao.saveOrUpdate(m);
	}
	/**
	 * 更新<M>
	 * @param m
	 */
	public void update(M m){
		baseDao.update(m);
	}
	/**
	 * 更新<M>
	 * @param m
	 */
	public void update(Collection<M> mList){
		baseDao.update(mList);
	}
	/**
	 * 更新<M>
	 * @param m
	 */
	public void merge(M m){
		baseDao.merge(m);
	}
	/**
	 * 更新<M>
	 * @param m
	 */
	public void merge(Collection<M> mList){
		baseDao.merge(mList);
	}
	/**
	 * 根据ID删除<M>
	 * @param id
	 */
	public void delete(Serializable id){
		baseDao.delete(entityClass, id);
	}
	/**
	 * 刷新
	 * @param m
	 */
	public void refresh(M m){
		baseDao.refresh(m);
	}
	/**
	 * 根据多个id删除
	 * @param ids
	 */
	public void delete(Serializable[] ids){
		StringBuffer strIds = new StringBuffer();
		for(Serializable id : ids)
			strIds.append(","+id);
		String hql = "delete "+entityClass.getSimpleName()+" where id in("+strIds.substring(1)+")";
		baseDao.execteBulk(hql,null);
	}
	/**
	 * 删除<M>
	 * @param m
	 */
	public void delete(M m){
		baseDao.delete(m);
	}
	/**
	 * 根据主键删除数据
	 * @param <M>
	 * @param 要删除的对象的Class
	 * @param id 主键值
	 */
	public void deletes(Serializable...ids){
		baseDao.deletes(entityClass, ids);
	}
	/**
	 * 根据主键删除数据
	 * @param <M>
	 * @param 要删除的对象的Class
	 * @param id 主键值
	 */
	public void deletes(Collection<Serializable> ids){
		baseDao.deletes(entityClass, ids);
	}
	/**
	 * 获取所有数量
	 */
	public int count(){
		return baseDao.count(entityClass);
	}
	/**
	 * 获取指定条件的集合数量
	 * @param criterions
	 */
	public int count(Criterion...criterions){
		return baseDao.count(entityClass, criterions);
	}
	/**
	 * 执行HQL语句
	 * @param hql
	 * @return
	 */
	public int executeHQL(String hql){
		return baseDao.getQuery(hql).executeUpdate();
	}
	
	/**
	 * 执行HQL语句
	 * @param hql
	 * @param 参数列表
	 * @return
	 */
	public int executeHQL(String hql, Object...params){
		Query query = baseDao.getQuery(hql);
		if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if(params[i] instanceof Date) {
                    query.setTimestamp(i, (Date)params[i]);
                } else {
                    query.setParameter(i, params[i]);
                }
            }
        }
		return query.executeUpdate();
	}
	/**
	 * 执行SQL语句
	 * @param sql
	 * @return
	 */
	public int executeSQL(String sql){
		return baseDao.getSQLQuery(sql).executeUpdate();
	}
	
	/**
	 * 执行SQL语句
	 * @param sql
	 * @param 参数列表
	 * @return
	 */
	public int executeSQL(String sql, Object...params){
		SQLQuery query = baseDao.getSQLQuery(sql);
		if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if(params[i] instanceof Date) {
                    query.setTimestamp(i, (Date)params[i]);
                } else {
                    query.setParameter(i, params[i]);
                }
            }
        }
		return query.executeUpdate();
	}
	/**
	 * 使用HQL语句查询单个值
	 * @param hql
	 * @return
	 */
	public Object queryHQL(String hql){
		return baseDao.getQuery(hql).uniqueResult();
	}
	/**
	 * 使用SQL查询单个String值
	 * @param sql
	 * @return
	 */
	public String queryForString(String sql){
		return baseDao.queryForString(sql);
	}

	public Object queryForObject(String sql){
		return baseDao.queryForObject(sql);
	}

	/**
	 * 使用SQL查询单个int值
	 * @param sql
	 * @return
	 */
	public int queryForInt(String sql){
		return baseDao.queryForInt(sql);
	}
	/**
	 * 使用sql查询单个Long值
	 * @param sql
	 * @return
	 */
	public long queryForLong(String sql){
		return baseDao.queryForLong(sql);
	}
	/**
	 * 使用SQL查询多列值
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> queryForList(String sql){
		return baseDao.queryForList(sql);
	}
	
	public Blob getBlob(Object obj){
		
		try {
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();  
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(obj);  
		    ByteArrayInputStream bis=new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		    Blob blob = Hibernate.getLobCreator(baseDao.getSession()).createBlob(bis, (long)bis.available());
		    bis.close();
		    
		    return blob;
		} catch (IOException e) {
			e.printStackTrace();
		}  
       
		return null;
	}
	/**
	 * 使用SQL语句查询
	 * @param sql
	 * @return
	 */
	public List<?> listByNativeSqlForList(String sql){
		return baseDao.listByNativeSqlForList(sql);
	}
	
	/**
	 * 获取总数
	 * @param pageBean
	 */
	protected void setTotalCount(PageBean pageBean) {
		if(ObjectUtil.isNotNull(pageBean.getCriterions()) && ObjectUtil.isNotEmpty(pageBean.getCriterions()))
			pageBean.setTotalCount(baseDao.count(entityClass,pageBean.getCriterions().toArray(new Criterion[]{})));
		else
			pageBean.setTotalCount(baseDao.count(entityClass));
	}
	
	/**
	 * 获取总数
	 * @param pageBean
	 */
	protected void setTotalCount(PageBean pageBean, String hql) {
		hql = "select count(*) " + hql.substring(hql.indexOf("from"));
		pageBean.setTotalCount(baseDao.count(entityClass, hql, pageBean.getParams()));
	}
	/**
	 * 获取总数
	 * @param pageBean
	 * @param condition
	 */
	private void setTotalCount(PageBean pageBean, Condition condition) {
		List<Criterion> criterions = new ArrayList<>();
		if(ObjectUtil.isNotNull(pageBean.getCriterions()) && ObjectUtil.isNotEmpty(pageBean.getCriterions()))
			criterions.addAll(pageBean.getCriterions());
		if(ObjectUtil.isNotNull(condition.getCriterions()) && ObjectUtil.isNotEmpty(condition.getCriterions()))
			criterions.addAll(condition.getCriterions());
		
		if(ObjectUtil.isNotEmpty(criterions))
			pageBean.setTotalCount(baseDao.count(entityClass,pageBean.getCriterions().toArray(new Criterion[]{})));
		else
			pageBean.setTotalCount(baseDao.count(entityClass));
	}
}