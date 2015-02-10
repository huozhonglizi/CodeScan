package com.wl.codescan.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wl.codescan.R;
import com.wl.codescan.util.SizeUtil;

/**
 * 
 * @类描述:删除提示对话框
 * @创建人:wanglei
 * @创建时间: 2014-12-5
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class DeleteNoticeDialog extends Dialog {
	private Button bt_delete_cancel;
	private Button bt_delete_ok;
	private TextView tv_notice;

	public DeleteNoticeDialog(Context context, View.OnClickListener listener) {
		super(context, R.style.CustomDialog);
		setContentView(R.layout.dlg_delete_notice);
		getWindow().setWindowAnimations(R.style.dialogWindowAnim);
		// getWindow().setLayout(SizeUtil.dpToPxInt(context, 200),
		// SizeUtil.dpToPxInt(context, 120));
		getWindow().setGravity(Gravity.CENTER);

		bt_delete_cancel = (Button) findViewById(R.id.bt_delete_cancel);
		bt_delete_ok = (Button) findViewById(R.id.bt_delete_ok);
		tv_notice = (TextView) findViewById(R.id.tv_notice);

		bt_delete_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}

		});

		bt_delete_ok.setOnClickListener(listener);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		tv_notice.setText(title);
	}

	/**
	 * 设置取消按钮是否可见
	 * 
	 * @param show
	 */
	public void setCancelBtnShow(boolean show) {
		if (show) {
			bt_delete_cancel.setVisibility(View.VISIBLE);
		} else {
			bt_delete_cancel.setVisibility(View.GONE);
		}
	}

}
