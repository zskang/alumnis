package com.nethsoft.web.controller.open;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nethsoft.web.controller.BaseController;


/**
 * 用于测试的controller
 * @author zengc
 *
 */
@Controller
@RequestMapping("/open/tools")
public class ToolsController extends BaseController<Object>{
	public final String TPL = "/open/tools/";
	
	@RequestMapping(value = "/{page}", method = { RequestMethod.GET })
	public String page(Model model, @PathVariable String page) {
		page = page.replace("-", "/"); //多级页面使用"-"隔开
		return TPL + page;
	}
}
