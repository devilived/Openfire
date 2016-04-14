package com.vidmt.of.plugin.sub.tel.old.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.old.dao.PhoneCityDao;
import com.vidmt.of.plugin.sub.tel.old.entity.PhoneCity;
import com.vidmt.of.plugin.sub.tel.old.utils.PhoneQueryUtil;

@Service
@Transactional(readOnly = false)
public class PhoneCityService extends CrudService<PhoneCityDao, PhoneCity> {
	private static final Logger log = LoggerFactory.getLogger(PhoneCityService.class);

	public PhoneCity getByPhone(String phone) {
		phone = phone.trim();
		if (phone.length() < 11) {
			throw new IllegalArgumentException("最低11位");
		}
		String prefix = phone.substring(0, 7);
		// 先查数据库
		PhoneCity pc = dao.getByPrefix(prefix);
		if (pc == null) {
			// 再查百度
			pc = PhoneQueryUtil.getFromBaidu(phone);
			if (pc == null) {
				// 再查财付通
				pc = PhoneQueryUtil.getFromTenpay(phone);
			}
			if (pc == null) {
				// 再查138
				pc = PhoneQueryUtil.getFromIp138(phone);
			}

			if (pc != null) {
				super.save(pc);
			}
		}

		// if (pc != null) {
		// SysCache.putPhonecity(pc);
		// }
		// }
		return pc;
	}
}
