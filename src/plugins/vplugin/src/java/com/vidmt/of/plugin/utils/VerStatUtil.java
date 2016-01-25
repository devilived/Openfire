package com.vidmt.of.plugin.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerStatUtil {
	private static final Map<String, Date> vermap = new HashMap<>();

	public static void put(String resource) {
		String[] arr = resource.split("-");
		if (arr.length > 2) {
			String os = arr[1];
			String key = arr[1] + "-" + arr[2];
			vermap.put(key, new Date());
		}
	}

	public static Map<String, Date> get() {
		return vermap;
	}

}
