package com.vidmt.of.plugin.sub.tel.old.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.entity.Location;
import com.vidmt.of.plugin.sub.tel.entity.User;

@MybatisAnno
public interface LocationDao extends CrudDao<Location> {
	@Override
	@Insert("INSERT INTO v_location(uid,lat,lon,time)VALUES" + "(#{uid},#{lat},#{lon},#{time})" + " ON DUPLICATE KEY "
			+ "	UPDATE lat=VALUES(lat), lon=VALUES(lon),`time`=VALUES(time)")
	public int saveOrUpdate(Location loc);

	public Location findByUid(Long uid);

	public List<Location> findListByUids(List<Long> uids);
	
	
	@Insert("INSERT INTO v_location_nearby(uid,lat,lon,time,loc_private,sex,birth)VALUES"
			+ "(#{loc.uid},#{loc.lat},#{loc.lon},#{loc.time},#{user.locPrivate},#{user.sex},#{user.birth})"
			+ " ON DUPLICATE KEY "
			+ "	UPDATE lat=#{loc.lat}, lon=#{loc.lon},`time`=#{loc.time},loc_private=#{user.locPrivate},sex=#{user.sex},birth=#{user.birth}")
	public int saveOrUpdateNearby(@Param("loc") Location location, @Param("user") User user);

	public List<Location> getNearBy(@Param("selfUid") Long uid, @Param("selfLon") Double lon,
			@Param("selfLat") Double lat, @Param("time") Date time, @Param("distance") Double distance,
			@Param("sex") Integer sex, @Param("birthStart") Date birthStart, @Param("birthEnd") Date birthEnd,
			@Param("offset") Integer offset, @Param("limit") Integer limit);

	@Delete("DELETE FROM v_location_nearby WHERE `time`<#{0}")
	public void deleteExpiredPoints(Date time);
	
	@Delete("DELETE FROM v_location WHERE uid=#{0}")
	public int deleteByUid(Long uid);
	
	@Delete("DELETE FROM v_location_nearby WHERE uid=#{0}")
	public int deleteNearbyByUid(Long uid);
}
