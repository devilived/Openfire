package com.vidmt.of.plugin.sub.tel.old.entity;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.utils.VUtil;

public class OldUser {
	private String uid;
	private String accType;
	private String account;
	private String nick;
	private Character gender;
	private Integer age;
	private String signature;
	private String address;
	private String avatarUri;
	private String albumUri;

	private String vipType;
	private Character locSecret;
	private Character avoidDisturb;
	private Long timeLeft;// 单位:ms

	private String token;

	public OldUser() {
	}

	public OldUser(User newUser) {
		if (newUser != null) {
			VUtil.mirrorObj(newUser, this);
			this.uid = newUser.getId() == null ? null : newUser.getId().toString();
			Date birth = newUser.getBirth();
			if (birth != null) {
				this.age = (int) ((System.currentTimeMillis() - birth.getTime()) / (365L * 24 * 60 * 60 * 1000));
			}
			if (newUser.getLvl() != null) {
				this.vipType = newUser.getLvl().name();
				this.timeLeft = newUser.getLvlttl() * 1000;
			}

			if (newUser.getPhotoUri() != null) {
				this.albumUri = newUser.getPhotoUri().replace("/static", "");
			}
			if (newUser.getAvatarUri() != null) {
				this.avatarUri = newUser.getAvatarUri().replace("/static", "");
			}

			// this.nick=newUser.getNick();
			this.accType = "PHONE";
			this.account = newUser.getPhone();
			this.gender = User.SEX_M == newUser.getSex() ? 'M' : 'F';
			this.avoidDisturb = newUser.getAvoidDisturb() != null && newUser.getAvoidDisturb() ? 'T' : 'F';
			this.locSecret = newUser.getLocPrivate() != null && newUser.getLocPrivate() ? 'T' : 'F';
			
			if (this.nick == null) {
				this.nick = this.account;
			}
		}
	}

	public User toUser() {
		User newUser = new User();
		VUtil.mirrorObj(this, newUser);
		newUser.setId(this.uid == null ? null : Long.valueOf(this.uid));
		if (this.age != null) {
			newUser.setBirth(new java.sql.Date(System.currentTimeMillis() - this.age * (365L * 24 * 60 * 60 * 1000)));
		}
		if (this.vipType != null) {
			newUser.setLvl(LvlType.valueOf(this.vipType));
			newUser.setLvlttl(this.timeLeft / 1000);
		}
		if (this.albumUri != null) {
			newUser.setPhotoUri("/static" + this.albumUri);
		}
		if (this.avatarUri != null) {
			newUser.setAvatarUri("/static" + this.albumUri);
		}
		// newUser.setNick(this.nick);
		newUser.setPhotoUri(this.albumUri);
		newUser.setPhone(this.getAccount());
		newUser.setSex(this.gender != null && 'M' == this.gender ? User.SEX_M : User.SEX_F);
		newUser.setLocPrivate(this.locSecret != null && this.locSecret == 'T');
		newUser.setAvoidDisturb(this.avoidDisturb != null && this.avoidDisturb == 'T');
		return newUser;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public Character getGender() {
		return gender;
	}

	public void setGender(Character gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
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
		if (avatarUri != null && avatarUri.startsWith("/static")) {
			return avatarUri.substring(7);
		}
		return avatarUri;
	}

	public void setAvatarUri(String avatarUri) {
		this.avatarUri = avatarUri;
	}

	public String getAlbumUri() {
		if (albumUri != null && albumUri.contains("/static")) {
			return albumUri.replace("/static", "");
		}
		return albumUri;
	}

	public void setAlbumUri(String albumUri) {
		this.albumUri = albumUri;
	}

	public String getVipType() {
		return vipType;
	}

	public void setVipType(String vipType) {
		this.vipType = vipType;
	}

	public Character getLocSecret() {
		return locSecret;
	}

	public void setLocSecret(Character locSecret) {
		this.locSecret = locSecret;
	}

	public Character getAvoidDisturb() {
		return avoidDisturb;
	}

	public void setAvoidDisturb(Character avoidDisturb) {
		this.avoidDisturb = avoidDisturb;
	}

	public Long getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(Long timeLeft) {
		this.timeLeft = timeLeft;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
