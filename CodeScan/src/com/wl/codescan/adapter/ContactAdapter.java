package com.wl.codescan.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackContactAdapter;
import com.wl.codescan.domain.Contact;
import com.wl.codescan.util.SizeUtil;
/**
 * 
 * @类描述:联系人数据适配器
 * @创建人:wanglei
 * @创建时间: 2014-11-23
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class ContactAdapter extends DataAdapter<Contact> {
	private CallBackContactAdapter listener;

	public ContactAdapter(Context context, CallBackContactAdapter listener) {
		super(context);
		this.listener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Contact contact = data.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.lv_contact_item, null);
			convertView.setLayoutParams(new ListView.LayoutParams(
					ListView.LayoutParams.MATCH_PARENT, SizeUtil.dpToPxInt(context, 55)));
			holder = new ViewHolder();

			holder.tv_displayName = (TextView) convertView
					.findViewById(R.id.tv_displayName);
			holder.tv_phoneNumber = (TextView) convertView
					.findViewById(R.id.tv_phoneNumber);
			holder.cb_checked = (CheckBox) convertView
					.findViewById(R.id.cb_checked);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_displayName.setText(contact.getDisplayName());
		holder.tv_phoneNumber.setText(contact.getPhoneNumber());
		holder.cb_checked.setChecked(contact.isChecked());

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.simpleClick(position, contact);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView tv_displayName;
		TextView tv_phoneNumber;
		CheckBox cb_checked;
	}

}
