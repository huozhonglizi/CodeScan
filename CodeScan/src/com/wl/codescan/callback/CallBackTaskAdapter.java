package com.wl.codescan.callback;

import com.wl.codescan.domain.Task;

public interface CallBackTaskAdapter {
	/**
	 * 选中
	 * 
	 * @param position
	 * @param task
	 */
	void checkClick(int position, Task task);

	/**
	 * 编辑
	 * 
	 * @param position
	 * @param task
	 */
	void editClick(int position, Task task);

	/**
	 * 进入详情界面
	 * 
	 * @param task
	 */
	void detailClick(Task task);
}
