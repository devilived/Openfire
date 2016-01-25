package com.vidmt.of.plugin.sub.extdb;

import java.io.File;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.AnnoPlugin;

@AnnoPlugin
public class ExtDbPlugin implements Plugin {
	private static final Logger log = LoggerFactory.getLogger(ExtDbPlugin.class);
	
	private static final String ADMIN_PROV_KEY = "provider.admin.className";
	private static final String AUTH_PROV_KEY = "provider.auth.className";
	private static final String USER_PROV_KEY = "provider.user.className";
	private static final String GROUP_PROV_KEY = "provider.group.className";

	private String adminPrv;
	private String authPrv;
	private String userPrv;
	private String groupPrv;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
//		try {
//			Connection conn = ExtUtil.getConnection();
//			if (!"mysql".equals(conn.getMetaData().getDatabaseProductName().toLowerCase())) {
//				DbConnectionManager.closeConnection(conn);
//				throw new IllegalStateException(
//						"the db must be [MYSQL] to use the plugin:" + ExtDbPlugin.class.getName());
//			}
//			DbConnectionManager.closeConnection(conn);
//		} catch (SQLException e) {
//			throw new IllegalStateException("Can not get the db connection", e);
//		}

		adminPrv = JiveGlobals.getProperty(ADMIN_PROV_KEY);
		authPrv = JiveGlobals.getProperty(AUTH_PROV_KEY);
		userPrv = JiveGlobals.getProperty(USER_PROV_KEY);
		groupPrv = JiveGlobals.getProperty(GROUP_PROV_KEY);
		log.info("extdbplg 初始化数据库开始。。。。。");
		JiveGlobals.setProperty(ADMIN_PROV_KEY, VAdminProvider.class.getCanonicalName());
		JiveGlobals.setProperty(AUTH_PROV_KEY, VAuthProvider.class.getCanonicalName());
		JiveGlobals.setProperty(USER_PROV_KEY, VUserProvider.class.getCanonicalName());
		log.info("extdbplg 初始化数据库结束。。。。。");
		// JiveGlobals.setProperty(GROUP_PROV_KEY,
		// VGroupProvider.class.getCanonicalName());
	}

	@Override
	public void destroyPlugin() {
		JiveGlobals.setProperty(ADMIN_PROV_KEY, adminPrv);
		JiveGlobals.setProperty(AUTH_PROV_KEY, authPrv);
		JiveGlobals.setProperty(USER_PROV_KEY, userPrv);
		JiveGlobals.setProperty(GROUP_PROV_KEY, groupPrv);
	}
}
