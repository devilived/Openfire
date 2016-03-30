package com.vidmt.of.plugin.sub.tel.old.utils;

import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.jetty.util.ArrayQueue;

import com.vidmt.of.plugin.sub.tel.entity.User;

public class UserStatUtil {
	private static Queue<UserItem> last10Stat = new ArrayBlockingQueue<>(10);
	private static int[] hourCntStat = new int[24];

	private static class UserItem {
		public Long uid;
		public String nick;
		public Date regtime;

		public UserItem(Long uid, String nick, Date regtime) {
			super();
			this.uid = uid;
			this.nick = nick;
			this.regtime = regtime;
		}

	}

	public static synchronized void put(User user) {
		Calendar cld = Calendar.getInstance();
		UserItem item = new UserItem(user.getId(), user.getNick(), cld.getTime());
		if (!last10Stat.offer(item)) {
			last10Stat.poll();
			last10Stat.offer(item);
		}
		int hour = cld.get(Calendar.HOUR_OF_DAY);
		hourCntStat[hour] += 1;
	}

	public static Queue<UserItem> getLast10Stat() {
		return last10Stat;
	}

	public static int[] getHourCntStat() {
		return hourCntStat;
	}
	
	public static void clear() {
		last10Stat.clear();
	}

}
