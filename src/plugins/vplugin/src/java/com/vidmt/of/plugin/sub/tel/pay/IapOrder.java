package com.vidmt.of.plugin.sub.tel.pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.tel.entity.Order;
import com.vidmt.of.plugin.utils.VUtil;

public class IapOrder extends Order {
	private static final Logger log = LoggerFactory.getLogger(Order.class);

	private static final long serialVersionUID = 1L;

	public IapOrder() {
		super();
		this.setPayType(PayType.IAP);
	}

	@Override
	public JSONObject toPayinfo() {
		JSONObject json = new JSONObject();
		json.put("notify_url", VUtil.getNotifyUrl(PayType.IAP));
		json.put("out_trade_no", this.id);
		return json;
	}
}
