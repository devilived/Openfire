package com.vidmt.of.plugin.sub.tel.misc;

import com.alibaba.fastjson.JSON;

public class KV {
	private String key;
	private Object value;

	public KV(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
