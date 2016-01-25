package com.vidmt.of.plugin.sub.tel.pay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.entity.Order;
import com.vidmt.of.plugin.sub.tel.old.pay.alipay.AlipayConfig;
import com.vidmt.of.plugin.sub.tel.old.pay.alipay.AlipayUtil;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.VUtil;

public class AliOrder extends Order {
	private static final Logger log = LoggerFactory.getLogger(Order.class);
	private static final long serialVersionUID = 1L;

	public AliOrder() {
		super();
		this.setPayType(PayType.ALI);
	}

	// TODO 阿里的RSA可能有问题
	@Override
	public JSONObject toPayinfo() {
		JSONObject json = new JSONObject(true);

		// 签约合作者身份ID
		json.put("partner", AlipayConfig.PARTNER);
		// 签约卖家支付宝账号
		json.put("seller_id", AlipayConfig.SELELR_ACCOUNT);
		// 商户网站唯一订单号
		json.put("out_trade_no", this.id);
		// 商品名称
		json.put("subject", this.subject);
		// 商品详情
		json.put("body", this.attach);
		// 商品金额
		json.put("total_fee", this.totalFee / 100f + "");
		// 服务器异步通知页面路径
		json.put("notify_url", VUtil.getNotifyUrl(PayType.ALI));
		// 服务接口名称， 固定值
		json.put("service", "mobile.securitypay.pay");
		// 支付类型， 固定值
		json.put("payment_type", "1");
		// 参数编码， 固定值
		json.put("_input_charset", "utf-8");

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		json.put("it_b_pay", "30m");
		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		json.put("&return_url","m.alipay.com");
		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		List<NameValuePair> list = new ArrayList<>();
		for (Entry<String, Object> entry : json.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
		}
		String sign = AlipayUtil.sign(list);
		// 仅需对sign 做URL编码
		sign = CommUtil.urlEnc(sign, "UTF-8");
		json.put("sign", sign);
		json.put("sign_type", "RSA");
		return json;
	}
}
