package com.wl.codescan.ui;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.j256.ormlite.table.TableUtils;
import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackUpdateShutdown;
import com.wl.codescan.domain.Task;
import com.wl.codescan.domain.TaskGroup;
import com.wl.codescan.domain.UpdateInfo;
import com.wl.codescan.util.Constant;
import com.wl.codescan.util.FileUtil;
import com.wl.codescan.util.IntentUtil;
import com.wl.codescan.util.SharedPreUtil;
import com.wl.codescan.util.UpdateManager;
import com.wl.codescan.view.AboutDialog;
import com.wl.codescan.view.DeleteNoticeDialog;

/**
 * 
 * @类描述:设置界面
 * @创建人:wanglei
 * @创建时间: 2014-12-2
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class SettingActivity extends BaseActivity {
	private LinearLayout ll_sound;
	private LinearLayout ll_vibrate;
	private LinearLayout ll_clean_Cache;
	private LinearLayout ll_about;
	private LinearLayout ll_help;
	private LinearLayout ll_checkVersion;

	private Button bt_left;

	private ImageView iv_sound;
	private ImageView iv_vibrate;

	private int unCheckRes = R.drawable.hm_checkbox_bd_off;
	private int checkRes = R.drawable.hm_checkbox_bd_no;

	private AboutDialog aboutDialog;
	private DeleteNoticeDialog deleteNoticeDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {
		ll_sound = (LinearLayout) findViewById(R.id.ll_sound);
		ll_vibrate = (LinearLayout) findViewById(R.id.ll_vibrate);
		ll_clean_Cache = (LinearLayout) findViewById(R.id.ll_clean_Cache);
		ll_about = (LinearLayout) findViewById(R.id.ll_about);
		ll_help = (LinearLayout) findViewById(R.id.ll_help);
		bt_left = (Button) findViewById(R.id.bt_left);
		ll_checkVersion = (LinearLayout) findViewById(R.id.ll_checkVersion);

		iv_sound = (ImageView) findViewById(R.id.iv_sound);
		iv_vibrate = (ImageView) findViewById(R.id.iv_vibrate);
	}

	@Override
	public void setListener() {
		ll_sound.setOnClickListener(this);
		ll_vibrate.setOnClickListener(this);
		ll_clean_Cache.setOnClickListener(this);
		ll_about.setOnClickListener(this);
		bt_left.setOnClickListener(this);
		ll_help.setOnClickListener(this);
		ll_checkVersion.setOnClickListener(this);
	}

	@Override
	public void initData() {
		updateSoundUI();
		updateVibrateUI();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_sound:
			SharedPreUtil.getInstance(this).setBoolData(
					SharedPreUtil.IS_SOUND,
					!SharedPreUtil.getInstance(this).getBoolData(
							SharedPreUtil.IS_SOUND, false));
			updateSoundUI();
			break;
		case R.id.ll_vibrate:
			SharedPreUtil.getInstance(this).setBoolData(
					SharedPreUtil.IS_VIBRATE,
					!SharedPreUtil.getInstance(this).getBoolData(
							SharedPreUtil.IS_VIBRATE, false));
			updateVibrateUI();
			break;
		case R.id.ll_clean_Cache:
			if (deleteNoticeDialog == null) {
				deleteNoticeDialog = new DeleteNoticeDialog(
						SettingActivity.this, new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									TableUtils.clearTable(getHelper()
											.getConnectionSource(), Task.class);
									TableUtils.clearTable(getHelper()
											.getConnectionSource(),
											TaskGroup.class);
									FileUtil.delFolder(Constant.IMAGE_PATH);
									showMsg("清除数据成功");
								} catch (SQLException e) {
									e.printStackTrace();
									showRedMsg("清除表中数据异常");
								}
							}
						});
			}
			deleteNoticeDialog.setTitle("你确定要清除所有数据吗?");
			deleteNoticeDialog.setCancelBtnShow(true);
			deleteNoticeDialog.show();
			break;
		case R.id.ll_about:
			if (aboutDialog == null) {
				aboutDialog = new AboutDialog(this, null);
			}
			aboutDialog.show();
			break;
		case R.id.bt_left:
			IntentUtil.finishActivity(this);
			break;
		case R.id.ll_help:
			IntentUtil.startActivity(this, HelpActivity.class, false);
			break;

		case R.id.ll_checkVersion: // 检测更新
			updateApp();
			break;

		}
	}

	private void updateApp() {
		String url = Constant.APP_UPDATE_URL;

		new AsyncTask<String, Void, UpdateInfo>() {

			@Override
			protected UpdateInfo doInBackground(String... params) {
				URL url;
				try {
					url = new URL(params[0]);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();
					if (length > 0) {
						return UpdateInfo.getFromInputstream(is);
					} else {
						return null;
					}

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(UpdateInfo result) {
				if (result == null) {
					showMsg("当前已经是最新版本");
				} else {
					String versionNow = getVersionName();
					if (!versionNow.equals(result.getVersion())) {
						UpdateManager updateManager = new UpdateManager(
								SettingActivity.this,new CallBackUpdateShutdown() {
									
									@Override
									public void onShutdown() {
										
									}
								});
						updateManager.checkUpdateInfo(SettingActivity.this,
								result);
					} else {
						showMsg("当前已经是最新版本");
					}
				}
				super.onPostExecute(result);
			}

		}.execute(url);
	}

	/**
	 * 更新声音
	 */
	private void updateSoundUI() {
		boolean isSound = SharedPreUtil.getInstance(this).getBoolData(
				SharedPreUtil.IS_SOUND, true);
		if (isSound) {
			iv_sound.setImageResource(checkRes);
		} else {
			iv_sound.setImageResource(unCheckRes);
		}
	}

	private void updateVibrateUI() {
		boolean isVibrate = SharedPreUtil.getInstance(this).getBoolData(
				SharedPreUtil.IS_VIBRATE, true);
		if (isVibrate) {
			iv_vibrate.setImageResource(checkRes);
		} else {
			iv_vibrate.setImageResource(unCheckRes);
		}

	}

	private String getVersionName() {
		PackageManager pManager = getPackageManager();
		try {
			PackageInfo info = pManager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
