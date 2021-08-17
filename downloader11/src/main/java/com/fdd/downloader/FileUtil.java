package com.fdd.downloader;

import android.text.TextUtils;

import java.io.File;

public class FileUtil {

	public static final long KB = 1024;
	public static final long MB = KB * 1024;
	public static final long GB = MB * 1024;

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

	public static String getParentFilePath(String filePath) {
		if (filePath.endsWith("/")) {
			filePath = filePath.substring(0, filePath.length() - 1);
		}
		int lastSplitIndex = filePath.lastIndexOf("/");
		if (lastSplitIndex >= 0) {
			return filePath.substring(0, lastSplitIndex);
		}
		return "";
	}

	public static boolean dirEquals(String dir1, String dir2) {
		if (dir1 != null && dir1.endsWith("/")) {
			dir1 = dir1.substring(0, dir1.length() - 1);
		}

		if (dir2 != null && dir2.endsWith("/")) {
			dir2 = dir2.substring(0, dir2.length() - 1);
		}

		return TextUtils.equals(dir1, dir2);
	}

	public static String convertFileSize(long size) {
    	if (size < KB) {
    		return size + " B";
		} else if (size < MB) {
    		return String.format("%.2f KB", (float) size / KB);
		} else if (size < GB) {
			return String.format("%.2f MB", (float) size / MB);
		} else {
			return String.format("%.2f GB", (float) size / GB);
		}
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

