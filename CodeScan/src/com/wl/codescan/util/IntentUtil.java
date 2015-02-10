package com.wl.codescan.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import com.wl.codescan.R;

import android.app.Activity;
import android.content.Intent;

/**
 * 
 * @类描述:意图工具类
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class IntentUtil {

	/**
	 * 进入其他界面
	 * 
	 * @param activity
	 * @param cls
	 */
	public static void startActivity(Activity activity, Class<?> cls,
			boolean isFinish) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
		if (isFinish) {
			activity.finish();
		}
	}

	/**
	 * 关闭界面
	 * 
	 * @param activity
	 */
	public static void finishActivity(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.in_from_left,
				R.anim.out_to_right);
	}
	
	
	public static void startActivity(Activity activity, Class<?> cls,
			Map<String, Serializable> values, boolean isFinish) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		for (Entry<String, Serializable> entry : values.entrySet()) {
			intent.putExtra(entry.getKey(), entry.getValue());
		}
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
		if (isFinish) {
			activity.finish();
		}
	}
}
