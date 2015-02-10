package com.wl.codescan.util;

/**
 * 
 * @类描述:常量类
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class Constant {
	/* QQ平台 */
	public static final String QQ_APP_ID = "1103517895";
	public static final String QQ_APP_KEY = "DqPg3ElOrUdbhxoi";
	/* 微信平台相关 */
	public static final String WX_APP_ID = "wx3d71cc60b7e44bae";
	/* 图片缓存地址 */
	public static final String IMAGE_PATH = "/sdcard/codescan/images/";
	/* 提交地址 */
//	public static final String COMMIT_URL = "http://api.ruijie.com.cn/ecp/orderproduct/commit";
	public static final String COMMIT_URL = "http://192.168.1.11:8080/Test/submit.txt";
	/**
	 * 退换货地址
	 */
//	public static final String CHANGE_URL = "http://api.ruijie.com.cn/ecp/orderproduct/change";
	public static final String CHANGE_URL = "http://192.168.1.11:8080/Test/submit.txt";
	/**
	 * 获取app更新地址
	 */
	public static final String APP_UPDATE_URL = "http://192.168.1.11:8080/Test/appupdate.xml";

}
