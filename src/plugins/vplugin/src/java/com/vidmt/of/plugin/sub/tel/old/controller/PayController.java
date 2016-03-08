package com.vidmt.of.plugin.sub.tel.old.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.extdb.Acc;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;
import com.vidmt.of.plugin.sub.tel.entity.Order;
import com.vidmt.of.plugin.sub.tel.entity.Order.OrderStatus;
import com.vidmt.of.plugin.sub.tel.entity.Order.PayType;
import com.vidmt.of.plugin.sub.tel.entity.Paylog;
import com.vidmt.of.plugin.sub.tel.entity.Paylog.PayEvent;
import com.vidmt.of.plugin.sub.tel.entity.SysLog;
import com.vidmt.of.plugin.sub.tel.entity.SysLog.Logtype;
import com.vidmt.of.plugin.sub.tel.old.pay.alipay.AlipayConfig;
import com.vidmt.of.plugin.sub.tel.old.pay.alipay.AlipayNotify;
import com.vidmt.of.plugin.sub.tel.old.pay.alipay.AlipayUtil;
import com.vidmt.of.plugin.sub.tel.old.pay.wxpay.WxPayConfig;
import com.vidmt.of.plugin.sub.tel.old.pay.wxpay.WxPayUtil;
import com.vidmt.of.plugin.sub.tel.old.service.LvlService;
import com.vidmt.of.plugin.sub.tel.old.service.PaylogService;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.sub.tel.pay.AliOrder;
import com.vidmt.of.plugin.sub.tel.pay.WxOrder;
import com.vidmt.of.plugin.sub.tel.service.OrderService;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.VUtil;
import com.vidmt.of.plugin.utils.XmlD4jUtil;

@Controller
@RequestMapping("/vplugin/api/1/pay")
public class PayController {
	private static final Logger log = LoggerFactory.getLogger(PayController.class);
	private static final String ALI_SUCCESS = "success";
	private static final String ALI_FAIL = "fail";
	private static final String WX_SUCCESS = "<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>";
	private static final String WX_FAIL = "<xml><return_code>FAIL</return_code><return_msg>NOT OK</return_msg></xml>";

	@Autowired
	private PaylogService paylogService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;

	@Autowired
	private LvlService lvlService;

	@ResponseBody
	@RequestMapping("/alipay/getPayInfo.*")
	public JSONObject getAlipayOrderInfo(String vipType, boolean debug, HttpServletRequest req,
			HttpServletResponse resp) {
		Long uid = (Long) req.getAttribute("uid");
		LvlType lvlType = LvlType.valueOf(vipType);
		Lvl lvl = lvlService.getLvlByType(lvlType);
		AliOrder order = new AliOrder();
		order.setId(Order.genId());
		order.setLvlType(lvlType);
		// order.setPayType(PayType.ALI);
		order.setStatus(OrderStatus.INIT);
		
		String prefix=String.format("%s%s", uid,order.getId());
		order.setSubject(Order.genSubject(lvl)+prefix);
		
		order.setTotalFee(debug ? 1 : lvl.getMoney());
		order.setUid(uid);
		order.setAttach(uid + "-" + lvlType.name());
		JSONObject payinfo = order.toPayinfo();
		List<NameValuePair> list = new ArrayList<>();
		for (Entry<String, Object> entry : payinfo.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
		}

		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("m", AlipayUtil.toParamString(list));
		log.info("ALI INFO:{}",json.toJSONString());
		return json;
	}

	@ResponseBody
	@RequestMapping("/wxpay/getPayInfo.*")
	public JSONObject getWxpayOrderInfo(final String vipType, boolean debug, HttpServletRequest req,
			HttpServletResponse resp) {
		Long uid = (Long) req.getAttribute("uid");
		LvlType lvlType = LvlType.valueOf(vipType);
		Lvl lvl = lvlService.getLvlByType(lvlType);
		WxOrder order = new WxOrder();
		order.setId(Order.genId());
		order.setLvlType(lvlType);
		order.setStatus(OrderStatus.INIT);
		// order.setPayType(PayType.WX);
		String prefix=String.format("%s%s", uid,order.getId());
		order.setSubject(Order.genSubject(lvl)+prefix);
		order.setTotalFee(debug ? 1 : lvl.getMoney());
		order.setUid(uid);
		order.setAttach(uid + "-" + lvlType.name());
		JSONObject json = order.toPayinfo();
		log.info("WX  INFO:{}",json.toJSONString());
		return json;
	}

	@ResponseBody
	@RequestMapping("/ali/notify.api")
	public String alipayNotify(String trade_no, String trade_status, String refund_status, HttpServletRequest req)
			throws IOException {
		// 获取支付宝POST过来反馈信息
		// 若未通知成功，一般情况下，25小时以内完成 8 次通知（通知的间隔频率一般是：2m,10m,10m,1h,2h,6h,15h）
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = req.getParameterMap();
		for (String name : requestParams.keySet()) {
			// String name = iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号
		// String out_trade_no = new
		// String(req.getParameter("out_trade_no").getBytes("ISO-8859-1"),
		// "UTF-8");
		log.info("ALI RCV:{}",JSON.toJSONString(params));

		if (AlipayNotify.verify(params)) {// 验证成功
			if (refund_status == null && !"TRADE_SUCCESS".equals(trade_status)) {
				if (!"WAIT_BUYER_PAY".equals(trade_status)) {
					log.info("支付宝通知无效,trade_no:{},refund_status:{}/trade_status:{}", trade_no, refund_status,
							trade_status);
				}
				return ALI_SUCCESS;
			}
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			// 商品描述信息
			String body = params.get("body");// 用户uid,vipType附在参数body里面
			Long uid = null;
			LvlType lvlType = null;
			// 交易金额
			String total_fee = params.get("total_fee");
			float moneyY = Float.parseFloat(total_fee);
			if (body.contains("#")) {// 兼容旧版本
				uid = Long.valueOf(body.substring(body.indexOf("#") + 1));
				if ((int) moneyY == 10) {
					lvlType = LvlType.TRY;
				} else if ((int) moneyY == 150) {
					lvlType = LvlType.YEAR;
				}
			} else {
				String[] barr = body.split("-");
				uid = Long.valueOf(barr[0]);
				lvlType = LvlType.valueOf(barr[1]);
			}
			String out_trade_no = params.get("out_trade_no");
			// 支付宝交易号
			// String trade_no = params.get("trade_no");
			// 买家支付宝账号
			String buyer_email = params.get("buyer_email");
			// 商品名称
			String subject = params.get("subject");
			// 交易付款时间
			String gmt_payment = params.get("gmt_payment");
			Date payTime = AlipayUtil.parseDate(gmt_payment);

			Order order = new Order();
			order.setId(out_trade_no);
			order.setUid(uid);
			order.setSubject(subject);
			order.setPayAcc(buyer_email);
			order.setTotalFee((int) moneyY * 100);
			order.setPayType(PayType.ALI);
			order.setTradeNo(trade_no);
			order.setPayTime(payTime);
			order.setStatus(OrderStatus.PAYED);
			order.setAttach(body);
			order.setLvlType(lvlType);
			if (refund_status != null) {
				if ("REFUND_SUCCESS".equals(refund_status)) {
					if ("TRADE_SUCCESS".equals(trade_status)) {
						VUtil.log(Logtype.REFUND, Acc.ADMIN_UID, uid, "全额退款成功");
						refundsuccess(order, JSON.toJSONString(params));
					} else if ("TRADE_CLOSED".equals(trade_status)) {
						VUtil.log(Logtype.REFUND, Acc.ADMIN_UID, uid, "非全额退款成功");
						refundsuccess(order, JSON.toJSONString(params));
					}
				} else {
					log.warn("支付宝通知无效,trade_no:{},refund_status:{}", trade_no, refund_status);
					VUtil.log(Logtype.ERROR, Acc.ADMIN_UID, uid, "支付宝通知无效,refund_status:" + refund_status);
				}
				return ALI_SUCCESS;
			}
			String sellerEmail = params.get("seller_email");
			if (!AlipayConfig.SELELR_ACCOUNT.equals(sellerEmail)) {// 服务器端验证
				log.warn("支付宝商户EMAIL错误:trade_no:{},{}", trade_no, sellerEmail);
				VUtil.log(Logtype.ERROR, uid, null, "支付宝商户EMAIL错误:" + sellerEmail);
				return ALI_SUCCESS;
			}
			if (!"TRADE_SUCCESS".equals(trade_status)) {
				log.info("支付宝通知无效,trade_no:{},trade_status:{}", trade_no, trade_status);
				return ALI_SUCCESS;
			}
			// 请在这里加上商户的业务逻辑程序代码
			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			if (trade_status.equals("TRADE_FINISHED")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				// 注意：
				// 该种交易状态只在两种情况下出现
				// 1、开通了普通即时到账，买家付款成功后。
				// 2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
				log.info("调试:TRADE_FINISHED:{}", JSON.toJSONString(params));
				return ALI_SUCCESS;// 此处因为之前已经处理，此处不会执行
			} else if (trade_status.equals("TRADE_SUCCESS")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				// 注意：
				// 该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
				Order oldOrder = orderService.load(out_trade_no);
				if (oldOrder == null) {
					paysuccess(order, JSON.toJSONString(params));
				} else {
					log.info("支付宝订单已存在!out_trade_no:{},trade_no:{}", out_trade_no, trade_no);
				}
				log.debug("alipay success!");
				return ALI_SUCCESS;
			}
			return ALI_SUCCESS;
			// ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
			// ////////////////////////////////////////////////////////////////////////////////////////
		} else {// 验证失败
			log.warn("支付宝验证失败!trade_no:{}", trade_no);
			VUtil.log(Logtype.ERROR, Acc.ADMIN_UID, null, "支付宝验证错误:" + JSON.toJSONString(params));
			return ALI_FAIL;
		}
	}

	@ResponseBody
	@RequestMapping("/wx/notify.api")
	public String wxpayNotify(HttpServletRequest req) {
		// 通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒(正确处理重复的通知)
		try {
			Document doc = XmlD4jUtil.getXmlDoc(new InputStreamReader(req.getInputStream(), "UTF-8"));
			Map<String, String> notifyMap = WxPayUtil.readXml(doc);
			log.info("WX  RCV:{}",JSON.toJSONString(notifyMap));
			
			if (!"SUCCESS".equals(notifyMap.get("return_code")) || !"SUCCESS".equals(notifyMap.get("result_code"))) {
				log.warn("微信支付通知无效：" + JSON.toJSONString(notifyMap));
				return WX_FAIL;
			}
			Long uid = null;
			LvlType lvlType = null;
			String attach = notifyMap.get("attach");
			int moneyY = Integer.parseInt(notifyMap.get("total_fee")) / 100;
			if (attach.contains("#")) {// 兼容旧版本
				uid = Long.valueOf(attach.split("#")[0]);
				if (moneyY == 10) {
					lvlType = LvlType.TRY;
				} else if (moneyY == 150) {
					lvlType = LvlType.YEAR;
				}
			} else {
				uid = Long.valueOf(attach.split("-")[0]);
				lvlType = LvlType.valueOf(attach.split("-")[1]);
			}
			String appId = notifyMap.get("appid");
			String mchId = notifyMap.get("mch_id");
			if (!WxPayConfig.WXPAY_APP_ID.equals(appId) || !WxPayConfig.WXPAY_MERCHANT_ID.equals(mchId)) {// 服务器端验证
				log.warn("微信支付通知失败,appid 或mch_id错误：" + JSON.toJSONString(notifyMap));
				VUtil.log(Logtype.ERROR, uid, null, "微信支付通知失败，appid 或mch_id错误" + appId + "/" + mchId);
				return WX_FAIL;
			}
			String outtradeNO = notifyMap.get("out_trade_no");
			String tradeNo = notifyMap.get("transaction_id");
			String openid = notifyMap.get("openid");
			Date payTime = WxPayUtil.parseDate(notifyMap.get("time_end"));
			String body = notifyMap.get("body");

			Order oldOrder = orderService.load(outtradeNO);
			if (oldOrder == null) {
				Order order = new Order();
				order.setId(outtradeNO);
				order.setUid(uid);
				order.setSubject(body);
				order.setPayAcc(openid);
				order.setTotalFee(moneyY * 100);
				order.setPayType(PayType.WX);
				order.setTradeNo(tradeNo);
				order.setPayTime(payTime);
				order.setStatus(OrderStatus.PAYED);
				order.setAttach(attach);
				order.setLvlType(lvlType);

				paysuccess(order, doc.asXML());
			} else {
				log.info("微信订单已存在!out_trade_no:{},trade_no:{}", outtradeNO, tradeNo);
			}
			log.debug("wxpay success!");
			return WX_SUCCESS;
		} catch (Throwable e) {
			log.error("微信支付失败未知原因:", e);
			VUtil.log(Logtype.ERROR, Acc.ADMIN_UID, null, "微信支付失败未知原因:" + CommUtil.fmtException(e));
			return WX_FAIL;
		}
	}

	private void paysuccess(Order order, String allparams) {
		SysLog log = new SysLog(Logtype.PAY, order.getUid());
		log.setTgtUid(order.getUid());
		log.setTime(order.getPayTime());
		log.setContent(
				String.format("[%s]购买会员[%s,%s]：", order.getUid(), order.getLvlType(), order.getTotalFee() / 100f));
		VUtil.log(log);

		orderService.save(order);
		Lvl lvl = lvlService.getLvlByType(order.getLvlType());

		userService.updateUserPayed(order.getUid(), lvl);

		Paylog paylog = new Paylog();
		paylog.setUid(order.getUid());
		paylog.setPayEvent(PayEvent.PAY);
		paylog.setPayType(order.getPayType());
		paylog.setPayAcc(order.getPayAcc());
		paylog.setPayTime(order.getPayTime());
		paylog.setTotalFee(order.getTotalFee());
		paylog.setTradeNo(order.getTradeNo());
		paylog.setContent(allparams);
		paylogService.save(paylog);
	}

	private void refundsuccess(Order order, String allparams) {
		orderService.updateStatus(OrderStatus.REFUND, order.getId());
		userService.updateUserExpired(order.getUid());
		Paylog paylog = new Paylog();
		paylog.setUid(order.getUid());
		paylog.setPayEvent(PayEvent.REFUND);
		paylog.setPayType(order.getPayType());
		paylog.setPayAcc(order.getPayAcc());
		paylog.setPayTime(order.getPayTime());
		paylog.setTotalFee(order.getTotalFee());
		paylog.setTradeNo(order.getTradeNo());
		paylog.setContent(allparams);
		paylogService.save(paylog);
	}
}
