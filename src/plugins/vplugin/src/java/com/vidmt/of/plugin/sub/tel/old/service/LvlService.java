package com.vidmt.of.plugin.sub.tel.old.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.sub.tel.cache.SysCache;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.entity.Lvl.LvlType;
import com.vidmt.of.plugin.sub.tel.old.dao.LvlDao;

@Service
@Transactional(readOnly = false)
public class LvlService extends CrudService<LvlDao, Lvl> {

	@Transactional(readOnly = true)
	public Lvl getLvlByType(LvlType lvlType) {
		Lvl lvl = (Lvl) SysCache.getLvl(lvlType);
		if (lvl == null) {
			lvl = dao.getLvlByType(lvlType);
			SysCache.putLvl(lvl);
		}
		return lvl;
	}
}
