package com.vidmt.of.plugin.sub.tel.old.packet;

import org.xmpp.packet.IQ;

import com.vidmt.of.plugin.sub.tel.TelIQHandler;

public class QueryIQ extends IQ{
	public QueryIQ(){
		this.setChildElement(TelIQHandler.EL_QUERY, TelIQHandler.NS_QUERY);
	}
}
