package com.dhl.base.utils;

import android.text.TextUtils;
import android.util.Log;

import com.dhl.base.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

public class Logger {

	public static final boolean PRINT_LOG = BuildConfig.DEBUG;
	public static final String TAG = "Logger";

	public static void v(String tag, String msg) {
		try {
			if (!PRINT_LOG) return;
			String[] info = getStackInfo();
			tag = TextUtils.isEmpty(tag) ? info[0] : tag;
			Log.v(tag, info[1] + ":" + msg);
		} catch (Exception e) {
		}
	}

	public static void d(String tag, String msg) {
		try {
			if (!PRINT_LOG) return;
			String[] info = getStackInfo();
			tag = TextUtils.isEmpty(tag) ? info[0] : tag;
			Log.d(tag, info[1] + ":" + msg);
		} catch (Exception e) {
		}
	}

	public static void i(String tag, String msg) {
		try {
			if (!PRINT_LOG) return;
			String[] info = getStackInfo();
			tag = TextUtils.isEmpty(tag) ? info[0] : tag;
			Log.i(tag, info[1] + ":" + msg);
		} catch (Exception e) {
		}
	}

	public static void w(String tag, String msg) {
		try {
			if (!PRINT_LOG) return;
			String[] info = getStackInfo();
			tag = TextUtils.isEmpty(tag) ? info[0] : tag;
			Log.w(tag, info[1] + ":" + msg);
		} catch (Exception e) {
		}
	}

	public static void e(String tag, String msg) {
		try {
			if (!PRINT_LOG) return;
			String[] info = getStackInfo();
			tag = TextUtils.isEmpty(tag) ? info[0] : tag;
			Log.e(tag, info[1] + ":" + msg);
		} catch (Exception e) {
		}
	}

	public static void json(String tag, Object msg) {
		try {
			if (!PRINT_LOG) return;
			String str = null;
			String className = null;
			if (msg instanceof JSONObject) {
				str = ((JSONObject)msg).toString(2);
			} else if (msg instanceof JSONArray) {
				str = ((JSONArray)msg).toString(2);
			} else {
				if (msg instanceof String) {
					str = (String) msg;
				} else {
					str = Gsons.INSTANCE.toJson(msg);
					if (msg != null) {
						className = msg.getClass().getSimpleName();
					}
				}
				if (str == null) {
					str = "";
				}
				if (str.startsWith("{")) {
					JSONObject jsonObject = new JSONObject(str);
					str = jsonObject.toString(2);
				} else if (str.startsWith("[")) {
					JSONArray jsonArray = new JSONArray(str);
					str = jsonArray.toString(2);
				}
			}

			String[] info = getStackInfo();
			tag = TextUtils.isEmpty(tag) ? info[0] : tag;

			int maxLength = 3000;

			if (str != null && str.length() <= maxLength) {
				String clsTag = (className == null ? "" : className + "->");
				Log.i(tag, info[1] + ":" + clsTag + str);
				return;
			}


			Log.d(tag, "============== long json begin ==============");
			String clsTag = (className == null ? " \n" : className + "->\n");
			while (str != null && str.length() > 0) {
				if (str.length() <= maxLength) {
					Log.i(tag, info[1] + ":" + clsTag + str);
					str = null;
				} else {
					int index = str.lastIndexOf(',', maxLength) + 2;
					Log.i(tag, info[1] + ":" + clsTag + str.substring(0, index));
					str = str.substring(index);
				}
			}
			Log.d(tag, "============== long json end ==============");
		} catch (Exception e) {
		}
	}

	private static String[] getStackInfo() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace == null || stackTrace.length < 5) return new String[]{TAG, ""};
		int index = 4;
		String className = stackTrace[index].getFileName();
		//String methodName = stackTrace[index].getMethodName();
		int lineNumber = stackTrace[index].getLineNumber();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("(")
				.append(className)
				.append(":")
				.append(lineNumber)
				.append(")");

		return new String[]{TAG, stringBuilder.toString()};
	}
}
