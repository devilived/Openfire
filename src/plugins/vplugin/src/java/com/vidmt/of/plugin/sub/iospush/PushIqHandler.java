package com.vidmt.of.plugin.sub.iospush;

import java.util.Map;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;

import com.vidmt.of.plugin.sub.tel.cache.UserCache;
import com.vidmt.of.plugin.sub.tel.entity.User;

public class PushIqHandler extends IQHandler {
	private Logger log = LoggerFactory.getLogger(PushPlugin.class);
	private UserManager usrMgr;
	private PushPlugin plg;
	private IQHandlerInfo info;

	public PushIqHandler(PushPlugin plg) {
		super(plg.getClass().getSimpleName());
		info = new IQHandlerInfo(PushPlugin.EL, PushPlugin.NS);
		usrMgr = XMPPServer.getInstance().getUserManager();
		this.plg = plg;
	}

	/**
	 * <iq type=set><cmd xmlns="vidmt.xmpp.cmd" action="setIosToken"><token>
	 * 123456</token></cmd></iq>
	 * <iq type=set><cmd xmlns="vidmt.xmpp.cmd" action="removeIosToken" /></iq>
	 */
	@Override
	public IQ handleIQ(IQ iq) throws UnauthorizedException {
		try {
			String from = iq.getFrom().getNode();
			Element el = iq.getChildElement();
			String action = el.attributeValue("action");
			if ("setIosToken".equals(action)) {
				String token = el.elementText("token");
				User user = UserCache.get(Long.valueOf(from));
				if (user != null) {
					user.setProp(PushPlugin.KEY_IOS_TOKEN, token);
					UserCache.put(user);
				}
				Map<String, String> prop = usrMgr.getUser(from).getProperties();
				prop.put(PushPlugin.KEY_IOS_TOKEN, token);
				log.debug(">>>>PUSH SETIOSTOKEN:" + token + "\n all packets:" + iq);
			} else if ("removeIosToken".equals(action)) {
				User user = UserCache.get(Long.valueOf(from));
				if (user != null) {
					user.setProp(PushPlugin.KEY_IOS_TOKEN, null);
					UserCache.put(user);
				}
				Map<String, String> prop = usrMgr.getUser(from).getProperties();
				prop.remove(PushPlugin.KEY_IOS_TOKEN);
				log.debug(">>>>REMOVE SETIOSTOKEN,all packets:" + iq);
			} else if ("resetMsgCnt".equals(action)) {
				// int cnt =
				// Integer.parseUnsignedInt(prop.getOrDefault(PushPlugin.KEY_MSG_CNT,
				// "0"));
				User user = UserCache.get(Long.valueOf(from));
				if (user != null) {
					user.setProp(PushPlugin.KEY_MSG_CNT, null);
				}
			}
		} catch (Throwable e) {
			System.out.println("error:update IOS ERROR" + e);
			log.error("error:update IOS ERROR", e);
		}
		IQ replyIQ = IQ.createResultIQ(iq);
		return replyIQ;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}
}
