package com.wl.codescan.view;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackOrderDialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
/**
 * 
 * @类描述:输入订单号对话框
 * @创建人:wanglei
 * @创建时间: 2014-12-2
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class OrderNumberDialog extends Dialog {

	private TextView tv_task_dlg_title;
	private EditText et_order_number;
	private Button bt_cancel;
	private Button bt_ok;

	public OrderNumberDialog(Context context, final CallBackOrderDialog listener) {
		super(context, R.style.CustomDialog);
		setContentView(R.layout.dlg_order_num);
		getWindow().setWindowAnimations(R.style.dialogWindowAnim);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.CENTER);

		et_order_number = (EditText) findViewById(R.id.et_order_number);
		bt_cancel = (Button) findViewById(R.id.bt_cancel);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		tv_task_dlg_title=(TextView) findViewById(R.id.tv_task_dlg_title);

		bt_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.clickCancel();
			}
		});
		bt_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.clickOK(et_order_number.getText().toString().trim());
			}
		});
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		tv_task_dlg_title.setText(title);
	}
	
	/**
	 * 设置提示文字
	 * @param hintText
	 */
	public void setHintText(String hintText){
		et_order_number.setHint(hintText);
	}
	
	public void clearData(){
		et_order_number.setText("");
	}

}
