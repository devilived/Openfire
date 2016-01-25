package com.vidmt.of.plugin.sub.extdb.pwdstrategy;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.Blowfish;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.extdb.Acc;
import com.vidmt.of.plugin.sub.extdb.pwdstrategy.PwdStrategy.ReversePwdStrategy;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;

public class BlowfishStrategy implements ReversePwdStrategy {
	private static Logger log = LoggerFactory.getLogger(BlowfishStrategy.class);
	private Map<String, Blowfish> blCache = new HashMap<>();

	@Override
	public String getPassword(Acc acc) throws UserNotFoundException {
		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String password = authsvc.getPwd(acc);
		if (password == null) {
			throw new UserNotFoundException(acc.toString());
		}
		String[] part = password.split("#");
		try {
			String keyString = part[1];
			Blowfish cypher = blCache.get(keyString);
			if (cypher == null) {
				cypher = new Blowfish(keyString);
				blCache.put(keyString, cypher);
			}
			return cypher.decryptString(part[2]);
		} catch (UnsupportedOperationException uoe) {
			log.error("blowfish解码错误", uoe);
			throw new UserNotFoundException("密码解码错误");
		}
	}

	@Override
	public void setPassword(Acc acc, String plainpwd) throws UserNotFoundException {
		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String keyString = JiveGlobals.getProperty("passwordKey");
		String password = fmtpwd("bf", keyString, plainpwd);
		authsvc.setPwd(password, acc);
	}

	protected String fmtpwd(String alg, String keyString, String plainpwd) {
		String encPwd = null;
		try {
			encPwd = AuthFactory.encryptPassword(plainpwd);
		} catch (UnsupportedOperationException uoe) {
			log.error("blowfish解码错误", uoe);
		}
		return String.format("%s#%s#%s", alg, keyString, encPwd);
	}

	@Override
	public boolean valid(Acc acc, String plainpwd) throws UserNotFoundException {
		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String password = authsvc.getPwd(acc);
		return valid(plainpwd, password);
	}

	@Override
	public boolean valid(String plainpwd, String savedPassword) {
		String[] part = savedPassword.split("#");
		try {
			String keyString = getKeyString(part[1]);
			Blowfish cypher = blCache.get(keyString);
			if (cypher == null) {
				cypher = new Blowfish(keyString);
				blCache.put(keyString, cypher);
			}
			return plainpwd.equals(cypher.decryptString(part[2]));
		} catch (UnsupportedOperationException uoe) {
			log.error("blowfish解码错误", uoe);
			return false;
		}
	}

	protected String getKeyString(String rawkeystring) {
		return rawkeystring;
	}
}
