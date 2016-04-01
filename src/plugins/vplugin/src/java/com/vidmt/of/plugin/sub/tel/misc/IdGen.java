package com.vidmt.of.plugin.sub.tel.misc;

public class IdGen {
	private static long curuid = -10;

	public static void initUid(long uid) {
		curuid = uid;
	}

	public static long curUid() {
		return curuid;
	}

	public static void updateUid() {
		curuid++;
	}

}
