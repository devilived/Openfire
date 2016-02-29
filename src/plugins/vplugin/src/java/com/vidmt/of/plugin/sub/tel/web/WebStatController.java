package com.vidmt.of.plugin.sub.tel.web;

import java.util.Arrays;
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
import com.vidmt.of.plugin.utils.VerStatUtil;

@Controller
@RequestMapping("/vplugin/api/web")
public class WebStatController {
	private static final Logger log = LoggerFactory.getLogger(WebStatController.class);

	public static final String KEY_WEEK = "KEY_WEEK";
	public static final String KEY_SUM_MONTH = "KEY_SUM_MONTH";

	@Autowired
	private PaylogService paylogService;

	@ResponseBody
	@RequestMapping("/sys/verinfo.*")
	public JSONObject verinfo() {
		JSONArray jarr = new JSONArray();
		Map<String, Date> map = VerStatUtil.get();
		for (Entry<String, Date> entry : map.entrySet()) {
			JSONObject unit = new JSONObject();
			unit.put("client", entry.getKey());
			unit.put("lasttime", entry.getValue());
			jarr.add(unit);
		}

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", jarr);
		return json;
	}

	@SuppressWarnings("unused")
	private static class ADayInfo {
		public int alimoney;
		public int wxmoney;
	}

	@ResponseBody
	@RequestMapping("/sys/moneyinfo.*")
	public JSONObject moneyinfo() {
		// MoneyStatUtil.main(null);
		List<Paylog> paylist = paylogService.find1monthAsc();

		ADayInfo[] weekdaySum = new ADayInfo[7];
		for (int i = 0; i < weekdaySum.length; i++) {
			weekdaySum[i] = new ADayInfo();
		}
		int sumMonth = 0;

		Calendar cld = Calendar.getInstance();
		Date now = cld.getTime();
		long aday = 24L * 60 * 60 * 1000;
		long todayEnd =now.getTime()- now.getTime() % aday+aday;

		cld.set(Calendar.DAY_OF_MONTH, 1);
		long firstDayOfMonth = cld.getTime().getTime()-cld.getTime().getTime() % aday;

		for (Paylog paylog : paylist) {
			long logtime = paylog.getPayTime().getTime();
			int days = (int) ((todayEnd - logtime) / aday);

			if (days < 7) {
				switch (paylog.getPayType()) {
				case ALI:
					weekdaySum[days].alimoney += paylog.getTotalFee();
					break;
				case WX:
					weekdaySum[days].wxmoney += paylog.getTotalFee();
					break;
				default:
					break;
				}
			}

			if (logtime > firstDayOfMonth) {
				sumMonth += paylog.getTotalFee();
			}
		}
		JSONObject data = new JSONObject();
		data.put(KEY_WEEK, weekdaySum);
		data.put(KEY_SUM_MONTH, sumMonth);

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", data);
		return json;
	}
}
