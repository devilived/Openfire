package com.vidmt.of.plugin.sub.tel.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.spring.CacheUtil;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;
import com.vidmt.of.plugin.sub.tel.old.entity.PhoneCity;

/**
 * 持久
 */
public class SysCache {
	private static final Logger log = LoggerFactory.getLogger(SysCache.class);
	private static final String CACHE_NAME = "sysCache";
	private static final String PRE_LVL_TYPE_ = "lvl_type_";
	private static final String PRE_PHONE_CITY_ = "phone_city_";

	public static Lvl getLvl(LvlType lvlType) {
		return (Lvl) CacheUtil.get(CACHE_NAME, PRE_LVL_TYPE_ + lvlType.name());
	}

	public static void putLvl(Lvl lvlType) {
		CacheUtil.put(CACHE_NAME, PRE_LVL_TYPE_ + lvlType.getName(), lvlType);
	}

	public static PhoneCity getPhoneCity(String prefix) {
		return (PhoneCity) CacheUtil.get(CACHE_NAME, PRE_PHONE_CITY_ + prefix);
	}

	public static void putPhonecity(PhoneCity phonecity) {
		CacheUtil.put(CACHE_NAME, PRE_PHONE_CITY_ + phonecity.getPrefix(), phonecity);
	}
}
