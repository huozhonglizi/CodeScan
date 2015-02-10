package com.wl.codescan.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackShareDialog;
import com.wl.codescan.domain.ShareType;

/**
 * 
 * @类描述:分享对话框
 * @创建人:wanglei
 * @创建时间: 2014-11-23
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class ShareDialog extends Dialog {
	private LinearLayout ll_share_emaill;
//	private LinearLayout ll_share_qq;
	private LinearLayout ll_share_sms;
	private LinearLayout ll_share_weixin;

	public ShareDialog(Context context, final CallBackShareDialog listener) {
		super(context, R.style.CustomDialog);
		setContentView(R.layout.dlg_share);
		getWindow().setWindowAnimations(R.style.dialogWindowAnim);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);

		ll_share_emaill = (LinearLayout) findViewById(R.id.ll_share_email);
//		ll_share_qq = (LinearLayout) findViewById(R.id.ll_share_qq);
		ll_share_sms = (LinearLayout) findViewById(R.id.ll_share_sms);
		ll_share_weixin = (LinearLayout) findViewById(R.id.ll_share_weixin);

		ll_share_emaill.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.share(ShareType.EMAIL);
			}
		});
//		ll_share_qq.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				listener.share(ShareType.QQ);
//
//			}
//		});
		ll_share_sms.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.share(ShareType.SMS);
			}
		});
		ll_share_weixin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.share(ShareType.WEIXIN);

			}
		});

	}

}
