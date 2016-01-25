package com.vidmt.of.plugin.sub.tel.old.packet;

import org.dom4j.Element;

public class RelIQ extends QueryIQ {
	private RelIQ(RelCMD cmd, String fromjid) {
		super();
		Element cmdEl = element.addElement("relationship").addElement(cmd.name());
		switch (cmd) {
		case add:
		case delete:
			cmdEl.addElement("sender").addAttribute("jid", fromjid);
			break;
		case agree:
		case reject:
			cmdEl.addElement("receiver").addAttribute("jid", fromjid);
			break;
		}
	}
	
	public static enum RelCMD {
		add, delete, agree, reject
	}
}
