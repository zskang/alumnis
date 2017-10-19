package com.nethsoft.web.controller.open.common;

import com.jrelax.app.fs.IRemoteFS;
import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/open/common/upload")
public class OpenUploadController extends BaseController<Object>{
	private final String UPLOAD_ROOT = ApplicationCoreConfigHelper.getProperty("upload.folder.root");


	@RequestMapping(value="/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doUpload(HttpServletRequest request,@RequestParam("file")CommonsMultipartFile[] uploadFiles,@RequestParam(required=false) String filename,@RequestParam(required=false) String filepath){
		if(uploadFiles.length > 0){
			List<String> pathList = new ArrayList<>();
			for (int i = 0; i < uploadFiles.length; i++) {
				CommonsMultipartFile file = uploadFiles[i];
				if(!file.isEmpty()){
					//1.获取文件后缀名
					String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
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
//					File dir = new File(uploadPath);
//					if(!dir.exists())
//						dir.mkdirs();
					//4.文件名
					String newFileName = "";
					if(StringUtil.isBlank(filename)){
						newFileName = "FN" + new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
						newFileName += Math.round(10+(Math.random()*9990));
						newFileName += ext;
					}else{
						newFileName = file.getOriginalFilename();
					}
					//5. 判断文件是否存在
					File f = new File(uploadPath+"/"+newFileName);
					if(f.exists()){
						return WebResult.error(newFileName + " 已存在！");
					}
					String path = "";
//					//6. 保存文件
//					try {
//						FileCopyUtils.copy(file.getBytes(), f);
					path = "/dl/"+uploadType+"/"+newFileName;
					path = path.replaceAll("//", "/");
//					} catch (IOException e) {
//						e.printStackTrace();
//					}

					//7. 保存到远程文件服务器
					Properties props_remote = ApplicationCoreConfigHelper.getPropertyGroup("upload.remote");
					boolean enabled = Boolean.parseBoolean(props_remote.getProperty("upload.remote.enabled", "false"));
					if(enabled){
						try {
							String rmi = props_remote.getProperty("upload.remote.rmi");
							IRemoteFS rfs = (IRemoteFS) Naming.lookup(rmi);

							path = rfs.upload(ApplicationCommon.APP, newFileName, file.getBytes());
						} catch (Exception e) {
							return WebResult.error("远程文件服务器访问失败！");
						}
					}

					pathList.add(path);
				}
			}
			return WebResult.success().element("path", StringUtil.toString(pathList));
		}else{
			return WebResult.error("请选择文件后再上传");
		}
	}

	/**
	 * 上传文件
	 * @param bytes  文件二进制数据
	 * @param suffix  文件后缀名
	 * @return  文件路径
	 */
	public static String upload(byte[] bytes,String suffix){
		//文件名
		String newFileName = "FN" + new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
		newFileName += Math.round(10 + (Math.random() * 9990));
		newFileName += "."+suffix;
		String path = "";
		//保存到远程文件服务器
		Properties props_remote = ApplicationCoreConfigHelper.getPropertyGroup("upload.remote");
		boolean enabled = Boolean.parseBoolean(props_remote.getProperty("upload.remote.enabled", "false"));
		if (enabled) {
			try {
				String rmi = props_remote.getProperty("upload.remote.rmi");
				IRemoteFS rfs = (IRemoteFS) Naming.lookup(rmi);
				path = rfs.upload(ApplicationCommon.APP, newFileName, bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return path;
	}
}
