package com.vidmt.of.plugin.sub.extdb.pwdstrategy;

import org.jivesoftware.openfire.user.UserNotFoundException;

import com.vidmt.of.plugin.sub.extdb.Acc;

public interface PwdStrategy {
	/**
	 * 把原始密码写入数据库
	 */
	public void setPassword(Acc acc, String plainpwd) throws UserNotFoundException;

	/**
	 * 调整策略后，暂时用不上
	 */
	@Deprecated
	public boolean valid(Acc acc, String plainpwd) throws UserNotFoundException;

	public boolean valid(String plainpwd, String savedPassword);

	public static interface ReversePwdStrategy extends PwdStrategy {
		/**
		 * 返回原始密码，用来做diguest
		 */
		public String getPassword(Acc acc) throws UserNotFoundException;
	}
}
