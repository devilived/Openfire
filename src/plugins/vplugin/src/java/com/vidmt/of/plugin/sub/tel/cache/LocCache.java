package com.vidmt.of.plugin.sub.tel.cache;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.spring.CacheUtil;
import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.tel.entity.Location;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.sub.tel.old.service.LocationService;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerAdapter;
import net.sf.ehcache.event.CacheEventListenerFactory;

/**
 * 非持久
 */
public class LocCache {
	private static final Logger log = LoggerFactory.getLogger(LocCache.class);
	private static final String CACHE_NAME = "locCache";
	private static final String PRE_UID_ = "uid_";

	public static class MyCacheEventListenerFactory extends CacheEventListenerFactory {
		@Override
		public CacheEventListener createCacheEventListener(Properties arg0) {
			return evtListener;
		}

		private CacheEventListener evtListener = new CacheEventListenerAdapter() {
			@Override
			public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
				log.debug("----loc cache put:", element.getObjectKey());
			}

			@Override
			public void notifyElementEvicted(Ehcache cache, Element element) {
				log.debug("----loc cache evicted:", element.getObjectKey());
			}

			@Override
			public void notifyElementExpired(Ehcache cache, Element element) {
				log.debug("----loc cache expired:", element.getObjectKey());
				LocationService locSvc = SpringContextHolder.getBean(LocationService.class);
				String key = (String) element.getObjectKey();
				if (key.startsWith(PRE_UID_)) {
					Location loc = (Location) element.getObjectValue();
					User user = UserCache.get(loc.getUid());
					if (user == null) {
						UserService userSvc = SpringContextHolder.getBean(UserService.class);
						user = userSvc.load(loc.getUid());
					}
					locSvc.saveOrUpdate(loc, user);
				}
			}
		};
	}

	public static Location get(Long uid) {
		return (Location) CacheUtil.get(CACHE_NAME, PRE_UID_ + uid);
	}

	@Deprecated
	public static List<Location> getAll() {
		return CacheUtil.getAll(CACHE_NAME);
	}

	public static void put(Location loc) {
		CacheUtil.put(CACHE_NAME, PRE_UID_ + loc.getUid(), loc);
	}

	public static void remove(Long uid) {
		CacheUtil.remove(CACHE_NAME, PRE_UID_ + uid);
	}
}
