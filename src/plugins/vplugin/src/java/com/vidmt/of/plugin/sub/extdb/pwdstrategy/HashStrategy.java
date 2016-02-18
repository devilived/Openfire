package com.vidmt.of.plugin.sub.extdb.pwdstrategy;

import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.extdb.Acc;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;

public class HashStrategy implements PwdStrategy {
	private static Logger log = LoggerFactory.getLogger(HashStrategy.class);

	private PasswordType pwdType;

	public HashStrategy(PasswordType pwdType) {
		this.pwdType = pwdType;
	}

	@Override
	public boolean valid(Acc acc, String plainpwd) throws UserNotFoundException {
		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String password = authsvc.getPwd(acc);
		if (password == null) {
			throw new UserNotFoundException(acc.toString());
		}
		String[] part = password.split("#");
		return valid(plainpwd, part[2]);
	}

	@Override
	public boolean valid(String plainpwd, String savedPassword) {
		return hashPwd(plainpwd, pwdType).equals(savedPassword.split("#")[2]);
	}

	@Override
	public void setPassword(Acc acc, String plainpwd) throws UserNotFoundException {
		try {
			plainpwd = AuthFactory.encryptPassword(plainpwd);
		} catch (UnsupportedOperationException uoe) {
			log.error("blowfish解码错误", uoe);
		}
		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String password = pwdType + "##" + hashPwd(plainpwd, pwdType);
		authsvc.setPwd(password, acc);
	}

	public enum PasswordType {
		/**
		 * The password is stored as a hex-encoded MD5 hash.
		 */
		md5,

		/**
		 * The password is stored as a hex-encoded SHA-1 hash.
		 */
		sha1,

		/**
		 * The password is stored as a hex-encoded SHA-256 hash.
		 */
		sha256,

		/**
		 * The password is stored as a hex-encoded SHA-512 hash.
		 */
		sha512;
	}

	public static String hashPwd(String pwd, PasswordType hashType) {
		if (hashType == PasswordType.md5) {
			return StringUtils.hash(pwd, "MD5");
		} else if (hashType == PasswordType.sha1) {
			return StringUtils.hash(pwd, "SHA-1");
		} else if (hashType == PasswordType.sha256) {
			return StringUtils.hash(pwd, "SHA-256");
		} else if (hashType == PasswordType.sha512) {
			return StringUtils.hash(pwd, "SHA-512");
		}
		throw new IllegalArgumentException("invalid hashtype:" + hashType);
	}
}
