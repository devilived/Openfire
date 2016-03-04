package com.vidmt.of.plugin.sub.tel;

import java.io.File;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.jivesoftware.openfire.Connection;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.SessionEventDispatcher;
import org.jivesoftware.openfire.nio.NIOConnection;
import org.jivesoftware.openfire.session.LocalClientSession;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.AnnoPlugin;
import com.vidmt.of.plugin.abs.AbsSessionEventListener;
import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.tel.cache.LocCache;
import com.vidmt.of.plugin.sub.tel.cache.UserCache;
import com.vidmt.of.plugin.sub.tel.entity.Location;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.sub.tel.old.service.LocationService;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.sub.tel.old.utils.VerStatUtil;
import com.vidmt.of.plugin.utils.ClzUtil;

@AnnoPlugin
public class TelPlugin extends AbsSessionEventListener implements Plugin {
	public static final String VAR_PAYTYPE = "{paytype}";
	public static final String VAR_UID = "{uid}";
	// D:\\deploys\\nginx\\html\\telephone
	public static final String KEY_RES_PATH = "plugin.vplugin.telplugin.respath";
	// http://telephone.vidmt.com/TelServer/api/1/pay/{paytype}/notify.json
	public static final String KEY_PAY_NOTIFY_URL = "plugin.vplugin.telplugin.pay_notify_url";

	public static final String KEY_XMPP_CLIENT_IDLE = "plugin.vplugin.telplugin.xmpp_client_idle";

	private static Logger log = LoggerFactory.getLogger(TelPlugin.class);
	private XMPPServer server;
	private TelIQHandler iqHandler;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		server = XMPPServer.getInstance();
		// 注册IQHandler
		iqHandler = new TelIQHandler();
		server.getIQRouter().addHandler(iqHandler);
		SessionEventDispatcher.addListener(this);
	}

	@Override
	public void destroyPlugin() {
		// HttpManager.get().notifyPluginDestroy();// do before重启插件
		server.getIQRouter().removeHandler(iqHandler);
		SessionEventDispatcher.removeListener(this);
	}

	@Override
	public void sessionCreated(Session session) {
		VerStatUtil.put(session.getAddress().getResource());
		Long uid = Long.valueOf(session.getAddress().getNode());
		UserService userSvc = SpringContextHolder.getBean(UserService.class);
		User user = userSvc.load(uid);// 加入cache
		if (user.getLvl() != null) {
			try {
				Connection conn = ((LocalClientSession) session).getConnection();
				IoSession iosess = (IoSession) ClzUtil.getField(conn, "ioSession");
				String prop = JiveGlobals.getProperty(KEY_XMPP_CLIENT_IDLE);
				if (prop != null) {
					int maxIdle = Integer.parseInt(prop) / 1000;
					if (maxIdle > 0) {
						iosess.getConfig().setIdleTime(IdleStatus.READER_IDLE, maxIdle / 2);
					}
				}
			} catch (Throwable e) {
				log.error("修订用户的超时时间出错", e);
			}
		}

		Location loc = LocCache.get(uid);
		if (loc != null) {
			LocationService locsvc = SpringContextHolder.getBean(LocationService.class);
			locsvc.saveOrUpdate(loc, user);
			LocCache.remove(uid);// 可以让用户登录后上传的第一个点立马被存入数据库
		}
	}

	@Override
	public void sessionDestroyed(Session session) {
		VerStatUtil.remove(session.getAddress().getResource());
		Long uid = Long.valueOf(session.getAddress().getNode());
		UserService userSvc = SpringContextHolder.getBean(UserService.class);
		User user = userSvc.load(uid);// 加入cache

		Location loc = LocCache.get(uid);
		if (loc != null) {
			LocationService locsvc = SpringContextHolder.getBean(LocationService.class);
			locsvc.saveOrUpdate(loc, user);
			LocCache.remove(uid);// 可以让用户登录后上传的第一个点立马被存入数据库
		}
		UserCache.remove(uid);
	}
}
