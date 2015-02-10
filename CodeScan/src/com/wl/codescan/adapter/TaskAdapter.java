package com.wl.codescan.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackTaskAdapter;
import com.wl.codescan.domain.Task;
import com.wl.codescan.util.SizeUtil;

public class TaskAdapter extends DataAdapter<Task> {
	private CallBackTaskAdapter listener;

	public TaskAdapter(Context context, CallBackTaskAdapter listener) {
		super(context);
		this.listener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Task task = data.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.lv_task_child_item,
					null);
			convertView.setLayoutParams(new ListView.LayoutParams(
					ListView.LayoutParams.FILL_PARENT, SizeUtil.dpToPxInt(
							context, 35)));
			holder = new ViewHolder();

			holder.cb_checked = (CheckBox) convertView
					.findViewById(R.id.cb_checked);
			holder.tv_result = (TextView) convertView
					.findViewById(R.id.tv_result);
			holder.tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.cb_checked.setChecked(task.isChecked());
		holder.tv_result.setText(task.getTaskResult());

		if (task.getStatus() == 1) {
			holder.tv_status.setText("已收货");
			holder.tv_status.setTextColor(Color.GREEN);
		} else if (task.getStatus() == 2) {
			holder.tv_status.setText("已退货");
			holder.tv_status.setTextColor(Color.BLACK);
		} else {
			holder.tv_status.setText("未提交");
			holder.tv_status.setTextColor(Color.RED);
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.detailClick(task);
			}
		});

		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				listener.editClick(position, task);
				return true;
			}
		});

		holder.cb_checked.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.checkClick(position, task);
			}
		});

		return convertView;
	}

	static class ViewHolder {
		CheckBox cb_checked;
		TextView tv_result;
		TextView tv_status;
	}

	@Override
	public void setData(List<Task> data) {
		List<Task> unSubmitList = new ArrayList<Task>();
		List<Task> submitList = new ArrayList<Task>();
		List<Task> changeList = new ArrayList<Task>();

		if (data != null && data.size() > 0) {
			for (Task task : data) {
				if (task.getStatus() == 0) {
					unSubmitList.add(task);
				} else if (task.getStatus() == 1) {
					submitList.add(task);
				} else if (task.getStatus() == 2) {
					changeList.add(task);
				}
			}
		}
		this.data.clear();
		this.data.addAll(unSubmitList);
		this.data.addAll(submitList);
		this.data.addAll(changeList);
		notifyDataSetChanged();
		
		
	}

}
