package com.vidmt.of.plugin.sub.extdb;

public final class Acc {
	private static final String NUM_PATTERN = "^[1-9]\\d{0,19}$";

	public static enum AccType {
		uid, name, nick, email, phone
	}

	public static final String ADMIN_NAME = "admin";
	public static final String ADMIN_PWD = "1";
	public static final Long ADMIN_UID = 1L;

	private final AccType type;
	private final String value;

	public Acc(String fullacc) {
		String[] arr;
		if (fullacc.contains(":")) {
			arr = fullacc.split(":");
		} else {
			arr = fullacc.split("\\\\3a");
		}
		if (arr.length == 1) {
			if (arr[0].matches(NUM_PATTERN)) {
				this.type = AccType.uid;
				this.value = arr[0];
			} else if (ADMIN_NAME.equals(arr[0])) {
				this.type = AccType.name;
				this.value = arr[0];
			} else {
				throw new IllegalArgumentException("非法的用户名类型:" + fullacc);
			}

		} else if (arr.length == 2) {
			try {
				this.type = AccType.valueOf(arr[0]);
			} catch (Throwable e) {
				throw new IllegalArgumentException("非法的用户名类型:" + fullacc);
			}
			if (this.type == AccType.uid && !arr[1].matches(NUM_PATTERN)) {
				throw new IllegalArgumentException("非法的用户名类型:" + fullacc);
			}
			this.value = arr[1];
		} else {
			throw new IllegalArgumentException("非法的用户名类型:" + fullacc);
		}
	}

	public AccType type() {
		return type;
	}

	public String value() {
		return value;
	}

	public boolean isAdmin() {
		return (AccType.name == type && ADMIN_NAME.equals(value))
				|| (AccType.uid == type && ADMIN_UID.equals(Long.valueOf(value)));
	}

	public String asString() {
		return type + ":" + value;
	}

	@Override
	public String toString() {
		return String.format("USER[%s/%s]", type, value);
	}
}
