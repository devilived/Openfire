package com.vidmt.of.plugin.sub.extdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtUtil {
	private static final Logger Log = LoggerFactory.getLogger(ExtUtil.class);

	private static final String dbDriver = null;// "com.mysql.jdbc.Driver";
	private static final String dbString = "jdbc:mysql://localhost:3306/openfire?rewriteBatchedStatements=true";
	private static final String dbUser = "root";
	private static final String dbPwd = "123456";

	static {
		if (dbDriver != null) {
			try {
				Class.forName(dbDriver).newInstance();
			} catch (Exception e) {
				Log.error("Unable to load JDBC driver: " + dbDriver, e);
			}
		}
	}

	public static Connection getConnection() throws SQLException {
		if (dbString != null) {
			DriverManager.getConnection(dbString, dbUser, dbPwd);
		}
		return DbConnectionManager.getConnection();
	}

	public static Acc getUsername(String username) {
		if (username.contains("@")) {
			// Check that the specified domain matches the server's domain
			int index = username.indexOf("@");
			String domain = username.substring(index + 1);
			if (domain.equals(XMPPServer.getInstance().getServerInfo().getXMPPDomain())) {
				username = username.substring(0, index);
			} else {
				// Unknown domain.
				return null;
			}
		}
		return new Acc(username);
	}
}
