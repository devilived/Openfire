package com.vidmt.of.plugin.sub.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;

import com.vidmt.of.plugin.utils.VUtil;

public class TestServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		AuthCheckFilter.addExclude("vplugin/test");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		VUtil.writeJson(resp, "{test:1}");
	}
}
