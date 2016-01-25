package com.vidmt.of.plugin.sub.tel.old.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.entity.Trace;
import com.vidmt.of.plugin.sub.tel.old.dao.TraceDao;

@Service
@Transactional(readOnly = false)
public class TraceService extends CrudService<TraceDao, Trace> {
	
	@Transactional(readOnly = true)
	public List<Trace> getByUid(Long uid) {
		return dao.getByUid(uid);
	}
}
