package com.wl.codescan.domain;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @类描述:任务
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
@DatabaseTable(tableName = "task")
public class Task implements Serializable{
	/**
	 * 任务id
	 */
	@DatabaseField(generatedId = true)
	private Long id;
	/**
	 * 任务扫描结果
	 */
	@DatabaseField(dataType = DataType.STRING)
	private String taskResult;
	/**
	 * 任务扫描成功时间
	 */
	@DatabaseField(dataType = DataType.LONG)
	private long scanTime;
	/**
	 * 任务图片所在的位置
	 */
	@DatabaseField(dataType = DataType.STRING)
	private String imgUrl;

	@DatabaseField(foreign = true, foreignColumnName = "id")
	private TaskGroup taskGroup;
	
	@DatabaseField
	private int status;

	/**
	 * 收货订单号
	 */
	@DatabaseField
	private String submitOrderNumber;
	/**
	 * 退货订单号
	 */
	@DatabaseField
	private String changeOrderNumber;
	
	/**
	 * 是否被选中
	 */
	private boolean isChecked;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskResult() {
		return taskResult;
	}

	public void setTaskResult(String taskResult) {
		this.taskResult = taskResult;
	}

	public long getScanTime() {
		return scanTime;
	}

	public void setScanTime(long scanTime) {
		this.scanTime = scanTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public TaskGroup getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(TaskGroup taskGroup) {
		this.taskGroup = taskGroup;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSubmitOrderNumber() {
		return submitOrderNumber;
	}

	public void setSubmitOrderNumber(String submitOrderNumber) {
		this.submitOrderNumber = submitOrderNumber;
	}

	public String getChangeOrderNumber() {
		return changeOrderNumber;
	}

	public void setChangeOrderNumber(String changeOrderNumber) {
		this.changeOrderNumber = changeOrderNumber;
	}

}
