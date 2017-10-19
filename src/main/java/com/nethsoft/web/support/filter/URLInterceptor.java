package com.nethsoft.web.support.filter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.HttpServletUtil;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.entity.system.Unit;
import com.nethsoft.web.entity.system.User;

/**
 * URL拦截器
 * 过滤用户访问的连接
 * 如果连接为非公开，并且当前用户没有访问该连接的权限，则转向权限错误页面
 * 如果连接为非公开，当前用户没有登录，则转向登录页面
 * @author ZENGCHAO
 *
 */
public class URLInterceptor implements HandlerInterceptor {
	private static List<String> execUrl = new ArrayList<String>();
	static{
		//访问控制例外的连接
		execUrl.add("/signin");//登录
		execUrl.add("/signout");//退出
		execUrl.add("/index");//系统首页
		execUrl.add("/error");//系统错误页面
		execUrl.add("/common");//系统公共组件，无需控制
		execUrl.add("/open");//对外公开
		execUrl.add("/notify");//通知中心
		execUrl.add("/uc");//个人中心
	}
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

		if(requestUrl.startsWith("/")){
			if(!ifInExecRes(execUrl,requestUrl)){
				//判断用户是否有访问这个连接的权限
				User currentUser = (User) request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
				//系统级别用户拥有所有权限
				for(Unit unit : currentUser.getUnits()){
					if(unit.isSystem())
						return true;
				}
				boolean allowAccess = false;//是否有访问权限
				boolean ifDisabled = false;//是否禁用
				String resName = "";
				for(Resource res : currentUser.getResources()){
					if(requestUrl.startsWith(res.getUrl()) && !StringUtil.isBlank(res.getUrl()) && !res.getUrl().equals("/")){
						allowAccess = true;
						if(!res.isEnabled()){
							ifDisabled = true;
							resName = res.getName();
						}
						break;
					}
				}
				if(!allowAccess){
					if(ObjectUtil.isNull(request.getHeader("x-requested-with")))
					{
						response.sendRedirect(request.getContextPath()+"/error/"+ControllerCommon.NO_PERMISSION);
					}
					else if(HttpServletUtil.isAjaxWithRequest(request))
					{
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.println("<script type=\"text/javascript\">");
						out.println("alert(\"无权访问!\");");
						out.println("</script>");
						out.close();
					}
					return false;
				}else if(ifDisabled){
					if(ObjectUtil.isNull(request.getHeader("x-requested-with")))
					{
						response.sendRedirect(request.getContextPath()+"/error/"+ControllerCommon.RES_DISABLED);
					}
					else if(HttpServletUtil.isAjaxWithRequest(request))
					{
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.println("<script type=\"text/javascript\">");
						out.println("alert(\"【"+resName+"】已停止使用!\");");
						out.println("</script>");
						out.close();
					//}
					return false;
					}
				}
			}
		}
		return true;
	}
	/**
	 * 判断请求的连接是否在访问控制连接的例外情况之内
	 * @param resUrls
	 * @param reqUrl
	 * @return
	 */
	public boolean ifInExecRes(List<String> resUrls,String reqUrl){
		boolean result = false;
		for(String url : resUrls){
			if(reqUrl.startsWith(url)){
				result = true;
				break;
			}
		}
		return result;
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
