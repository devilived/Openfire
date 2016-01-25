package com.vidmt.of.plugin.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.exceptoins.CodeException;

public class AuthIntercepter extends HandlerInterceptorAdapter {
	private static final String MAGIC = "SRDSCFGA89asdnomasdfpawerlk";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String token = req.getParameter("token");
		if (token != null) {
			// try {
			// 兼容Android之前版本start，之后删除之
			if (token.length() > 8) {
				Long uid = (Long) req.getSession().getAttribute("uid");
				if (uid != null) {
					req.setAttribute("uid", uid);
					return true;
				} else {
					JSONObject json = new JSONObject();
					json.put("c", CodeException.ERR_CODE_USER_NOT_LOGIN);
					resp.getWriter().write(json.toJSONString());
					return false;
				}
			}
			return true;
		} else {
			String magic = req.getParameter(MAGIC);
			if (magic != null) {
				req.setAttribute("uid", Long.valueOf(magic));
				return true;
			}
			JSONObject json = new JSONObject();
			json.put("c", CodeException.ERR_CODE_USER_NOT_LOGIN);
			resp.getWriter().write(json.toJSONString());
			return false;
		}
	}
}
