package com.nethsoft.core.web.session;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.support.ApplicationContextHelper;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.web.service.system.UserService;
import org.springframework.stereotype.Service;

/**
 * session监听器
 * @author ZENGCHAO
 *
 */
@Service
public class SessionListener implements HttpSessionListener{
	
	public void sessionCreated(HttpSessionEvent se) {
		//session创建
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		//session失效
		User user = (User) se.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
		if(ObjectUtil.isNotNull(user)){
			ApplicationCommon.LOGIN_USERS.remove(user.getUserName());
			ApplicationCommon.LOGIN_SESSIONS.remove(user.getUserName());
			
			UserService userService = ApplicationContextHelper.getInstance().getBean(UserService.class);
			LogService logService = ApplicationContextHelper.getInstance().getBean(LogService.class);
			userService.executeHQL("update User set online=false where id='"+user.getId()+"'");
			logService.info("登录系统", "用户："+user.getUserName()+" 超时，自动退出系统！", "SYSTEM", "SYSTEM");
		}
	}

}
