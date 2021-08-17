package com.fdd.downloader;

import android.text.TextUtils;

import java.io.File;

class FileUtil {

	public static String getFileName(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		int lastIndex = path.lastIndexOf("/");
		if (lastIndex > 0 && lastIndex < path.length() - 1) {
			return path.substring(lastIndex + 1);
		} else {
			return path;
		}
	}

	public static String getFileNameWithoutExtension(String path) {
		String filename = getFileName(path);
		if (filename.length() > 0) {
			int dotPos = filename.lastIndexOf('.');
			if (0 < dotPos) {
				return filename.substring(0, dotPos);
			}
		}
		return filename;
	}

	public static String getFileExtension(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}

		int dotIndex = path.lastIndexOf(".");
		if (dotIndex <= 0 || dotIndex >= path.length() - 1) {
			return "";
		}
		int slashIndex = path.lastIndexOf("/");
		if (slashIndex > dotIndex) {
			return "";
		}

		return path.substring(dotIndex + 1);
	}

	/**
	 * 拼接路径
	 * @param basePath
	 * @param path
	 * @return
	 */
	public static String concat(final String basePath, final String path) {
		final int len = basePath == null ? 0 : basePath.length();
		if (len == 0) {
			return path;
		}
		final char ch = basePath.charAt(len - 1);
		if (ch == File.separatorChar) {
			return basePath + path;
		}
		return basePath + File.separatorChar + path;
	}
}

