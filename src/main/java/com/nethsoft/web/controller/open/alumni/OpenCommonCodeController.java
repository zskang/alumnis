package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.service.campus.CommonCodeService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用字典
 */
@RestController
@RequestMapping(value = "/open/alumni/commoncode/")
public class OpenCommonCodeController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private CommonCodeService commonCodeService;

	/**
	 * 获取年份范围数据
	 * @return
     */
	@RequestMapping(value="yearrange")
	public JSONObject getYearRange(){
		try{
			JSONObject jsonObject = WebResult.success();
			jsonObject.accumulateAll(commonCodeService.getYearRangeOpen());
			return jsonObject;
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 获取专业数据
	 * @return
     */
	@RequestMapping(value="major")
	public JSONObject getMajor(){
		try{
			return WebResult.success().element("industry",commonCodeService.majorTree());
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 获取行业数据
	 * @return
     */
	@RequestMapping(value="industry")
	public JSONObject getIndustry(){
		try{
			return WebResult.success().element("industry",commonCodeService.industryTree());
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 辖区数据
	 * @return
     */
	@RequestMapping(value="region4IOS")
	public JSONObject getRegion(){
		try{
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[]{"xh"});
			JSONArray array = JSONArray.fromObject(commonCodeService.regionTree4IOS(),jsonConfig);
			return WebResult.success().element("root",array);
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

}
