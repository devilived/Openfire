package com.vidmt.of.plugin.sub.tel.old.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.exceptoins.CodeException;
import com.vidmt.of.plugin.sub.tel.old.entity.PhoneCity;
import com.vidmt.of.plugin.sub.tel.old.service.PhoneCityService;

@Controller
@RequestMapping("/vplugin/api/1/utils")
public class UtilController {
	@Autowired
	private PhoneCityService phonecityService;

	@ResponseBody
	@RequestMapping("/getPhoneCity.*")
	public JSONObject getLvl(String phone) {
		PhoneCity pc = phonecityService.getByPhone(phone);
		if (pc == null) {
			JSONObject json = new JSONObject();
			json.put("c", CodeException.ERR_UNKOWN);
			return json;
		}
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", pc);
		return json;
	}
}
