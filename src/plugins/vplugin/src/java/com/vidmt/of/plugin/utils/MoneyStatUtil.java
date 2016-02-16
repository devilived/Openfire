package com.vidmt.of.plugin.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.alibaba.fastjson.JSON;
import com.vidmt.of.plugin.sub.tel.entity.Order.PayType;

public class MoneyStatUtil {
	public static void main(String[] args) {
		long ADAY = 24 * 60 * 60 * 1000;
		Date now = new Date();
		put(PayType.ALI, now, 1);
		System.out.println("第一次        ：" + JSON.toJSON(get()));
		put(PayType.ALI, new Date(now.getTime() + 111), 1);
		System.out.println("同一天ALI：" + JSON.toJSON(get()));
		put(PayType.WX, new Date(now.getTime() + 111), 1);
		System.out.println("同一天WX ：" + JSON.toJSON(get()));

		put(PayType.ALI, new Date(now.getTime() + ADAY), 1);
		System.out.println("差一天ALI：" + JSON.toJSON(get()));
		put(PayType.WX, new Date(now.getTime() + ADAY), 1);
		System.out.println("差一天WX ：" + JSON.toJSON(get()));

		put(PayType.ALI, new Date(now.getTime() + ADAY * 30), 1);
		System.out.println("差一月ALI：" + JSON.toJSON(get()));
		put(PayType.WX, new Date(now.getTime() + ADAY * 30), 1);
		System.out.println("差一月WX ：" + JSON.toJSON(get()));

		put(PayType.ALI, new Date(now.getTime() + ADAY * 30 + 111), 1);
		System.out.println("同一天ALI：" + JSON.toJSON(get()));
		put(PayType.WX, new Date(now.getTime() + ADAY * 30 + 111), 1);
		System.out.println("同一天WX ：" + JSON.toJSON(get()));

	}

	private static class ADayInfo {
		public Date time;
		public int alimoney;
		public int wxmoney;

		public ADayInfo(Date time, PayType paytype, int money) {
			this.time = time;
			if (paytype == PayType.ALI) {
				this.alimoney = money;
			} else if (paytype == PayType.WX) {
				this.wxmoney = money;
			}
		}
	}

	private static final Map<String, Object> map = new HashMap<>();
	public static final String KEY_START_TIME = "KEY_START_TIME";
	public static final String KEY_STACK = "KEY_STACK";
	public static final String KEY_SUM_MONTH = "KEY_SUM_MONTH";
	public static final String KEY_SUM_LASTMONTH="KEY_SUM_LASTMONTH";

	public static synchronized void put(PayType paytype, Date time, int money) {
		Object starttime = map.get(KEY_START_TIME);
		if (starttime == null) {
			starttime = time;
		}

		ADayInfo dayinfo = new ADayInfo(time, paytype, money);

		Stack<ADayInfo> stack = (Stack<ADayInfo>) map.get(KEY_STACK);
		if (stack == null) {
			stack = new Stack<>();
			map.put(KEY_STACK, stack);
		} else if (stack.size() == 7) {
			stack.pop();
		}
		Date lastTime = null;
		if (stack.isEmpty()) {
			stack.push(dayinfo);
		} else {
			ADayInfo lastItem = stack.get(stack.size() - 1);
			lastTime = lastItem.time;
			// 如果同一天，需要相加
			if (DateUtil.sameDay(time, lastTime)) {
				lastItem.time = time;
				if (paytype == PayType.ALI) {
					lastItem.alimoney += money;
				} else if (paytype == PayType.WX) {
					lastItem.wxmoney += money;
				}
			} else {
				stack.push(dayinfo);
			}
		}

		if (lastTime == null || !DateUtil.sameMonth(time, lastTime)) {
			int thismonth=(int) map.getOrDefault(KEY_SUM_MONTH,0);
			map.put(KEY_SUM_LASTMONTH, thismonth);
			
			map.put(KEY_SUM_MONTH, money);
		} else {
			// 如果是同一个月，就相加
			int monthsum = (int) map.get(KEY_SUM_MONTH);
			monthsum += money;
			map.put(KEY_SUM_MONTH, monthsum);
		}
	}

	public static Map<String, Object> get() {
		return map;
	}

}
