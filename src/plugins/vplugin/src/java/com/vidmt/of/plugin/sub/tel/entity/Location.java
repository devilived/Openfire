package com.vidmt.of.plugin.sub.tel.entity;

import java.util.Date;

import com.vidmt.of.plugin.abs.CrudEntity;
import com.vidmt.of.plugin.utils.VUtil;

public class Location extends CrudEntity {
	private static final long serialVersionUID = 1L;
	
	private Long uid;
	private Double lat;
	private Double lon;
	private Date time;

	private Double distance;

	public Location() {
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = VUtil.float6(lat);
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = VUtil.float6(lon);
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
}
