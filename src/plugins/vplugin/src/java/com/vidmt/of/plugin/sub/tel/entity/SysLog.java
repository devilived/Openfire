package com.vidmt.of.plugin.sub.tel.entity;

import java.util.Date;

import com.vidmt.of.plugin.abs.CrudEntity;
import com.vidmt.of.plugin.sub.extdb.Acc;

public class SysLog extends CrudEntity {
	private static final long serialVersionUID = 1L;
	private Logtype type;
	private Date time;
	private Long createBy;
	private Long tgtUid;
	private String content;

	public SysLog(Logtype logtype) {
		this(logtype, Acc.ADMIN_UID);
	}

	public SysLog(Logtype logtype, Long createBy) {
		this.type = logtype;
		this.createBy = createBy;
	}

	public Logtype getType() {
		return type;
	}

	public void setType(Logtype type) {
		this.type = type;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Long getTgtUid() {
		return tgtUid;
	}

	public void setTgtUid(Long tgtUid) {
		this.tgtUid = tgtUid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static enum Logtype {
		ERROR, REGISTER, PAY, CHG_PWD, REFUND
	}

}
