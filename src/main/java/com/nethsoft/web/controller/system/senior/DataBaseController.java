package com.nethsoft.web.controller.system.senior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.Table;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.datasource.DataSourceSwitcher;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.service.system.DataBaseService;

@Controller
@RequestMapping("/system/senior/database")
public class DataBaseController extends BaseController<Object>{
	private Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "system/senior/database/";
	
	@Autowired
	private DataBaseService dbService;
	@Autowired
	private DataSourceSwitcher dsSwitcher;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		List<String> dataSources = dsSwitcher.getAllDataSources();
		model.addAttribute("dataSources", dataSources);//所有数据源名称集合
		model.addAttribute("cds", dsSwitcher.getDataSourceName());//当前使用数据源名称
		logger.info("访问数据库管理");
		return TPL + "index";
	}
	
	/**
	 * 切换数据源
	 * @param ds
	 * @return
	 */
	@RequestMapping(value="/switch/{ds}")
	public String switchDS(@PathVariable String ds){
		dsSwitcher.setDataSource(ds);
		return "redirect:/system/senior/database";
	}
	/**
	 * 数据库表
	 * @return
	 */
	@RequestMapping(value="/table", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONArray table(){
		JSONArray data = new JSONArray();
		
		List<String> tableNames = new ArrayList<String>();
		SessionFactory sf = dbService.getBaseDao().getSessionFactory();
		Map<String, ClassMetadata> map = sf.getAllClassMetadata();
		for(Entry<String, ClassMetadata> entry : map.entrySet()){
			EntityPersister ep = (EntityPersister) entry.getValue();
			try {
				Class<?> clazz = Class.forName(ep.getEntityName());
				Table table = clazz.getAnnotation(Table.class);
				tableNames.add(table.name().toLowerCase());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		//排序
		Collections.sort(tableNames, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.toLowerCase().compareTo(o2.toLowerCase());
			}
		});
		
		for(String table : tableNames){
			JSONObject node = new JSONObject();
			
			node.element("id", table);
			node.element("text", table);
			
			data.add(node);
		}
		
		return data;
	}
	
	/**
	 * 执行sql语句
	 * @param model
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/query", method={RequestMethod.GET, RequestMethod.POST})
	public String query(Model model, String sql){
		if(StringUtil.isEmpty(sql)){
			model.addAttribute("error", "SQL语句错误！");
			return TPL + "query";
		}else if(sql.toLowerCase().startsWith("delete")){
			model.addAttribute("error", "只支持SELECT/UPDATE/INSERT开头的SQL语句!");
			return TPL + "query";
		}
		try {
			if(!sql.toLowerCase().startsWith("select")){
				int count = dbService.executeSQL(sql);
				model.addAttribute("update", true);
				model.addAttribute("count", count);
				return TPL + "query";
			}
			List<?> list = dbService.executeQuery(sql);
			
			List<String> cols = new ArrayList<String>();
			if(list.size()>0){
				Map<String,Object> map = (Map<String, Object>) list.get(0);
				Iterator<String> keys = map.keySet().iterator();
				while(keys.hasNext())
					cols.add(keys.next());
				
				Collections.sort(cols);//列排序
				if(cols.contains("name")){
					cols.remove("name");
					cols.add(0, "name");
				}
				if(cols.contains("id")){
					cols.remove("id");
					cols.add(0, "id");
				}
			}
			model.addAttribute("cols", cols);
			List<List<Object>> values = new ArrayList<List<Object>>();
			for(Object obj : list){
				Map<String,Object> map = (Map<String, Object>) obj;
				List<Object> value = new ArrayList<Object>();
				Iterator<String> keys = cols.iterator();
				while(keys.hasNext())
					value.add(map.get(keys.next()));
				
				values.add(value);
			}
			model.addAttribute("values", values);
		}catch(SQLGrammarException e){
			logger.error(e);
			model.addAttribute("error", "SQL语句错误："+e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			model.addAttribute("error", e.toString());
		}
		logger.info("执行SQL："+sql);
		return TPL + "query";
	}
}
