package com.vidmt.of.plugin.sub.tel.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.vidmt.of.plugin.abs.CrudEntity;
import com.vidmt.of.plugin.utils.CommUtil;

public class Trace extends CrudEntity {
	private static final long serialVersionUID = 1L;

	private Long uid;
	private Date date;
	@JSONField(serialize = false)
	private List<Location> points = new ArrayList<>();

	public Trace() {
	}

	public Trace(Long uid) {
		this.uid = uid;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPointsJson() {
		if (this.points.size() == 0) {
			return "";
		}
		// 9+10+10+2+1
		JSONArray jarr = new JSONArray();
		StringBuilder sb = new StringBuilder(32);
		for (Location loc : this.points) {
			sb.append(loc.getLat()).append("|").append(loc.getLon()).append("|").append(loc.getTime().getTime() / 1000);
			jarr.add(sb.toString());
			sb.setLength(0);
		}
		return jarr.toJSONString();
	}

	public void setPointsJson(String json) {
		JSONArray jarr = JSON.parseArray(json);
		for (int i = 0; i < jarr.size(); i++) {
			String[] locStr = jarr.getString(i).split("\\|");
			Location loc = new Location();
			loc.setLat(Double.valueOf(locStr[0]));
			loc.setLon(Double.valueOf(locStr[1]));
			loc.setTime(new Date(Long.parseLong(locStr[2]) * 1000));
			this.points.add(loc);
		}
	}

	public List<Location> getPoints() {
		return points;
	}

	public void setPoints(List<Location> points) {
		this.points = points;
	}

	@JSONField(serialize = false)
	public String getHash() {
		if (this.uid != null && this.points != null && points.size() > 0) {
			java.util.Date startTime = points.get(0).getTime();
			String raw = uid + CommUtil.fmtDate(startTime);
			String md5 = CommUtil.md5(CommUtil.getStrByte(raw, "UTF-8"));
			return md5;
		}
		return null;
	}
}