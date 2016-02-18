package com.vidmt.of.plugin.sub.tel.web;

import static com.vidmt.of.plugin.sub.tel.TelPlugin.KEY_PAY_NOTIFY_URL;
import static com.vidmt.of.plugin.sub.tel.TelPlugin.KEY_RES_PATH;
import static com.vidmt.of.plugin.sub.tel.TelPlugin.KEY_XMPP_CLIENT_IDLE;

import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.misc.KV;

@ResponseBody
@Controller
@RequestMapping("/vplugin/api/web/setting")
public class WebSettingController {
	private static final Logger log = LoggerFactory.getLogger(WebSettingController.class);

	@RequestMapping("/list.*")
	public JSONObject list() {
		JSONArray jarr = new JSONArray();
		String[] keys = { KEY_RES_PATH, KEY_PAY_NOTIFY_URL, KEY_XMPP_CLIENT_IDLE };
		for (String key : keys) {
			String value = JiveGlobals.getProperty(key);
			jarr.add(new KV(key, value));
		}

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", jarr);
		return json;
	}

	@RequestMapping("/update.*")
	public JSONObject update(String key, String value) {
		JiveGlobals.setProperty(key, value.trim());

		JSONObject json = new JSONObject();
		json.put("c", 0);
		return json;
	}

	@RequestMapping("/delete.*")
	public JSONObject delete(String key) {
		JiveGlobals.deleteProperty(key);

		JSONObject json = new JSONObject();
		json.put("c", 0);
		return json;
	}
}
