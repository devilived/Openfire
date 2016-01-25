package com.vidmt.of.plugin.sub.token;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.SessionEventDispatcher;
import org.jivesoftware.openfire.event.SessionEventListener;
import org.jivesoftware.openfire.session.Session;

public class TokenPlugin implements Plugin, SessionEventListener {
	private Map<String, String> tokenMap;

	public String getUidByToken(String token) {
		return tokenMap.get(token);
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		tokenMap = new HashMap<String, String>(1000);
		SessionEventDispatcher.addListener(this);
	}

	@Override
	public void destroyPlugin() {
		SessionEventDispatcher.removeListener(this);
		tokenMap.clear();
		tokenMap = null;
	}

	@Override
	public synchronized void sessionCreated(Session session) {
		String streamId = session.getStreamID().getID();
		String uid = session.getAddress().getNode();
		tokenMap.put(streamId, uid);
		System.out.println("======add session====" + streamId + "||" + uid);
	}

	@Override
	public synchronized void sessionDestroyed(Session session) {
		tokenMap.remove(session.getStreamID().getID());
		System.out.println("======remove session====" + session.getStreamID().getID());
	}

	@Override
	public void anonymousSessionCreated(Session session) {
	}

	@Override
	public void anonymousSessionDestroyed(Session session) {
	}

	@Override
	public void resourceBound(Session session) {
	}

}
