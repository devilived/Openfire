package com.vidmt.of.plugin.sub.tel.old.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.vidmt.of.plugin.abs.CrudDao;
import com.vidmt.of.plugin.spring.MybatisAnno;
import com.vidmt.of.plugin.sub.tel.entity.Order;
import com.vidmt.of.plugin.sub.tel.entity.Order.OrderStatus;

@MybatisAnno
public interface OrderDao extends CrudDao<Order> {
	@Select("SELECT * FROM v_order WHERE id=#{0}")
	public Order load(String id);
	
	@Select("SELECT * FROM v_order WHERE uid=#{0}")
	public List<Order> findByUid(Long uid);

	@Override
	@Insert("INSERT INTO v_order(id,uid,subject,attach,total_fee,pay_type,trade_no,pay_acc,status,pay_time,create_time)"
			+ "VALUES(#{id},#{uid},#{subject},#{attach},#{totalFee},#{payType},#{tradeNo},#{payAcc},#{status},#{payTime},now())")
	public int save(Order entity);
	
	@Update("UPDATE v_order SET status=#{0} WHERE id=#{1}")
	public int updateStatus(OrderStatus status,String id);
}
