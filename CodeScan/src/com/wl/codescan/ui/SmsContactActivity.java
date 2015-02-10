package com.wl.codescan.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wl.codescan.R;
import com.wl.codescan.adapter.ContactAdapter;
import com.wl.codescan.callback.CallBackContactAdapter;
import com.wl.codescan.domain.Contact;
import com.wl.codescan.util.DataProvider;
import com.wl.codescan.util.IntentUtil;

/**
 * 
 * @类描述:短信联系人界面
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class SmsContactActivity extends BaseActivity {
	private Button bt_left;
	private ImageView iv_ok;
	private ListView lv_contact;
	private TextView tv_checked_contacts;

	private ContactAdapter adapter;

	private ArrayList<Contact> selectedContacts = new ArrayList<Contact>();
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_sms_contact);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {
		bt_left = (Button) findViewById(R.id.bt_left);
		iv_ok = (ImageView) findViewById(R.id.iv_ok);
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		tv_checked_contacts = (TextView) findViewById(R.id.tv_checked_contacts);
	}

	@Override
	public void setListener() {
		bt_left.setOnClickListener(this);
		iv_ok.setOnClickListener(this);
	}

	@Override
	public void initData() {
		selectedContacts.clear();
		pd = new ProgressDialog(this);
		pd.setMessage("正在获取联系人信息");

		new AsyncTask<Void, Void, List<Contact>>() {

			@Override
			protected void onPreExecute() {
				pd.show();
				super.onPreExecute();
			}

			@Override
			protected List<Contact> doInBackground(Void... params) {

				return DataProvider.getContactData(SmsContactActivity.this);
			}

			@Override
			protected void onPostExecute(List<Contact> result) {
				pd.dismiss();
				if (result != null && result.size() > 0) {
					if (adapter == null) {
						adapter = new ContactAdapter(SmsContactActivity.this,
								new CallBackContactAdapter() {

									@Override
									public void simpleClick(int position,
											Contact contact) {
										contact.setChecked(!contact.isChecked());
										if (contact.isChecked()) {
											selectedContacts.add(contact);
										} else {
											selectedContacts.remove(contact);
										}
										adapter.getDataSource().set(position,
												contact);
										adapter.notifyDataSetChanged();
										updateCheckedList();
									}
								});
						adapter.setData(result);
						lv_contact.setAdapter(adapter);
					} else {
						adapter.setData(result);
						adapter.notifyDataSetChanged();
					}
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			IntentUtil.finishActivity(this);
			break;

		case R.id.iv_ok:
			if (selectedContacts.size() < 1) {
				showRedMsg("没有选择联系人");
				return;
			}

			setResult(
					200,
					getIntent().putStringArrayListExtra("contacts",
							getPhoneNumbers()));
			IntentUtil.finishActivity(SmsContactActivity.this);
			break;

		}
	}

	/**
	 * 从选择的联系人中选择出电话号码
	 * 
	 * @return
	 */
	private ArrayList<String> getPhoneNumbers() {
		ArrayList<String> numbers = new ArrayList<String>();
		if (selectedContacts != null && selectedContacts.size() > 0) {
			for (Contact contact : selectedContacts) {
				numbers.add(contact.getPhoneNumber());
			}
		}
		return numbers;
	}

	/**
	 * 更新选中列表的显示
	 */
	private void updateCheckedList() {
		StringBuilder builder = new StringBuilder();
		for (Contact contact : selectedContacts) {
			builder.append(contact.getDisplayName());
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		tv_checked_contacts.setText(builder.toString());
	}

}
