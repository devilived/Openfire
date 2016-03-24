package com.vidmt.of.plugin.sub.tel.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.entity.Paylog;
import com.vidmt.of.plugin.sub.tel.old.service.PaylogService;

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
	public JSONObject moneyinfo(String tno, String phone) {
		List<Paylog> data;
		if (tno != null) {
			data = new ArrayList<>(1);
			data.add(paylogService.findByTradeno(tno));
		} else if (phone != null) {
			data = paylogService.findByPhone(phone);
		} else {
			data = null;
		}
		
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", data);
		return json;
	}
}
