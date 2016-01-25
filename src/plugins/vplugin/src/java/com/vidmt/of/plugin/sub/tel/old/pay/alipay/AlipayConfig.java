package com.vidmt.of.plugin.sub.tel.old.pay.alipay;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static final String PARTNER = "2088611906393954";
	// 商户的私钥
	public static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMWpAmjMoJmM17Td7BIcv5BOPXP2lxaOk93RIBgF3l9MMpQf81XowWXbchXtZhmcVv1J+mM7vBsToJkMwOLmiNy1D6QDEFW0tN4HaTNxCb6R0rethCbofsOljxOMbSANDLEo8+mAvqxIW2KXXaCYMizsFqnDMoVMz0O45PMBgabPAgMBAAECgYEAvfjuQ+NIB8yCFWel2NKgmwVXsvixxhBKtcqk2fHqBBWsFwmOSmdmWYtMXx7IHp1QTiin6nAjHrMCi5biXDHOHv8XxlLVe8vtSotPLRIOPm+rZ9yiEuI3DposLpfBdPet0qtoskIb95TND5K9XPaoEUxc3HmnMXcY3gka+tzWWoECQQD7n6n4tER7Jn5xoAgIsjlvC2H+h5nFBnbxhzy8DxtnN9sZPIxOwmueWpk8jL2KunoWFnMsq6E7mWVvKX/HuayRAkEAyRkTsyQqMKBonyBrcGym+axduwqZcaiUbKaaPYiC3O52yjx0MAaFKxC3ia5oKlX/dCgzw+sBZuRVo62uNQNNXwJAfo/8qn/hwh/Gkdhwsg8THGuZSrFiAtwRj8L6JY1jtzM9HSB7YlIgV3IyiWYxIEpThjCcleduHdA2WQeCoi8eQQJAV/z77J7/QsV84VsrB0bTDF1JqaQqnVt4jc2boR7Qu2AvY69t2vGPZNdspvZDp3p3SlioxalWtLFlczd0CMgIGwJBAJMATCm2nqYHR9tO6Du9/b+GBw87n2jdlYoCjSVSDa/JLCWs40sv4cPSl7OIiQi0ryJlWLcojYUj1qv1Gbq0yh4=";

	// 支付宝的公钥，无需修改该值
	public static final String ALI_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	public static final String SELELR_ACCOUNT = "chonghuawei@vidmt.com";

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static final String input_charset = "utf-8";

	// 签名方式 不需修改
	public static final String sign_type = "RSA";
}
