package com.wl.codescan.domain;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @类描述:任务分组实体
 * @创建人:wanglei
 * @创建时间: 2014-11-22
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
@DatabaseTable(tableName = "taskGroup")
public class TaskGroup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 任务分组id
	 */
	@DatabaseField(generatedId = true)
	private Long id;
	/**
	 * 分组名称
	 */
	@DatabaseField(dataType = DataType.STRING)
	private String categoryName;
	/**
	 * 分组创建时间
	 */
	@DatabaseField(dataType = DataType.LONG)
	private long createTime;
	/**
	 * 分组中任务的数量
	 */
	@DatabaseField(dataType = DataType.INTEGER)
	private int taskCount;
	/**
	 * 判断是否被选中
	 */
	private boolean isChecked;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
