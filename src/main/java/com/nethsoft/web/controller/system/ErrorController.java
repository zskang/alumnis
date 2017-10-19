package com.nethsoft.web.controller.system;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.service.system.LogService;

/**
 * 错误处理控制类
 * @author ZENGCHAO
 *
 */
@Controller
@RequestMapping("/error")
public class ErrorController extends BaseController<Object>{
	@Autowired
	private LogService logService;
	
	@RequestMapping("/e/{errorCode}")
	public String processingErrors(HttpServletRequest request, @PathVariable String errorCode){
		User user = getCurrentUser();
		String loginName = "system";
		if(ObjectUtil.isNotNull(user)){
			loginName = user.getUserName();
		}
		logService.error("系统异常","错误代码:"+errorCode+"，访问路径:"+request.getAttribute("javax.servlet.forward.servlet_path"),loginName , getRequestAddr());
		return processingErrors2(errorCode);
	}
	@RequestMapping("/{errorCode}")
	public String processingErrors2(@PathVariable String errorCode){
		return "_error/"+errorCode;
	}
}
