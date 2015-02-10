package com.wl.codescan.view;

import com.wl.codescan.R;
import com.wl.codescan.util.SizeUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

public class AboutDialog extends Dialog{

	public AboutDialog(Context context,View.OnClickListener listener) {
		super(context, R.style.CustomDialog);
		setContentView(R.layout.dlg_about_us);
		getWindow().setWindowAnimations(R.style.dialogWindowAnim);
		getWindow().setLayout(SizeUtil.dpToPxInt(context, 200), SizeUtil.dpToPxInt(context, 245));
		getWindow().setGravity(Gravity.CENTER);
	}

}
