package com.vidmt.of.plugin.sub.tel.entity;

import com.vidmt.of.plugin.abs.CrudEntity;

public class Lvl extends CrudEntity{
	private static final long serialVersionUID = 1L;
	
	public static enum LvlType{YEAR, TRY}
	private LvlType name;
	private Integer money;
	private Long during;
	public LvlType getName() {
		return name;
	}
	public void setName(LvlType name) {
		this.name = name;
	}
	public Integer getMoney() {
		return money;
	}
	public void setMoney(Integer money) {
		this.money = money;
	}
	public Long getDuring() {
		return during;
	}
	public void setDuring(Long during) {
		this.during = during;
	}
}
