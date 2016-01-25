package com.vidmt.of.plugin.spring;

import java.util.ArrayList;
import java.util.List;

import com.vidmt.of.plugin.spring.beans.SpringContextHolder;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Cache工具类
 * 
 * @author XingQisheng
 * @version 2015-11-3
 */
public class CacheUtil {

	private static CacheManager cacheManager = ((CacheManager) SpringContextHolder.getBean("cacheManager"));

	private static final String SYS_CACHE = "sysCache";

	/**
	 * 获取SYS_CACHE缓存
	 * 
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		return get(SYS_CACHE, key);
	}

	/**
	 * 写入SYS_CACHE缓存
	 * 
	 * @param key
	 * @return
	 */
	public static void put(String key, Object value) {
		put(SYS_CACHE, key, value);
	}

	/**
	 * 从SYS_CACHE缓存中移除
	 * 
	 * @param key
	 * @return
	 */
	public static void remove(String key) {
		remove(SYS_CACHE, key);
	}

	/**
	 * 获取缓存
	 * 
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static Object get(String cacheName, String key) {
		Element element = getCache(cacheName).get(key);
		return element == null ? null : element.getObjectValue();
	}

	/**
	 * 写入缓存
	 * 
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public static void put(String cacheName, String key, Object value) {
		Element element = new Element(key, value);
		getCache(cacheName).put(element);
	}

	/**
	 * 从缓存中移除
	 * 
	 * @param cacheName
	 * @param key
	 */
	public static void remove(String cacheName, String key) {
		getCache(cacheName).remove(key);
	}

	/**
	 * 获得一个Cache，没有则创建一个。
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Cache getCache(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
			cache.getCacheConfiguration().setEternal(true);
		}
		return cache;
	}

	/**
	 * 性能较低，请谨慎使用
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> List<T> getAll(String cacheName) {
		Cache cache = getCache(cacheName);
		List<String> keys = cache.getKeysWithExpiryCheck();
		List<T> list = new ArrayList<>(keys.size());
		for (String key : keys) {
			Element element = cache.get(key);
			if (element != null) {
				list.add((T) element.getObjectValue());
			}
		}
		return list;
	}
	@Deprecated
	@SuppressWarnings("unchecked")
	public static List<Element> getAllWithStatistics(String cacheName) {
		Cache cache = getCache(cacheName);
		List<String> keys = cache.getKeysWithExpiryCheck();
		List<Element> list = new ArrayList<>(keys.size());
		for (String key : keys) {
			Element element = cache.get(key);
			if (element != null) {
				list.add(element);
			}
		}
		return list;
	}
	public static CacheManager getCacheManager() {
		return cacheManager;
	}

}
