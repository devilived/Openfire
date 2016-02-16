package com.vidmt.of.plugin.sub.tel.old.task;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.spring.StatisticsIntercepter;
import com.vidmt.of.plugin.sub.tel.cache.TraceCache;
import com.vidmt.of.plugin.sub.tel.entity.Trace;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.DateUtil;

@Service
@Transactional(readOnly = true)
public class SaveCacheTask {
	private static Logger log = LoggerFactory.getLogger(StatisticsIntercepter.class);
	private Integer lastUpdateDate;

	/**
	 * 注意必须在spring-context.xml的default-lazy-init设定为false，否则，其由于没人引用，也就不会初始化，
	 * 因此也就不会自动执行
	 */
	// @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行一次
	// public void saveTrace() {
	// long start = System.currentTimeMillis();
	// doSaveTrace();
	// long end = System.currentTimeMillis();
	// long timesec = (end - start) / 1000;
	// if (timesec > 5) {
	// log.warn("执行时间超过5秒,为{}秒：保存足迹saveTrace", timesec);
	// } else {
	// log.debug("执行时间{}秒:保存足迹saveTrace", timesec);
	// }
	// }

	private void doSaveTraceWithTime() {
		Date now = new Date();
		int date = DateUtil.getDate(now);
		if (lastUpdateDate == null) {
			lastUpdateDate = date;// 第一天什么也不执行
		} else {
			synchronized (lastUpdateDate) {
				if (date != lastUpdateDate) {
					doSaveTrace();
					lastUpdateDate = date;
				}
			}
		}
	}

	@Transactional(readOnly = false)
	private void doSaveTrace() {
		@SuppressWarnings("deprecation")
		List<Trace> traces = TraceCache.getAll();
		for (Trace trace : traces) {
			synchronized (trace) {
				TraceCache.removeAndSave(trace);
			}
		}
		log.info("保存足迹成功：" + CommUtil.fmtDate(new Date()));
	}
}
