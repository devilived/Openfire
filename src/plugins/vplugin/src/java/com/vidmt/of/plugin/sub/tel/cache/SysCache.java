package com.vidmt.of.plugin.sub.tel.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.spring.CacheUtil;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;

/**
 * 持久
 */
public class SysCache {
	private static final Logger log = LoggerFactory.getLogger(SysCache.class);
	private static final String CACHE_NAME = "sysCache";
	private static final String PRE_LVL_TYPE_ = "lvl_type_";

	public static Lvl getLvl(LvlType lvlType) {
		return (Lvl) CacheUtil.get(CACHE_NAME, PRE_LVL_TYPE_ + lvlType.name());
	}

	public static void putLvl(Lvl lvlType) {
		CacheUtil.put(CACHE_NAME, PRE_LVL_TYPE_ + lvlType.getName(), lvlType);
	}
}
