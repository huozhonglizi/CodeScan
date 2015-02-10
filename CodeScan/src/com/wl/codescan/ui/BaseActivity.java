package com.wl.codescan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View.OnClickListener;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.wl.codescan.db.DatabaseHelper;
import com.wl.codescan.util.IntentUtil;
import com.wl.codescan.util.MsgUtil;

/**
 * 
 * @类描述:所有Activity父类
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public abstract class BaseActivity extends Activity implements OnClickListener {
	protected DatabaseHelper databaseHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViews();
		setListener();
		initData();
	}

	public abstract void findViews();

	public abstract void setListener();

	public abstract void initData();

	@Override
	public void onBackPressed() {
		IntentUtil.finishActivity(this);
		super.onBackPressed();
	}

	/**
	 * 获取数据库操作对象
	 * 
	 * @return
	 */
	protected DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	/**
	 * 消息提示
	 * 
	 * @param msg
	 */
	protected void showMsg(String msg) {
		MsgUtil.showAppMsgGreen(this, msg, Gravity.TOP);
	}
	
	protected void showRedMsg(String msg){
		MsgUtil.showAppMsgRed(this, msg, Gravity.TOP);
	}

	@Override
	protected void onDestroy() {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
		super.onDestroy();

	}
}
