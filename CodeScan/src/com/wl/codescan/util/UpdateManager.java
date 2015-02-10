package com.wl.codescan.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackUpdateShutdown;
import com.wl.codescan.domain.UpdateInfo;
import com.wl.codescan.view.MyProgress;

@SuppressLint("NewApi")
public class UpdateManager {

	private UpdateInfo updateInfo;

	private Context mContext;

	private Dialog downloadDialog;
	/* 下载包安装路径 */
	private static final String savePath = "/sdcard/RuiJieScanCode/";

	private static final String saveFileName = savePath + "RuiJieScanCode.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private MyProgress mProgress;

	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;

	private int progress;

	private Thread downLoadThread;
	private boolean isStar = false;

	private boolean interceptFlag = false;
	
	private CallBackUpdateShutdown listener;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				if (downloadDialog.isShowing()) {
					downloadDialog.dismiss();
				}
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context,CallBackUpdateShutdown listener) {
		this.mContext = context;
		this.listener=listener;
	}

	// 外部接口让主Activity调用
	public void checkUpdateInfo(Activity activity, UpdateInfo updateInfo) {
		this.updateInfo = updateInfo;
		showDownloadDialog(activity);
	}

	private void showDownloadDialog(Activity activity) {
		downloadDialog = new Dialog(activity, R.style.CustomDialog);
		final LayoutInflater inflater = LayoutInflater.from(activity);
		View v = inflater.inflate(R.layout.dlg_update, null);
		Button dialog_btncancel = (Button) v
				.findViewById(R.id.dialog_btncancel);
		final Button dialog_btnok = (Button) v.findViewById(R.id.dialog_btnok);
		TextView upContent2 = (TextView) v.findViewById(R.id.upContent2);
		TextView dialog_titles = (TextView) v.findViewById(R.id.dialog_titles);
		downloadDialog.setContentView(v);
		upContent2.setText(updateInfo.getDescription().replace("|", "\n"));
		dialog_titles.setText("最新版本:" + updateInfo.getVersion());
		dialog_btnok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_btnok.setClickable(false);
				if (downLoadThread != null && isStar) {
					downLoadThread.stop();
				}
				downloadApk();
			}
		});

		dialog_btncancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				downloadDialog.dismiss();
				listener.onShutdown();
			}
		});
		mProgress = (MyProgress) v.findViewById(R.id.progress);
		mProgress.setMax(100);
		mProgress.setText();
		downloadDialog.setCancelable(false);
		// dialog_btncancel.setVisibility(View.GONE);
		downloadDialog.show();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(updateInfo.getUrl());
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length)*100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 * @param url
	 */

	private void downloadApk() {
		if (!isStar) {
			downLoadThread = new Thread(mdownApkRunnable);
			downLoadThread.start();
			isStar = true;
		}

	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
