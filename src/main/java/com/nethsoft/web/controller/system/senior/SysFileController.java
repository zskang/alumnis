package com.nethsoft.web.controller.system.senior;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.common.NSFile;

@Controller
@RequestMapping("/system/senior/sysfile")
public class SysFileController extends BaseController<Object>{
	private final String ROOTFOLDER = ApplicationCoreConfigHelper.getProperty("system.file.folder.root");//文件浏览器的根目录
	private Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "system/senior/sysfile/";
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(HttpServletRequest request, Model model){
		try {
			List<NSFile> list = new ArrayList<NSFile>();
			File root = new File(request.getServletContext().getRealPath(ROOTFOLDER));
			if(root.exists()){
				File[] files = root.listFiles();
				for(File f : files){
					if(!f.isDirectory())
						continue;
					NSFile uf = new NSFile();
					uf.setName(f.getName());
					uf.setPath(f.getName());
					uf.setType(f.isFile()?1:2);
					uf.setIcon(getIcon(f));
					if(f.isFile())
						uf.setSize(f.length());
					uf.setModifyTime(DateUtil.format(new Date(f.lastModified()), "yyyy-MM-dd HH:mm:ss"));
					
					list.add(uf);
				}
				for(File f : files){
					if(!f.isFile())
						continue;
					NSFile uf = new NSFile();
					uf.setName(f.getName());
					uf.setPath(f.getName());
					uf.setType(f.isFile()?1:2);
					uf.setIcon(getIcon(f));
					if(f.isFile())
						uf.setSize(f.length());
					uf.setModifyTime(DateUtil.format(new Date(f.lastModified()), "yyyy-MM-dd HH:mm:ss"));
					
					list.add(uf);
				}
			}
			model.addAttribute("list", list);
			logger.info("访问系统文件管理");
			return TPL + "index";
		} catch (Exception e) {
			return ControllerCommon.ERROR;
		}
	}
	/**
	 * 子目录
	 * @param model
	 * @param path
	 * @return
	 */
	@RequestMapping(value="/folder", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject children(HttpServletRequest request, String path){
		try {
			if(!StringUtil.isBlank(path)){
				path = path + "/";
			}
			List<NSFile> list = new ArrayList<NSFile>();
			File root = new File(request.getServletContext().getRealPath(ROOTFOLDER + path));
			if(root.exists()){
				File[] files = root.listFiles();
				//先显示文件夹
				for(File f : files){
					if(!f.isDirectory())
						continue;
					NSFile uf = new NSFile();
					uf.setName(f.getName());
					uf.setPath(path+f.getName());
					uf.setType(f.isFile()?1:2);
					uf.setIcon(getIcon(f));
					if(f.isFile())
						uf.setSize(f.length());
					uf.setModifyTime(DateUtil.format(new Date(f.lastModified()), "yyyy-MM-dd HH:mm:ss"));
					
					list.add(uf);
				}
				for(File f : files){
					if(!f.isFile())
						continue;
					NSFile uf = new NSFile();
					uf.setName(f.getName());
					uf.setPath(path+f.getName());
					uf.setType(f.isFile()?1:2);
					uf.setIcon(getIcon(f));
					if(f.isFile())
						uf.setSize(f.length());
					uf.setModifyTime(DateUtil.format(new Date(f.lastModified()), "yyyy-MM-dd HH:mm:ss"));
					
					list.add(uf);
				}
			}
			return WebResult.success().element("data", JSONArray.fromObject(list).toString());
		} catch (Exception e) {
			return WebResult.error(e);
		}
	}
	
	/**
	 * 编辑文件
	 * @param model
	 * @param 文件路径
	 * @return
	 */
	@RequestMapping(value="/edit", method={RequestMethod.GET, RequestMethod.POST})
	public String edit(HttpServletRequest request, Model model, String path){
		path = ROOTFOLDER + path;
		File file = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
		if(file.exists()){
			try {
				String content = FileUtils.readFileToString(file, "utf-8");
				content = content.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				model.addAttribute("content", content);
				model.addAttribute("path", path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return TPL + "edit";
	}
	/**
	 * 保存文件
	 * @param path
	 * @param content
	 * @return
	 */
	@RequestMapping(value="/save", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject save(HttpServletRequest request, String path, String content){
		try {
			content = content.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			//先备份文件
			path = request.getServletContext().getRealPath(path);
			File file = new File(path);
			if(file.exists()){
				File descFile =  new File(path+".bak");
				FileUtils.copyFile(file, descFile);
				//更新文件
				FileUtils.writeStringToFile(file, content, "utf-8");
			}else{
				return WebResult.error("文件不存在！");
			}
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	private String getIcon(File file){
		if(file.isDirectory())
			return "fa fa-folder-open";
		String name = file.getName();
		if(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif") || name.endsWith(".png") || name.endsWith(".bmp"))
			return "fa fa-file-image-o";
		else if(name.endsWith(".doc") || name.endsWith(".docx"))
			return "fa fa-file-word-o";
		else if(name.endsWith(".xls") || name.endsWith(".xlsx"))
			return "fa fa-file-excel-o";
		else if(name.endsWith(".ppt") || name.endsWith(".pptx"))
			return "fa fa-file-powerpoint-o";
		else if(name.endsWith(".pdf"))
			return "fa fa-file-pdf-o";
		else if(name.endsWith(".rar") || name.endsWith(".zip"))
			return "fa fa-file-zip-o";
		else if(name.endsWith(".js") || name.endsWith(".java") || name.endsWith(".html"))
			return "fa fa-file-code-o";
		else if(name.endsWith(".wav") || name.endsWith(".mp3"))
			return "fa fa-file-sound-o";
		else if(name.endsWith(".text"))
			return "fa fa-file-text-o";
		
		return "fa fa-file";
	}
}
