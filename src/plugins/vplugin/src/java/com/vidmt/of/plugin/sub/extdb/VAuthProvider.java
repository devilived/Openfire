package com.vidmt.of.plugin.sub.extdb;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.auth.AuthProvider;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.extdb.pwdstrategy.HashStrategy;
import com.vidmt.of.plugin.sub.extdb.pwdstrategy.HashStrategy.PasswordType;
import com.vidmt.of.plugin.sub.extdb.pwdstrategy.PlainStrategy;
import com.vidmt.of.plugin.sub.extdb.pwdstrategy.PwdStrategy;
import com.vidmt.of.plugin.sub.extdb.pwdstrategy.PwdStrategy.ReversePwdStrategy;
import com.vidmt.of.plugin.sub.extdb.pwdstrategy.ScramStrategy;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;

/**
 * 根据jdbcauthprovider的描述,如果存储为plain,那么才能执行getPassword,此时才能用diguest-md5
 * 如果存储不是plain,那么sasl就只能用plain,,那么就执行authenticate(String username, String
 * password),从而执行md5验证. 因此,存储是plain,那么sasl就需要是md5,如果存储是md5,那么sasl就需要时plain
 * 
 * 本类与jdbcauthprovider相似, 但是区分了用户名大小写,设定了admin的用户,并且把一些设定的写成了常量,去掉了用户的自动创建功能
 * 注意，如果是scram验证，根本不走此方法，那将走loaduser自行验证，跟此类无关.
 * 
 * @author xingqisheng
 *
 */
public class VAuthProvider implements AuthProvider {

	private static final Logger log = LoggerFactory.getLogger(VAuthProvider.class);

	private static final Map<String, PwdStrategy> strategyMap = new HashMap<>();
	public static final PwdStrategy WRITE_STRATEGY = new ScramStrategy();

	static {
		strategyMap.put("plain", new PlainStrategy());
		strategyMap.put("md5", new HashStrategy(PasswordType.md5));
		strategyMap.put("sha1", new HashStrategy(PasswordType.sha1));
		strategyMap.put("sha256", new HashStrategy(PasswordType.sha256));
		strategyMap.put("sha512", new HashStrategy(PasswordType.sha512));
		strategyMap.put("scram", WRITE_STRATEGY);
	}

	public VAuthProvider() {
		log.info("初始化[{}]", VAuthProvider.class.getName());
	}

	/**
	 * 如果sasl是plain,存储是md5的时候执行. web页面的也会走这个方法
	 */
	@Override
	public void authenticate(String username, String password) throws UnauthorizedException {
		if (username == null || password == null) {
			throw new UnauthorizedException();
		}
		Acc acc = ExtUtil.getUsername(username);
		if (acc == null) {
			throw new UnauthorizedException("invalide domain");
		}
		if (acc.isAdmin()) {
			autoCreateAdmin();
//			if (!Acc.ADMIN_PWD.equalsIgnoreCase(password)) {
//				throw new UnauthorizedException();
//			}
//			return;
		}

		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String rawpwd = authsvc.getPwd(acc);
		if (rawpwd == null) {
			throw new UnauthorizedException(acc.toString());
		}
		String[] arr = rawpwd.split("#");
		if (arr.length > 2) {
			PwdStrategy strategy = strategyMap.get(arr[0]);
			if (!strategy.valid(password, rawpwd)) {
				throw new UnauthorizedException();
			}
		}

	}

	@Override
	public void authenticate(String username, String token, String digest) throws UnauthorizedException {
		if (!(WRITE_STRATEGY instanceof ReversePwdStrategy)) {
			throw new UnsupportedOperationException("Digest authentication not supported because the password strategy["
					+ WRITE_STRATEGY.getClass().getName() + "] dont support reversion");
		}
		if (username == null || token == null || digest == null) {
			throw new UnauthorizedException();
		}
		Acc acc = ExtUtil.getUsername(username);
		if (acc == null) {
			throw new UnauthorizedException("invalide domain");
		}
		if (acc.isAdmin()) {
			autoCreateAdmin();
//			String anticipatedDigest = AuthFactory.createDigest(token, Acc.ADMIN_PWD);
//			if (!digest.equalsIgnoreCase(anticipatedDigest)) {
//				throw new UnauthorizedException();
//			}
//			return;
		}
		String password;
		try {
			password = ((ReversePwdStrategy) WRITE_STRATEGY).getPassword(acc);
		} catch (UserNotFoundException e) {
			throw new UnauthorizedException(e);
		}
		String anticipatedDigest = AuthFactory.createDigest(token, password);
		if (!digest.equalsIgnoreCase(anticipatedDigest)) {
			throw new UnauthorizedException();
		}
	}

	@Override
	public boolean isPlainSupported() {
		return WRITE_STRATEGY instanceof ReversePwdStrategy;
	}

	@Override
	public boolean isScramSupported() {
		return false;// strategy instanceof ScramStrategy;
	}

	@Override
	public boolean isDigestSupported() {
		return WRITE_STRATEGY instanceof ReversePwdStrategy;
	}

	/**
	 * 根据SASLAuthentication中对scram-sha-1算法的描述，如果此为true，那么不会去掉scram-sha-1,
	 * 因此此处必须是scram才行
	 */
	@Override
	public boolean supportsPasswordRetrieval() {
		return false;// strategy instanceof ScramStrategy;
	}

	@Override
	public String getPassword(String username) throws UserNotFoundException, UnsupportedOperationException {
		if (!supportsPasswordRetrieval()) {
			throw new UnsupportedOperationException("不支持获取密码");
		}
		Acc acc = ExtUtil.getUsername(username);
		if (acc == null) {
			throw new UserNotFoundException("invalide domain");
		}
		UserService authsvc = SpringContextHolder.getBean(UserService.class);
		String rawpwd = authsvc.getPwd(acc);
		String[] arr = rawpwd.split("#");
		if (arr.length > 2) {
			PwdStrategy strategy = strategyMap.get(arr[0]);
			if (strategy instanceof ReversePwdStrategy) {
				return ((ReversePwdStrategy) strategy).getPassword(acc);
			}
		}
		throw new UnsupportedOperationException("无法获取密码");
	}

	@Override
	public void setPassword(String username, String password) throws UserNotFoundException {
		Acc acc = ExtUtil.getUsername(username);
		if (acc == null) {
			throw new UserNotFoundException("invalide domain");
		}
		// if(acc.isAdmin()){
		// new BlowfishStrategy().setPassword(acc, password);
		// }else{
		WRITE_STRATEGY.setPassword(acc, password);
		// }
	}

	/**
	 * Checks to see if the user exists; if not, a new user is created.
	 *
	 * @param username
	 *            the username.
	 */
	private static void autoCreateAdmin() {
		// See if the user exists in the database. If not, automatically create
		// them.
		UserManager userManager = UserManager.getInstance();
		try {
			userManager.getUser("name:admin");
		} catch (UserNotFoundException unfe) {
			try {
				log.debug("JDBCAuthProvider: Automatically creating new user account for " + Acc.ADMIN_PWD);
				UserManager.getUserProvider().createUser("uid:" + Acc.ADMIN_UID, Acc.ADMIN_PWD,
						Acc.ADMIN_NAME, null);
			} catch (UserAlreadyExistsException uaee) {
				// Ignore.
			}
		}
	}
}
