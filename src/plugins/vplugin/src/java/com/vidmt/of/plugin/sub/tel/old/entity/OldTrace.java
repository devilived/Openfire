package com.vidmt.of.plugin.sub.tel.old.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.entity.Trace;
import com.vidmt.of.plugin.utils.CommUtil;

public class OldTrace {
	private static final Logger log = LoggerFactory.getLogger(OldTrace.class);
	private String uid;
	private String dateStr;
	private String traceJson;

	private List<OldLocation> locations;

	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	public OldTrace() {
	}

	public OldTrace(List<Trace> tracelist) {
		if (!CommUtil.isEmpty(tracelist)) {
			Trace trace = tracelist.get(0);
			this.uid = trace.getUid().toString();
			this.dateStr = df.format(trace.getDate());
			JSONArray jarr = new JSONArray();
			for (Trace tr : tracelist) {
				jarr.addAll(tr.getPoints());
				jarr.add(new OldLocation(-1, -1));
			}
//			jarr.remove(jarr.size() - 1);
			JSONObject json = new JSONObject();
			json.put("list", jarr);
			this.traceJson = json.toJSONString();
		}
	}

	public List<Trace> toTrace() {
		List<Trace> tracelist = new ArrayList<>();
		if (!CommUtil.isEmpty(this.locations)) {
			Trace trace = null;
			for (OldLocation loc : locations) {
				if (trace != null && loc.getLon() == -1 && loc.getLat() == -1) {
					tracelist.add(trace);
					continue;
				}
				trace = new Trace();
				trace.setUid(Long.valueOf(this.getUid()));
				if (dateStr != null) {
					try {
						trace.setDate(new java.sql.Date(df.parse(this.dateStr).getTime()));
					} catch (ParseException e) {
						log.error("转换足迹日期错误", e);
					}
				}
				trace.getPoints().add(loc.toLocation());
			}
		} else {
			Trace trace = new Trace();
			trace.setUid(Long.valueOf(this.getUid()));
			if (dateStr != null) {
				try {
					trace.setDate(new java.sql.Date(df.parse(this.dateStr).getTime()));
				} catch (ParseException e) {
					log.error("转换足迹日期错误", e);
				}
			}
			tracelist.add(trace);
		}
		return tracelist;
	}

	@Override
	public String toString() {
		return "Trace [uid=" + uid + ", dateStr=" + dateStr + ", traceJson=" + traceJson + ", locations=" + locations
				+ "]";
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getTraceJson() {
		return traceJson;
	}

	public void setTraceJson(String data) {
		this.traceJson = data;
	}

	public List<OldLocation> getLocations() {
		return locations;
	}

	public void setLocations(List<OldLocation> locations) {
		this.locations = locations;
	}

}