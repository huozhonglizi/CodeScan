package com.wl.codescan.domain;

import java.io.Serializable;

/**
 * 
 * @类描述:联系人实体
 * @创建人:wanglei
 * @创建时间: 2014-11-23
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class Contact implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 联系人姓名
	 */
	private String displayName;
	/**
	 * 联系人电话号码
	 */
	private String phoneNumber;
	
	private boolean isChecked;

	public Contact() {
	}

	public Contact(String displayName, String phoneNumber) {
		this.displayName = displayName;
		this.phoneNumber = phoneNumber;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	

}
