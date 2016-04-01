package com.vidmt.of.plugin.sub.tel.old.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;
import com.vidmt.of.plugin.sub.tel.entity.User;

@MybatisAnno
public interface UserDao extends CrudDao<User> {
	@Override
//	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("INSERT INTO v_user(id,`name`,nick,email,phone,sex,birth,status,create_date) "
			+ "VALUES(#{id},#{name},#{nick},#{email},#{phone},#{sex},#{birth},#{status},now())")
	public int save(User entity);
	
	@Select("SELECT MAX(id) FROM v_user")
	public long maxUid();

	@Override
	public User load(Long id);

	public List<User> fuzzySearch(String where,String limit);

	public User findByColumn(@Param("column") String column, String value);
	
	@Select("select pwd from v_user where `${param1}`=#{1}")
	public String getPassword(String column, String value);

	@Update("update v_user set pwd=#{0} where `${column}`=#{2}")
	public void setPassword(String password, @Param("column") String column, String value);

	public List<User> findByUids(@Param("uids") List<Long> uids);

	public List<User> findByPhones(@Param("phones") String[] phones);
	
	@Update("UPDATE v_user SET `${column}`=#{2} WHERE id=#{0}")
	public int updateColumn(Long uid, @Param("column") String column, Object value);

	@Update("UPDATE v_user SET `status`='PAYED', lvl=#{1},lvl_end=DATE_ADD(IFNULL(lvl_end,NOW()),INTERVAL #{2} SECOND) WHERE id=#{0}")
	public int updateUserPayed(Long uid, LvlType lvltype, Long second);

	@Update("UPDATE v_user SET `status`='NORMAL', lvl=NULL,lvl_end=NULL WHERE id=#{0}")
	public int updateUserExpired(Long uid);

	@Update("UPDATE v_user SET id=#{0} WHERE `${column}`=#{2}")
	public int updateUid(Long uid, @Param("column") String column, Object value);

	@Select("SELECT count(0) FROM v_user")
	public long getUserCount();

	@Select("SELECT id FROM v_user")
	public List<Long> getAllUid();
	
	@Delete("DELETE FROM v_user WHERE `${column}`=#{1}")
	public int deleteByColumn(@Param("column") String column, Object value);

	// public List<User> getAllUser(String excludeUid);
	// public void updateUser(User user);
}
