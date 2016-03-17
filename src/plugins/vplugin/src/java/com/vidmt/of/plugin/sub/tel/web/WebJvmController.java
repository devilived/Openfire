package com.vidmt.of.plugin.sub.tel.web;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.spring.CacheUtil;
import com.vidmt.of.plugin.utils.CommUtil;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Statistics;

@Controller
@ResponseBody
@RequestMapping("/vplugin/api/web")
public class WebJvmController {
	private static final Logger log = LoggerFactory.getLogger(WebJvmController.class);

	@Deprecated
	@RequestMapping("/sys/cache/clear.*")
	public JSONObject clearcache() {
		String[] names = { "userCache", "locCache", "traceCache", };
		for (String cname : names) {
			CacheUtil.getCache(cname).removeAll();
		}
		JSONObject json = new JSONObject();
		json.put("c", 0);
		return json;
	}

	@RequestMapping("/sys/cache/list.*")
	public JSONObject listcache() {
		JSONArray data = new JSONArray();
		String[] names = { "userCache", "locCache", "traceCache", };
		for (String cname : names) {
			JSONObject itemJson = new JSONObject();
			data.add(itemJson);
			itemJson.put("name", cname);
			Cache cache = CacheUtil.getCache(cname);
			itemJson.put("avgGetTime", cache.getAverageGetTime());
			itemJson.put("avgSearchTime", cache.getAverageSearchTime());
			itemJson.put("size", cache.getSize());
			itemJson.put("memSize", cache.getMemoryStoreSize());
			itemJson.put("diskSize", cache.getDiskStoreSize());
			itemJson.put("offheapSize", cache.getOffHeapStoreSize());

			itemJson.put("calMemSize", cache.calculateInMemorySize());
			itemJson.put("calDiskSize", cache.calculateOnDiskSize());
			itemJson.put("calOffheapSize", cache.calculateOffHeapSize());

			Statistics statics = cache.getStatistics();
			itemJson.put("cacheHits", statics.getCacheHits());
			itemJson.put("cacheMis", statics.getCacheMisses());
			itemJson.put("objCnt", statics.getObjectCount());
			itemJson.put("diskHits", statics.getOnDiskMisses());
			itemJson.put("diskMis", statics.getOnDiskHits());
			itemJson.put("diskObjCnt", statics.getDiskStoreObjectCount());
		}

		// CacheUtil.getCache("sysCache").removeAll();

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", data);
		return json;
	}

	@RequestMapping("/sys/jvm.*")
	public JSONObject jvm() {
		JSONObject data = new JSONObject();
		MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();

		MemoryUsage heap = mxbean.getHeapMemoryUsage();
		data.put("heap", heap);

		MemoryUsage nonheap = mxbean.getNonHeapMemoryUsage();
		data.put("nonheap", nonheap);

		List<MemoryPoolMXBean> poollist = ManagementFactory.getMemoryPoolMXBeans();
		JSONArray poolarr = new JSONArray();
		data.put("pools", poolarr);
		for (MemoryPoolMXBean pool : poollist) {
			JSONObject pooljson = new JSONObject();
			MemoryUsage peadusg = pool.getPeakUsage();
			pooljson.put("peakusg", peadusg);
			pooljson.put("type", pool.getType());
			pooljson.put("name", pool.getName());

			// pooljson.put("threashold", pool.getUsageThreshold());
			// pooljson.put("threasholdcnt", pool.getUsageThresholdCount());
			poolarr.add(pooljson);
		}

		ClassLoadingMXBean cllbean = ManagementFactory.getClassLoadingMXBean();
		JSONObject cllJson = new JSONObject();
		data.put("cll", cllJson);
		cllJson.put("loadedcnt", cllbean.getLoadedClassCount());
		cllJson.put("unloadedcnt", cllbean.getUnloadedClassCount());
		cllJson.put("totalcnt", cllbean.getTotalLoadedClassCount());

		RuntimeMXBean runtimebean = ManagementFactory.getRuntimeMXBean();
		JSONObject runtimeJson = new JSONObject();
		data.put("runtime", runtimeJson);
		runtimeJson.put("name", runtimebean.getName());
		runtimeJson.put("specname", runtimebean.getSpecName());
		runtimeJson.put("vmname", runtimebean.getVmName());
		runtimeJson.put("starttime", CommUtil.fmtDate(new Date(runtimebean.getStartTime())));
		// runtimeJson.put("libpath", runtimebean.getLibraryPath());
		// runtimeJson.put("clzpath", runtimebean.getClassPath());
		runtimeJson.put("cpus", ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());

		ThreadMXBean threadbean = ManagementFactory.getThreadMXBean();
		JSONObject threadJson = new JSONObject();
		data.put("thread", threadJson);
		threadJson.put("daemoncnt", threadbean.getDaemonThreadCount());
		threadJson.put("peakcnt", threadbean.getPeakThreadCount());
		threadJson.put("totalcnt", threadbean.getTotalStartedThreadCount());

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", data);
		return json;
	}
}
