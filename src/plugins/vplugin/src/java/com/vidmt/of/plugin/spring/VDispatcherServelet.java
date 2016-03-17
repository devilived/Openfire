package com.vidmt.of.plugin.spring;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import com.vidmt.of.plugin.VClassLoader;
import com.vidmt.of.plugin.VPlugin;

public class VDispatcherServelet extends DispatcherServlet {
	private static final Logger log = LoggerFactory.getLogger(VDispatcherServelet.class);
	private static final Logger statlog = LoggerFactory.getLogger(StatisticsIntercepter.class);
	private static String HOST;
	private static final long serialVersionUID = 1L;

	public static String getServer() {
		return HOST;
	}

	/**
	 * 比VPlugin初始化的早
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		// org.apache.log4j.LogManager.getRootLogger().setLevel(Level.DEBUG);
		long start = System.currentTimeMillis();
		ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(VClassLoader.get());
		File dir = new File(JiveGlobals.getHomeDirectory(), "plugins/" + VPlugin.NAME + "/web/WEB-INF/cache");
		System.setProperty("ehcache.disk.store.dir", dir.getAbsolutePath());
		this.setContextConfigLocation("classpath*:com/vidmt/of/plugin/spring/spring-*.xml");
		super.init(config);
		AuthCheckFilter.addExclude("vplugin/*.json");
		AuthCheckFilter.addExclude("vplugin/*.xml");
		AuthCheckFilter.addExclude("vplugin/*.api");
		Thread.currentThread().setContextClassLoader(oldCl);
		long time = System.currentTimeMillis() - start;
		log.info("==========================");
		log.info("====维艾德HTTP初始化完毕===");
		log.info("====用时{}秒==", time / 1000);
		log.info("==========================");
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (HOST == null) {
			String host = req.getServerName();
			if (!"localhost".equalsIgnoreCase(host) && !"127.0.0.1".equals(host)) {
				int port = req.getServerPort();
				HOST = port == 80 ? host : (host + ":" + port);
			}
			log.debug("server name[{}]/servletepath[{}]", req.getServerName(), req.getServletPath());
		}

		ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(VClassLoader.get());
		long start = System.currentTimeMillis();
		super.service(req, resp);
		long end = System.currentTimeMillis();
		long timesec = (end - start) / 1000;
		String url=req.getQueryString()!=null?req.getRequestURI():req.getRequestURI() + "?" + req.getQueryString();
		if (timesec > 5) {
			statlog.warn("执行时间超过5秒,为{}秒：{}", timesec, url);
		} else {
			statlog.debug("执行时间{}秒：{}", timesec, url);
		}
		Thread.currentThread().setContextClassLoader(oldcl);
	}

	@Override
	public void destroy() {
		AuthCheckFilter.removeExclude("vplugin/*.json");
		AuthCheckFilter.removeExclude("vplugin/*.xml");
		AuthCheckFilter.removeExclude("vplugin/*.api");
		super.destroy();
	}
}
