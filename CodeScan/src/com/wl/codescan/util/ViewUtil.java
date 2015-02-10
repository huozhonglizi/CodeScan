package com.wl.codescan.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.wl.codescan.R;

/**
 * 
 * @类描述:view工具类
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class ViewUtil {

	/**
	 * shake view and focus view
	 * 
	 * @param view
	 *            view对象
	 * @param isFocus
	 *            是否需要focus
	 */
	public static void shakeAndFocusOnView(View view, boolean isFocus) {
		Animation shakeAniamtion = AnimationUtils.loadAnimation(
				view.getContext(), R.anim.shake);
		shakeAniamtion.setDuration(2000);
		view.startAnimation(shakeAniamtion);

		if (isFocus) {
			view.requestFocus();
		}
	}

	/**
	 * 设置控件是否可用
	 * 
	 * @param view
	 * @param isEnabled
	 *            是否可用
	 */
	public static void setEnable(View view, boolean isEnabled) {
		if (view != null) {
			view.setEnabled(isEnabled);
		}
	}
	
}
