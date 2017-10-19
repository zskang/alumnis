package com.nethsoft.web.controller.open.dev;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 开发 - UI布局设计器
 * @author zengchao
 *
 */
@Controller
@RequestMapping("/open/dev/layout")
public class LayoutitController {
	private final String TPL = "open/dev/layoutit/";
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model){
		return TPL + "index";
	}
	/**
	 * 保存布局
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/download", method={RequestMethod.GET, RequestMethod.POST})
	public String saveLayout(HttpServletRequest request, Model model, String html){
		File file = new File(request.getServletContext().getRealPath("/WEB-INF/tpl/open/dev/layoutit/template/content.html"));
		try {
			String content = FileUtils.readFileToString(file, "UTF-8");
			html = content.replace("#code", html);
			html = html.replaceAll("#Pager", "#pager");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("html", html);
		return TPL + "template/download";
	}
	
}
