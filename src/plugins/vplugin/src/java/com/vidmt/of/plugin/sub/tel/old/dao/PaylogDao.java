package com.vidmt.of.plugin.sub.tel.old.dao;

import java.util.Date;
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
	public int save(Paylog entity);
	
	@Select("SELECT pay.uid,phone AS 'user.phone',nick AS 'user.nick',lvl AS 'user.lvl',lvl_end AS 'user.lvl_end',pay_type,total_fee,trade_no,pay_event,pay_time FROM v_paylog pay LEFT JOIN v_user usr ON pay.uid=usr.id  WHERE trade_no=#{0}")
	public Paylog findByTradeno(String tradeno);

	@Select("SELECT `pay_time` , pay_type, total_fee FROM `v_paylog` WHERE `pay_time` > #{0}")
	public List<Paylog> findLatest(Date date);

	@Select("SELECT SUM(total_fee) FROM `v_paylog` WHERE `pay_time` > #{0}")
	public Integer findTotalFee(Date start);
}
