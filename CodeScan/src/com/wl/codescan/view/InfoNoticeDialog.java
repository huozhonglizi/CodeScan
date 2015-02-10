package com.wl.codescan.view;

import com.wl.codescan.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
/**
 * 
 * @类描述:信息提示框
 * @创建人:wanglei
 * @创建时间: 2014-12-26
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class InfoNoticeDialog extends Dialog{
	
	private ImageView iv_close;
	private TextView tv_info;

	public InfoNoticeDialog(Context context) {
		super(context, R.style.CustomDialog);
		setContentView(R.layout.dlg_info_notice);
		getWindow().setWindowAnimations(R.style.dialogWindowAnim);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.CENTER);
		setCanceledOnTouchOutside(false);
		
		iv_close=(ImageView) findViewById(R.id.iv_close);
		tv_info=(TextView) findViewById(R.id.tv_info);
		
		iv_close.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	/**
	 * 设置显示的文字
	 * @param info
	 */
	public void setInfo(String info,int textSize){
		tv_info.setText(info);
		tv_info.setTextSize(textSize);
	}

}
