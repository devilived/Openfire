package com.vidmt.of.plugin.sub.tel.cache;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import com.vidmt.of.plugin.spring.CacheUtil;
import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.tel.entity.Trace;
import com.vidmt.of.plugin.sub.tel.old.service.TraceService;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerAdapter;
import net.sf.ehcache.event.CacheEventListenerFactory;

/**
 * 持久
 */
public class TraceCache {
	private static Logger log = LoggerFactory.getLogger(CacheUtil.class);
	public static final int MIN_POINTS = 5;
	private static final String CACHE_NAME = "traceCache";
	private static final String PRE_UID_ = "uid_";

	// 每timetoidle=30分钟，清空一下cache，存入数据库，注意此方法不要调用get，remove等，否则会再次出发此方法，导致存储两次
	public static class MyCacheEventListenerFactory extends CacheEventListenerFactory {
		@Override
		public CacheEventListener createCacheEventListener(Properties arg0) {
			return new CacheEventListenerAdapter() {
				public void notifyElementExpired(Ehcache cache, Element element) {
					String key = (String) element.getObjectKey();
					if (key.startsWith(PRE_UID_)) {
						Trace trace = (Trace) element.getObjectValue();
						if (trace.getPoints().size() > MIN_POINTS) {
							TraceService traceSvc = SpringContextHolder.getBean(TraceService.class);
							try {
								traceSvc.save(trace);
								log.debug("保存过期足迹:" + trace);
							} catch (DuplicateKeyException e) {
								log.warn("保存足迹重复：" + trace.getUid() + trace.getHash());
							}
						}
					}
				}
			};
		}
	}

	public static Trace get(Long uid) {
		return (Trace) CacheUtil.get(CACHE_NAME, PRE_UID_ + uid);
	}

	public static void put(Trace trace) {
		CacheUtil.put(CACHE_NAME, PRE_UID_ + trace.getUid(), trace);
	}

	@Deprecated
	public static List<Trace> getAll() {
		return CacheUtil.getAll(CACHE_NAME);
	}

	@Deprecated
	public static List<Element> getAllWithStatistics() {
		return CacheUtil.getAllWithStatistics(CACHE_NAME);
	}

	public static void removeAndSave(Trace trace) {
		if (trace.getPoints().size() > MIN_POINTS) {
			TraceService traceSvc = SpringContextHolder.getBean(TraceService.class);
			try {
				traceSvc.save(trace);
			} catch (DuplicateKeyException e) {
				log.warn("保存足迹重复：" + trace.getUid() + trace.getHash());
			}
			log.debug("保存定时足迹:" + trace);
		}
		CacheUtil.remove(CACHE_NAME, PRE_UID_ + trace.getUid());
	}
}
