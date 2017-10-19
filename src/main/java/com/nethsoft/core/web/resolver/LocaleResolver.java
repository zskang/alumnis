package com.nethsoft.core.web.resolver;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.ObjectUtil;

/**
 * 国际化支持类
 * @author zenghao
 *
 */
public class LocaleResolver extends AcceptHeaderLocaleResolver{
	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = (Locale)request.getAttribute(ApplicationCommon.LOCALE_REQUEST);
		if(ObjectUtil.isNull(locale))
			locale = (Locale)request.getSession().getAttribute(ApplicationCommon.LOCALE_SESSION);
		if(ObjectUtil.isNull(locale))
			return request.getLocale();
		else
			return locale;
	}
	
	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		request.setAttribute(ApplicationCommon.LOCALE_REQUEST, locale);//支持request
		request.getSession().setAttribute(ApplicationCommon.LOCALE_SESSION, locale);//支持session
	}
}
