package com.vidmt.of.plugin.sub.tel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.entity.Order;
import com.vidmt.of.plugin.sub.tel.entity.Order.OrderStatus;
import com.vidmt.of.plugin.sub.tel.old.dao.OrderDao;

@Service
public class OrderService extends CrudService<OrderDao, Order>{
	public Order load(String id){
		return dao.load(id);
	}
	
	public List<Order> findByUid(Long uid){
		return dao.findByUid(uid);
	}

	public int updateStatus(OrderStatus status,String id){
		return dao.updateStatus(status,id);
	}
}
