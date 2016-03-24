package com.vidmt.of.plugin.sub.tel.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.vidmt.of.plugin.abs.CrudEntity;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;

public final class User extends CrudEntity {
	private static final long serialVersionUID = 1L;

	public static final Integer SEX_M = 1;
	public static final Integer SEX_F = 0;
	// private String accType;
	// private String account;
	private Long id;
	private String name;
	private String nick;
	private String email;
	private String phone;
	// TODO NOT USED
	private UserStatus status;
	private Integer sex;
	private java.sql.Date birth;
	private String signature;
	private String address;
	private String avatarUri;
	private String photoUri;
	private Boolean locPrivate;
	private Boolean avoidDisturb;
	private Date createDate;
	private Date modifyDate;

	private LvlType lvl;
	private transient Long lvlttl;// 单位:秒

//	@JSONField(serialize = false)
	private Date lvlEnd;
	@JSONField(serialize = false)
	private String rawpwd;
	/**
	 * 此属性用于那些要求不用特别精确的，不用持久化，重启后允许重置的属性，比如IOS推送消息的个数等. 为了提高效率，无需持久化,此属性是对
	 * OF自带properties的补充，但是不用持久化，因此提高了效率
	 */
	@JSONField(serialize = false)
	private Map<String, Object> props = new HashMap<>();

	public void setProp(String key, Object value) {
		if (value == null) {
			props.remove(key);
		} else {
			props.put(key, value);
		}
	}

	public Object getProp(String key,Object defaultValue) {
		return props.getOrDefault(key, defaultValue);
	}

	public User() {
	}

	public User(Long uid, String name, String email, Date createDate, Date modifyDate) {
		this.id = uid;
		this.name = name;
		this.email = email;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public java.sql.Date getBirth() {
		return birth;
	}

	public void setBirth(java.sql.Date birth) {
		this.birth = birth;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAvatarUri() {
		return avatarUri;
	}

	public void setAvatarUri(String avatarUri) {
		this.avatarUri = avatarUri;
	}

	public String getPhotoUri() {
		return photoUri;
	}

	public void setPhotoUri(String photoUri) {
		this.photoUri = photoUri;
	}

	public Boolean getLocPrivate() {
		return locPrivate;
	}

	public void setLocPrivate(Boolean locPrivate) {
		this.locPrivate = locPrivate;
	}

	public Boolean getAvoidDisturb() {
		return avoidDisturb;
	}

	public void setAvoidDisturb(Boolean avoidDisturb) {
		this.avoidDisturb = avoidDisturb;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public LvlType getLvl() {
		return lvl;
	}

	public void setLvl(LvlType lvl) {
		this.lvl = lvl;
	}

	public Long getLvlttl() {
		return lvlttl;
	}

	public void setLvlttl(Long lvlttl) {
		this.lvlttl = lvlttl;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getRawpwd() {
		return rawpwd;
	}

	public void setRawpwd(String rawpwd) {
		this.rawpwd = rawpwd;
	}

	public Date getLvlEnd() {
		return lvlEnd;
	}

	public void setLvlEnd(Date lvlEnd) {
		this.lvlEnd = lvlEnd;
	}

	public static enum UserStatus {
		INIT, NORMAL, PAYED, DELETED
	}
}
