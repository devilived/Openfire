package com.vidmt.of.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.PluginClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 由于PluginClassLoader的URL是用jar:开头的，因此，在加载资源文件的时候出错，导致getResources()接口出错。
 * 此类对PluginClassLoader进行纠正，使其变成普通的file:类型的url
 * 同时，如果如果资源文件跟源文件放在一起，那么of会把资源文件放在java目录下， 而PluginClassLoader却不加载该目录，此处加上
 * 
 * @author xingqisheng
 */
public class VClassLoader extends URLClassLoader {
	private static final Logger log = LoggerFactory.getLogger(VClassLoader.class);
	private static VClassLoader instance;

	public static VClassLoader get() {
		if (instance == null) {
			PluginClassLoader pcl = VPlugin.getClassLoader();
			instance = new VClassLoader(pcl);
		}
		return instance;
	}

	private VClassLoader(PluginClassLoader plgcl) {
		// ClassLoader cl = Thread.currentThread().getContextClassLoader();
		super(new URL[] {}, plgcl);
		ClassLoader parentLoader = plgcl.getParent();
		boolean startWithBat = parentLoader.getClass().getName().endsWith("JiveClassLoader");
		if (startWithBat) {
			try {
				Enumeration<URL> urls = parentLoader.getResources("com/vidmt");
				if (urls.hasMoreElements()) {
					log.error("!!!!!!OPENFIRE编译错误，请清空后先编译OF，再编译插件!!!!!!");
					log.error("!!!!!!OPENFIRE编译错误，请清空后先编译OF，再编译插件!!!!!!");
					log.error("!!!!!!OPENFIRE编译错误，请清空后先编译OF，再编译插件!!!!!!");
					log.error("!!!!!!重要的事情说三遍!!!!!!");
					XMPPServer.getInstance().stop();
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		URL[] urls = plgcl.getURLs();
		for (int i = 0; i < urls.length; i++) {
			String url = urls[i].toString();
			try {
				if (url.startsWith("jar:")) {
					File file = new File(new URI(url.substring(4, url.length() - 2)));
					if (!file.exists()) {
						log.error("文件 不存在：" + file.getName());
					} else {
						this.addURL(file.toURI().toURL());
					}
				} else {
					// 在非clipse环境下，of打包不会包含xml配置文件，因此此处包含
					// 在eclipse环境下，work目录已经有资源文件了，而非class文件在classpath中只能有一份，因此此处做个区分
					if (startWithBat) {
						File file = new File(urls[i].toURI());
						if (file.getName().equals("web")) {
							File javaDir = new File(file.getParentFile(), "java");
							if (javaDir.exists()) {
								this.addURL(javaDir.toURI().toURL());
							}
						}
					}
					this.addURL(urls[i]);
				}
			} catch (MalformedURLException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	private static void validOpenfire() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader().getParent();
		if (loader.getClass().getName().endsWith("JiveClassLoader")) {
			try {
				Enumeration<URL> urls = loader.getResources("com/vidmt");
				if (urls.hasMoreElements()) {
					log.error("!!!!!!OPENFIRE编译错误，请清空后先编译OF，再编译插件!!!!!!");
					log.error("!!!!!!OPENFIRE编译错误，请清空后先编译OF，再编译插件!!!!!!");
					log.error("!!!!!!OPENFIRE编译错误，请清空后先编译OF，再编译插件!!!!!!");
					log.error("!!!!!!重要的事情说三遍!!!!!!");
					XMPPServer.getInstance().stop();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
