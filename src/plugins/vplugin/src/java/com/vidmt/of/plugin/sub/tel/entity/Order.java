package com.vidmt.of.plugin.sub.tel.entity;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.abs.CrudEntity;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.VUtil;

public class Order extends CrudEntity {
	private static final long serialVersionUID = 1L;

	public static String genId() {
		return VUtil.time14() + CommUtil.randString(18);
	}

	public static String genSubject(Lvl lvl) {
		String subject = "手机号定位:";
		if (lvl.getName() == LvlType.YEAR) {
			subject += "年会员";
		} else if (lvl.getName() == LvlType.TRY) {
			subject += "临时会员(" + lvl.getDuring() / (24 * 60 * 60) + "天)";
		}
		return subject;
	}

	public static enum OrderStatus {
		INIT, PAYED, REFUND, CLOSED
	}

	protected String id;
	protected Long uid;
	protected LvlType lvlType;
	protected String subject;
	protected String attach;
	protected Integer totalFee;
	protected PayType payType;
	protected String tradeNo;
	protected String payAcc;
	// TODO NOT USED
	protected OrderStatus status;
	protected Date payTime;
	protected Date createTime;
	protected Date modifyTime;

	public JSONObject toPayinfo() {
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public LvlType getLvlType() {
		return lvlType;
	}

	public void setLvlType(LvlType lvlType) {
		this.lvlType = lvlType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}

	public PayType getPayType() {
		return payType;
	}

	public void setPayType(PayType payType) {
		this.payType = payType;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getPayAcc() {
		return payAcc;
	}

	public void setPayAcc(String payAcc) {
		this.payAcc = payAcc;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public static enum PayType {
		WX, ALI, MM, IAP
	}
}
