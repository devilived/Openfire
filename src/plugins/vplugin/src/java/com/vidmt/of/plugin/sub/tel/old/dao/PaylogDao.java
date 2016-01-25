package com.vidmt.of.plugin.sub.tel.old.dao;

import org.apache.ibatis.annotations.Insert;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.entity.Paylog;

@MybatisAnno
public interface PaylogDao extends CrudDao<Paylog> {
	@Override
	@Insert("INSERT INTO v_paylog(uid,pay_event,pay_type,pay_acc,total_fee,trade_no,pay_time,content)"
			+ "VALUES(#{uid},#{payEvent},#{payType},#{payAcc},#{totalFee},#{tradeNo},#{payTime},#{content})")
	int save(Paylog entity);
}
