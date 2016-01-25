package com.vidmt.of.plugin.exceptoins;

import com.vidmt.of.plugin.sub.extdb.Acc;

public class UserNotExistsException extends CodeException {
	private static final long serialVersionUID = 1L;

	public UserNotExistsException() {
		this("用户不存在");
	}

	public UserNotExistsException(Acc acc) {
		this("用户不存在:" + acc);
	}

	public UserNotExistsException(String msg) {
		super(CodeException.ERR_CODE_USER_NOT_EXISTS, msg);
	}

}
