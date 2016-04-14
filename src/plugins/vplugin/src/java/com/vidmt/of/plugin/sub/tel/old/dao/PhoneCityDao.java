package com.vidmt.of.plugin.sub.tel.old.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.old.entity.PhoneCity;

@MybatisAnno
public interface PhoneCityDao extends CrudDao<PhoneCity> {
	@Select("SELECT prefix,supplier,province,city,suit FROM v_phone_city WHERE prefix = #{0}")
	public PhoneCity getByPrefix(String prefix);

	@Override
	@Insert("INSERT INTO v_phone_city(prefix,supplier,province,city,suit,from)VALUES(#{prefix},#{supplier},#{province},#{city},#{suit},#{from})")
	public int save(PhoneCity entity);
}
