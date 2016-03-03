package com.vidmt.of.plugin.sub.tel.old.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerStatUtil {
	private static final Map<String, Date> lastLoginMap = new HashMap<>();
	private static final Map<String, Integer> osCntMap = new HashMap<>();

	public static void put(String resource) {
		String[] arr = resource.split("-");
		if (arr.length > 2) {
			String os = arr[1];
			int ioscnt = osCntMap.getOrDefault(os, 0);
			osCntMap.put(os, ioscnt + 1);

			String key = arr[1] + "-" + arr[2];
			lastLoginMap.put(key, new Date());
		}
	}

	public void remove(String resource) {
		String[] arr = resource.split("-");
		if (arr.length > 2) {
			String os = arr[1];
			int ioscnt = osCntMap.getOrDefault(os, 0);
			osCntMap.put(os, ioscnt - 1);
		}
	}

	public static Map<String, Date> getLastLoginMap() {
		return lastLoginMap;
	}

	public static Map<String, Integer> getOsCntMap() {
		return osCntMap;
	}

}
