package com.vidmt.of.plugin.sub.tel.old.entity;

import java.util.ArrayList;
import java.util.List;

import com.vidmt.of.plugin.abs.CrudEntity;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;

public class OldLvl extends CrudEntity {
	private LvlType type;
	private float money;
	private int expire;

	public OldLvl() {
	}

	public OldLvl(Lvl lvl) {
		this.type = lvl.getName();
		this.money = lvl.getMoney() / 100f;
		this.expire = (int) (lvl.getDuring() / (24 * 60 * 60));
	}

	public static List<OldLvl> fromLvlList(List<Lvl> lvllist) {
		List<OldLvl> list = new ArrayList<>(lvllist.size());
		for (Lvl lvl : lvllist) {
			list.add(new OldLvl(lvl));
		}
		return list;
	}

	public LvlType getType() {
		return type;
	}

	public void setType(LvlType type) {
		this.type = type;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

}
