package com.vidmt.of.plugin.sub.tel.old.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;
@MybatisAnno
public interface LvlDao extends CrudDao<Lvl>{
	@Override
	@Select("SELECT * FROM v_lvl")
	List<Lvl> findAll();
	
	@Select("SELECT * FROM v_lvl WHERE name = #{0}")
	public Lvl getLvlByType(LvlType type);
}
