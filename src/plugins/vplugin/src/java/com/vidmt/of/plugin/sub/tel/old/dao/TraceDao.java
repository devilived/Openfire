package com.vidmt.of.plugin.sub.tel.old.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.entity.Trace;
@MybatisAnno
public interface TraceDao extends CrudDao<Trace> {
	@Select("SELECT id,uid,points AS points_json,`date` FROM v_trace WHERE uid=#{0}")
	public List<Trace> getByUid(Long uid);
	
	@Override
	@Select("SELECT id,uid,points AS points_json,`date` FROM v_trace WHERE	uid=#{uid} AND `date`=#{date}")
	List<Trace> findList(Trace entity);
	
	@Override
	@Update("UPDATE v_trace SET	points=#{pointsJson} WHERE uid=#{uid} AND Date(`date`)=#{date}")
	int update(Trace entity);
	
	@Override
	@Insert("INSERT INTO v_trace(uid,points,`date`,`hash`)VALUES(#{uid},#{pointsJson},#{date},#{hash})")
	int save(Trace entity);
	
	@Delete("DELETE FROM v_trace WHERE uid=#{0}")
	public void deleteByUid(Long uid);
}
