package com.vidmt.of.plugin.sub.msgreply;

import java.io.File;
import java.io.IOException;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.LocalClientSession;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import com.vidmt.of.plugin.VPlugin;
import com.vidmt.of.plugin.utils.CommUtil;

public class MsgReplyPlugin implements PacketInterceptor, Plugin {

	private static final Logger Log = LoggerFactory.getLogger(MsgReplyPlugin.class);

	private InterceptorManager interceptorManager;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		interceptorManager = InterceptorManager.getInstance();

		interceptorManager.addInterceptor(this);
	}

	@Override
	public void destroyPlugin() {
		interceptorManager.removeInterceptor(this);
		interceptorManager = null;
	}

	@Override
	public void interceptPacket(Packet packet, final Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {
		if (!incoming || processed) {
			return;
		}

		if (packet instanceof Message) {
			final Message msge = (Message) packet;
			if (msge.getType().equals(Message.Type.error) || msge.getType().equals(IQ.Type.result)) {
				return;
			}
			try {
				VPlugin.runAsyc(new Runnable() {

					@Override
					public void run() {

						try {
							handleMessageRequest(session, msge);
						} catch (Throwable e) {
							System.err.println("============START==========");
							System.err.println("xqs!!!handleMSGError:" + e);
							System.err.println("============END==========");
							e.printStackTrace();
						}
					}
				});
			} catch (Throwable e) {
				System.err.println("xqs!!!====" + e);
			}
		}
	}

	private void handleMessageRequest(Session session, final Message msg) throws IOException {
		try {
			// System.err.println("xqs===========got iq:" + iq.toXML());
			if (session instanceof LocalClientSession) {
				// Check if we could process messages from the recipient.
				// If not, return a not-acceptable error as per XEP-0016:
				// If the user attempts to send an outbound stanza to a contact
				// and that stanza type is blocked, the user's server MUST NOT
				// route the stanza to the contact but instead MUST return a
				// <not-acceptable/> error
				Message replyMsg = new Message();
				// dummyMessage.setFrom(msg.getTo());
				replyMsg.setID(msg.getID());
				replyMsg.setTo(msg.getFrom());
				replyMsg.setType(Message.Type.headline);
				if (((LocalClientSession) session).canProcess(replyMsg)) {
					((LocalClientSession) session).deliver(replyMsg);
				}
			}
		} catch (Throwable e) {
			System.err.println("========START=============");
			System.err.println("report location error");
			System.err.println("xqs!!!msg:" + msg.toXML());
			System.err.println("xqs!!!exception:" + CommUtil.fmtException(e));
			System.err.println("========END===============");
		}
	}
}
