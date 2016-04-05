package com.vidmt.of.plugin.sub.tel.old.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class UserStatUtil {
	private static Queue<RegItem> last10Stat = new ArrayBlockingQueue<>(10);
	private static int[] hourRegStat = new int[24];
	private static int[] hourPayStat = new int[24];
	private static int[] hourMoneyStat = new int[24];
	private static int today = -1;

	private static class RegItem {
		public Long uid;
		public String nick;
		public Date regtime;

		public RegItem(Long uid, String nick, Date regtime) {
			this.uid = uid;
			this.nick = nick;
			this.regtime = regtime;
		}
	}

	public static synchronized void putReg(long uid, String nick) {
		Calendar cld = Calendar.getInstance();
		RegItem item = new RegItem(uid, nick, cld.getTime());
		if (!last10Stat.offer(item)) {
			last10Stat.poll();
			last10Stat.offer(item);
		}
		int date = cld.get(Calendar.DAY_OF_MONTH);
		if (date != today) {
			Arrays.fill(hourRegStat, 0);
			Arrays.fill(hourPayStat, 0);
			Arrays.fill(hourMoneyStat, 0);
			today = date;
		}
		int hour = cld.get(Calendar.HOUR_OF_DAY);
		hourRegStat[hour] += 1;
	}

	public static synchronized void putPay(int money) {
		Calendar cld = Calendar.getInstance();
		int date = cld.get(Calendar.DAY_OF_MONTH);
		if (date != today) {
			Arrays.fill(hourRegStat, 0);
			Arrays.fill(hourPayStat, 0);
			Arrays.fill(hourMoneyStat, 0);
			today = date;
		}
		int hour = cld.get(Calendar.HOUR_OF_DAY);
		hourPayStat[hour] += 1;
		hourMoneyStat[hour] += money;
	}

	public static Queue<RegItem> getLast10Stat() {
		return last10Stat;
	}

	public static int[] getHourRegStat() {
		return hourRegStat;
	}

	public static int[] getHourPayStat() {
		return hourPayStat;
	}

	public static int[] getHourMoneyStat() {
		return hourMoneyStat;
	}

}
