package com.vidmt.of.plugin.sub.tel.old.service;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.cache.SysCache;
import com.vidmt.of.plugin.sub.tel.old.dao.PhoneCityDao;
import com.vidmt.of.plugin.sub.tel.old.entity.PhoneCity;
import com.vidmt.of.plugin.sub.tel.old.utils.HttpUtil;

@Service
@Transactional(readOnly = false)
public class PhoneCityService extends CrudService<PhoneCityDao, PhoneCity> {
	private static final Logger log = LoggerFactory.getLogger(PhoneCityService.class);

	public PhoneCity getByPhone(String phone) {
		if (phone.length() < 11) {
			throw new IllegalArgumentException("最低11位");
		}
		String prefix = phone.substring(0, 7);
		// PhoneCity pc = SysCache.getPhoneCity(prefix);
		// if (pc == null) {
		PhoneCity pc = dao.getByPrefix(prefix);
		if (pc == null) {
			String url = "http://apis.baidu.com/apistore/mobilenumber/mobilenumber";
			HttpGet get = HttpUtil.buildGet(url, "phone", phone);
			get.addHeader("apikey", "508d58b79ff250781ad5180786936341");
			try {
				String resp = HttpUtil.httpStr(get);
				JSONObject json = JSON.parseObject(resp);
				if (json.getIntValue("errNum") == 0) {
					JSONObject data = json.getJSONObject("retData");
					pc = new PhoneCity();
					pc.setPrefix(data.getString("prefix"));
					pc.setProvince(data.getString("province"));
					pc.setCity(data.getString("city"));
					pc.setSupplier(data.getString("supplier"));
					pc.setSuit(data.getString("supplier"));
					super.save(pc);
				} else {
					log.error("获取号码所在地出错:{}", resp);
				}
			} catch (HttpException e) {
				log.error("获取号码所在地出错", e);
			}
		}
		// if (pc != null) {
		// SysCache.putPhonecity(pc);
		// }
		// }
		return pc;
	}
}
