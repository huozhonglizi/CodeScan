package com.wl.codescan.domain;

import java.io.InputStream;

import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class UpdateInfo {
	/**
	 * 版本号
	 */
	private String version;
	/**
	 * app名称
	 */
	private String name;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 下载地址
	 */
	private String url;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static UpdateInfo getFromInputstream(InputStream is)
			throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");

		UpdateInfo updateInfo = null;

		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("update".equals(parser.getName())) {
					updateInfo = new UpdateInfo();
				} else if ("version".equals(parser.getName())) {
					eventType = parser.next();
					updateInfo.setVersion(parser.getText());
				} else if ("name".equals(parser.getName())) {
					eventType = parser.next();
					updateInfo.setName(parser.getText());
				} else if ("description".equals(parser.getName())) {
					eventType = parser.next();
					updateInfo.setDescription(parser.getText());
				} else if ("url".equals(parser.getName())) {
					eventType = parser.next();
					updateInfo.setUrl(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:

				break;
			}
			eventType = parser.next();
		}
		return updateInfo;
	}

}
