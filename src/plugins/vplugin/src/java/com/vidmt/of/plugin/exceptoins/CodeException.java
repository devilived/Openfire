package com.vidmt.of.plugin.exceptoins;

public class CodeException extends Exception {
	private static final long serialVersionUID = 1L;

	public static final int ERR_CODE_USER_ALREADY_EXISTS = 101;
	public static final int ERR_CODE_USER_NOT_LOGIN = 102;
	public static final int ERR_CODE_USER_NOT_EXISTS = 103;
	
	public static final int ERR_INVALID_PARAM = 201;
	public static final int ERR_UNKOWN = 1;

	private int code;

	public CodeException(int code) {
		this(code, "unkown error");
	}

	public CodeException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
