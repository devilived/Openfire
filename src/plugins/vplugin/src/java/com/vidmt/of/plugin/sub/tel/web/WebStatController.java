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
import com.vidmt.of.plugin.sub.tel.old.utils.UserStatUtil;
import com.vidmt.of.plugin.sub.tel.old.utils.VerStatUtil;
import com.vidmt.of.plugin.utils.DateUtil;

@Controller
@ResponseBody
@RequestMapping("/vplugin/api/web")
public class WebStatController {
	private static final Logger log = LoggerFactory.getLogger(WebStatController.class);

	public static final String KEY_WEEK = "KEY_WEEK";
	public static final String KEY_SUM_MONTH = "KEY_SUM_MONTH";

	@Autowired
	private PaylogService paylogService;

	@RequestMapping("/sys/last10info.*")
	public JSONObject last10info() {
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", UserStatUtil.getLast10Stat());
		return json;
	}

	@RequestMapping("/sys/hourinfo.*")
	public JSONObject hourinfo() {
		JSONObject data = new JSONObject();
		data.put("reg", UserStatUtil.getHourRegStat());
		data.put("pay", UserStatUtil.getHourPayStat());
		data.put("money", UserStatUtil.getHourMoneyStat());

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", data);
		return json;
	}

	@RequestMapping("/sys/verinfo.*")
	public JSONObject verinfo() {
		JSONArray jarr = new JSONArray();
		Map<String, Date> map = VerStatUtil.getLastLoginMap();
		for (Entry<String, Date> entry : map.entrySet()) {
			JSONObject unit = new JSONObject();
			unit.put("client", entry.getKey());
			unit.put("lasttime", entry.getValue());
			jarr.add(unit);
		}
		JSONObject data = new JSONObject();
		Map<String, Integer> osCntMap = VerStatUtil.getOsCntMap();
		data.put("ios", osCntMap.get("i"));
		data.put("android", osCntMap.get("a"));
		data.put("lastlogin", jarr);

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", data);
		return json;
	}

	@SuppressWarnings("unused")
	private static class ADayInfo {
		public int alimoney;
		public int alicnt;
		public int wxmoney;
		public int wxcnt;
		public int iapmoney;
		public int iapcnt;

		public int yearcnt;
	}

	@RequestMapping("/sys/moneyinfo.*")
	public JSONObject moneyinfo() {
		ADayInfo[] weekdaySum = new ADayInfo[8];
		for (int i = 0; i < weekdaySum.length; i++) {
			weekdaySum[i] = new ADayInfo();
		}

		Calendar cld = Calendar.getInstance();
		Date now = cld.getTime();
		long aday = 24L * 60 * 60 * 1000;
		Date todayStart = DateUtil.getDateStart(now);
		long todayEnd = todayStart.getTime() + aday - 1;

		List<Paylog> paylist = paylogService.findLatest(new Date(todayEnd - 8 * aday));

		for (Paylog paylog : paylist) {
			long logtime = paylog.getPayTime().getTime();
			int days = (int) ((todayEnd - logtime) / aday);
			int totalfee = paylog.getTotalFee();
			if (totalfee == 15000 || totalfee == 158000) {
				weekdaySum[days].yearcnt += 1;
			}
			if (days < 8) {
				switch (paylog.getPayType()) {
				case ALI:
					weekdaySum[days].alimoney += totalfee;
					weekdaySum[days].alicnt += 1;
					break;
				case WX:
					weekdaySum[days].wxmoney += totalfee;
					weekdaySum[days].wxcnt += 1;
					break;
				case IAP:
					weekdaySum[days].iapmoney += totalfee;
					weekdaySum[days].iapcnt += 1;
					break;
				default:
					break;
				}
			}
		}

		cld.set(Calendar.DAY_OF_MONTH, 1);
		Integer sumMonth = paylogService.findTotalFee(DateUtil.getDateStart(cld.getTime()));
		if (sumMonth == null) {
			sumMonth = 0;
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
