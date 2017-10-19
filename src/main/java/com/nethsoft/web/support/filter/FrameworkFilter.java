package com.nethsoft.web.support.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.nethsoft.core.util.ObjectUtil;

public class FrameworkFilter implements Filter{
	
	private Logger logger = Logger.getLogger(this.getClass());

	public void destroy() {
		
	}
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		/*String requestUrl = request.getRequestURI().replace(request.getRequestURI().substring(0, request.getRequestURI().indexOf(request.getContextPath())+request.getContextPath().length()), "");
		if(requestUrl.replace("/", "").equals("admin")){
			request.getRequestDispatcher("/admin/index").forward(arg0, arg1);
		}else{
			chain.doFilter(arg0, arg1);
		}*/
		String qs = request.getQueryString();
		if(ObjectUtil.isNotNull(qs)){
			qs = "?" + qs;
		}else{
			qs = "";
		}
		logger.debug(request.getRequestURI()+qs+"\t"+request.getRemoteAddr());
		chain.doFilter(arg0, arg1);
	}

	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
