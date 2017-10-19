package com.nethsoft.web.support.filter;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.HttpServletUtil;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.system.User;

/**
 * 后台过滤器
 * 过滤未登录的用户
 * @author ZENGCHAO
 *
 */
public class AdminInterceptor implements HandlerInterceptor{
	/**
	 * 在业务处理器处理之前调用
	 * 如果返回false
	 *    则从当前的处理器往回执行afterCompletion(),再退出拦截连
	 * 如果返回true
	 *    执行下一个拦截器，知道所有拦截器你执行完毕
	 *    在执行业务处理器Controller
	 *    然后进入拦截器链
	 *    从最后一个拦截器往回执行所有的postHandle()
	 *    接着再从最后一个拦截器往回执行所有的afterCompletion()
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object object) throws Exception {
		String requestUrl = request.getRequestURI().replace(request.getRequestURI().substring(0, request.getRequestURI().indexOf(request.getContextPath())+request.getContextPath().length()), "");
		//公共组件无需验证
		if(requestUrl.startsWith("/open") || requestUrl.startsWith("/error"))
			return true;

		if(!requestUrl.startsWith("/signin")){
			if(ObjectUtil.isNotNull(request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN))){
				//判断是否存在于ApplicationCommon中
				User cUser = (User) request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
				if(!ApplicationCommon.LOGIN_USERS.contains(cUser.getUserName())){
					//在服务器热重启的情况下 ApplicationCommon中的数据丢失
					ApplicationCommon.LOGIN_USERS.add(cUser.getUserName());
					ApplicationCommon.LOGIN_SESSIONS.put(cUser.getUserName(), request.getSession());
				}
			}else{
				//判断是否是ajax请求，如果不是ajax请求则直接重定向
				if(ObjectUtil.isNull(request.getHeader("x-requested-with")))
				{
					//增加cookie
					String queryString = StringUtil.isEmpty(request.getQueryString())?"":"&"+request.getQueryString();
					/*Cookie cookie = new Cookie("RETURN_URL", request.getRequestURI()+queryString);
					cookie.setMaxAge(60*24);//设置cookie有效期为一天
					response.addCookie(cookie);*/
					response.sendRedirect(request.getContextPath()+"/signin?return_url="+request.getRequestURI()+queryString);
				}
				else if(HttpServletUtil.isAjaxWithRequest(request) && request.getMethod().equals("GET"))
				{
					response.setContentType("text/json");
					PrintWriter out = response.getWriter();
					out.println(WebResult.error("请求超时，请重新登录！"));
					out.close();
				}else if(HttpServletUtil.isAjaxWithRequest(request) && request.getMethod().equals("POST")){
					response.setContentType("text/json");
					PrintWriter out = response.getWriter();
					out.println(WebResult.error("请求超时，请重新登录！"));
					out.close();
				}else{
					String queryString = StringUtil.isEmpty(request.getQueryString())?"":"&"+request.getQueryString();
					response.sendRedirect(request.getContextPath()+"/signin?return_url="+request.getRequestURI()+queryString);
				}
				return false;
			}
		}else if(!requestUrl.startsWith("/signout")){//如果是登录页面，判断当前的session是否已经登录，如果已经登录则直接转向首页
			if(ObjectUtil.isNotNull(request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN))){
				response.sendRedirect(request.getContextPath()+"/index");
			}
		}
		return true;
	}
	/**
	 * 在业务处理器处理完成之后执行
	 * 在生成视图操作之前执行
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object object, ModelAndView mav) throws Exception {
	}
	/**
	 * 在DispatcherServlet完全处理完请求后调用
	 *    如果发生异常，则会从当前的拦截器往回执行所有的afterCompletion	
	 */
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object object, Exception exception)
			throws Exception {
		
	}
}
