package com.vidmt.of.plugin.sub.tel.pay;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.entity.Order;
import com.vidmt.of.plugin.sub.tel.old.pay.wxpay.WxHttps;
import com.vidmt.of.plugin.sub.tel.old.pay.wxpay.WxPayConfig;
import com.vidmt.of.plugin.sub.tel.old.pay.wxpay.WxPayUtil;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.VUtil;
import com.vidmt.of.plugin.utils.XmlD4jUtil;

public class WxOrder extends Order {
	private static final Logger log = LoggerFactory.getLogger(Order.class);

	private static final long serialVersionUID = 1L;
	private static final int TRY_CNT = 5;

	public WxOrder() {
		super();
		this.setPayType(PayType.WX);
	}

	@Override
	public JSONObject toPayinfo() {
		String prepayid = null;
		try {
			prepayid = getPrepayid();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		String nonceStr = CommUtil.randString(32);
		long timesec = System.currentTimeMillis() / 1000;
		String sign = genSign(nonceStr, prepayid, timesec);
		JSONObject json = new JSONObject();
		json.put("appId", WxPayConfig.WXPAY_APP_ID);
		json.put("partnerId", WxPayConfig.WXPAY_MERCHANT_ID);
		json.put("prepayId", prepayid);
		json.put("packageValue", "Sign=WXPay");
		json.put("nonceStr", nonceStr);
		json.put("timeStamp", timesec);
		json.put("sign", sign);
		return json;
	}

	private String getPrepayid() throws IOException {
		String nonceStr = CommUtil.randString(32);
		List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
		packageParams.add(new BasicNameValuePair("appid", WxPayConfig.WXPAY_APP_ID));
		// packageParams.add(new BasicNameValuePair("attach", uid + "#"
		// +
		// subject));// 注意参数列表按ascii顺序
		packageParams.add(new BasicNameValuePair("attach", this.attach));// 注意参数列表按ascii顺序
		packageParams.add(new BasicNameValuePair("body", subject));// 注:防止汉字导致的"签名错误"或支付页面乱码
		packageParams.add(new BasicNameValuePair("mch_id", WxPayConfig.WXPAY_MERCHANT_ID));
		packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
		packageParams.add(new BasicNameValuePair("notify_url", VUtil.getNotifyUrl(PayType.WX)));
		packageParams.add(new BasicNameValuePair("out_trade_no", this.id));
		packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
		packageParams.add(new BasicNameValuePair("total_fee", this.totalFee + ""));
		packageParams.add(new BasicNameValuePair("trade_type", "APP"));

		String sign = WxPayUtil.sign(packageParams);
		packageParams.add(new BasicNameValuePair("sign", sign));
		String xmlstring = WxPayUtil.toParamString(packageParams);

		String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		int trycnt = 0;
		while (trycnt < TRY_CNT) {
			byte[] buf = WxHttps.httpPost(url, xmlstring);
			if (buf == null) {
				throw new IOException("访问微信服务器失败");
			}
			String content = CommUtil.newString(buf, "utf-8");
			try {
				Map<String, String> xml = WxPayUtil.readXml(XmlD4jUtil.getXmlDoc(content));
				String retCode = xml.get("return_code");
				String resultCode = xml.get("result_code");
				if ("SUCCESS".equals(retCode) && "SUCCESS".equals(resultCode)) {
					return xml.get("prepay_id");
				} else {
					log.warn("获取prepay失败{}次[{}]", trycnt, xml);
					trycnt++;
				}
			} catch (DocumentException e) {
				log.error("获取微信XML错误:" + content, e);
				trycnt++;
			}
		}
		throw new IllegalArgumentException("cant get prepayid from WX after " + TRY_CNT + " times try");
	}

	private static String genSign(String nonceStr, String prepayId, long timesec) {
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", WxPayConfig.WXPAY_APP_ID));
		signParams.add(new BasicNameValuePair("noncestr", nonceStr));
		signParams.add(new BasicNameValuePair("package", "Sign=WXPay"));
		signParams.add(new BasicNameValuePair("partnerid", WxPayConfig.WXPAY_MERCHANT_ID));
		signParams.add(new BasicNameValuePair("prepayid", prepayId));
		signParams.add(new BasicNameValuePair("timestamp", timesec + ""));
		String sign = WxPayUtil.sign(signParams);
		return sign;
	}

}
