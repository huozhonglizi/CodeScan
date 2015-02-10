package com.wl.codescan.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackMenuPop;
import com.wl.codescan.util.SizeUtil;

public class MenuPopupWindow extends PopupWindow {
	private TextView tv_delete;
	private TextView tv_share;
	private TextView tv_add;

	public MenuPopupWindow(Context context, final CallBackMenuPop listener) {
		super(context);
		View contentView = View.inflate(context, R.layout.pop_menu, null);
		setContentView(contentView);
		setWidth(SizeUtil.dpToPxInt(context, 140));
		setHeight(SizeUtil.dpToPxInt(context, 135));
		tv_delete = (TextView) contentView.findViewById(R.id.tv_delete);
		tv_share = (TextView) contentView.findViewById(R.id.tv_share);
		tv_add = (TextView) contentView.findViewById(R.id.tv_add);
		
		tv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.deleteClick();
			}
		});
		
		tv_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.shareClick();
			}
		});
		
		tv_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.addClick();
			}
		});
		

		setFocusable(true);// 取得焦点
		setBackgroundDrawable(new ColorDrawable(Color.rgb(63, 63,
				63)));
		/** 设置PopupWindow弹出和退出时候的动画效果 */
		setAnimationStyle(R.style.CustomDialog);
	}

}
