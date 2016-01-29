package com.vidmt.of.plugin.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于获取指定包名下的所有类名.<br/>
 * 并可设置是否遍历该包名下的子包的类名.<br/>
 * 并可通过Annotation(内注)来过滤，避免一些内部类的干扰.<br/>
 * 
 * @author Sodino E-mail:sodino@qq.com
 * @version Time：2014年2月10日 下午3:55:59
 */
public class PkgUtil {
	private static final Logger log = LoggerFactory.getLogger(PkgUtil.class);

	public static void main(String[] args) {
		// 标识是否要遍历该包路径下子包的类名
		// boolean recursive = false;
		boolean recursive = true;
		// 指定的包名
		// String pkg = "javax.crypto.spec";//
		// 为java/jre6/lib/jce.jar，普通的java工程默认已引用
		// String pkg = "javax.crypto";
		// String pkg = "lab.sodino";
		String pkg = "lab.sodino.clazz";
		List list = null;
		// list = getClassList(pkg, recursive, null);
		// 增加 author.class的过滤项，即可只选出ClassTestDemo
		// list = getClassList(pkg, recursive, author.class);
		//
		// for(int i = 0;i < list.size(); i ++){
		// System.out.println(i +":"+list.get(i));
		// }
	}

	public static Set<Class<?>> getClassList(String pkgName, boolean isRecursive,
			Class<? extends Annotation> annotation) throws ClassNotFoundException {
		Set<Class<?>> classList = new HashSet<Class<?>>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			// 按文件的形式去查找
			String strFile = pkgName.replaceAll("\\.", "/");
			Enumeration<URL> urls = loader.getResources(strFile);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url != null) {
					String protocol = url.getProtocol();
					String pkgPath = url.getPath();
					if ("file".equals(protocol)) {
						// 本地自己可见的代码
						findClassName(classList, pkgName, pkgPath, isRecursive, annotation);
					} else if ("jar".equals(protocol)) {
						// 引用第三方jar的代码
						findClassName(classList, pkgName, url, isRecursive, annotation);
					}
				}
			}
		} catch (IOException e) {
			log.error("获取class出错", e);
		}

		return classList;
	}

	/**
	 * class文件的查找。<br/>
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static void findClassName(Set<Class<?>> clazzList, String pkgName, String pkgPath, boolean isRecursive,
			Class<? extends Annotation> annotation) throws ClassNotFoundException {
		if (clazzList == null) {
			return;
		}
		File[] files = filterClassFiles(pkgPath);// 过滤出.class文件及文件夹
		if (files != null) {
			for (File f : files) {
				String fileName = f.getName();
				if (f.isFile()) {
					// .class 文件的情况
					String clazzName = getClassName(pkgName, fileName);
					addClassName(clazzList, clazzName, annotation);
				} else {
					// 文件夹的情况
					if (isRecursive) {
						// 需要继续查找该文件夹/包名下的类
						String subPkgName = pkgName + "." + fileName;
						String subPkgPath = pkgPath + "/" + fileName;
						findClassName(clazzList, subPkgName, subPkgPath, true, annotation);
					}
				}
			}
		}
	}

	/**
	 * 第三方Jar类库的引用。<br/>
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void findClassName(Set<Class<?>> clazzList, String pkgName, URL url, boolean isRecursive,
			Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException {
		JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
		JarFile jarFile = jarURLConnection.getJarFile();
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
			String clazzName = jarEntryName.replace("/", ".").replace(".class", "");
			int endIndex = clazzName.lastIndexOf(".");
			String prefix = null;
			if (endIndex > 0) {
				String prefix_name = clazzName.substring(0, endIndex);
				endIndex = prefix_name.lastIndexOf(".");
				if (endIndex > 0) {
					prefix = prefix_name.substring(0, endIndex);
				}
			}
			if (prefix != null && jarEntryName.endsWith(".class")) {
				// System.out.println("prefix:" + prefix +" pkgName:" +
				// pkgName);
				if (prefix.equals(pkgName)) {
					addClassName(clazzList, clazzName, annotation);
				} else if (isRecursive && prefix.startsWith(pkgName)) {
					// 遍历子包名：子类
					addClassName(clazzList, clazzName, annotation);
				}
			}
		}
	}

	private static File[] filterClassFiles(String pkgPath) {
		if (pkgPath == null) {
			return null;
		}
		// 接收 .class 文件 或 类文件夹
		return new File(pkgPath).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
	}

	private static String getClassName(String pkgName, String fileName) {
		int endIndex = fileName.lastIndexOf(".");
		String clazz = null;
		if (endIndex >= 0) {
			clazz = fileName.substring(0, endIndex);
		}
		String clazzName = null;
		if (clazz != null) {
			clazzName = pkgName + "." + clazz;
		}
		return clazzName;
	}

	private static void addClassName(Set<Class<?>> clazzList, String clazzName, Class<? extends Annotation> annotation)
			throws ClassNotFoundException {
		if (clazzList != null && clazzName != null&&!clazzName.matches(".+\\$\\d+$")) {
			Class<?> clazz = Class.forName(clazzName);
			if (clazz != null) {
				if (annotation == null||clazz.isAnnotationPresent(annotation)) {
					clazzList.add(clazz);
				}
			}
		}
	}
}