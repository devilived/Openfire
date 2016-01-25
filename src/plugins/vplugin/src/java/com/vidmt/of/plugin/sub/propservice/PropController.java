package com.vidmt.of.plugin.sub.propservice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

//@Controller
@RequestMapping("/vplugin")
public class PropController {
	private static final Logger log = LoggerFactory.getLogger(PropController.class);

	@ResponseBody
	@RequestMapping({ "/propservice.json", "/propservice.xml" })
	public JSONObject getPropservice(String[] propNames, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObj = new JSONObject(propNames.length);

		for (String propName : propNames) {
			String propValue = JiveGlobals.getProperty(propName);
			if (propValue != null) {
				jObj.put(propName, propValue);
			}
		}
		return jObj;
	}
}