package com.vidmt.of.plugin.sub.tel.entity;

import java.util.Date;

import com.vidmt.of.plugin.abs.CrudEntity;
import com.vidmt.of.plugin.sub.tel.entity.Order.PayType;

public class Paylog extends CrudEntity {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long uid;
	private PayEvent payEvent;
	private PayType payType;
	private String payAcc;
	private Integer totalFee;
	private String tradeNo;
	private Date payTime;
	private Date createTime;
	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public PayType getPayType() {
		return payType;
	}

	public void setPayType(PayType payType) {
		this.payType = payType;
	}

	public String getPayAcc() {
		return payAcc;
	}

	public void setPayAcc(String payAcc) {
		this.payAcc = payAcc;
	}

	public Integer getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public PayEvent getPayEvent() {
		return payEvent;
	}

	public void setPayEvent(PayEvent payEvent) {
		this.payEvent = payEvent;
	}

	public static enum PayEvent {
		PAY, REFUND
	}
}
