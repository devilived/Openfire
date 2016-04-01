package com.vidmt.of.plugin.sub.tel.misc;

public class IdGen {
	private static long curid = -10;

	public synchronized static void initUid(long uid) {
		curid = uid;
	}

	public synchronized static long nextUid() {
		return ++curid;
	}

}
