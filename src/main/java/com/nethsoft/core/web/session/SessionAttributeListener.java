package com.nethsoft.core.web.session;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.web.entity.system.User;

/**
 * session属性编辑器
 * @author ZENGCHAO
 *
 */
public class SessionAttributeListener implements HttpSessionAttributeListener {

	public void attributeAdded(HttpSessionBindingEvent se) {
		if(ApplicationCommon.SESSION_ADMIN.equals(se.getName())){
			if(ObjectUtil.isNotNull(se.getValue())){
				User user = (User) se.getValue();
				ApplicationCommon.LOGIN_USERS.add(user.getUserName());
			}
			
		}
	}

	public void attributeRemoved(HttpSessionBindingEvent se) {
		if(ApplicationCommon.SESSION_ADMIN.equals(se.getName())){
			if(ObjectUtil.isNotNull(se.getValue())){
				User user = (User) se.getValue();
				ApplicationCommon.LOGIN_USERS.remove(user.getUserName());
			}
		}
	}

	public void attributeReplaced(HttpSessionBindingEvent se) {
		if(ApplicationCommon.SESSION_ADMIN.equals(se.getName())){
			if(ObjectUtil.isNotNull(se.getValue())){
				User user = (User) se.getValue();
				ApplicationCommon.LOGIN_USERS.remove(user.getUserName());
				ApplicationCommon.LOGIN_USERS.add(user.getUserName());
			}
		}
	}

}
