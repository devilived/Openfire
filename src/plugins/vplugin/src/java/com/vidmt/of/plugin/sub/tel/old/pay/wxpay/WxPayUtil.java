package com.vidmt.of.plugin.sub.tel.old.pay.wxpay;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.sub.tel.entity.Order;
import com.vidmt.of.plugin.sub.tel.old.utils.MD5;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.XmlD4jUtil;

public class WxPayUtil {
	private static final Logger log = LoggerFactory.getLogger(WxPayUtil.class);
	private static final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

	public static Date parseDate(String str) {
		try {
			return df.parse(str);
		} catch (ParseException e) {
			log.error("支付宝时间转换错误", e);
			return null;
		}
	}

	/**
	 * 生成签名
	 */
	public static String sign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(WxPayConfig.WXPAY_API_KEY);
		String appSign = MD5.getMessageDigest(sb.toString()).toUpperCase();
		return appSign;
	}

	public static String toParamString(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");
			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");
		return sb.toString();
	}

	public static Map<String, String> readXml(Document document) throws DocumentException {
		Map<String, String> map = new HashMap<String, String>();
		Element root = document.getRootElement();
		Iterator<Element> it = root.elementIterator();
		while (it.hasNext()) {
			Element e = it.next();
			map.put(e.getName(), e.getText());
		}
		return map;
	}
}
