package com.wl.codescan.callback;

import com.wl.codescan.domain.TaskGroup;

public interface CallBackTaskGroupAdapter {

	/**
	 * 编辑
	 * 
	 * @param position
	 * @param group
	 */
	void editClick(int position, TaskGroup group);
	/**
	 * 选中某个条目
	 * @param position
	 * @param group
	 */
	void checkClick(int position,TaskGroup group);
	/**
	 * 进入详情界面
	 * @param group
	 */
	void showDetailClick(TaskGroup group);

}
