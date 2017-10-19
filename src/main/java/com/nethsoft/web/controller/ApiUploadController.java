package com.nethsoft.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;

@Controller
@RequestMapping("/open/api/upload")
public class ApiUploadController extends BaseController<Object>{
	private final String UPLOAD_ROOT = ApplicationCoreConfigHelper.getProperty("upload.folder.root");
	
	@RequestMapping(value="/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doUpload(HttpServletRequest request,@RequestParam("file")MultipartFile uploadFiles,@RequestParam(required=false) String filename,@RequestParam(required=false) String filepath){

		if(!uploadFiles.isEmpty()){
			//1.获取文件后缀名
			String ext = uploadFiles.getOriginalFilename().substring(uploadFiles.getOriginalFilename().indexOf("."));
			//2.判断文件类型，选择存放位置
			//2.1如果用户填写了存放位置，那么以用户填写的存放位置为准
			String uploadType = StringUtil.isEmpty(filepath)?"file":filepath;
			if(StringUtil.isEmpty(filepath) && ".jpg".equals(ext) || ".jpeg".equals(ext) || ".gif".equals(ext) || ".png".equals(ext) || ".bmp".equals(ext))
				uploadType = "image";
			if(StringUtil.isEmpty(filepath))//如果未指定上传路劲，则在类型文件夹下新增日期文件夹
				uploadType += "/" +DateUtil.format(new Date(), "yyyy-MM-dd");
			String uploadPath = uploadType.startsWith("/")?uploadType:UPLOAD_ROOT+uploadType;
			uploadPath = request.getSession().getServletContext().getRealPath(uploadPath);
			//3.存放文件夹
			File dir = new File(uploadPath);
			if(!dir.exists())
				dir.mkdirs();
			//4.文件名
			String newFileName = "";
			if(StringUtil.isBlank(filename)){
				newFileName = "FN" + new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
				newFileName += Math.round(10+(Math.random()*9990));
				newFileName += ext;
			}else{
				newFileName = filename + ext;
			}
			//5. 判断文件是否存在
			File f = new File(uploadPath+"/"+newFileName);
			if(f.exists()){
				return WebResult.error(newFileName + " 已存在！");
			}
			//6. 保存文件
			try {
				FileCopyUtils.copy(uploadFiles.getBytes(), f);
				String path = "/dl/"+uploadType+"/"+newFileName;
				path = path.replaceAll("//", "/");
				return WebResult.success().element("path", path);
			} catch (IOException e) {
				e.printStackTrace();
				return WebResult.error(e.toString());
			}
		}else{
			return WebResult.error("请选择文件后再上传");
		}
	}
}
