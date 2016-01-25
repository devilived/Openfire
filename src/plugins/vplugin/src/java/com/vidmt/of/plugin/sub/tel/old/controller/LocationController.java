package com.vidmt.of.plugin.sub.tel.old.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.cache.LocCache;
import com.vidmt.of.plugin.sub.tel.cache.TraceCache;
import com.vidmt.of.plugin.sub.tel.entity.Location;
import com.vidmt.of.plugin.sub.tel.entity.Trace;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.sub.tel.old.entity.OldLocation;
import com.vidmt.of.plugin.sub.tel.old.service.LocationService;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.sub.tel.old.utils.GeoUtil;
import com.vidmt.of.plugin.utils.VUtil;

@Controller
@RequestMapping("/vplugin/api/1/location")
public class LocationController {
	private static final Logger log = LoggerFactory.getLogger(LocationController.class);
	@Autowired
	private LocationService locationService;
	@Autowired
	private UserService userService;

	// 因为定时任务暂时不可用，在此处判定是否清空足迹缓存
	// @Autowired
	// private SaveCacheTask saveCacheTask;

	@ResponseBody
	@RequestMapping("/upload.*")
	public JSONObject uploadLocation(OldLocation loc, HttpServletRequest req, HttpServletResponse resp) {
		// saveCacheTask.saveTrace();
		Long uid = (Long) req.getAttribute("uid");
		Location newLoc = loc.toLocation();
		newLoc.setUid(uid);
		newLoc.setTime(new Date());

		Location cacheloc = LocCache.get(uid);
		if (cacheloc == null) {
			User user = userService.load(uid);
			locationService.saveOrUpdate(newLoc, user);
		}

		LocCache.put(newLoc);
		cacheTracePoint(newLoc);
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("m", "OK");
		return json;
	}

	@ResponseBody
	@RequestMapping("/getFriends.*")
	public JSONObject getFriendLocs(String uids, HttpServletRequest req, HttpServletResponse resp) {
		String jsonUidsStr = EncryptUtil.decryptParam(uids);
		List<Long> uidlist = JSONArray.parseArray(jsonUidsStr, Long.class);
		JSONObject json = new JSONObject();
		if (uidlist.size() == 0) {
			json.put("c", 0);
			json.put("list", Collections.emptyList());
			return json;
		}
		List<Location> cachedLocs = locationService.findListByUids(uidlist);

		json.put("c", 0);
		json.put("list", OldLocation.toOldLocationList(cachedLocs));
		return json;
	}

	@ResponseBody
	@RequestMapping("/getNearby.*")
	public JSONObject getNearbyLocations(String gender, Long time, Double distance, Integer ageStart, Integer ageEnd,
			Integer currentPage, Integer pageSize, HttpServletRequest req, HttpServletResponse resp) {
		Long uid = (Long) req.getAttribute("uid");

		Location curLoc = locationService.findByUid(uid);
		JSONObject json = new JSONObject();
		if (curLoc == null) {
			json.put("c", 0);
			json.put("list", Collections.emptyList());
			return json;
		}
		Date timeDate = null;
		if (time != null) {
			timeDate = new Date(System.currentTimeMillis() - time);
		}
		Integer sex = null;
		if (gender != null) {
			sex = "M".equalsIgnoreCase(gender) ? User.SEX_M : User.SEX_F;
		}
		java.sql.Date birthStart = null, birthEnd = null;
		Long ayear = 1L * 365 * 24 * 60 * 60 * 1000;
		// 生日和年龄顺序是相反的
		if (ageEnd != null) {
			birthStart = new java.sql.Date(System.currentTimeMillis() - ageEnd * ayear);
		}
		if (ageStart != null) {
			birthEnd = new java.sql.Date(System.currentTimeMillis() - ageStart * ayear);
		}
		// if (birthStart == null || birthEnd == null) {
		// birthStart = birthEnd = null;
		// }
		List<Location> locList = locationService.getNearBy(uid, curLoc.getLon(), curLoc.getLat(), timeDate, sex,
				distance, birthStart, birthEnd, (currentPage - 1) * pageSize, pageSize);
		log.info("获取附近的人:" + JSON.toJSON(locList));
		json.put("c", 0);
		json.put("list", OldLocation.toOldLocationList(locList));
		return json;
	}

	private void cacheTracePoint(Location loc) {// 缓存足迹点
		Trace trace = TraceCache.get(loc.getUid());
		if (trace == null) {
			trace = new Trace(loc.getUid());
			trace.setDate(new java.sql.Date(loc.getTime().getTime()));
		}
		List<Location> points = trace.getPoints();
		if (points.size() > 0) {
			Location lastLoc = points.get(points.size() - 1);
			double dist = GeoUtil.getDistance(lastLoc, loc);
			if (dist > 10) {// 小于10m的点无效
				points.add(loc);
			}
		} else {
			points.add(loc);
		}

		TraceCache.put(trace);
		if (points.size() > 1) {
			int firstDate = VUtil.getDate(points.get(0).getTime());
			int lastDate = VUtil.getDate(points.get(points.size() - 1).getTime());
			// 如果隔天，那么就存入数据库一次
			if (firstDate != lastDate) {
				trace.getPoints().remove(points.size() - 1);
				TraceCache.removeAndSave(trace);
			}
		}
	}
}
