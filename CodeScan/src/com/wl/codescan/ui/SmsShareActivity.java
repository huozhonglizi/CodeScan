package com.wl.codescan.ui;

import java.util.ArrayList;
import java.util.List;

import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.wl.codescan.R;
import com.wl.codescan.util.IntentUtil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 
 * @类描述:短信分享界面
 * @创建人:wanglei
 * @创建时间: 2014-11-24
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class SmsShareActivity extends BaseActivity {
	private EditText et_numbers;
	private ImageView iv_choose;
	private Button bt_share;
	private Button bt_left;

	private static final int CONTACT_REQ = 100;
	private String content; // 分享的内容

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_sms_share);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {
		et_numbers = (EditText) findViewById(R.id.et_numbers);
		iv_choose = (ImageView) findViewById(R.id.iv_choose);
		bt_share = (Button) findViewById(R.id.bt_share);
		bt_left = (Button) findViewById(R.id.bt_left);
	}

	@Override
	public void setListener() {
		iv_choose.setOnClickListener(this);
		bt_share.setOnClickListener(this);
		bt_left.setOnClickListener(this);
	}

	@Override
	public void initData() {
		if (getIntent().hasExtra("content")) {
			content = getIntent().getStringExtra("content");

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_choose:
			Intent intent = new Intent(SmsShareActivity.this,
					SmsContactActivity.class);
			startActivityForResult(intent, CONTACT_REQ);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.bt_share:
			// 分享
			final List<String> selectedNumbers = getSelectedNumbers();
			if (selectedNumbers.size() < 1) {
				showRedMsg("请输入手机号码");
				return;
			}
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					sendMessage(selectedNumbers); // 发送短信
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					showMsg("分享成功");
					super.onPostExecute(result);
				}
				
				
				
			}.execute();
			break;
		case R.id.bt_left:
			IntentUtil.finishActivity(SmsShareActivity.this);
			break;

		}
	}

	/**
	 * 获取用户选择的手机号码
	 * 
	 * @return
	 */
	private List<String> getSelectedNumbers() {
		List<String> selectedNumbers = new ArrayList<String>();
		String numbers = et_numbers.getText().toString().trim();
		if (TextUtils.isEmpty(numbers)) {
			showRedMsg("请输入手机号码");
			return null;
		}

		String[] sNumbers = numbers.split(",");
		if (sNumbers != null && sNumbers.length > 0) {
			for (String number : sNumbers) {
				selectedNumbers.add(number);
			}
		}
		return selectedNumbers;
	}

	private void sendMessage(List<String> numbers) {
		SmsManager smsManager = SmsManager.getDefault();

		for (String number : numbers) {
			ArrayList<String> messages = smsManager.divideMessage(content);
			for (String message : messages) {
				smsManager.sendTextMessage(number, null, message, null, null);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CONTACT_REQ && resultCode == 200) {
			ArrayList<String> selectedNumbers = data
					.getStringArrayListExtra("contacts");
			StringBuilder builder = new StringBuilder();
			if (selectedNumbers != null && selectedNumbers.size() > 0) {
				String originNumber = et_numbers.getText().toString().trim();
				builder.append(originNumber);

				if (originNumber.endsWith(",")) {
					for (String number : selectedNumbers) {
						builder.append(number);
						builder.append(",");
					}
					builder.deleteCharAt(builder.length() - 1);
				} else {
					if (TextUtils.isEmpty(originNumber)) {
						for (String number : selectedNumbers) {
							builder.append(number);
							builder.append(",");
						}
						builder.deleteCharAt(builder.length() - 1);
					} else {
						for (String number : selectedNumbers) {
							builder.append(",");
							builder.append(number);
						}
					}

				}
				et_numbers.setText(builder.toString());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
