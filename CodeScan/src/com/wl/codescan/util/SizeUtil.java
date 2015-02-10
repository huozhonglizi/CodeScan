package com.wl.codescan.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * 
 * @类描述：尺寸工具类
 * @创建人:wanglei
 * @创建时间: 2014-10-1
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class SizeUtil {
	/**
	 * 获取屏幕宽度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Activity activity) {

		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

	public static float dpToPx(Context context, float dp) {
		if (context == null) {
			return -1;
		}
		return dp * context.getResources().getDisplayMetrics().density;
	}

	public static float pxToDp(Context context, float px) {
		if (context == null) {
			return -1;
		}
		return px / context.getResources().getDisplayMetrics().density;
	}

	public static int dpToPxInt(Context context, float dp) {
		return (int) (dpToPx(context, dp) + 0.5f);
	}

	public static int pxToDpCeilInt(Context context, float px) {
		return (int) (pxToDp(context, px) + 0.5f);
	}
	/**
     * sp转px.
     *
     * @param value the value
     * @return the int
     */
    public static int sp2px(Context context,float value) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

}
