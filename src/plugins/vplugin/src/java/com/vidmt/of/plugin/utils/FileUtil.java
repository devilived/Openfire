package com.vidmt.of.plugin.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class FileUtil {
	public static String getExt(String f) {
		int idx = f.lastIndexOf('.');
		if (idx > -1) {
			return f.substring(f.lastIndexOf('.')+1);
		}
		return null;
	}

	/** 把文件中的字符串读出来 **/
	public static String readFile(File f, String cs) {
		if (f.length() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("file is too large");
		}
		ByteArrayOutputStream baos = null;

		BufferedInputStream bis = null;
		try {
			baos = new ByteArrayOutputStream((int) f.length());

			bis = new BufferedInputStream(new FileInputStream(f));
			byte[] buffer = new byte[1024];
			int len;
			while ((len = bis.read(buffer)) > 0) {
				baos.write(buffer, 0, len);
			}
			return new String(baos.toByteArray(), cs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommUtil.close(baos, bis);
		}
		return null;
	}

	/**
	 * <p>
	 * 把文件从src复制到dest
	 * </p>
	 * 
	 * @param src
	 * @param dest
	 */
	public static void copy(String src, String dest) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(src));
			bos = new BufferedOutputStream(new FileOutputStream(dest));
			byte b[] = new byte[1024];
			while (bis.read(b) > 0) {
				bos.write(b);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CommUtil.close(bis);
			CommUtil.close(bos);
		}
	}
}
