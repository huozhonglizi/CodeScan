package com.wl.codescan.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

public class DownLoadFileTask {
	public static File getFile(String urlPath, String localPath,
			ProgressDialog pd) throws Exception {
		URL url = new URL(urlPath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5000);
		connection.setRequestMethod("GET");
		if (connection.getResponseCode() == 200) {
			int total = connection.getContentLength(); // 获取总长度
			int progress = 0; // 当前下载的长度
			InputStream is = connection.getInputStream();
			File file = new File(localPath);
			FileOutputStream fos = new FileOutputStream(file);
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) != -1) {
				progress += len;
				pd.setProgress(progress);
				fos.write(buffer, 0, len);
			}
			fos.flush(); // 将缓存区的内容写入文件
			fos.close();
			is.close();
			return file;
		}
		return null;

	}

}
