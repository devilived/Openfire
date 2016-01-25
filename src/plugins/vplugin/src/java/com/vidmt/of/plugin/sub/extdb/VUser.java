package com.vidmt.of.plugin.sub.extdb;

import java.util.Date;

import org.jivesoftware.openfire.user.User;

import com.vidmt.of.plugin.utils.CommUtil;

public class VUser extends User {
	private Long uid;

	public VUser() {
		super();
	}

	public VUser(org.jivesoftware.openfire.user.User ofuser) {
		super(ofuser.getUsername(), ofuser.getName(), ofuser.getEmail(), ofuser.getCreationDate(),
				ofuser.getModificationDate());
		this.uid = Long.valueOf(ofuser.getUsername());
	}

	public VUser(com.vidmt.of.plugin.sub.tel.entity.User dbuser) {
		this(dbuser.getId(), getDisplayName(dbuser), dbuser.getEmail(), dbuser.getCreateDate(), dbuser.getModifyDate());
	}

	public VUser(Long uid, String name, String email, Date creationDate, Date modificationDate) {
		super(uid.toString(), name, email, creationDate, modificationDate);
		this.uid = uid;
	}

	private static String getDisplayName(com.vidmt.of.plugin.sub.tel.entity.User dbuser) {
		if (dbuser.getNick() != null) {
			return dbuser.getNick() + "-" + dbuser.getPhone();
		}
		if (dbuser.getName() != null) {
			return dbuser.getName();
		}
		if (dbuser.getPhone() != null) {
			return dbuser.getPhone();
		}
		return dbuser.getId().toString();
	}

	// public VUser(String username, String name, String email, Date
	// creationDate, Date modificationDate) {
	// super(username, name, email, creationDate, modificationDate);
	// this.uid = Long.valueOf(username);
	// }

	public com.vidmt.of.plugin.sub.tel.entity.User toDbUser() {
		com.vidmt.of.plugin.sub.tel.entity.User user = new com.vidmt.of.plugin.sub.tel.entity.User(this.uid,
				this.getUsername(), this.getEmail(), this.getCreationDate(), this.getModificationDate());
		return user;
	}

	public Long getUid() {
		return this.uid;
	}

	@Override
	public String getUsername() {
		if (super.getUsername() == null) {
			return uid.toString();
		}
		return super.getUsername();
	}
}
