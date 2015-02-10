package com.wl.codescan.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @类描述:请求客户端,所以请求都通过此类发出
 * @创建人:wanglei
 * @创建时间: 2014-10-13
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class RequestClient {
	private static AsyncHttpClient client = new AsyncHttpClient();

	static {
		client.setTimeout(5000);
	}

	/**
	 * get方法
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}

	/**
	 * get方法
	 * 
	 * @param url
	 * @param params
	 * @param handler
	 */
	public static void get(String url, RequestParams params,
			JsonHttpResponseHandler handler) {
		client.get(url, params, handler);
	}

	/**
	 * post方法
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}

	/**
	 * post方法
	 * 
	 * @param url
	 * @param params
	 * @param handler
	 */
	public static void post(String url, RequestParams params,
			JsonHttpResponseHandler handler) {
		client.post(url, params, handler);
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 * @param handler
	 */
	public static void downloadFile(String url,
			BinaryHttpResponseHandler handler) {
		client.get(url, handler);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}

}
