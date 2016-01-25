package com.vidmt.of.plugin.sub.extdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.openfire.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.VPlugin;
import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.extdb.Acc.AccType;
import com.vidmt.of.plugin.sub.tel.entity.SysLog;
import com.vidmt.of.plugin.sub.tel.entity.SysLog.Logtype;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.VUtil;

/**
 * 本类与jdbcUserprovider相似, 但是区分了用户名大小写,并且把一些设定的写成了常量
 * 
 * @author xingqisheng
 *
 */
public class VUserProvider implements UserProvider {
	private static final Logger log = LoggerFactory.getLogger(VPlugin.class);

	private static final boolean IS_READ_ONLY = false;

	public VUserProvider() {
		log.debug("初始化[{}]", VUserProvider.class.getName());
	}

	@Override
	public VUser loadUser(String username) throws UserNotFoundException {
		Acc acc = ExtUtil.getUsername(username);
		UserService usersvc = SpringContextHolder.getBean(UserService.class);
		com.vidmt.of.plugin.sub.tel.entity.User user = usersvc.findByAcc(acc);
		if (user == null) {
			throw new UserNotFoundException("user not found:" + acc);
		}
		VUser vuser = new VUser(user);
//		String rawpwd = user.getRawpwd();
		// of3.9的时候需要，目前已被authprovider代替
		// if (!CommUtil.isEmpty(rawpwd)) {
		// String[] arr = rawpwd.split("#");
		// String key = arr[1];
		// String[] scramarr = key.split("@");
		// if (scramarr.length > 4) {
		// vuser.setStoredKey(scramarr[1]);
		// vuser.setServerKey(scramarr[2]);
		// vuser.setSalt(scramarr[3]);
		// vuser.setIterations(Integer.parseInt(scramarr[4]));
		// }
		// }
		return vuser;
	}

	@Override
	public VUser createUser(String username, String password, String name, String email)
			throws UserAlreadyExistsException {
		Acc acc = ExtUtil.getUsername(username);
		if (acc.type() == AccType.name) {
			name = acc.value();
		} else if (acc.type() == AccType.uid && CommUtil.isEmpty(name)) {
			name = acc.value();
		}
		UserService usersvc = SpringContextHolder.getBean(UserService.class);
		com.vidmt.of.plugin.sub.tel.entity.User user = usersvc.register(acc, password);
		SysLog syslog = new SysLog(Logtype.REGISTER, Acc.ADMIN_UID);
		syslog.setTime(new Date());
		syslog.setTgtUid(user.getId());
		syslog.setContent("注册新用户:" + acc);
		VUtil.log(syslog);

		VUser vuser = new VUser(user.getId(), user.getName(), user.getEmail(), user.getCreateDate(),
				user.getModifyDate());
		return vuser;
	}

	@Override
	public void deleteUser(String username) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getUserCount() {
		UserService usersvc = SpringContextHolder.getBean(UserService.class);
		return usersvc.getUserCount().intValue();
	}

	@Override
	public Collection<User> getUsers() {
		List<VUser> userlist = fuzzySearch(null, null, 0, Integer.MAX_VALUE);
		return new ArrayList<User>(userlist);
	}

	@Override
	public Collection<String> getUsernames() {
		UserService usersvc = SpringContextHolder.getBean(UserService.class);
		List<Long> uids = usersvc.getAllUid();
		List<String> usernamebs = new ArrayList<>(uids.size());
		for (Long uid : uids) {
			usernamebs.add(uid.toString());
		}
		return usernamebs;
	}

	@Override
	public Collection<User> getUsers(int startIndex, int numResults) {
		List<VUser> userlist = fuzzySearch(null, null, startIndex, numResults);
		return new ArrayList<User>(userlist);
	}

	@Override
	public void setName(String username, String name) throws UserNotFoundException {
		Acc acc = ExtUtil.getUsername(username);
		UserService usersvc = SpringContextHolder.getBean(UserService.class);
		Long uid;
		if (acc.type() == AccType.uid) {
			uid = Long.valueOf(acc.value());
		} else {
			uid = usersvc.findByAcc(acc).getId();
		}
		usersvc.updateName(uid, name);
	}

	@Override
	public void setEmail(String username, String email) throws UserNotFoundException {
		Acc acc = ExtUtil.getUsername(username);
		UserService usersvc = SpringContextHolder.getBean(UserService.class);
		Long uid;
		if (acc.type() == AccType.uid) {
			uid = Long.valueOf(acc.value());
		} else {
			uid = usersvc.findByAcc(acc).getId();
		}
		usersvc.updateEmail(uid, email);
	}

	@Override
	public void setCreationDate(String username, Date creationDate) throws UserNotFoundException {
		// Reject the operation since the provider is read-only
		throw new UnsupportedOperationException();
	}

	@Override
	public void setModificationDate(String username, Date modificationDate) throws UserNotFoundException {
		// Reject the operation since the provider is read-only
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getSearchFields() throws UnsupportedOperationException {
		return new LinkedHashSet<String>(Arrays.asList("Username", "Name", "Email"));
	}

	@Override
	public Collection<User> findUsers(Set<String> fields, String query) throws UnsupportedOperationException {
		return findUsers(fields, query, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<User> findUsers(Set<String> fields, String query, int startIndex, int numResults)
			throws UnsupportedOperationException {
		List<VUser> ulist = fuzzySearch(fields, query, startIndex, numResults);
		return new ArrayList<User>(ulist);
	}

	@Override
	public boolean isReadOnly() {
		return IS_READ_ONLY;
	}

	public boolean isNameRequired() {
		return false;
	}

	@Override
	public boolean isEmailRequired() {
		return false;
	}

	private List<VUser> fuzzySearch(Set<String> fields, String query, int offset, int limit)
			throws UnsupportedOperationException {
		if (fields == null) {
			fields = Collections.emptySet();
		}
		UserService usersvc = SpringContextHolder.getBean(UserService.class);
		if (fields.contains("Uid") || fields.contains("Username")) {
			VUser user = new VUser(usersvc.load(Long.valueOf(query)));
			return Arrays.asList(user);
		}
		Set<String> newSet = new HashSet<>(fields.size());
		for (String field : fields) {
			newSet.add(field.toLowerCase());
		}
		List<com.vidmt.of.plugin.sub.tel.entity.User> userList = usersvc.fuzzySearch(newSet, query, (long) offset,
				limit);
		List<VUser> vuserlist = new ArrayList<>(userList.size());
		for (com.vidmt.of.plugin.sub.tel.entity.User user : userList) {
			vuserlist.add(new VUser(user));
		}
		return vuserlist;
	}

	/**
	 * Make sure that Log.isDebugEnabled()==true before calling this method.
	 * Twenty elements will be logged in every log line, so for 81-100 elements
	 * five log lines will be generated
	 * 
	 * @param listElements
	 *            a list of Strings which will be logged
	 */
	private static void LogResults(List<? extends User> listElements) {
		String callingMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
		StringBuilder sb = new StringBuilder(256);
		int count = 0;
		for (User element : listElements) {
			if (count > 20) {
				log.debug(callingMethod + " results: " + sb.toString());
				sb.delete(0, sb.length());
				count = 0;
			}
			sb.append(element.getName()).append(",");
			count++;
		}
		sb.append(".");
		log.debug(callingMethod + " results: " + sb.toString());
	}
}
