package com.vidmt.of.plugin.sub.tel.old.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.cache.TraceCache;
import com.vidmt.of.plugin.sub.tel.entity.Trace;
import com.vidmt.of.plugin.sub.tel.old.entity.OldTrace;
import com.vidmt.of.plugin.sub.tel.old.service.TraceService;

@Controller
@RequestMapping("/vplugin/api/1/trace")
public class TraceController {
	@Autowired
	private TraceService traceService;

	@ResponseBody
	@RequestMapping("/get.*")
	public JSONObject getTrace(final OldTrace trace, HttpServletRequest req, HttpServletResponse resp) {
		Trace tr = trace.toTrace().get(0);
		List<Trace> trlist = traceService.findList(tr);
		Trace cachTrace = TraceCache.get(tr.getUid());
		// 注意，java.sql.date不能直接比较是否相等，要么转化后再比较，要么比较字符串
		long aday = 24 * 60 * 60 * 1000;
		if (tr.getDate() == null
				|| (cachTrace != null && (tr.getDate().getTime() - cachTrace.getDate().getTime()) / aday == 0)) {
			trlist.add(cachTrace);
		}
		OldTrace oldtrace = new OldTrace(trlist);
		return (JSONObject) JSON.toJSON(oldtrace);
	}
}
