package com.vidmt.of.plugin.sub.tel.old.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.old.entity.OldLvl;
import com.vidmt.of.plugin.sub.tel.old.service.LvlService;

@Controller
@RequestMapping("/vplugin/api/1/lvl")
public class LvlController {
	@Autowired
	private LvlService lvlService;
	
	@ResponseBody
	@RequestMapping("/get.*")
	public JSONObject getLvl() {
		List<Lvl> lvls = lvlService.findAll();
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("list", OldLvl.fromLvlList(lvls));
		return json;
	}
}
