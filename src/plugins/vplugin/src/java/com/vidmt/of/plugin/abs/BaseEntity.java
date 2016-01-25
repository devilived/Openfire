package com.vidmt.of.plugin.abs;

import com.alibaba.fastjson.JSON;

public abstract class BaseEntity {
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
