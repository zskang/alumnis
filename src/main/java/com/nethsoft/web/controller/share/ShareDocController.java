package com.nethsoft.web.controller.share;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.common.NSFile;
import com.nethsoft.web.service.system.LogService;

/**
 * 文档共享
 * @author zengchao
 *
 */
@Controller
@RequestMapping("/share/doc")
public class ShareDocController extends BaseController<Object>{
	private Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "share/doc/";
	@Autowired
	private LogService logService;
	
	private final String ROOTFOLDER = ApplicationCoreConfigHelper.getProperty("share.folder.root");//文档共享根目录
	private final String CONVERTFOLDER=ApplicationCoreConfigHelper.getProperty("share.folder.convert");//转换目录
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(HttpServletRequest request, Model model){
		String path = request.getServletContext().getRealPath(ROOTFOLDER);
		File dir = new File(path);
		if(!dir.exists()){
			dir.mkdirs();
		}
		try {
			List<NSFile> list = new ArrayList<NSFile>();
			File root = new File(request.getServletContext().getRealPath(ROOTFOLDER));
			if(root.exists()){
				File[] files = root.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if(CONVERTFOLDER.equals(name))
							return false;
						return true;
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
			model.addAttribute("root", ROOTFOLDER);
			logger.info("访问文档共享");
			return TPL +"index";
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
				path = path+"/";
			}
			List<NSFile> list = new ArrayList<NSFile>();
			File root = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
			if(root.exists()){
				File[] files = root.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if(CONVERTFOLDER.equals(name))
							return false;
						return true;
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
					if(f.isDirectory())
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
	 * 创建文件夹
	 * @param path
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/createFolder", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject createFolder(HttpServletRequest request, String path, String name){
		try {
			File root = new File(request.getServletContext().getRealPath(ROOTFOLDER+path+"/"+name));
			if(root.exists())
				return WebResult.error("此文件夹已存在！");
			if(!root.mkdirs())
				return WebResult.error("文件夹创建失败！");
			return WebResult.success();
		} catch (Exception e) {
			return WebResult.error(e);
		}
	}
	
	/**
	 * 远程获取文件
	 * @param path
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/get", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String path, String url){
		try {
			HttpClient client = new HttpClient();
			GetMethod get = null;
			
			try {
				get = new GetMethod(url);
				get.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				get.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");
				int i = client.executeMethod(get);
				if(i == 200){//如果存在
					String filename = url.substring(url.lastIndexOf("/"));
					filename = filename.replaceAll(",", "_").replaceAll("&", "_").replaceAll("/", "").replaceAll("=", "_");
					File storeFile = new File(request.getServletContext().getRealPath(ROOTFOLDER + path + "/" + filename));
					if(!storeFile.exists())
						storeFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(storeFile);
					fos.write(get.getResponseBody());
					fos.close();
					logService.error("共享中心", "文件下载成功["+path+"/"+filename+"]", "SYSTEM", "SYSTEM");
				}else{
					logService.error("共享中心", "文件下载失败["+url+"]", "SYSTEM", "SYSTEM");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logService.error("共享中心", "文件下载失败[url="+url+"]", "SYSTEM", "SYSTEM");
			}finally{
				get.releaseConnection();
				client.getHttpConnectionManager().closeIdleConnections(0);
			}
			
			
			//File root = new File(request.getServletContext().getRealPath(ROOTFOLDER+path+"/"+name));
			//if(root.exists())
			//	return WebResult.error("此文件夹已存在！");
			//if(!root.mkdirs())
			//	return WebResult.error("文件夹创建失败！");
			return WebResult.success();
		} catch (Exception e) {
			return WebResult.error(e);
		}
	}
	
	/**
	 * 文件重命名
	 * @param request
	 * @param path
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/rename", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject rename(HttpServletRequest request, String path, String name) {
		try {
			String dir = path.substring(0, path.lastIndexOf("/")+1);
			
			String newPath = dir+name;
			
			File source = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
			File target = new File(request.getServletContext().getRealPath(ROOTFOLDER+newPath));
			
			source.renameTo(target);
			
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.toString());
		}
	}
	
	/**
	 * 删除文件及文件夹
	 * @param request
	 * @param path
	 * @return
	 */
	@RequestMapping(value="/del", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject del(HttpServletRequest request, String path){
		try {
			File root = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
			if(root.exists()){
				if(FileUtils.deleteQuietly(root))
					return WebResult.success();
				if(root.isFile()){
					path = path.substring(0,path.lastIndexOf("/"))+"/"+CONVERTFOLDER+"/"+path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
					File file = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
					file.delete();
					return WebResult.success();
				}
			}
			return WebResult.error("删除失败！");
		} catch (Exception e) {
			return WebResult.error(e);
		}
	}
	
	/**
	 * 文件预览
	 * @param request
	 * @param path
	 * @return
	 */
	@RequestMapping(value="/preview", method={RequestMethod.GET, RequestMethod.POST})
	public String preview(HttpServletRequest request, Model model, String path){
		File file = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
		if(file.isFile()){
			String ext = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
			try {
				if(".txt".equals(ext)){
					model.addAttribute("brush", "html");
					model.addAttribute("content", StringUtil.htmlencode(FileUtils.readFileToString(file,"UTF-8")));
					return TPL + "preview-text";
				}else if(".htm".equals(ext) || ".html".equals(ext) || ".xhtml".equals(ext)){
					model.addAttribute("brush", "html");
					String content = FileUtils.readFileToString(file, "UTF-8");
					content = StringUtil.htmlencode(content);
					model.addAttribute("content", content);
					return TPL + "preview-text";
				}else if(".xml".equals(ext)){
					model.addAttribute("brush", "xml");
					String content = FileUtils.readFileToString(file, "UTF-8");
					content = StringUtil.htmlencode(content);//html特殊字符串替换
					model.addAttribute("content", content);
					return TPL + "preview-text";
				}else if(".java".equals(ext) || ".js".equals(ext) || ".cs".equals(ext) || ".css".equals(ext)){
					model.addAttribute("brush", ext.substring(1));
					model.addAttribute("content", FileUtils.readFileToString(file,"UTF-8"));
					return TPL + "preview-text";
				}else if(".doc".equals(ext) || ".docx".equals(ext) || ".xls".equals(ext) || ".xlsx".equals(ext) || ".ppt".equals(ext) || ".pptx".equals(ext)){
					//先转换成PDF
					File dir = new File(request.getServletContext().getRealPath("/resources/share/"+CONVERTFOLDER));
					if(!dir.exists())
						dir.mkdirs();
					File pdf = new File(dir.getPath()+"/"+file.getName().replace(ext, ".pdf"));
					try {
						File swf = new File(dir.getPath()+"/"+file.getName().replace(ext, ".swf"));
						if(!swf.exists()){
							if(!pdf.exists()){//不存在，则转换
								Properties prop = ApplicationCoreConfigHelper.getPropertyGroup("share.openoffice.");
								OpenOfficeConnection connection = new SocketOpenOfficeConnection(prop.getProperty("share.openoffice.host"), Integer.parseInt(prop.getProperty("share.openoffice.port")));
								DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
								converter.convert(file, pdf);
								connection.disconnect();
							}
							//存在不转换
							if(!swf.exists()){
								Runtime r = Runtime.getRuntime();
								File pdf2swf = new File(request.getServletContext().getRealPath("/resources/application/office/pdf2swf.exe"));
								Process p = r.exec(pdf2swf.getPath()+" "+ pdf.getPath() + " -o "+ swf.getPath() + " -T 9");  
		                        loadStream(p.getInputStream());
		                        loadStream(p.getErrorStream());
		                        
		                        pdf.delete();
							}
						}
						model.addAttribute("swf", CONVERTFOLDER+"/"+swf.getName());
					} catch(OpenOfficeException e){
						logger.error("OpenOffice转换失败，请检查配置，确保安装OpenOffice组件！");
						model.addAttribute("error", true);
					}catch (Exception e) {
						e.printStackTrace();
					}
					return TPL + "preview-swf";
				}else if(".swf".equals(ext)){
					model.addAttribute("swf", path);
					return TPL + "preview-swf";
				}else if(".pdf".equals(ext)){
					File dir = new File(request.getServletContext().getRealPath("/resources/share/"+CONVERTFOLDER));
					//存在不转换
					File swf = new File(dir.getPath()+"/"+file.getName().replace(ext, ".swf"));
					if(!swf.exists()){
						Runtime r = Runtime.getRuntime();
						File pdf2swf = new File(request.getServletContext().getRealPath("/resources/application/office/pdf2swf.exe"));
						Process p = r.exec(pdf2swf.getPath()+" "+ file.getPath() + " -o "+ swf.getPath() + " -T 9");  
                        loadStream(p.getInputStream());
                        loadStream(p.getErrorStream());
					}
					model.addAttribute("swf", CONVERTFOLDER+"/"+swf.getName());
					return TPL + "preview-swf";
				}else if(".jpg".equals(ext) || ".jpeg".equals(ext) || ".png".equals(ext) || ".gif".equals(ext) || ".bmp".equals(ext)){
					model.addAttribute("imgName", file.getName());
					model.addAttribute("imgPath", path);
					//获取到该目录下的此图片后的20个
					File[] images = file.getParentFile().listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							name = name.toLowerCase();
							if(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".bmp"))
								return true;
							return false;
						}
					});
					List<JSONObject> imageList = new ArrayList<JSONObject>();
					for(int i=0;i<images.length;i++){
						File f = images[i];
						if(f.getName().equals(file.getName()))
							continue;
						JSONObject json = new JSONObject();
						json.element("name", f.getName());
						String p = "";
						if(path.lastIndexOf("/")>0)
							p = path.substring(0,path.lastIndexOf("/"));
						json.element("path", p+"/"+f.getName());
						
						imageList.add(json);
					}
					model.addAttribute("imageList", imageList);
					return TPL + "preview-img";
				}else if(".mp4".equals(ext) || ".flv".equals(ext)){//视频
					model.addAttribute("video", path);
					return TPL + "preview-video";
				}else{
					model.addAttribute("path", path);
					return TPL + "preview-nosupport";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			return TPL + "preview-nosupport";
		}
		return TPL + "preview-text";
	}
	
	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @param path
	 * @throws IOException
	 */
	@RequestMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response, String path) throws IOException{
		File file = new File(request.getServletContext().getRealPath(ROOTFOLDER+path));
		if(file.isFile()){
			response.setHeader("Pragma", "No-cache");  
			response.setHeader("Cache-Control", "No-cache");  
			response.setDateHeader("Expires", 0);
			//支持中文名
			response.addHeader("Content-Disposition", "attachment;filename="+new String(file.getName().getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(FileUtils.readFileToByteArray(file));
            toClient.flush();
            toClient.close();
		}else{
			response.sendError(404, "File Not Found");
		}
	}
	
	private String loadStream(InputStream in) throws IOException {  
		  
        int ptr = 0;  
        in = new BufferedInputStream(in);  
        StringBuffer buffer = new StringBuffer();  
  
        while ((ptr = in.read()) != -1) {  
            buffer.append((char) ptr);  
        }  
  
        return buffer.toString();  
    }  
	private String getIcon(File file){
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
		else if(name.endsWith(".js") || name.endsWith(".java") || name.endsWith(".html"))
			return "fa fa-file-code-o";
		else if(name.endsWith(".wav") || name.endsWith(".mp3"))
			return "fa fa-file-sound-o";
		else if(name.endsWith(".text"))
			return "fa fa-file-text-o";
		
		return "ti-file";
	}
}
