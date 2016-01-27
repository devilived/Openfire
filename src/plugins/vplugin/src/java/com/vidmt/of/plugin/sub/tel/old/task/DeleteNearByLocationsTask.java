package com.vidmt.of.plugin.sub.tel.old.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.spring.StatisticsIntercepter;
import com.vidmt.of.plugin.sub.tel.old.service.LocationService;

@Service
@Transactional(readOnly = false)
public class DeleteNearByLocationsTask {
	private static Logger log = LoggerFactory.getLogger(StatisticsIntercepter.class);
	@Autowired
	private LocationService locSvc;

	/**
	 * 注意必须在spring-context.xml的default-lazy-init设定为false，否则，其由于没人引用，也就不会初始化，
	 * 因此也就不会自动执行
	 */
	@Scheduled(cron = "0 0 3 ? * WED") // 每周三的凌晨3点执行一次
	public void clearPoint() {
		long start = System.currentTimeMillis();
		doBackup();
		long end = System.currentTimeMillis();
		long timesec = (end - start) / 1000;
		if (timesec > 5) {
			log.warn("执行时间超过5秒,为{}秒：保存足迹saveTrace", timesec);
		} else {
			log.debug("执行时间{}秒:保存足迹saveTrace", timesec);
		}
	}

	private void doBackup() {
		Date time = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
		locSvc.deleteExpiredNearby(time);
		log.info("删除附近过期的点成功成功：" + time);
	}
}
