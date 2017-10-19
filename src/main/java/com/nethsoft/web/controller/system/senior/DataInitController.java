package com.nethsoft.web.controller.system.senior;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.service.system.LogService;

@Controller
@RequestMapping("/system/senior/init")
public class DataInitController extends BaseController<Object>{
	private Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "system/senior/init/";
	
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		logger.info("访问数据初始化");
		return TPL + "index";
	}
	
	/**
	 * 清理日志
	 * @return
	 */
	@RequestMapping(value="/clear/log", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject clearLog(){
		try{
			logService.executeHQL("delete from Log");
			logService.info("数据初始化", "清除日志", getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		}catch(Exception e){
			logger.error(e.toString());
			return WebResult.error(e);
		}
	}
	
	/**
	 * 清理历史通知
	 * @return
	 */
	@RequestMapping(value = "/clear/notify/history", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject clearHistoryNotify() {
		try {
			logService.executeHQL("delete from UserNotify where recread = true");
			logService.info("数据初始化", "清除历史通知", getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}
	
	/**
	 * 清理所有通知
	 * @return
	 */
	@RequestMapping(value = "/clear/notify/all", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject clearNotify() {
		try {
			logService.executeHQL("delete from UserNotify");
			logService.executeHQL("delete from Notify");
			logService.info("数据初始化", "清除历史通知", getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}
	
}
