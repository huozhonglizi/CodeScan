package com.wl.codescan.view;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackTaskDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
/**
 * 
 * @类描述:任务对话框
 * @创建人:wanglei
 * @创建时间: 2014-11-25
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class TaskDialog extends Dialog {
	private TextView tv_task_dlg_title;
	private EditText et_task_group;
	private Button bt_cancel;
	private Button bt_ok;

	public TaskDialog(Context context, final CallBackTaskDialog listener) {
		super(context, R.style.CustomDialog);
		setContentView(R.layout.dlg_task_input);
		getWindow().setWindowAnimations(R.style.dialogWindowAnim);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.CENTER);

		tv_task_dlg_title = (TextView) findViewById(R.id.tv_task_dlg_title);
		et_task_group = (EditText) findViewById(R.id.et_task_group);
		bt_cancel = (Button) findViewById(R.id.bt_cancel);
		bt_ok = (Button) findViewById(R.id.bt_ok);

		tv_task_dlg_title.setText("编辑任务");
		bt_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.clickCancel();
			}
		});

		bt_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.clickOK(et_task_group.getText().toString());
			}
		});
	}
	
	public void setText(String text){
		et_task_group.setText(text);
		et_task_group.setSelection(et_task_group.getText().length());
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		tv_task_dlg_title.setText(title);
	}
}
