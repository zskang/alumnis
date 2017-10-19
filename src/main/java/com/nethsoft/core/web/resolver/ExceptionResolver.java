package com.nethsoft.core.web.resolver;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.HttpServletUtil;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.web.support.WebContextHelper;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.service.system.LogService;

public class ExceptionResolver implements HandlerExceptionResolver{
	private final Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private LogService logService;

	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object obj, Exception e) {
		Object user = WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
		String userName = "SYSTEM";
		if(ObjectUtil.isNotNull(user)){
			userName = ((User)user).getUserName();
		}
		try {
			int errorCode = 500;
			String errorMsg = "服务器繁忙，请稍后再试！";
			if(e instanceof ConversionNotSupportedException){//Web服务器内部异常
				errorMsg = "服务器内部异常！";
				errorCode = 406;
			}else if(e instanceof HttpMediaTypeNotAcceptableException){//无和请求Accept匹配的MIME类型
				errorMsg = "无和请求Accept匹配的MIME类型！";
				errorCode = 415;
			}else if(e instanceof HttpMediaTypeNotSupportedException){//不支持的MIME类型
				errorMsg = "不支持的MIME类型！";
				errorCode = 400;
			}else if(e instanceof HttpMessageNotReadableException){//Bad Request
				errorCode = 500;
			}else if(e instanceof HttpMessageNotWritableException){//406
				errorMsg = "消息转换异常！";
				errorCode = 405;
			}else if(e instanceof HttpRequestMethodNotSupportedException){//不支持的请求方法
				errorMsg = "不支持的请求方法:"+request.getMethod();
				errorCode = 400;
			}else if(e instanceof MissingServletRequestParameterException){//400
				errorMsg = "请求出错！";
				errorCode = 400;
			}else if(e instanceof NoSuchRequestHandlingMethodException){//找不到匹配资源
				errorMsg = "找不到请求的资源！";
				errorCode = 404;
			}else if(e instanceof TypeMismatchException){//400
				errorMsg = "类型转换错误";
				errorCode = 400;
			}else if(e instanceof SQLException){
				errorMsg = "数据库操作异常！";
				errorCode = 500;
			}
			logger.error(e);
			e.printStackTrace();
			logService.error("请求出错",e.toString() ,userName, request.getRemoteAddr());
			//判断是否是ajax请求，如果不是ajax请求则直接重定向
			if(HttpServletUtil.isAjaxWithRequest(request)){
				//错误
				response.setContentType("text/json");
				response.getWriter().print(WebResult.error(errorMsg));
				response.getWriter().close();
				return new ModelAndView("_error/500");
			}else if(ObjectUtil.isNull(request.getHeader("x-requested-with")))
			{
				response.sendRedirect(ApplicationCommon.BASEPATH+"/error/e/"+errorCode);
				return new ModelAndView("_error/"+errorCode);
			}
		} catch (Exception e2) {
			logger.error(e.getMessage());
			logService.error("请求错误",e.toString(), userName , request.getRemoteAddr());
		}
		return new ModelAndView("_error/500");
	}

}
