package com.nethsoft.core.factory;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener{
	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext arg0) {
		return super.initWebApplicationContext(arg0);
	}
}
