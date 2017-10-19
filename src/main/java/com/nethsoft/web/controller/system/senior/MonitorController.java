package com.nethsoft.web.controller.system.senior;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 数据库连接池监控
 * @author zengchao
 *
 */
@Controller
@RequestMapping("/system/senior/monitor")
public class MonitorController {
	private final String TPL = "/system/senior/monitor/";
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String methodName(Model model){
		return TPL + "index";
	}
	
}
