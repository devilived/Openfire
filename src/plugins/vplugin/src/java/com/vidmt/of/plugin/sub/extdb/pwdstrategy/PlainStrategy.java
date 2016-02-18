package com.vidmt.of.plugin.sub.extdb.pwdstrategy;

import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.extdb.Acc;
import com.vidmt.of.plugin.sub.extdb.pwdstrategy.PwdStrategy.ReversePwdStrategy;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;

public class PlainStrategy implements ReversePwdStrategy {
	private static Logger log = LoggerFactory.getLogger(PlainStrategy.class);

	@Override
	public String getPassword(Acc acc) throws UserNotFoundException {
		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String password = authsvc.getPwd(acc);
		if (password == null) {
			throw new UserNotFoundException(acc.toString());
		}
		String[] part = password.split("#");
		return part[2];
	}

	@Override
	public void setPassword(Acc acc, String plainpwd) throws UserNotFoundException {
		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String password = "plain##" + plainpwd;
		authsvc.setPwd(password, acc);
	}

	@Override
	public boolean valid(Acc acc, String plainpwd) throws UserNotFoundException {
		return valid(plainpwd, getPassword(acc));
	}

	@Override
	public boolean valid(String plainpwd, String savedPassword) {
		return plainpwd.equals(savedPassword.split("#")[2]);
	}
}
