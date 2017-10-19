package com.nethsoft.web.support;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.collections.map.LinkedMap;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;

public class EntityUtil {
	
	
	/**
	 * 将一个实体转化为Map<String,Object> 忽略Hibernate关联成员
	 * @param object
	 * @return
	 * @author kenyon
	 */
	public  static Map<String,Object> entityToMap(Object object) {
		if(ObjectUtil.isNull(object)) {
			return null;
		}
		Map<String,Object> map =new LinkedHashMap<String,Object>();
		Field[] fields=object.getClass().getDeclaredFields();
		Method[] methods=object.getClass().getMethods();
		Map<String,Method> methodMap=  new HashMap<String,Method>();
		if(ObjectUtil.isNotEmpty(methods)){
			for(Method method : methods) {
				methodMap.put(method.getName(), method);//获得所有类方法
			}
		}

		if(ObjectUtil.isNotEmpty(fields))  {
			for(Field field : fields) {
				if("handler".equals(field.getName())) {
					continue;
				}
				
				//所有关联字段忽略
				if(field.isAnnotationPresent(ManyToMany.class)||field.isAnnotationPresent(OneToMany.class)||field.isAnnotationPresent(OneToOne.class)||field.isAnnotationPresent(ManyToOne.class))
				{
					continue;
				}
		
				try {		
									//根据字段名称构建方法名
					String metodName=createMethodNameForGet(field.getName());
					Method method=methodMap.get(metodName);
					if(ObjectUtil.isNotNull(method))
					{
						Object value=method.invoke(object);
						map.put(field.getName(), value);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		
			}
		}
		return map;
	}
	
	
	private static String createMethodNameForGet(String arg)
	{
		if(StringUtil.isNotEmpty(arg))
		{
			return String.format("get%s", arg.replaceFirst(arg.substring(0, 1),arg.substring(0, 1).toUpperCase()));
		}
		return null;
	}
	
	
	
	
	/**
	 * 将实体的List<E>集合转化为Map<id,E>  以Id为key
	 * @param list
	 * @return
	 * @author kenyon
	 */
	public static <E> Map<String,E> entityListToMap(List<E> list) {
		Map<String,E> map = new LinkedHashMap<String,E>();
		if(ObjectUtil.isNotEmpty(list)) {
			
			for(E e:list) {
				try {
					Method method=e.getClass().getMethod("getId");
					if(ObjectUtil.isNotNull(method)) {
						map.put((String)method.invoke(e), e);
					}
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				} 
				
			}
		}
		return map;
	}
	
	
	
	/**
	 * 将实体的List<E>集合转化为Map<property,E>  以实体某个属性为key的map
	 * @param list
	 * @return
	 * @author kenyon
	 */
	public static <E> Map<Object,E> entityListToMap(List<E> list,String properTyName) {
		Map<Object,E> map = new LinkedHashMap<Object,E>();
		String[] properArray=properTyName.split("[.]");
		if(ObjectUtil.isNotEmpty(list)) {
			for(E e:list) {
				try {
					
					if(ObjectUtil.isNotEmpty(properArray)) {
						Object o=e;
						for(int i=0;i < properArray.length;i++) {
							String properTy=properArray[i];
							Method method=o.getClass().getMethod(createMethodNameForGet(properTy));
							if(ObjectUtil.isNotNull(method)) {
								o=method.invoke(o);
							}
						}
						map.put(o, e);
						
					}
					
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				} 
				
			}
		}
		return map;
	}
}
