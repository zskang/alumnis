package com.nethsoft.web.controller.common;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.common.NSFile;

/**
 * 文件浏览器
 * @author zengchao
 *
 */
@Controller
@RequestMapping("/common/filebrowser")
public class FileBrowserController extends BaseController<Object>{
	private final static String ROOTFOLDER = "/resources/upload/";//文件浏览器的根目录
	/**
	 * 上传目录首页
	 * @param model
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(HttpServletRequest request, Model model, @RequestParam(required=false)final String filter){
		try {
			List<NSFile> list = new ArrayList<NSFile>();
			File root = new File(request.getServletContext().getRealPath(ROOTFOLDER));
			if(root.exists()){
				File[] files = root.listFiles(new FileFilter() {
					
					public boolean accept(File file) {
						if(file.isDirectory())
							return true;
						if(StringUtil.isBlank(filter))
							return true;
						String[] filters = filter.split(",");
						for(String f : filters){
							f = f.replace("*", "");
							if(file.getName().toLowerCase().endsWith(f))
								return true;
						}
						return false;
					}
				});
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
			return "common/filebrowser/index";
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
	public JSONObject children(HttpServletRequest request, String path, @RequestParam(required=false)final String filter){
		try {
			if(!StringUtil.isBlank(path)){
				path = path+"/";
			}
			List<NSFile> list = new ArrayList<NSFile>();
			File root = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
			if(root.exists()){
				File[] files = root.listFiles(new FileFilter() {
					//文件筛选
					public boolean accept(File file) {
						if(file.isDirectory())
							return true;
						if(StringUtil.isBlank(filter))
							return true;
						String[] filters = filter.split(",");
						for(String f : filters){
							f = f.replace("*", "");
							if(file.getName().toLowerCase().endsWith(f))
								return true;
						}
						return false;
					}
				});
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
	 * 文件预览
	 * @param model
	 * @param path
	 * @return
	 */
	@RequestMapping(value="/preview", method={RequestMethod.GET, RequestMethod.POST})
	public String preview(HttpServletRequest request, Model model, String path){
		File file = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
		if(file.exists()){//判断文件是否存在
			if(file.isFile()){//判断是否是文件
				String name = file.getName().toLowerCase();
				if(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif") || name.endsWith(".png") || name.endsWith(".bmp"))
					model.addAttribute("obj", "<center><img src='"+ApplicationCommon.BASEPATH+"/dl/"+path+"'/></center>");
				else if(name.endsWith(".mp3"))
					model.addAttribute("obj", "<center><video controls autoplay name=\"media\"><source src=\""+ApplicationCommon.BASEPATH+"/dl/"+path+"\" type=\"audio/mpeg\"></video></center>");
				else
					model.addAttribute("obj", "<center>暂不支持此文件预览！</center>");
			}else{
				model.addAttribute("obj", "<center>文件夹不支持预览！</center>");
			}
		}else{
			model.addAttribute("obj", "<center>文件不存在！</center>");
		}
		return "common/filebrowser/preview";
	}
	
	private  String getIcon(File file){
		if(file.isDirectory())
			return "ti-folder";
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
		else if(name.endsWith(".js") || name.endsWith(".java"))
			return "fa fa-file-code-o";
		else if(name.endsWith(".wav") || name.endsWith(".mp3"))
			return "fa fa-file-sound-o";
		else if(name.endsWith(".text"))
			return "fa fa-file-text-o";
		
		return "ti-file";
	}
}
