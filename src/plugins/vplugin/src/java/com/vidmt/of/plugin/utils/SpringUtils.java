package com.vidmt.of.plugin.utils;

import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;

public class SpringUtils {
	public static ServletContext getServletContext(){
		return 	ContextLoader.getCurrentWebApplicationContext().getServletContext();
	}
}
