package com.vidmt.of.plugin.sub.tel.old.packet;

import org.dom4j.Element;

import com.vidmt.of.plugin.utils.CommUtil;

public class CmdIQ extends QueryIQ {
	public CmdIQ(CmdIQ.CMD cmd, String from) {
		super();
		Element cmdEl = element.addElement("cmd").addElement(cmd.name());
		if (!CommUtil.isEmpty(from)) {
			cmdEl.addAttribute("from", from);
		}
	}
	public static enum CMD {
		alarm, remoteAudio, hideIcon, showIcon
	}
}