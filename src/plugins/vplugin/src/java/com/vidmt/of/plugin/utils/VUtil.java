package com.vidmt.of.plugin.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.vidmt.of.plugin.VPlugin;
import com.vidmt.of.plugin.spring.VDispatcherServelet;
import com.vidmt.of.plugin.spring.beans.SpringContextHolder;
import com.vidmt.of.plugin.sub.tel.TelPlugin;
import com.vidmt.of.plugin.sub.tel.entity.Order.PayType;
import com.vidmt.of.plugin.sub.tel.entity.SysLog;
import com.vidmt.of.plugin.sub.tel.entity.SysLog.Logtype;
import com.vidmt.of.plugin.sub.tel.old.controller.FileController.ResType;
import com.vidmt.of.plugin.sub.tel.old.dao.LogDao;

public class VUtil {
	private static final Logger log = LoggerFactory.getLogger(VPlugin.class);
	private static final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

	public static String time14() {
		return df.format(new Date());
	}

	public static double float6(double d) {
		return Double.parseDouble(String.format("%.6f", d));
	}

	public static int getDate(Date d) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(d);
		return cld.get(Calendar.DAY_OF_MONTH);
	}

	public static void log(SysLog log) {
		LogDao logdao = SpringContextHolder.getBean(LogDao.class);
		logdao.save(log);
	}

	public static void log(Logtype type, Long createBy, Long tgtuid, String content) {
		SysLog log = new SysLog(type, createBy);
		log.setTgtUid(tgtuid);
		log.setContent(content);
		log.setTime(new Date());
	}

	public static void mirrorObj(Object src, Object dest) {
		Field[] srcFields = src.getClass().getDeclaredFields();
		Class<?> destClz = dest.getClass();
		for (Field f : srcFields) {
			int modifierSrc = f.getModifiers();
			if (!Modifier.isStatic(modifierSrc) && !Modifier.isFinal(modifierSrc)) {
				try {
					Field destField = destClz.getDeclaredField(f.getName());
					int modifierDest = destField.getModifiers();
					if (!Modifier.isStatic(modifierDest) && !Modifier.isFinal(modifierSrc)) {
						destField.setAccessible(true);
						f.setAccessible(true);
						destField.set(dest, f.get(src));
						destField.setAccessible(false);
						f.setAccessible(false);
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public static String getNotifyUrl(PayType paytype) {
		String rawurl = JiveGlobals.getProperty(TelPlugin.KEY_PAY_NOTIFY_URL);
		if (!CommUtil.isEmpty(rawurl)) {
			return rawurl.replace(TelPlugin.VAR_PAYTYPE, paytype.name().toLowerCase());
		} else if (VDispatcherServelet.getServer() != null) {
			rawurl = "http://" + VDispatcherServelet.getServer() + "/plugins/vplugin/api/1/pay/{paytype}/notify.json";
		}
		return null;
	}

	public static String getDomain() {
		return XMPPServer.getInstance().getServerInfo().getXMPPDomain();
	}

	public static String buildJid(String uid) {
		return uid + "@" + getDomain();
	}

	public static String buildFullJid(String uid, String resource) {
		return buildJid(uid) + "/" + resource;
	}

	public static String getDateStr(int offset) {
		return CommUtil.fmtDate(new Date(System.currentTimeMillis() + offset * 24 * 60 * 60 * 1000));
	}

	public static File getRealPath(String path) {
		String ofHome = JiveGlobals.getHomeDirectory();
		File plgDir = new File(ofHome, "/plugins/" + VPlugin.NAME);
		return new File(plgDir, path);
		// Plugin vplugin =
		// XMPPServer.getInstance().getPluginManager().getPlugin(VPlugin.NAME);
		// PluginClassLoader pcl =
		// XMPPServer.getInstance().getPluginManager().getPluginClassloader(vplugin);
		// return pcl.getResource(path).toString();
	}

	public static String getStaticDir() {
		return JiveGlobals.getProperty(TelPlugin.KEY_RES_PATH, getRealPath("/").getAbsolutePath());
	}

	public static String saveRes(ResType type, Long uid, MultipartFile file) throws IOException {
		String staticPath = getStaticDir();
		String relPath = "/static/users/" + (uid / 100000) + "/" + uid + "/" + type.name().toLowerCase() + "/"
				+ VUtil.time14() + CommUtil.randInt(8);
		String ext = FileUtil.getExt(file.getOriginalFilename());
		if (!CommUtil.isEmpty(ext)) {
			relPath += ("." + ext);
		}
		File dstFile = new File(staticPath, relPath);
		File parent = dstFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		} else {
			if (type == ResType.AVATAR) {
				File[] farr = parent.listFiles();
				if (farr != null && farr.length > 0) {
					for (File f : farr) {
						f.delete();
					}
				}
			}
		}

		InputStream is = null;
		OutputStream os = null;
		try {
			is = file.getInputStream();
			os = new FileOutputStream(dstFile);
			FileCopyUtils.copy(is, os);
			return relPath;
		} finally {
			CommUtil.close(is, os);
		}
	}

	public static void deleteRes(Long uid) {
		String staticPath = getStaticDir();
		String relPath = "/static/users/" + (uid / 100000) + "/" + uid;
		File dstFile = new File(staticPath, relPath);
		if (dstFile.exists()) {
			try {
				FileUtils.deleteDirectory(dstFile);
			} catch (IOException e) {
				log.error("删除用户目录错误", e);
			}
		}
	}

}
