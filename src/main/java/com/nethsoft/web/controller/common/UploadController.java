package com.nethsoft.web.controller.common;

import com.jrelax.app.fs.IRemoteFS;
import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.support.Constant;
import net.coobird.thumbnailator.Thumbnails;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/common/upload")
public class UploadController extends BaseController<Object>{

	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(){
		return "common/upload/index";
	}

	/**
	 * 组件的通用上传方法
	 * @param request
	 * @param uploadFiles
	 * @param filename
	 * @param filepath
     * @return
     */
	@RequestMapping(value="/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doUpload(HttpServletRequest request,@RequestParam("file")CommonsMultipartFile[] uploadFiles,@RequestParam(required=false) String filename,@RequestParam(required=false) String filepath){
		if(uploadFiles.length > 0){
			List<String> pathList = new ArrayList<>();
			for (int i = 0; i < uploadFiles.length; i++) {
				CommonsMultipartFile file = uploadFiles[i];
				if(!file.isEmpty()){
					String path = null;
					try {
						//获取文件后缀名
						String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
						BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
						//确定是图片,当图片的宽度和高度都超过一定值时对图片进行压缩，以定值为基准压缩
						if(bufferedImage != null && (bufferedImage.getWidth()>Constant.WIDTH_NEW_VIEW
								|| bufferedImage.getHeight()>Constant.HEIGHT_NEWS_VIEW)){
								path = upload(bufferedImage,suffix,Constant.WIDTH_NEW_VIEW,Constant.HEIGHT_NEWS_VIEW);
						}else{//不是图片直接上传
							path = upload(file.getBytes(),suffix);
						}
					} catch (Exception e) {
						e.printStackTrace();
						return WebResult.error("文件上传失败");
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
	 * summernote文本编辑器的上传图片
	 * @param request
	 * @param file
     * @return
     */
	@RequestMapping(value="/summernote", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doUpload4Summernote(HttpServletRequest request, @RequestParam("file")CommonsMultipartFile file){
		if(file.isEmpty()){
			return WebResult.error("请选择文件后再上传");
		}else{
			String path = "";
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
			try {
				BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
				//确定是图片，当图片的宽度和高度都超过一定值时对图片进行压缩，以定值为基准压缩
				if(bufferedImage != null && (bufferedImage.getWidth()>Constant.WIDTH_NEWS_CONTENT
						|| bufferedImage.getHeight()>Constant.HEIGHT_NEWS_CONTENT)) {
						path = upload(bufferedImage,suffix,Constant.WIDTH_NEWS_CONTENT,Constant.HEIGHT_NEWS_CONTENT);
				}else{//直接上传
					path = upload(file.getBytes(),suffix);
				}
				return WebResult.success().element("path", path);
			} catch (Exception e) {
				e.printStackTrace();
				return WebResult.error("文件上传失败");
			}
		}
	}


	/**
	 * 上传文件
	 * @param bytes  文件二进制数据
	 * @param suffix  文件后缀名
	 * @return  文件路径
	 */
	public static String upload(byte[] bytes,String suffix) throws Exception {
		//文件名
		String newFileName = "FN" + new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
		newFileName += Math.round(10 + (Math.random() * 9990));
		newFileName += "."+suffix;
		String path = "";
		//保存到远程文件服务器
		Properties props_remote = ApplicationCoreConfigHelper.getPropertyGroup("upload.remote");
		boolean enabled = Boolean.parseBoolean(props_remote.getProperty("upload.remote.enabled", "false"));
		if (enabled) {
			String rmi = props_remote.getProperty("upload.remote.rmi");
			IRemoteFS rfs = (IRemoteFS) Naming.lookup(rmi);
			path = rfs.upload(ApplicationCommon.APP, newFileName, bytes);
		}
		return path;
	}

	/**
	 * 上传文件
	 * @param bufferedImage  图片对象
	 * @param suffix  后缀名
	 * @param width  图片压缩的固定宽度
	 * @param height  图片压缩的固定高度
	 * @return
     * @throws Exception
     */
	public static String upload(BufferedImage bufferedImage, String suffix, int width, int height) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bufferedImage = Thumbnails.of(bufferedImage).size(width,height).asBufferedImage();
		ImageIO.write(bufferedImage, suffix, out);
		return upload(out.toByteArray(),suffix);
	}
}
