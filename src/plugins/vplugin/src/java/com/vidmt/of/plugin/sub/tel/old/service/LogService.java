package com.vidmt.of.plugin.sub.tel.old.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.entity.SysLog;
import com.vidmt.of.plugin.sub.tel.old.dao.LogDao;

@Service
@Transactional(readOnly = false)
public class LogService extends CrudService<LogDao, SysLog> {
	@Override
	public int save(SysLog entity) {
		return super.save(entity);
	}
}
