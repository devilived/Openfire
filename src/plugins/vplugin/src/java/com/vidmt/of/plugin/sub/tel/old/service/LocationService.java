package com.vidmt.of.plugin.sub.tel.old.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.cache.LocCache;
import com.vidmt.of.plugin.sub.tel.entity.Location;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.sub.tel.old.dao.LocationDao;

@Service
@Transactional(readOnly = false)
public class LocationService extends CrudService<LocationDao, Location> {
	private static final Logger log = LoggerFactory.getLogger(LocationService.class);

	@Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Location entity, User user) {
		dao.saveOrUpdate(entity);
		dao.saveOrUpdateNearby(entity, user);
	}
	//涉及到locationcache，因此此处可能写入
	@Transactional(readOnly = true)
	public List<Location> findListByUids(List<Long> uids) {
		List<Location> loclist = new ArrayList<>(uids.size());

		List<Long> tmpuidlist = new ArrayList<>(uids);
		for (int i = 0; i < uids.size(); i++) {
			Long uid = uids.get(i);
			Location loc = (Location) LocCache.get(uid);
			if (loc != null) {
				loclist.add(loc);
				tmpuidlist.remove(uid);
			}
		}
		if (tmpuidlist.size() > 0) {
			List<Location> tmpLocList = dao.findListByUids(tmpuidlist);
			for (Location loc : tmpLocList) {
				loclist.add(loc);
			}
		}
		return loclist;
	}
	@Transactional(readOnly = true)
	public Location findByUid(Long uid) {
		Location loc = LocCache.get(uid);
		if (loc == null) {
			loc = dao.findByUid(uid);
			if (loc != null) {
				LocCache.put(loc);
			}
		}
		return loc;
	}
	@Transactional(readOnly = true)
	public List<Location> getNearBy(Long uid, Double lon, Double lat, Date time, Integer sex, Double distance,
			Date birthStart, Date birthEnd, Integer offset, Integer limit) {
		return dao.getNearBy(uid, lon, lat, time, distance, sex, birthStart, birthEnd, offset, limit);
	}

	public void deleteExpiredNearby(Date time) {
		dao.deleteExpiredPoints(time);
	}
}
