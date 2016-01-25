package com.vidmt.of.plugin.sub.iospush;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.AnnoPlugin;

@AnnoPlugin
public class PushPlugin implements Plugin {
	private Logger log = LoggerFactory.getLogger(PushPlugin.class);
	public static final String EL = "x-cmd";
	public static final String NS = "vidmt.xmpp.cmd";

	public static final String KEY_IOS_TOKEN = "key_ios_token";
	public static final String KEY_MSG_CNT = "key_msg_cnt";
	private PushIqHandler sessIQListener;
	private PushInterceptor pushInterceptor;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		sessIQListener = new PushIqHandler(this);
		XMPPServer.getInstance().getIQRouter().addHandler(sessIQListener);

		pushInterceptor = new PushInterceptor(this);
		InterceptorManager.getInstance().addInterceptor(pushInterceptor);
	}

	@Override
	public void destroyPlugin() {
		XMPPServer.getInstance().getIQRouter().removeHandler(sessIQListener);
		sessIQListener = null;

		InterceptorManager.getInstance().removeInterceptor(pushInterceptor);
		pushInterceptor = null;
	}

}
