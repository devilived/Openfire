package com.vidmt.of.plugin.sub.tel.old.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.entity.Paylog;

@MybatisAnno
public interface PaylogDao extends CrudDao<Paylog> {
	@Override
	@Insert("INSERT INTO v_paylog(uid,pay_event,pay_type,pay_acc,total_fee,trade_no,pay_time,content)"
			+ "VALUES(#{uid},#{payEvent},#{payType},#{payAcc},#{totalFee},#{tradeNo},#{payTime},#{content})")
	int save(Paylog entity);
	
	@Select("SELECT `pay_time` , pay_type, total_fee FROM `v_paylog` WHERE `pay_time` > DATE_ADD(NOW(),INTERVAL -1 MONTH) ORDER BY pay_time ASC")
	List<Paylog> find1monthAsc();
}
