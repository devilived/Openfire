package com.vidmt.of.plugin.sub.tel.old.utils;

import com.vidmt.of.plugin.sub.tel.entity.Location;

public class GeoUtil {
	private static final double EARTH_RADIUS = 6378137.0;

	public static double getDistance(Location loc1, Location loc2) {// 单位：米
		double radLat1 = (loc1.getLat() * Math.PI / 180.0);
		double radLat2 = (loc2.getLat() * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (loc1.getLon() - loc2.getLon()) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		return Double.valueOf(s);
	}
}
