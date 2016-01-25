package com.vidmt.of.plugin.sub.tel.old.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidmt.of.plugin.abs.CrudService;
import com.vidmt.of.plugin.exceptoins.UserNotExistsException;
import com.vidmt.of.plugin.sub.extdb.Acc;
import com.vidmt.of.plugin.sub.extdb.Acc.AccType;
import com.vidmt.of.plugin.sub.tel.cache.UserCache;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.sub.tel.entity.User.UserStatus;
import com.vidmt.of.plugin.sub.tel.old.dao.LocationDao;
import com.vidmt.of.plugin.sub.tel.old.dao.TraceDao;
import com.vidmt.of.plugin.sub.tel.old.dao.UserDao;
import com.vidmt.of.plugin.utils.CommUtil;

@Service
@Transactional(readOnly = false)
public class UserService extends CrudService<UserDao, User> {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private LocationDao locationDao;
	@Autowired
	private TraceDao traceDao;

	@Override
	public User load(Long id) {
		User user = UserCache.get(id);
		if (user != null) {
			this.checkLvlttl(user);
		} else {
			user = dao.load(id);
			if (user != null) {
				UserCache.put(user);
			}
		}
		return user;
	}
	@Transactional(readOnly = true)
	public String getPwd(Acc acc) {
		if (AccType.uid == acc.type()) {
			return dao.getPassword("id", acc.value());
		} else {
			return dao.getPassword(acc.type().name(), acc.value());
		}
	}

	public void setPwd(String password, Acc acc) {
		if (acc.type() == AccType.uid) {
			dao.setPassword(password, "id", acc.value());
		} else {
			dao.setPassword(password, acc.type().name(), acc.value());
		}

	}

	public List<User> fuzzySearch(Set<String> columns, String query, Long offset, Integer limit) {
		if (!CommUtil.isEmpty(columns) && !Arrays.asList("name", "nick", "email", "phone").contains(columns)) {
			throw new IllegalArgumentException("illegal comuns:" + columns);
		}
		StringBuilder dsf = new StringBuilder();
		for (String col : columns) {
			dsf.append(String.format("AND `%s` LIKE '%%%s%%'", col, query));
		}

		String limitstr = null;
		if (offset != null || limit != null) {
			limitstr = String.format(" LIMIT %d,%d", offset, limit);
		}
		return dao.fuzzySearch(dsf.length() == 0 ? null : dsf.toString(), limitstr);
	}

	public int updateName(Long uid, String name) {
		UserCache.remove(uid);
		return dao.updateColumn(uid, "name", name);
	}

	public int updateEmail(Long uid, String email) {
		UserCache.remove(uid);
		return dao.updateColumn(uid, "email", email);
	}

	public int updateAvatar(Long uid, String relurl) {
		UserCache.remove(uid);
		return dao.updateColumn(uid, "avatar_uri", relurl);
	}

	public int updatePhoto(Long uid, String relurl) {
		UserCache.remove(uid);
		return dao.updateColumn(uid, "photo_uri", relurl);
	}

	public void updateUserExpired(Long uid) {
		UserCache.remove(uid);
		dao.updateUserExpired(uid);
	}

	public void updateUserPayed(Long uid, Lvl lvl) {
		UserCache.remove(uid);
		dao.updateUserPayed(uid, lvl.getName(), lvl.getDuring());
	}
	@Transactional(readOnly = true)
	public Long getUserCount() {
		return dao.getUserCount();
	}
	@Transactional(readOnly = true)
	public List<Long> getAllUid() {
		return dao.getAllUid();
	}

	public User register(Acc acc, String plainpwd) throws UserAlreadyExistsException {
		if (CommUtil.isEmpty(plainpwd)) {
			throw new IllegalArgumentException("password cant be empty");
		}
		// if (!acc.isAdmin() && AccType.uid == acc.type()) {
		// 非admin不能通过UID注册
		// throw new IllegalArgumentException("uid cant be registered by api
		// except admin");
		// }
		User user = createUserByAcc(acc);
		user.setStatus(UserStatus.NORMAL);
		user.setSex(User.SEX_F);
		user.setBirth(new java.sql.Date(System.currentTimeMillis() - 20L * 365 * 24 * 60 * 60 * 1000));
		try {
			dao.save(user);
			if (acc.isAdmin()) {
				dao.updateUid(Acc.ADMIN_UID, "id", user.getId());
			} else if (AccType.uid == acc.type()) {
				dao.updateUid(Long.valueOf(acc.value()), "id", user.getId());
			}
			try {
				AuthFactory.setPassword(acc.asString(), plainpwd);
			} catch (UnsupportedOperationException | ConnectionException | InternalUnauthenticatedException e) {
				throw new UserNotFoundException(e);
			}
			return user;
		} catch (DuplicateKeyException e) {
			throw new UserAlreadyExistsException("用户已经存在" + acc);
		} catch (UserNotFoundException e) {
			log.error("注册用户失败!", e);
			return null;
		}
	}
	public User login(Acc acc, String plainpwd) throws UserNotExistsException {
		if (acc.isAdmin()) {
			throw new IllegalArgumentException("admin cant be login by api");
		}
		try {
			AuthFactory.authenticate(acc.asString(), plainpwd);
			User user = findByAcc(acc);
			this.checkLvlttl(user);
			return user;
		} catch (UnauthorizedException | ConnectionException | InternalUnauthenticatedException e) {
			throw new UserNotExistsException(acc);
		}
	}
	public User findByAcc(Acc acc) {
		User user=null;
		if (acc.type() == Acc.AccType.uid) {
			user = this.load(Long.valueOf(acc.value()));
		} else {
			user = dao.findByColumn(acc.type().name(), acc.value());
		}
		if (user != null) {
			this.checkLvlttl(user);
		}
		return user;
	}
	public List<User> findByUids(List<Long> uids) {
		List<Long> tmpuids = new ArrayList<>(uids);

		List<User> ulists = new ArrayList<>(uids.size());
		for (Long uid : uids) {
			User user = UserCache.get(uid);
			if (user != null) {
				this.checkLvlttl(user);
				ulists.add(user);
				tmpuids.remove(uid);
			}
		}
		if (tmpuids.size() > 0) {
			ulists.addAll(dao.findByUids(tmpuids));
		}
		return ulists;
	}
	public List<User> findUsersByPhones(String[] phones) {
		List<User> users = dao.findByPhones(phones);
		for (User user : users) {
			this.checkLvlttl(user);
		}
		return users;
	}

	@Override
	public int update(User user) {
		return dao.update(user);
	}

	private void checkLvlttl(User user) {
		if (user.getLvl() != null) {
			Date lvlend = user.getLvlEnd();
			long ttl = (lvlend.getTime() - System.currentTimeMillis()) / 1000;
			if (ttl > 0) {
				user.setLvlttl(ttl);
			} else {
				this.updateUserExpired(user.getId());
				user.setStatus(UserStatus.NORMAL);
				user.setLvl(null);
				user.setLvlttl(null);
			}
		}
	}
	
	public void deleteByUid(Long uid){
		dao.deleteByColumn("id", uid);
		locationDao.deleteByUid(uid);
		traceDao.deleteByUid(uid);
	}

	private static User createUserByAcc(Acc acc) {
		User user = new User();
		switch (acc.type()) {
		case uid:
			user.setId(Long.valueOf(acc.value()));
			if (acc.isAdmin()) {
				user.setName(Acc.ADMIN_NAME);
			}
			break;
		case name:
			user.setName(acc.value());
			if (acc.isAdmin()) {
				user.setId(Acc.ADMIN_UID);
			}
			break;
		case nick:
			user.setNick(acc.value());
			break;
		case email:
			user.setEmail(acc.value());
			break;
		case phone:
			user.setPhone(acc.value());
			break;
		}
		return user;
	}

}
