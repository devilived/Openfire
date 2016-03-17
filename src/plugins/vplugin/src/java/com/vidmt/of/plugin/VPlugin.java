package com.vidmt.of.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginClassLoader;
import org.jivesoftware.openfire.container.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;

import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.utils.PkgUtil;

public class VPlugin implements Plugin {
	private static final Logger log = LoggerFactory.getLogger(VPlugin.class);

	public static final String NAME = "vplugin";

	private static ExecutorService threadPool;

	private Plugin[] subPlugins;

	@SuppressWarnings("unchecked")
	public <T> T getSubPlugin(Class<T> clz) {
		if (subPlugins != null) {
			for (Plugin plg : subPlugins) {
				if (plg.getClass() == clz) {
					return (T) plg;
				}
			}
		}
		return null;
	}

	public static VPlugin get() {
		return (VPlugin) XMPPServer.getInstance().getPluginManager().getPlugin(NAME);
	}

	public static PluginClassLoader getClassLoader() {
		return XMPPServer.getInstance().getPluginManager().getPluginClassloader(get());
	}

	/**
	 * 比VDispatcherServelet初始化晚
	 */
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		long start = System.currentTimeMillis();
		ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(VClassLoader.get());
		try {
			Set<Class<?>> plugins = PkgUtil.getClassList("com.vidmt.of.plugin.sub", true, AnnoPlugin.class);
			subPlugins = new Plugin[plugins.size()];
			int i = 0;
			for (Class<?> clz : plugins) {
				try {
					subPlugins[i] = (Plugin) clz.newInstance();
					log.info("安装插件[{}]开始......", subPlugins[i].getClass().getName());
					subPlugins[i].initializePlugin(manager, pluginDirectory);
					log.info("安装插件[{}]结束......", subPlugins[i].getClass().getName());
					i++;
				} catch (Throwable e) {
					log.error("安装插件[{}]失败!!!", e);
					destroyPlugin();
				}
			}
			Thread.currentThread().setContextClassLoader(oldCl);
			long time = System.currentTimeMillis() - start;
			log.info("==========================");
			log.info("====维艾德插件初始化完毕===");
			log.info("====用时{}秒===", time / 1000);
			log.info("==========================");
			log.info("============ 启  ==========");
			log.info("============ 动  ==========");
			log.info("============ 成  ==========");
			log.info("============ 功  ==========");
			log.info("==========================");
		} catch (ClassNotFoundException e1) {
			log.error("获取子插件出错", e1);
		}
	}

	@Override
	public void destroyPlugin() {
		if (threadPool != null) {
			threadPool.shutdownNow();
			threadPool = null;
		}

		if (subPlugins != null) {
			for (Plugin p : subPlugins) {
				try {
					log.info("卸载插件[{}]开始......", p.getClass().getName());
					p.destroyPlugin();
					log.info("卸载插件[{}]结束......", p.getClass().getName());
				} catch (Throwable e) {
					log.error("卸载插件[{}]失败!!!", e);
				}
			}
			subPlugins = null;
		}

		ApplicationContext ctx = SpringContextHolder.getApplicationContext();
		if (ctx instanceof AbstractApplicationContext) {
			((AbstractApplicationContext) ctx).close();
			log.debug("清空spring");
		}
		SpringContextHolder.clearHolder();
	}

	public static void runAsyc(Runnable r) {
		if (threadPool == null) {
			threadPool = Executors.newFixedThreadPool(3);
		}
		threadPool.execute(r);
	}
}
