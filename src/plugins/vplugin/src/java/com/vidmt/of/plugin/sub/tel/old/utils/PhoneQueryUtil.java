package com.vidmt.of.plugin.sub.tel.old.utils;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.old.entity.PhoneCity;
import com.vidmt.of.plugin.utils.XmlD4jUtil;

public class PhoneQueryUtil {
	private static final Logger log = LoggerFactory.getLogger(PhoneQueryUtil.class);

	public static PhoneCity getFromBaidu(String phone) {
		String url = "http://apis.baidu.com/apistore/mobilenumber/mobilenumber";
		HttpGet get = HttpUtil.buildGet(url, "phone", phone);
		get.addHeader("apikey", "508d58b79ff250781ad5180786936341");
		try {
			String resp = HttpUtil.httpStr(get);
			JSONObject json = JSON.parseObject(resp);
			if (json.getIntValue("errNum") == 0) {
				JSONObject data = json.getJSONObject("retData");
				PhoneCity pc = new PhoneCity();
				pc.setPrefix(data.getString("prefix"));
				pc.setProvince(data.getString("province"));
				pc.setCity(data.getString("city"));
				pc.setSupplier(data.getString("supplier"));
				pc.setSuit(data.getString("suit"));
				pc.setFrom("baidu.com");
				return pc;
			} else {
				log.error("获取百度号码所在地出错:{}", resp);
				return null;
			}
		} catch (HttpException e) {
			log.error("获取百度号码所在地出错", e);
			return null;
		}
	}

	public static PhoneCity getFromTenpay(String phone) {
		String url = "http://life.tenpay.com/cgi-bin/mobile/MobileQueryAttribution.cgi";

		try {
			org.dom4j.Document doc = XmlD4jUtil.getXmlDoc(new URL(url + "?chgmobile=" + phone));
			org.dom4j.Element root = doc.getRootElement();
			if ("0".equals(root.elementText("retcode"))) {
				PhoneCity pc = new PhoneCity();
				pc.setPrefix(phone.substring(0, 7));
				pc.setProvince(root.elementText("province"));
				pc.setCity(root.elementText("city"));
				pc.setSupplier(root.elementText("supplier"));
				// pc.setSuit(data.getString("suit"));
				pc.setFrom("tenpay.com");
				if (pc.getCity() == null) {
					log.error("获取财付通号码所在地出错:{}", doc.asXML());
					return null;
				}
				return pc;
			} else {
				log.error("获取财付通号码所在地出错:{}", doc.asXML());
				return null;
			}
		} catch (Exception e) {
			log.error("获取财付通号码所在地出错", e);
			return null;
		}
	}

	public static void main(String[] args) {
		// PhoneCity pc = getFromBaidu("18610280167");
		PhoneCity pc = getFromTenpay("18610280167");

		System.out.println(pc);
	}

	public static PhoneCity getFromIp138(String phone) {
		String url = "http://www.ip138.com:8080/search.asp";
		try {
			// String resp = HttpUtil.getStr(url, "mobile",
			// phone,"action","mobile");
			Document doc = Jsoup.connect(url + "?mobile=" + phone + "&action=mobile")
					.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
					.get();
			Element el = doc.select("table").last();
			if (el != null) {
				PhoneCity pc = new PhoneCity();
				Element areatd = el.select("tr:eq(2) td:eq(1)").first();
				String rawarea = areatd.text().replace("\u00A0", " ").trim();
				System.out.println(rawarea);
				String[] arr = rawarea.split(" ");
				if (arr.length == 1) {
					pc.setProvince(arr[0]);
					pc.setCity(arr[0]);
				} else {
					pc.setProvince(arr[0]);
					pc.setCity(arr[1]);
				}

				Element supplierTd = el.select("tr:eq(3) td:eq(1)").first();
				String supplierTxt = supplierTd.text().trim();
				int idx = supplierTxt.length() - 4;
				pc.setSuit(supplierTxt.substring(0, idx));
				pc.setSupplier(supplierTxt.substring(idx));

				pc.setPrefix(phone.substring(0, 7));
				pc.setFrom("ip138.com");
				return pc;
			}
			return null;
		} catch (IOException e) {
			log.error("获取ip138号码所在地出错", e);
			return null;
		}
	}

}
