package com.wl.codescan.ui;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackUpdateShutdown;
import com.wl.codescan.domain.UpdateInfo;
import com.wl.codescan.util.Constant;
import com.wl.codescan.util.IntentUtil;
import com.wl.codescan.util.SharedPreUtil;
import com.wl.codescan.util.UpdateManager;

/**
 * 
 * @类描述:splash界面
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class SplashActivity extends BaseActivity {
	private Handler handler = new Handler();
	private static final long DELAY_MILLS = 1000L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_splash);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {

	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {
		// 检测是否需要更新
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
				if (result == null||TextUtils.isEmpty(result.getUrl())) {
					loadUI();
				} else {
					String versionNow = getVersionName();
//					showMsg(versionNow);
					if (!versionNow.equals(result.getVersion())) {
						UpdateManager updateManager = new UpdateManager(
								SplashActivity.this,new CallBackUpdateShutdown() {
									
									@Override
									public void onShutdown() {
										loadUI();
									}
								});
						updateManager.checkUpdateInfo(SplashActivity.this,
								result);
					} else {
						loadUI(); // 不需要升级
					}
				}
				super.onPostExecute(result);
			}

		}.execute(url);
	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 获取应用程序的版本号
	 * 
	 * @return 该应用的当前版本号
	 */
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

	/**
	 * 进入其他UI
	 */
	private void loadUI() {
//		showMsg("当前已经是最新版本");
		final boolean isFirstIn = SharedPreUtil.getInstance(this).getBoolData(
				SharedPreUtil.IS_FIRST_IN, true);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (isFirstIn) {
					IntentUtil.startActivity(SplashActivity.this,
							HelpActivity.class, true);
				} else {
					IntentUtil.startActivity(SplashActivity.this,
							TaskGroupManageActivity.class, true);
				}
			}
		}, DELAY_MILLS);

	}

}
