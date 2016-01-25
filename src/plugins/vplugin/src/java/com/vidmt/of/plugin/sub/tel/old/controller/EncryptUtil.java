package com.vidmt.of.plugin.sub.tel.old.controller;

import com.vidmt.of.plugin.sub.tel.old.utils.HexUtil;
import com.vidmt.of.plugin.utils.CommUtil;

public class EncryptUtil {
	public static String encryptParam(String msg) {
		byte[] bytes = CommUtil.getStrByte(msg, "UTF-8");
		return HexUtil.toHexString(bytes);
	}

	public static String decryptParam(String param) {
		byte[] bytes = HexUtil.toByteArray(param);
		return CommUtil.newString(bytes, "UTF-8");
	}

}
