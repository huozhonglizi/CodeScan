package com.wl.codescan.util;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wl.codescan.view.AppMsg;

/**
 * @ClassName: MsgUtil
 * @Description: TODO(显示消息的工具类)
 * @author: 石浩
 * @date: 2014-10-17 下午2:37:59
 * @最后修改人：石浩
 * @最后修改时间：2014-10-17 下午2:37:59
 * 
 * 
 */
public class MsgUtil {

	/**
	 * @Title:
	 * @Description: TODO(显示自定义形式的消息，背景为红色)
	 * @最后修改人：石浩
	 * @最后修改时间：2014-10-17 下午2:57:23
	 * @param activity
	 *            当前Activity
	 * @param msg
	 *            所要显示的消息
	 * @param gravity
	 *            显示的位置Gravity.bottom/Gravity.top
	 */
	public static void showAppMsgRed(Activity activity, String msg, int gravity) {
		AppMsg appMsg = AppMsg.makeText(activity, msg, AppMsg.STYLE_ALERT);
		appMsg.setPriority(AppMsg.PRIORITY_HIGH);
		// appMsg.setLayoutGravity(gravity);
		appMsg.setAnimation(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		FrameLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.topMargin = SizeUtil.dpToPxInt(activity, 45);
		params.gravity = gravity;
		appMsg.setLayoutParams(params);
		appMsg.cancelAll();
		appMsg.show();
	}

	/**
	 * @Title:
	 * @Description: TODO(显示自定义形式的消息，背景为蓝色)
	 * @最后修改人：石浩
	 * @最后修改时间：2014-10-17 下午2:57:23
	 * @param activity
	 *            当前Activity
	 * @param msg
	 *            所要显示的消息
	 * @param gravity
	 *            显示的位置Gravity.bottom/Gravity.top
	 */
	public static void showAppMsgBlue(Activity activity, String msg, int gravity) {
		AppMsg appMsg = AppMsg.makeText(activity, msg, AppMsg.STYLE_CONFIRM);
		appMsg.setPriority(AppMsg.PRIORITY_HIGH);
		appMsg.setLayoutGravity(gravity);
		appMsg.setAnimation(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		appMsg.cancelAll();
		appMsg.show();
	}

	/**
	 * @Title:
	 * @Description: TODO(显示自定义形式的消息，背景为绿色)
	 * @最后修改人：石浩
	 * @最后修改时间：2014-10-17 下午2:57:23
	 * @param activity
	 *            当前Activity
	 * @param msg
	 *            所要显示的消息
	 * @param gravity
	 *            显示的位置Gravity.bottom/Gravity.top
	 */
	public static void showAppMsgGreen(Activity activity, String msg,
			int gravity) {
		AppMsg appMsg = AppMsg.makeText(activity, msg, AppMsg.STYLE_INFO);
		appMsg.setPriority(AppMsg.PRIORITY_HIGH);
		// appMsg.setLayoutGravity(gravity);
		appMsg.setAnimation(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		FrameLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.topMargin = SizeUtil.dpToPxInt(activity, 45);
		params.gravity = gravity;
		appMsg.setLayoutParams(params);
		appMsg.cancelAll();
		appMsg.show();
	}

	/**
	 * @Title:
	 * @Description: TODO(显示短时间toast)
	 * @最后修改人：石浩
	 * @最后修改时间：2014-10-17 下午2:42:23
	 * @param context
	 * @param text
	 *            显示的文本
	 */
	public static void showToastShort(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @Title:
	 * @Description: TODO(显示长时间的toast)
	 * @最后修改人：石浩
	 * @最后修改时间：2014-10-17 下午2:43:04
	 * @param context
	 * @param text
	 *            显示的文本
	 */
	public static void showToastLong(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
}
