package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.web.support.WebResult;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.Properties;

/**
 * 版本
 */
@Controller
@RequestMapping(value = "/open/alumni/version")
public class OpenAlumniVersionController {

	/**
	 * 获取版本
	 * @param type  客户端类型，android/iOS
	 * @return
	 */
	@RequestMapping()
	public @ResponseBody JSONObject index(String type) {
		String[] arr = {"iOS","android"};
		if(!ArrayUtils.contains(arr,type)){
			return WebResult.error("客户端类型错误");
		}
		Properties prop = ApplicationCoreConfigHelper.getProperties();
		String version = "";
		String url = "";
		if("android".equals(type)){
			url =  prop.getProperty("app.android.url");
			version = prop.getProperty("app.android.version");
		}else{
			url =  prop.getProperty("app.ios.url");
			version = prop.getProperty("app.ios.version");
		}
		return WebResult.success().element("version",version).element("url",url);
	}

	private final String TPL = "/alumni/version/";

	/**
	 * 获取IOS版本
	 * @param model
	 * @return
	 */
	@RequestMapping("/ios")
	public String ios(Model model) {
		String version = ApplicationCoreConfigHelper.getProperties().getProperty("app.ios.version");
		model.addAttribute("version", version);
		return TPL + "ios";
	}

	/**
	 * 更新IOS版本
	 * @param version
	 * @return
	 */
	@RequestMapping("/update/ios")
	public @ResponseBody JSONObject updateIos(@RequestParam("version") String version) {
		Properties prop = new Properties();
		try{
			File file = ApplicationCoreConfigHelper.getFile();
			InputStream is = new FileInputStream(file);
			prop.load(is);
			is.close();
			OutputStream out = new FileOutputStream(file);
			prop.setProperty("app.ios.version",version);
			prop.store(out,"");
			return WebResult.success();
		}catch (Exception e){
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 获取安卓版本
	 * @param model
	 * @return
	 */
	@RequestMapping("/android")
	public String android(Model model) {
		String version = ApplicationCoreConfigHelper.getProperties().getProperty("app.android.version");
		model.addAttribute("version", version);
		return TPL + "android";
	}

	/**
	 * 更新安卓版本
	 * @param version
	 * @return
	 */
	@RequestMapping("/update/android")
	public @ResponseBody JSONObject updateAndroid(@RequestParam("version") String version) {
		Properties prop = new Properties();
		try{
			File file = ApplicationCoreConfigHelper.getFile();
			InputStream is = new FileInputStream(file);
			prop.load(is);
			is.close();
			OutputStream out = new FileOutputStream(file);
			prop.setProperty("app.android.version",version);
			prop.store(out,"");
			return WebResult.success();
		}catch (Exception e){
			return WebResult.error(e.getMessage());
		}
	}

}
