package com.vidmt.of.plugin.sub.tel.old.dao;

import org.apache.ibatis.annotations.Insert;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.entity.SysLog;

@MybatisAnno
public interface LogDao extends CrudDao<SysLog> {
	@Override
	@Insert("INSERT INTO v_log(`type`,`time`,create_by,tgt_uid,`content`)values"
			+ "(#{type},#{time},#{createBy},#{tgtUid},#{content})")
	int save(SysLog entity);
}
