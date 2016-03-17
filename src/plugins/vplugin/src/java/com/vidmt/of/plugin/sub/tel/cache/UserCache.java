package com.vidmt.of.plugin.sub.tel.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.spring.CacheUtil;
import com.vidmt.of.plugin.sub.tel.entity.User;

/**
 * 非持久
 * 
 * @author xingqisheng
 */
public class UserCache {
	private static final Logger log = LoggerFactory.getLogger(UserCache.class);
	private static final String CACHE_NAME = "userCache";
	private static final String PRE_UID_ = "uid_";

	public static User get(Long uid) {
		return (User) CacheUtil.get(CACHE_NAME, PRE_UID_ + uid);
	}

	public static void put(User user) {
		CacheUtil.put(CACHE_NAME, PRE_UID_ + user.getId(), user);
	}

	public static void remove(Long uid) {
		CacheUtil.remove(CACHE_NAME, PRE_UID_ + uid);
	}

	public static void clear() {
		
	}
}
