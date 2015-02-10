package com.wl.codescan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * @类描述：sharePreferences工具类
 * @创建人:wanglei
 * @创建时间: 2014-10-1
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class SharedPreUtil {
	private static SharedPreUtil sharedPreUtil;
	private static SharedPreferences sharedPreferences;
	private static Editor editor;

	private final static String SP_NAME = "config";
	public final static String IS_SOUND="isSound";
	public final static String IS_VIBRATE="isVibrate";
	public final static String IS_FIRST_IN="isFirstIn";

	private SharedPreUtil(Context context) {
		sharedPreferences = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}

	public synchronized static SharedPreUtil getInstance(Context context) {
		if (sharedPreUtil == null) {
			sharedPreUtil = new SharedPreUtil(context);
		}
		return sharedPreUtil;
	}

	public int getIntData(String key, int defValue) {
		return sharedPreferences.getInt(key, defValue);
	}

	public long getLongData(String key, long defValue) {
		return sharedPreferences.getLong(key, defValue);
	}

	public String getStringData(String key, String defValue) {
		return sharedPreferences.getString(key, defValue);
	}

	public boolean getBoolData(String key, boolean defValue) {
		return sharedPreferences.getBoolean(key, defValue);
	}

	public void setIntData(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void setLongData(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	public void setStringData(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void setBoolData(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void remove(String key) {
		editor.remove(key);
		editor.commit();
	}

}
