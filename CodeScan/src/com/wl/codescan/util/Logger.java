package com.wl.codescan.util;

import android.util.Log;

/**
 * log类的重写 控制log输出及屏蔽 debug模式下允许输出日志
 * 
 * @author wangLei
 */

public class Logger {
	private static String TAG = "CodeScan";

	public static void d(String tag, Object msg) {
		if (SysConfig.isDebug) {
			Log.d(tag, String.valueOf(msg));
		}
	}

	public static void e(String tag, Object msg) {
		if (SysConfig.isDebug) {
			Log.e(tag, String.valueOf(msg));
		}
	}

	public static void i(String tag, Object msg) {
		if (SysConfig.isDebug) {
			Log.i(tag, String.valueOf(msg));
		}
	}

	public static void v(String tag, Object msg) {
		if (SysConfig.isDebug) {
			Log.v(tag, String.valueOf(msg));
		}
	}

	public static void w(String tag, Object msg) {
		if (SysConfig.isDebug) {
			Log.w(tag, String.valueOf(msg));
		}
	}

	public static void d(Object msg) {
		if (SysConfig.isDebug) {
			Log.d(TAG, String.valueOf(msg));
		}
	}

	public static void e(Object msg) {
		if (SysConfig.isDebug) {
			Log.e(TAG, String.valueOf(msg));
		}
	}

	public static void i(Object msg) {
		if (SysConfig.isDebug) {
			Log.i(TAG, String.valueOf(msg));
		}
	}

	public static void v(Object msg) {
		if (SysConfig.isDebug) {
			Log.v(TAG, String.valueOf(msg));
		}
	}

	public static void w(Object msg) {
		if (SysConfig.isDebug) {
			Log.w(TAG, String.valueOf(msg));
		}
	}

}
