package com.vidmt.of.plugin.sub.iospush;

import java.io.File;
import java.util.Map;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.jivesoftware.openfire.Connection;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.SessionEventDispatcher;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.session.LocalClientSession;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.UserManager;
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
public class PushPlugin extends AbsSessionEventListener implements Plugin {
	private Logger log = LoggerFactory.getLogger(PushPlugin.class);
	public static final String EL = "x-cmd";
	public static final String NS = "vidmt.xmpp.cmd";

	public static final String KEY_IOS_TOKEN = "key_ios_token";
	public static final String KEY_MSG_CNT = "key_msg_cnt";
	private PushIqHandler sessIQListener;
	private PushInterceptor pushInterceptor;
	private UserManager usrMgr;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		sessIQListener = new PushIqHandler(this);
		XMPPServer.getInstance().getIQRouter().addHandler(sessIQListener);

		pushInterceptor = new PushInterceptor(this);
		InterceptorManager.getInstance().addInterceptor(pushInterceptor);
		usrMgr = XMPPServer.getInstance().getUserManager();
		SessionEventDispatcher.addListener(this);
	}

	@Override
	public void destroyPlugin() {
		XMPPServer.getInstance().getIQRouter().removeHandler(sessIQListener);
		sessIQListener = null;

		InterceptorManager.getInstance().removeInterceptor(pushInterceptor);
		pushInterceptor = null;

		usrMgr = null;
		SessionEventDispatcher.removeListener(this);
	}

	@Override
	public void sessionCreated(final Session session) {
		String resource = session.getAddress().getResource();
		try {
			Connection conn = ((LocalClientSession) session).getConnection();
			IoSession iosess = (IoSession) ClzUtil.getField(conn, "ioSession");

			if (resource.contains("-i-")) {
				iosess.getFilterChain().addLast("vidmt", new IoFilterAdapter() {
					public void messageReceived(org.apache.mina.core.filterchain.IoFilter.NextFilter nextFilter,
							IoSession isSess, Object message) throws Exception {
						try {
							String msg = (String) message;
							if ("</stream:stream>".equals(msg)) {
								String from = session.getAddress().getNode();
								User user = UserCache.get(Long.valueOf(from));
								if (user != null) {
									user.setProp(PushPlugin.KEY_IOS_TOKEN, null);
									UserCache.put(user);
								}
								Map<String, String> prop = usrMgr.getUser(from).getProperties();
								prop.remove(PushPlugin.KEY_IOS_TOKEN);
								log.debug(">>>>用户手动退出登录,清空session");
							}
						} catch (Throwable e) {
							log.error("接受MINA 消息出错", e);
						}finally{
							super.messageReceived(nextFilter, isSess, message);
						}
					};
				});
			}

		} catch (Throwable e) {
			log.error("登录时操作MINA IOSESSION出错", e);
		}
	}

	@Override
	public void sessionDestroyed(Session session) {
		String resource = session.getAddress().getResource();
		try {
			Connection conn = ((LocalClientSession) session).getConnection();
			IoSession iosess = (IoSession) ClzUtil.getField(conn, "ioSession");

			if (resource.contains("-i-")) {
				iosess.getFilterChain().remove("vidmt");
			}
		} catch (Throwable e) {
			log.error("退出时操作MINA IOSESSION出错", e);
		}
	}

}
