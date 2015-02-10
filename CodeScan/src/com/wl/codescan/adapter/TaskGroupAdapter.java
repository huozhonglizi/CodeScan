package com.wl.codescan.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.wl.codescan.R;
import com.wl.codescan.callback.CallBackTaskGroupAdapter;
import com.wl.codescan.domain.TaskGroup;
import com.wl.codescan.util.SizeUtil;

/**
 * 
 * @类描述:任务分组数据适配器
 * @创建人:wanglei
 * @创建时间: 2014-11-22
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class TaskGroupAdapter extends DataAdapter<TaskGroup> {
	private CallBackTaskGroupAdapter listener;

	public TaskGroupAdapter(Context context, CallBackTaskGroupAdapter listener) {
		super(context);
		this.listener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final TaskGroup group = data.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.lv_task_item, null);
			convertView.setLayoutParams(new ListView.LayoutParams(
					LayoutParams.FILL_PARENT, SizeUtil.dpToPxInt(context, 35)));
			holder = new ViewHolder();

			holder.tv_groupName = (TextView) convertView
					.findViewById(R.id.tv_groupName);
			holder.cb_checked=(CheckBox) convertView.findViewById(R.id.cb_checked);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_groupName.setText(group.getCategoryName());
		holder.cb_checked.setChecked(group.isChecked());
		
		holder.cb_checked.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.checkClick(position,group);
			}
		});
		
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				listener.editClick(position, group);
				return true;
			}
		});
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.showDetailClick(group);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView tv_groupName;
		CheckBox cb_checked;
	}

}
