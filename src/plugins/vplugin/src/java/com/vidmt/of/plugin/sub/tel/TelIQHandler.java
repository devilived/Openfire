package com.vidmt.of.plugin.sub.tel;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;

import com.vidmt.of.plugin.sub.tel.old.packet.CmdIQ;

public class TelIQHandler extends IQHandler {
	private static final Logger log = LoggerFactory.getLogger(TelIQHandler.class);
	public static final String NS_QUERY = "query";
	public static final String EL_QUERY = "vidmt.xmpp";

	public TelIQHandler() {
		super("telephone");
	}

	@Override
	public IQHandlerInfo getInfo() {
		return new IQHandlerInfo(EL_QUERY, NS_QUERY);// 设置监听的命名空间
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		try {
			Element rootNode = packet.getChildElement();
			if (!NS_QUERY.equals(rootNode.getNamespaceURI())) {
				return null;
			}
			log.debug("RCV CMD：" + packet.toXML());

			String from = packet.getFrom().toBareJID();
			String rootName = rootNode.getName();
			if ("query".equals(rootName)) {
				if (Type.get.equals(packet.getType())) {
				} else if (Type.set.equals(packet.getType())) {
					Element cmdEl = rootNode.element("cmd");
					if (cmdEl != null) {// 控制指令Command
						Element cmdE = (Element) cmdEl.elements().get(0);
						CmdIQ cmd = new CmdIQ(CmdIQ.CMD.valueOf(cmdE.getName()), from);
						cmd.setFrom(from);
						cmd.setType(Type.set);// 兼容Android2.6.6之前版本，之后删除之
						cmd.setTo(packet.getTo());// must full JID
						log.debug("SENT CMD：" + cmd.toXML());
						XMPPServer.getInstance().getIQRouter().route(cmd);
						return null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
