package com.vidmt.of.plugin.sub.tel.old.pay.alipay;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlipayUtil {
	private static final Logger log = LoggerFactory.getLogger(AlipayUtil.class);
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Date parseDate(String str) {
		try {
			return df.parse(str);
		} catch (ParseException e) {
			log.error("支付宝时间转换错误", e);
			return null;
		}
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public static String sign(List<NameValuePair> params) {
		return AliSignUtils.sign(toParamString(params), AlipayConfig.PRIVATE_KEY);
	}

	public static String toParamString(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append("\""+params.get(i).getValue()+"\"");
			sb.append('&');
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
