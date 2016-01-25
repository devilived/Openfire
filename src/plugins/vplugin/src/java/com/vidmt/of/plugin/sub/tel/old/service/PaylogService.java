package com.vidmt.of.plugin.sub.tel.old.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.entity.Paylog;
import com.vidmt.of.plugin.sub.tel.old.dao.PaylogDao;

@Service
@Transactional(readOnly = false)
public class PaylogService extends CrudService<PaylogDao, Paylog> {
}
