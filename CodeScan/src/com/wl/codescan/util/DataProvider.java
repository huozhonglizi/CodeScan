package com.wl.codescan.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.wl.codescan.domain.Contact;

/**
 * 
 * @类描述:数据提供
 * @创建人:wanglei
 * @创建时间: 2014-11-23
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class DataProvider {

	/**
	 * 获取手机联系人新
	 * 
	 * @param context
	 * @return
	 */
	public static List<Contact> getContactData(Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneData = resolver.query(Phone.CONTENT_URI, new String[] {
				Phone.DISPLAY_NAME, Phone.NUMBER }, null, null, null);
		if (phoneData != null) {
			while (phoneData.moveToNext()) {
				String phoneNumber = phoneData.getString(1);
				if (!TextUtils.isEmpty(phoneNumber)) {
					String displayName = phoneData.getString(0);
					contacts.add(new Contact(displayName, phoneNumber));
				}
			}
			phoneData.close();
		}

		// 获取sim卡上的联系人信息
		Uri uri = Uri.parse("content://icc/adn");
		Cursor simCursor = resolver.query(uri, new String[] {
				Phone.DISPLAY_NAME, Phone.NUMBER }, null, null, null);
		if (simCursor != null) {
			while (simCursor.moveToNext()) {
				String phoneNumber = simCursor.getString(1);
				if (!TextUtils.isEmpty(phoneNumber)) {
					String displayName = simCursor.getString(0);
					contacts.add(new Contact(displayName, phoneNumber));
				}
			}
			simCursor.close();
		}
		return contacts;
	}

}
