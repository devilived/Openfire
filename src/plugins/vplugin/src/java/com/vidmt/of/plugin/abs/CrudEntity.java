package com.vidmt.of.plugin.abs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public abstract class CrudEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JSONField(serialize = false)
	protected Map<String, String> sqlMap;

	public Map<String, String> getSqlMap() {
		if (sqlMap == null) {
			sqlMap = new HashMap<String, String>();
		}
		return sqlMap;
	}

	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
