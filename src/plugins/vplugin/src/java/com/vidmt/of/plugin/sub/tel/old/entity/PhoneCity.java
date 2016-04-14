package com.vidmt.of.plugin.sub.tel.old.entity;

import com.vidmt.of.plugin.abs.CrudEntity;

public class PhoneCity extends CrudEntity {
	private String prefix;
	private String supplier;
	private String province;
	private String city;
	private String suit;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSuit() {
		return suit;
	}

	public void setSuit(String suit) {
		this.suit = suit;
	}

}
