package com.nethsoft.web.controller.open.dev;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.service.system.DataBaseService;

import de.hunsicker.jalopy.Jalopy;

@Controller
@RequestMapping("/open/dev/code")
public class CodeController extends BaseController<Object>{
	private final Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "/open/dev/code/";
	
	@Autowired
	private DataBaseService dbService;
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model, PageBean pageBean){	
		logger.debug("访问代码生成工具 - 首页");
		List<String> catalogs = dbService.getCatalogs();
		model.addAttribute("list", catalogs);
		
		return TPL + "step1";
	}
	
	@RequestMapping(value="/step/2", method={RequestMethod.GET, RequestMethod.POST})
	public String step2(Model model, String catalog){
		List<String> tableNames = dbService.getTables(catalog);
		model.addAttribute("list", tableNames);
		model.addAttribute("catalog", catalog);
		return TPL + "step2";
	}
	
	@RequestMapping(value="/step/3", method={RequestMethod.GET, RequestMethod.POST})
	public String step3(Model model, String catalog, String[] tables){
		model.addAttribute("tables", StringUtil.toString(tables));
		//按照选择的表，自动生成表前缀，和包名
		String prefix = "";
		String pack = "";
		for(String t : tables){
			if(prefix.length()==0){
				prefix = t.substring(0, t.indexOf("_")+1);
			}else{
				if(!t.startsWith(prefix))
					prefix = "";
			}
		}
		pack = prefix.replaceAll("_", "");
		//获取表字段
		Map<String, Map<String, String>> tableColumns = new LinkedHashMap<>();
		for(String table : tables){
			tableColumns.put(table, dbService.getTableColumnsName(catalog, table));
		}
		model.addAttribute("tableColumns", tableColumns);
		model.addAttribute("prefix", prefix);
		model.addAttribute("pack", pack);
		model.addAttribute("catalog", catalog);
		return TPL + "step3";
	}
	
	@RequestMapping(value="/step/4", method={RequestMethod.GET, RequestMethod.POST})
	public String step4(Model model,final String columnMapping, final String catalog, final String tables, final String tablePrefix, final String packagePrefix, final String packageName, final String savePath){
		final String p_controller = packagePrefix + ".controller." + packageName;
		final String p_entity = packagePrefix + ".entity." + packageName;
		final String p_service = packagePrefix + ".service." + packageName;
		final String tableFirstName = StringUtil.firstUpperCase(tablePrefix.replace("_", ""));
		final JSONArray mappings = JSONArray.fromObject(columnMapping);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//0. 初始化Velocity + 数据库表
				Properties prop = new Properties();
				String loadPath = ApplicationCommon.WEBAPPS_PATH+"/resources/application/code/template";
				prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, loadPath);
				VelocityEngine ve = new VelocityEngine(prop);
				ve.init();
				
				String[] tableArray = tables.split(",");
				for(String table : tableArray){
					/**************************初始化参数***************************/
					JSONObject mapping = new JSONObject();
					for(int i=0;i<mappings.size();i++){
						JSONObject obj = mappings.getJSONObject(i);
						if(table.equals(obj.getString("table"))){
							mapping = obj.getJSONObject("columns");
							break;
						}
					}
					VelocityContext context = new VelocityContext();
					Map<String, Map<String, Object>> columns = dbService.getTableColumnsFull(catalog, table, mapping);
					Map<String, String> forgetColumns = dbService.getTableForget(catalog, table);
					Map<String, List<String>> _forgetColumns = new HashMap<>();
					for(String key : forgetColumns.keySet()){
						List<String> infos = new ArrayList<String>();
						String tableName = forgetColumns.get(key);//表名
						String className = this.getClassName(tableName);//类名
						String vName = StringUtil.firstLowerCase(className.replace(tableFirstName, ""));//类变量名
						
						infos.add(className);
						infos.add(vName);
						infos.add(StringUtil.firstUpperCase(vName));
						
						_forgetColumns.put(key, infos);
					}
					context.put("basePath", "$!basePath");
					context.put("basepack", packagePrefix);//包根目录
					context.put("columns", columns);//列
					context.put("forgetColumns", _forgetColumns);//外键
					context.put("tableName", table);//表名
					String className = this.getClassName(table);
					String classNameLower = StringUtil.firstLowerCase(className);
					
					context.put("className", className);//类名
					context.put("classNameLower", classNameLower);//类名首字母小写
					
					/**************************生成类***************************/
					//1. 生成实体类
					context.put("pack", p_entity);
					context.put("entity_pack", p_entity);
					context.put("hasBigDecimal", false);
					for(String col : columns.keySet()){
						Map<String, Object> map = columns.get(col);
						if(map.get("type").equals("BigDecimal"))
							context.put("hasBigDecimal", true);
					}
					//获取外键
					Template template = ve.getTemplate("Entity.j", "utf-8");
					String dir = savePath +"/src/"+ p_entity.replace(".", "/")+"/";
					File fDir = new File(dir);
					if(!fDir.exists())
						fDir.mkdirs();
					String path = dir + className+".java";
					File entity = new File(path);
					mergeTemplate(context, template, entity, false);
					codeFormater(entity);
					
					//2. 生成Service类
					context.put("pack", p_service);
					context.put("service_pack", p_service);
					template = ve.getTemplate("Service.j", "utf-8");
					dir = savePath +"/src/"+ p_service.replace(".", "/")+"/";
					fDir = new File(dir);
					if(!fDir.exists())
						fDir.mkdirs();
					path = dir + className+"Service.java";
					File service = new File(path);
					mergeTemplate(context, template, service, false);
					codeFormater(service);
					
					//3. 生成Controller类
					context.put("pack", p_controller);
					context.put("requestMapping", "/"+table.replace("_", "/"));
					template = ve.getTemplate("Controller.j", "utf-8");
					dir = savePath +"/src/"+ p_controller.replace(".", "/")+"/";
					fDir = new File(dir);
					if(!fDir.exists())
						fDir.mkdirs();
					path = dir + className+"Controller.java";
					File controller = new File(path);
					mergeTemplate(context, template, controller, false);
					codeFormater(controller);
					
					/**************************生成页面***************************/
					String simpleClassNameLower = this.getSimpleClassName(table);//弃用
					simpleClassNameLower = table.replace(packageName+"_", "").replaceAll("_", "/");
					//4. 生成index页面
					context.put("action", "$!basePath/"+packageName+"/"+simpleClassNameLower);
					template = ve.getTemplate("page/index.html", "utf-8");
					dir = savePath +"/webapps/WEB-INF/tpl/"+packageName+"/"+simpleClassNameLower+"/";
					fDir = new File(dir);
					if(!fDir.exists())
						fDir.mkdirs();
					path = dir + "index.html";
					File index = new File(path);
					mergeTemplate(context, template, index, true);
					//5. 生成add页面
					template = ve.getTemplate("page/add.html", "utf-8");
					dir = savePath +"/webapps/WEB-INF/tpl/"+packageName+"/"+simpleClassNameLower+"/";
					fDir = new File(dir);
					if(!fDir.exists())
						fDir.mkdirs();
					path = dir + "add.html";
					File add = new File(path);
					mergeTemplate(context, template, add, true);
					//6. 生成edit页面
					template = ve.getTemplate("page/edit.html", "utf-8");
					dir = savePath +"/webapps/WEB-INF/tpl/"+packageName+"/"+simpleClassNameLower+"/";
					fDir = new File(dir);
					if(!fDir.exists())
						fDir.mkdirs();
					path = dir + "edit.html";
					File edit = new File(path);
					mergeTemplate(context, template, edit, true);
					//7. 生成detail页面
					template = ve.getTemplate("page/detail.html", "utf-8");
					dir = savePath +"/webapps/WEB-INF/tpl/"+packageName+"/"+simpleClassNameLower+"/";
					fDir = new File(dir);
					if(!fDir.exists())
						fDir.mkdirs();
					path = dir + "detail.html";
					File detail = new File(path);
					mergeTemplate(context, template, detail, true);
				}
			}
			/**
			 * 生成类名
			 * @param tableName
			 * @return
			 */
			public String getSimpleClassName(String tableName){
				String _name = tableName.replace(tablePrefix, "");
				String[] array = _name.split("_");
				String className = "";
				for(String n : array){
					className += StringUtil.firstUpperCase(n);
				}
				return className;
			}
			/**
			 * 生成类名
			 * @param tableName
			 * @return
			 */
			public String getClassName(String tableName){
				String _name = tableName.replace(tablePrefix, "");
				String[] array = _name.split("_");
				String className = "";
				for(String n : array){
					className += StringUtil.firstUpperCase(n);
				}
				className = tableFirstName + className;
				return className;
			}
			
			/**
			 * Java代码格式化
			 * @param entity
			 */
			private void codeFormater(File entity) {
				//代码格式化
				try {
					Jalopy ja = new Jalopy();
					ja.setInput(entity);
					ja.setOutput(entity);
					ja.setEncoding("UTF-8");
					ja.format();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			/**
			 * Velocity 生成类和页面
			 * @param context
			 * @param template
			 * @param add
			 */
			private void mergeTemplate(VelocityContext context, Template template, File add, boolean encoding) {
				try {
					FileOutputStream fos = new FileOutputStream(add);
					OutputStreamWriter osw;
					if(!encoding)
						osw= new OutputStreamWriter(fos);
					else
						osw= new OutputStreamWriter(fos, "utf-8");
					BufferedWriter bw = new BufferedWriter(osw);
					template.merge(context, bw);
					bw.close();
					osw.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		
		model.addAttribute("savePath", savePath);
		return TPL + "step4";
	}
	
	/**
	 * 打开本地目录
	 * @param path
	 * @return
	 */
	@RequestMapping(value="/open/folder", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject openFolder(String path){
		try{
			if(getRequestAddr().equals("localhost")){
				try {
					String[] cmd = new String[5];
					cmd[0] = "cmd";
					cmd[1] = "/c";
					cmd[2] = "start";
					cmd[3] = " ";
					cmd[4] = path;
					Runtime.getRuntime().exec(cmd);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return WebResult.success();
			}else{
				return WebResult.error("只能打开本地目录！");
			}
		}catch(Exception e){
			logger.error(e.toString());
			return WebResult.error(e);
		}
	}
}
