package com.vidmt.of.plugin.sub.tel.old.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.entity.Paylog;
import com.vidmt.of.plugin.sub.tel.old.dao.PaylogDao;

@Service
@Transactional(readOnly = false)
public class PaylogService extends CrudService<PaylogDao, Paylog> {
	public List<Paylog> findLatest(Date date) {
		return dao.findLatest(date);
	}

	public Integer findTotalFee(Date start) {
		return dao.findTotalFee(start);
	}

	public Paylog findByTradeno(String tradeno) {
		return dao.findByTradeno(tradeno);
	}

	public List<Paylog> findByPhone(String phone) {
		return dao.findByPhone(phone);
	}
}
