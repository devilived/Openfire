package com.vidmt.of.plugin.sub.tel.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.entity.Paylog;
import com.vidmt.of.plugin.sub.tel.old.service.PaylogService;
import com.vidmt.of.plugin.sub.tel.old.utils.VerStatUtil;
import com.vidmt.of.plugin.utils.DateUtil;

@Controller
@ResponseBody
@RequestMapping("/vplugin/api/web/paylog")
public class WebPaylogController {
	private static final Logger log = LoggerFactory.getLogger(WebPaylogController.class);

	public static final String KEY_WEEK = "KEY_WEEK";
	public static final String KEY_SUM_MONTH = "KEY_SUM_MONTH";

	@Autowired
	private PaylogService paylogService;

	@RequestMapping("/list.*")
	public JSONObject moneyinfo(String tradeno) {
		Paylog paylog = paylogService.findByTradeno(tradeno);

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", paylog);
		return json;
	}
}
