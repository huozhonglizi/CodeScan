package com.wl.codescan.view;

import android.app.Dialog;
import android.content.Context;

import com.wl.codescan.R;

/**
 * 自定义对话框
 * 
 * @author wanglei
 * 
 */
public class CustomDlg extends Dialog {

	private static CustomDlg authDialog;
	private Context context;

	private CustomDlg(Context context) {
		super(context);
		this.context = context;

	}

	private CustomDlg(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public static CustomDlg createDlg(Context context) {

		authDialog = new CustomDlg(context, R.style.CustomDialog);
		return authDialog;
	}
}
