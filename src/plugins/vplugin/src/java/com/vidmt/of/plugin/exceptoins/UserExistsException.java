package com.vidmt.of.plugin.exceptoins;

import com.vidmt.of.plugin.sub.extdb.Acc;

public class UserExistsException extends CodeException {
	private static final long serialVersionUID = 1L;

	public UserExistsException() {
		this("用户已存在");
	}
	public UserExistsException(Acc acc) {
		this("用户已存在:"+acc);
	}
	public UserExistsException(String msg) {
		super(CodeException.ERR_CODE_USER_ALREADY_EXISTS, msg);
	}

}
