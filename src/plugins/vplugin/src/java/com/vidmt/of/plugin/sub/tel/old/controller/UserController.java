package com.vidmt.of.plugin.sub.tel.old.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.roster.RosterItem;
import org.jivesoftware.openfire.roster.RosterItem.AskType;
import org.jivesoftware.openfire.roster.RosterItem.SubType;
import org.jivesoftware.openfire.roster.RosterManager;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.exceptoins.CodeException;
import com.vidmt.of.plugin.exceptoins.UserNotExistsException;
import com.vidmt.of.plugin.sub.extdb.Acc;
import com.vidmt.of.plugin.sub.tel.cache.UserCache;
import com.vidmt.of.plugin.sub.tel.entity.SysLog;
import com.vidmt.of.plugin.sub.tel.entity.SysLog.Logtype;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.sub.tel.old.entity.OldUser;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.VUtil;

@Controller
@RequestMapping("/vplugin/api/1/user")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	private static final String PHONE_PATTERN = "^1\\d{10}$";

	@ResponseBody
	@RequestMapping("/register.*")
	public JSONObject register(final String account, final String pwd, HttpServletRequest req,
			HttpServletResponse resp) {
		JSONObject json = new JSONObject();
		if (!account.matches(PHONE_PATTERN)) {
			json.put("c", CodeException.ERR_INVALID_PARAM);
			json.put("m", "非法的手机号");
			return json;
		}
		if (pwd.length() > 12) {
			json.put("c", CodeException.ERR_INVALID_PARAM);
			json.put("m", "密码太长");
			return json;
		}
		OldUser oldUser;
		try {
			Acc acc = new Acc("phone:" + account);
			User user = userService.register(acc, pwd);

			// UserManager.getUserProvider().createUser("10", pwd, null, null);
			// User user = null;
			// try {
			// org.jivesoftware.openfire.user.User ofuser =
			// UserManager.getUserProvider().loadUser(acc.asString());
			// Acc tmpacc = new Acc(ofuser.getUsername());
			// if(tmpacc.type()!=AccType.uid){
			// ClzUtil.setField(ofuser, "username", ""+10);
			// }
			// user = new VUser(ofuser).toDbUser();
			// } catch (UserNotFoundException e) {
			// e.printStackTrace();
			// return null;
			// }

			oldUser = new OldUser(user);
			json.put("c", 0);
			json.put("m", oldUser.getUid());
			SysLog syslog = new SysLog(Logtype.REGISTER, user.getId());
			syslog.setTime(new Date());
			syslog.setTgtUid(user.getId());
			syslog.setContent("注册新用户:" + acc);
			VUtil.log(syslog);
		} catch (UserAlreadyExistsException e) {
			json.put("c", CodeException.ERR_CODE_USER_ALREADY_EXISTS);
			json.put("m", "注册用户已存在");
		}
		/* return new JsonResult("OK"); */
		return json;// 兼容旧版本
	}

	@ResponseBody
	@RequestMapping("/login.*")
	public JSONObject login(final String account, final String pwd, HttpServletRequest req, HttpServletResponse resp) {
		JSONObject json = new JSONObject();
		try {
			User user = userService.login(new Acc("phone:" + account), pwd);
			String sessionID = req.getSession().getId();
			OldUser oldUser = new OldUser(user);
			oldUser.setToken(sessionID);
			// json.put("c", 0);
			// json.put("d", oldUser);
			req.getSession().setAttribute("token", sessionID);
			req.getSession().setAttribute("uid", user.getId());
			User cachUser = UserCache.get(user.getId());
			if (cachUser == null) {
				UserCache.put(user);
			}
			return (JSONObject) JSON.toJSON(oldUser);
		} catch (UserNotExistsException e) {
			log.debug("用户不存在", e);
			json.put("c", CodeException.ERR_CODE_USER_NOT_EXISTS);
			json.put("m", "用户不存在");
			return json;
		}
	}

	/**
	 * 兼容Android2.6.6之前版本，之后删除之
	 */
	@ResponseBody
	@RequestMapping("/logout.*")
	public JSONObject logout(HttpServletRequest req) {
		Long uid = (Long) req.getSession().getAttribute("uid");
		UserCache.remove(uid);
		req.getSession().invalidate();
		JSONObject json = new JSONObject();
		json.put("c", 0);
		return json;
	}

	@ResponseBody
	@RequestMapping("/reconnect.*")
	public JSONObject reconnect(final String account, final String pwd, HttpServletRequest req,
			HttpServletResponse resp) {
		JSONObject json = new JSONObject();
		try {
			User user = userService.login(new Acc("phone:" + account), pwd);
			OldUser oldUser = new OldUser(user);
			String sessionID = req.getSession().getId();
			oldUser.setToken(sessionID);
			json.put("c", 0);
			json.put("m", sessionID);
			req.getSession().setAttribute("token", sessionID);
			req.getSession().setAttribute("uid", user.getId());
			return json;
		} catch (UserNotExistsException e) {
			log.error("用户不存在", e);
			json.put("c", CodeException.ERR_CODE_USER_NOT_EXISTS);
			json.put("m", "用户不存在");
			return json;
		}
	}

	// @ResponseBody
	// @RequestMapping("/getUidByAccount.*")
	// public JSONObject getUidByAccount(final String account, final String pwd,
	// HttpServletRequest req,
	// HttpServletResponse resp) {
	// JSONObject json = new JSONObject();
	// try {
	// OldUser user = new OldUser(userService.login(new Acc("phone:" + account),
	// pwd));
	// json.put("c", 0);
	// json.put("d", user.getUid());
	// return json;
	// } catch (UserNotExistsException e) {
	// log.debug("用户不存在", e);
	// json.put("c", CodeException.ERR_CODE_USER_NOT_EXISTS);
	// return json;
	// }
	//
	// }

	@ResponseBody
	@RequestMapping("/changePwd.*")
	public JSONObject changePwd(final String account, final String newpwd, HttpServletRequest req,
			HttpServletResponse resp) {
		JSONObject json = new JSONObject();
		try {
			Acc acc = new Acc("phone:" + account);
			User user = userService.findByAcc(acc);
			if (user == null) {
				json.put("c", CodeException.ERR_CODE_USER_NOT_EXISTS);
				json.put("m", "用户不存在" + acc);
				return json;
			}
			SysLog syslog = new SysLog(Logtype.CHG_PWD, user.getId());
			syslog.setTime(new Date());
			syslog.setContent(acc + "修改了密码:" + newpwd);
			VUtil.log(syslog);

			try {
				AuthFactory.setPassword(acc.asString(), newpwd);
			} catch (UnsupportedOperationException | ConnectionException | InternalUnauthenticatedException e) {
				throw new UserNotFoundException(e);
			}
			json.put("c", 0);
			return json;
		} catch (UserNotFoundException e) {
			log.error("修改密码失败", e);
			json.put("c", CodeException.ERR_CODE_USER_NOT_EXISTS);
			return json;
		}

	}

	@ResponseBody
	@RequestMapping("/matchPhones.*")
	public JSONObject matchPhones(final String[] phones, HttpServletRequest req, HttpServletResponse resp) {
		if (CommUtil.isEmpty(phones)) {
			JSONObject json = new JSONObject();
			json.put("c", 0);
			json.put("list", Collections.emptyList());
			return json;
		}
		List<User> users = userService.findUsersByPhones(phones);
		List<OldUser> oldUsers = new ArrayList<>(users.size());
		for (User user : users) {
			oldUsers.add(new OldUser(user));
		}
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("list", oldUsers);
		return json;
	}

	@ResponseBody
	@RequestMapping("/getByAcc.*")
	public JSONObject getByAccount(final String account, HttpServletRequest req, HttpServletResponse resp) {
		User user = userService.findByAcc(new Acc("phone:" + account));
		if (user == null) {
			JSONObject json = new JSONObject();
			json.put("c", 0);
			return json;
		}
		OldUser oldUser = new OldUser(user);
		return (JSONObject) JSON.toJSON(oldUser);
	}

	@ResponseBody
	@RequestMapping("/get.*")
	public JSONObject getUser(Long uid, HttpServletRequest req, HttpServletResponse resp) {
		OldUser oldUser = new OldUser(userService.load(uid));
		return (JSONObject) JSON.toJSON(oldUser);
	}

	@ResponseBody
	@RequestMapping("/getMult.*")
	public JSONObject getMultUser(final String uids, HttpServletRequest req, HttpServletResponse resp) {
		String jsonUidsStr = EncryptUtil.decryptParam(uids);
		List<Long> uidList = JSONArray.parseArray(jsonUidsStr, Long.class);
		List<User> users = userService.findByUids(uidList);
		List<OldUser> oldUsers = new ArrayList<>(users.size());
		for (User user : users) {
			oldUsers.add(new OldUser(user));
		}
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("list", oldUsers);
		return json;
	}

	@ResponseBody
	@RequestMapping("/getReqUsers.*")
	public JSONObject getRequestUsers(String justLatestOne, HttpServletRequest req, HttpServletResponse resp) {
		Long uid = (Long) req.getAttribute("uid");
		List<Long> uids = new ArrayList<Long>();
		Iterator<RosterItem> it = RosterManager.getRosterItemProvider().getItems(uid.toString());
		while (it.hasNext()) {
			RosterItem item = it.next();
			SubType subType = item.getSubStatus();
			AskType askType = item.getAskStatus();
			// The roster item has a subscription to the roster owner’s
			// presence.
			// And The roster item has been asked for permission to
			// subscribe to its presence but no response has been
			// received.
			if (subType == SubType.getTypeFromInt(2) && askType == AskType.getTypeFromInt(-1)) {
				String itemUid = item.getJid().getNode();
				uids.add(Long.valueOf(itemUid));
			}
		}
		JSONObject json = new JSONObject();
		if (CommUtil.isEmpty(uids)) {
			json.put("c", 0);
			json.put("list", Collections.emptyList());
			return json;
		}
		if ("T".equals(justLatestOne)) {
			Long lastestUid = uids.get(uids.size() - 1);
			uids = new ArrayList<Long>();
			uids.add(lastestUid);
		}
		Collections.reverse(uids);
		List<User> allUser = userService.findByUids(uids);
		List<OldUser> oldUsers = new ArrayList<>(allUser.size());
		for (User user : allUser) {
			oldUsers.add(new OldUser(user));
		}
		json.put("c", 0);
		json.put("list", oldUsers);
		return json;

	}

	@ResponseBody
	@RequestMapping("/update.*")
	public JSONObject updateUser(OldUser user, HttpServletRequest req) {
		Long uid = (Long) req.getAttribute("uid");
		user.setUid(uid.toString());
		User newUser = userService.load(uid);
		if (user.getAddress() != null) {
			newUser.setAddress(EncryptUtil.decryptParam(user.getAddress()));
		}
		if (user.getNick() != null) {
			newUser.setNick(EncryptUtil.decryptParam(user.getNick()));
		}
		if (user.getSignature() != null) {
			newUser.setSignature(EncryptUtil.decryptParam(user.getSignature()));
		}
		if (user.getAge() != null) {
			newUser.setBirth(
					new java.sql.Date(System.currentTimeMillis() - user.getAge() * (365L * 24 * 60 * 60 * 1000)));
		}
		if (user.getGender() != null) {
			newUser.setSex(user.getGender() == 'M' ? User.SEX_M : User.SEX_F);
		}
		if (user.getLocSecret() != null) {
			newUser.setLocPrivate(user.getLocSecret() == 'T');
		}
		if (user.getAvoidDisturb() != null) {
			newUser.setAvoidDisturb(user.getAvoidDisturb() == 'T');
		}
		UserCache.remove(newUser.getId());
		userService.update(newUser);
		JSONObject json = new JSONObject();
		json.put("c", 0);
		return json;
	}
}
