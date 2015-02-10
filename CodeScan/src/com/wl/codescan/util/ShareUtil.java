package com.wl.codescan.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.tauth.IUiListener;

/**
 * 
 * @类描述:分享工具类
 * @创建人:wanglei
 * @创建时间: 2014-11-22
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class ShareUtil {
	private static ShareUtil instance;
	private static IWXAPI api;
	private static SendMessageToWX.Req req;

	private ShareUtil(Context context) {
		api = WXAPIFactory.createWXAPI(context, Constant.WX_APP_ID);
		api.registerApp(Constant.WX_APP_ID);
	}

	public static ShareUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (ShareUtil.class) {
				instance = new ShareUtil(context);
			}
		}
		return instance;
	}

	/**
	 * 分享到qq
	 * 
	 * @param activity
	 * @param title
	 * @param summary
	 * @param imageUrl
	 * @param listener
	 */
	public static void shareToQQ(Activity activity, String title,
			String summary, String imageUrl, IUiListener listener) {
		QQAuth mQQAuth = QQAuth.createInstance(Constant.QQ_APP_ID,
				activity.getApplicationContext());
		QQToken token = mQQAuth.getQQToken();
		QQShare qqShare = new QQShare(activity.getApplicationContext(), token);
		Bundle params = new Bundle();
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		if (!TextUtils.isEmpty(imageUrl)) {
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, imageUrl);
		}
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "扫码通");
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,
				QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
		qqShare.shareToQQ(activity, params, listener);
	}

	/**
	 * 分享到微信
	 * 
	 * @param text
	 *            分享的文字
	 * @param imagePath
	 *            图片地址
	 * @param isSendToMates
	 *            是否分享到朋友圈
	 */
	public void shareToWX(String text, String imagePath, boolean isSendToMates) {
		File file = new File(imagePath);
		WXMediaMessage msg = new WXMediaMessage();
		if (file.exists()) {
			WXImageObject imgObj = new WXImageObject();
			imgObj.setImagePath(imagePath);

			msg.mediaObject = imgObj;
			msg.title = text;

			Bitmap bmp = BitmapFactory.decodeFile(imagePath);
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
			bmp.recycle();
			msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);
		} else {
			WXTextObject textObject = new WXTextObject();
			textObject.text = text;

			msg.mediaObject = textObject;
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = isSendToMates ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	/**
	 * 发短信分享
	 * 
	 * @param address
	 * @param content
	 */
	public static void shareToSms(List<String> address, String content) {
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> contents = smsManager.divideMessage(content);
		if (address.size() > 0) {
			for (String addre : address) {
				for (String con : contents) {
					smsManager.sendTextMessage(addre, null, con, null, null);
				}
			}
		}
	}
}
