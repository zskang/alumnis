package com.nethsoft.web.controller.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.Md5Util;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.web.service.system.UserService;

@Controller
@RequestMapping("/")
public class SignController extends BaseController<User>{
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private LogService logService;
	@Autowired
	private UserService userService;
	/**
	 * 登录
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/signin", method={RequestMethod.GET, RequestMethod.POST})
	public String signin(Model model, @RequestParam(required=false)String return_url){
		if(ObjectUtil.isNotNull(return_url)&& return_url.indexOf("/sign")<0)
			model.addAttribute("return_url", return_url.replaceAll("&", "%"));
		
		//记录日志
		logger.debug(String.format("ip: %s, return_url: %s", getRequestAddr(), return_url));
		return "signin";
	}
	/**
	 * 登录 2
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/signin2", method={RequestMethod.GET, RequestMethod.POST})
	public String signin2(Model model, @RequestParam(required=false)String return_url){
		if(ObjectUtil.isNotNull(return_url)&& return_url.indexOf("/sign")<0)
			model.addAttribute("return_url", return_url.replaceAll("&", "%"));
		
		//记录日志
		logger.debug(String.format("ip: %s, return_url: %s", getRequestAddr(), return_url));
		return "signin2";
	}
	/**
	 * 登录操作
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/signin/do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject doSignin(HttpSession session, HttpServletRequest request, String username, String password, @RequestParam(required=false)String verifycode){
		username = username.trim();
		password = password.trim();
		if(ObjectUtil.isNotNull(verifycode))
			verifycode = verifycode.toLowerCase();
		if(ObjectUtil.isNull(username) || StringUtil.isBlank(username)){
			return WebResult.error("用户名或邮箱不能为空!");//用户名不能为空
		}
		if(ObjectUtil.isNull(password) || StringUtil.isBlank(password)){
			return WebResult.error("密码不能为空!");
		}
		if(password.length() != 32)
			password = Md5Util.encode(password);
		//根据用户名或密码查询
		if(userService.count(Restrictions.or(Restrictions.eq("userName", username),Restrictions.eq("email", username),Restrictions.eq("mobile", username)), Restrictions.eq("password", password))>0){
			//记录日志
			logger.debug(String.format("[UserName: %s] 登录成功", username));
			// 用户名密码正确, 初始化用户权限
			return userService.executeLogin(username, password, verifycode, session);
		}else{
			//记录日志
			logger.debug(String.format("[UserName: %s, Password: %s] 登录失败", username, password));
			return WebResult.error("用户名或密码错误！");
		}
	}
	
	/**
	 * 退出
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/signout", method={RequestMethod.GET, RequestMethod.POST})
	public String signout(HttpSession session, Model model){
		Object obj = session.getAttribute(ApplicationCommon.SESSION_ADMIN);
		if(ObjectUtil.isNotNull(obj)){
			User user = (User) obj;
			userService.executeHQL("update User set online=false where id='"+user.getId()+"'");
			session.removeAttribute(ApplicationCommon.SESSION_ADMIN);
			ApplicationCommon.LOGIN_SESSIONS.remove(user.getUserName());
			ApplicationCommon.LOGIN_USERS.remove(user.getUserName());
			logger.debug("[UserName:"+user.getUserName()+"] 退出系统");
			logService.info("退出系统", String.format("用户：[%s] 退出系统", user.getUserName()), user.getUserName(), getRequestAddr());
			session.invalidate();
		}
		return "redirect:signin";
	}
}
