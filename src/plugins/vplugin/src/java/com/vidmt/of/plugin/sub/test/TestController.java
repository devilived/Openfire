package com.vidmt.of.plugin.sub.test;

import javax.servlet.http.HttpServletRequest;

import org.jivesoftware.openfire.XMPPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.VPlugin;
import com.vidmt.of.plugin.spring.CacheUtil;
import com.vidmt.of.plugin.sub.iospush.PushInterceptor;
import com.vidmt.of.plugin.sub.iospush.PushPlugin;
import com.vidmt.of.plugin.sub.tel.cache.LocCache;
import com.vidmt.of.plugin.sub.tel.cache.TraceCache;
import com.vidmt.of.plugin.sub.tel.cache.UserCache;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.sub.tel.old.utils.VerStatUtil;
import com.vidmt.of.plugin.utils.CommUtil;

//@Controller
@RequestMapping("/vplugin/api/")
public class TestController {
	private static final Logger log = LoggerFactory.getLogger(TestController.class);
	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping("/test.*")
	public JSONObject testapi(HttpServletRequest req) {
		log.debug("测试");
		JSONObject json = new JSONObject();
		json.put("c", "0");
		json.put("m", "测试api url正常:" + req.getRequestURI());
		return json;
	}

	@ResponseBody
	@RequestMapping("/test/cache/clear.*")
	public JSONObject removeallcache() {
		CacheUtil.getCache("sysCache").removeAll();
		CacheUtil.getCache("userCache").removeAll();
		CacheUtil.getCache("locCache").removeAll();
		CacheUtil.getCache("traceCache").removeAll();

		return JSON.parseObject("{'c':0}");
	}

	@ResponseBody
	@RequestMapping("/test/cache/clear/{cachename}.*")
	public JSONObject removecache(@PathVariable String cachename) {
		CacheUtil.getCache(cachename + "Cache").removeAll();
		return JSON.parseObject("{'c':0}");
	}

	@ResponseBody
	@RequestMapping("/test/cache/clear/user/{uid}.*")
	public JSONObject removeUserCache(@PathVariable Long uid) {
		UserCache.remove(uid);
		return JSON.parseObject("{'c':0}");
	}

	@ResponseBody
	@RequestMapping("/test/cache/location.*")
	public JSONObject locCacheAll() {
		return locCache(null);
	}

	@ResponseBody
	@RequestMapping(value = { "/test/cache/location/{uid}.*" })
	public JSONObject locCache(@PathVariable Long uid) {
		JSONObject json = new JSONObject();
		json.put("c", "0");
		if (uid == null) {
			json.put("d", LocCache.getAll());
		} else {
			json.put("d", LocCache.get(uid));
		}
		return json;
	}

	@ResponseBody
	@RequestMapping("/test/cache/trace.*")
	public JSONObject traceCacheAll() {
		return traceCache(null);
	}

	@ResponseBody
	@RequestMapping(value = { "/test/cache/trace/{uid:}.*" })
	public JSONObject traceCache(@PathVariable Long uid) {
		JSONObject json = new JSONObject();
		json.put("c", "0");
		if (uid == null) {
			json.put("d", TraceCache.getAllWithStatistics());
		} else {
			json.put("d", TraceCache.get(uid));
		}
		return json;
	}

	@ResponseBody
	@RequestMapping("/test/push.json")
	public JSONObject tracePush(String token, String msg) {
		VPlugin plg = (VPlugin) XMPPServer.getInstance().getPluginManager().getPlugin(VPlugin.NAME);
		PushPlugin pushplg = plg.getSubPlugin(PushPlugin.class);
		PushInterceptor pic = new PushInterceptor(pushplg);
		JSONObject json = new JSONObject();
		try {
			pic.pns(token, msg, 0);
			json.put("c", 0);
			json.put("msg", "push OK");
			return json;
		} catch (Exception e) {
			json.put("c", 1);
			json.put("msg", "error" + CommUtil.fmtException(e));
			return json;
		}
	}
}
