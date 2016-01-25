package com.vidmt.of.plugin.sub.iospush;

import java.io.File;
import java.util.Map;

import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.vidmt.of.plugin.VPlugin;
import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.extdb.Acc;
import com.vidmt.of.plugin.sub.extdb.Acc.AccType;
import com.vidmt.of.plugin.sub.tel.cache.UserCache;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.utils.CommUtil;

/**
 * <b>function:</b> send offline msg plugin
 * 
 * @author MZH
 */
public class PushInterceptor implements PacketInterceptor {
	private static final Logger log = LoggerFactory.getLogger(PushPlugin.class);
	private UserManager userManager;
	private static final boolean DEBUG = false;
	private static final String CER_PWD = "123456";
	private File apnsCerFile;
	private PushPlugin plg;
	private UserService userService;

	public PushInterceptor(PushPlugin plg) {
		if (DEBUG) {
			apnsCerFile = new File(JiveGlobals.getHomeDirectory(),
					"plugins/" + VPlugin.NAME + "/web/WEB-INF/res/apns_debug.p12");
		} else {
			apnsCerFile = new File(JiveGlobals.getHomeDirectory(),
					"plugins/" + VPlugin.NAME + "/web/WEB-INF/res/apns.p12");
		}

		this.plg = plg;
		XMPPServer server = XMPPServer.getInstance();
		userManager = server.getUserManager();
		userService = SpringContextHolder.getBean(UserService.class);
	}

	/**
	 * intercept message
	 */
	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {
		if (processed || !incoming) {
			return;
		}
		if (packet instanceof IQ) {
			return;
		}
		if (packet instanceof Message && Message.Type.chat != ((Message) packet).getType()) {
			return;
		}
		this.doAction(packet, incoming, processed, session);
	}

	/**
	 * <b>send offline msg from this function </b>
	 */
	private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {
		log.debug(">>>>>>>>>begin:pkt:" + packet);
		String from = packet.getFrom().getNode();
		if (from == null) {
			return;
		}
		Acc acc = new Acc(from);
		if (!AccType.uid.equals(acc.type())) {
			return;
		}
		String msg = null;
		Long fromuid = Long.valueOf(acc.value());
		if (packet instanceof Presence) {
			Presence presence = (Presence) packet;
			Presence.Type ptype = presence.getType();
			if (ptype == null) {
				return;
			}
			switch (ptype) {
			case subscribe:
				UserService usersvc = SpringContextHolder.getBean(UserService.class);
				User user = usersvc.load(fromuid);
				if (user != null && user.getLocPrivate()) {
					return;
				}
				msg = "有人添加您为好友";
				break;
			case subscribed:
				msg = "有人同意您为好友";
				break;
			default:
				return;
			}
		} else if (packet instanceof Message) {
			Message message = (Message) packet;
			Element multiEl = message.getChildElement("multimedia", "http://www.vidmt.com/multimedia");
			User fromuser = userService.load(fromuid);
			String displayName = getDisplayName(fromuser);
			if (multiEl != null) {
				String type = multiEl.element("data").attributeValue("type");
				if ("AUDIO".equalsIgnoreCase(type)) {
					msg = displayName + ":发送了一条语音";
				} else if ("IMAGE".equalsIgnoreCase(type)) {
					msg = displayName + ":发送了一张图片";
				}
			} else {
				msg = (displayName + ":" + message.getBody());
				if (CommUtil.getStrByte(msg, "UTF-8").length > 255) {
					msg = displayName + ":发送了一条消息";
				}
			}
		}

		if (CommUtil.isEmpty(msg)) {
			return;
		}
		// User user = UserCache.get(fromuid);
		// if(user==null){
		// return;
		// }
		JID recipient = packet.getTo();
		if (recipient == null) {
			return;
		}
		Long touid = Long.valueOf(recipient.getNode());
		User toUser = userService.load(touid);
		if (toUser == null) {
			return;
		}
		try {
			String devicetoken = (String) toUser.getProp(PushPlugin.KEY_IOS_TOKEN, null);
			if (devicetoken == null) {
				Map<String, String> props = userManager.getUser(recipient.getNode()).getProperties();
				devicetoken = props.get(PushPlugin.KEY_IOS_TOKEN);
				if (devicetoken != null) {
					toUser.setProp(PushPlugin.KEY_IOS_TOKEN, devicetoken);
					UserCache.put(toUser);
				} else {
					return;
				}
			}
			if (hasToken(devicetoken)) {
				log.debug("pns msg:" + msg);
				Integer cnt = 1;
				if (toUser != null) {
					cnt = 1 + (int) toUser.getProp(PushPlugin.KEY_MSG_CNT, 0);
					toUser.setProp(PushPlugin.KEY_MSG_CNT, cnt);
				}
				pns(devicetoken, msg, cnt);
			}
		} catch (UserNotFoundException e) {
			log.error("user not found", e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("<<<<<<<<<<<<<pns end");
	}

	/**
	 * 判断是否苹果
	 * 
	 * @param deviceToken
	 * @return
	 */
	private boolean hasToken(String deviceToken) {
		if (deviceToken != null && deviceToken.length() > 0) {
			return true;
		}
		return false;
	}

	private String getDisplayName(User user) {
		try {
			if (user != null) {
				if (user.getNick() != null) {
					return user.getNick();
				} else if (user.getName() != null) {
					return user.getName();
				} else if (user.getPhone() != null) {
					return user.getPhone();
				}
			}
			return user.getId().toString();
		} catch (Throwable e) {
			log.error("获取用户昵称出错", e);
		}
		return "";
	}

	public void pns(String token, String msg, Integer badge) throws Exception {
		String sound = "default";// 铃音
		if (apnsCerFile == null || !apnsCerFile.exists()) {
			log.error("IOS CER NOT EXIST!!" + apnsCerFile);
			return;
		}
		log.debug(">>> PUSH pwd is:" + CER_PWD);
		log.debug(">>> PUSH cerfile is:" + apnsCerFile);
		String certificatePath = apnsCerFile.getAbsolutePath();
		try {
			ApnsService service = APNS.newService().withCert(certificatePath, CER_PWD).withAppleDestination(!DEBUG)
					.build();
			String payload = APNS.newPayload().alertBody(msg).sound(sound).badge(badge).build();
			service.push(token, payload);

		} catch (Exception e) {
			log.error("PUSH ERROR!", e);
			throw e;
		}
	}

}
