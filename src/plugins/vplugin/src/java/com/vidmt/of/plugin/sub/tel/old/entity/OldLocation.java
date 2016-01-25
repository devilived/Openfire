package com.vidmt.of.plugin.sub.tel.old.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vidmt.of.plugin.sub.tel.entity.Location;

public class OldLocation {
	private String uid;
	private Double lat;
	private Double lon;
	private Long time;

	private Double distance;

	public OldLocation() {
	}

	public OldLocation(Location loc) {
		this.lat = loc.getLat();
		this.lon = loc.getLon();
		this.uid = loc.getUid().toString();
		this.time = loc.getTime().getTime();
		this.distance = loc.getDistance();
	}

	public Location toLocation() {
		Location loc = new Location();
		if (this.uid != null) {
			loc.setUid(Long.valueOf(this.uid));
		}
		loc.setLon(this.getLon());
		loc.setLat(this.getLat());
		if (this.time != null) {
			loc.setTime(new Date(this.time));
		}
		loc.setDistance(this.getDistance());
		return loc;
	}

	public static List<Location> toLocationList(Collection<OldLocation> oldLocList) {
		List<Location> list = new ArrayList<>(oldLocList.size());
		for (OldLocation oldLoc : oldLocList) {
			list.add(oldLoc.toLocation());
		}
		return list;
	}

	public static List<OldLocation> toOldLocationList(Collection<Location> locList) {
		List<OldLocation> list = new ArrayList<>(locList.size());
		for (Location loc : locList) {
			list.add(new OldLocation(loc));
		}
		return list;
	}

	public OldLocation(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public OldLocation(double lat, double lon, long time) {
		this.lat = lat;
		this.lon = lon;
		this.time = time;
	}

	@Override
	public String toString() {
		return "Location [uid=" + uid + ", lat=" + lat + ", lon=" + lon + ", time=" + time + ", distance=" + distance
				+ "]";
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

}
