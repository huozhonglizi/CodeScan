package com.wl.codescan.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.wl.codescan.R;
import com.wl.codescan.adapter.TaskGroupAdapter;
import com.wl.codescan.callback.CallBackTaskGroupAdapter;
import com.wl.codescan.domain.Task;
import com.wl.codescan.domain.TaskGroup;
import com.wl.codescan.util.CharacterParser;
import com.wl.codescan.util.DataCache;
import com.wl.codescan.util.IntentUtil;
import com.wl.codescan.util.SizeUtil;
import com.wl.codescan.view.CustomDlg;
import com.wl.codescan.view.DeleteNoticeDialog;
import com.wl.codescan.view.Sidebar;
import com.wl.codescan.view.Sidebar.OnTouchingLetterChangedListener;

/**
 * 
 * @类描述:任务组别管理
 * @创建人:wanglei
 * @创建时间: 2014-11-22
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class TaskGroupManageActivity extends BaseActivity implements
		CallBackTaskGroupAdapter {
	private Button bt_setting;
	private ImageView iv_add;
	private ListView lv_group;

	private EditText et_query;
	private ImageButton ib_search_clear;
	private TextView tv_letter;
	private Sidebar sidebar;
	private CheckBox cb_all_selected;
	private ImageButton ib_delete;
	private TextView tv_checked_num;

	private TaskGroupAdapter adapter;

	// ------任务对话框相关------
	private TextView tv_task_dlg_title;
	private EditText et_task_group;
	private Button bt_cancel;
	private Button bt_ok;
	private CustomDlg taskDlg;

	// -----------删除提示对话框---------------
	private CustomDlg deleteNoticeDlg;
	private Button bt_delete_cancel;
	private Button bt_delete_ok;

	// --------保存逻辑数据--------------
	private int selectPosition = -1;
	private TaskGroup selectGroup = null;
	private static final int STYLE_TASK_DLG_ADD = 10;
	private static final int STYLE_TASK_DLG_UPDATE = 20;
	private int curStyle = 10;

	private Handler handler = new Handler();
	private OverLayThread overLayThread = new OverLayThread();

	private DeleteNoticeDialog deleteNoticeDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_group);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {
		bt_setting = (Button) findViewById(R.id.bt_setting);
		iv_add = (ImageView) findViewById(R.id.iv_add);
		lv_group = (ListView) findViewById(R.id.lv_group);

		et_query = (EditText) findViewById(R.id.et_query);
		ib_search_clear = (ImageButton) findViewById(R.id.ib_search_clear);
		tv_letter = (TextView) findViewById(R.id.tv_letter);
		sidebar = (Sidebar) findViewById(R.id.sidebar);
		cb_all_selected = (CheckBox) findViewById(R.id.cb_all_selected);
		ib_delete = (ImageButton) findViewById(R.id.ib_delete);
		tv_checked_num = (TextView) findViewById(R.id.tv_checked_num);

		initTaskDlg();
		initDeleteNoticeDlg();
	}

	@Override
	public void setListener() {
		bt_setting.setOnClickListener(this);
		iv_add.setOnClickListener(this);
		ib_search_clear.setOnClickListener(this);
		ib_delete.setOnClickListener(this);

		et_query.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String text = et_query.getText().toString().trim();
				if (hasFocus) {
					if (!TextUtils.isEmpty(text)) {
						ib_search_clear.setVisibility(View.VISIBLE);
					} else {
						ib_search_clear.setVisibility(View.INVISIBLE);
					}
				}
			}
		});

		et_query.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text = et_query.getText().toString().trim();
				if (et_query.isFocused() && !TextUtils.isEmpty(text)) {
					ib_search_clear.setVisibility(View.VISIBLE);
				} else {
					ib_search_clear.setVisibility(View.INVISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = et_query.getText().toString().trim();
				if (!TextUtils.isEmpty(text)) {
					getData(et_query.getText().toString().trim());
				} else {
					getData("");
				}
			}
		});

		cb_all_selected
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						changeAllGroupState(isChecked);
						tv_checked_num.setText(getSelectedGroupNumbers());
					}
				});
	}

	@Override
	public void initData() {
		DataCache.SCREEN_WIDTH = SizeUtil.getScreenWidth(this);
		DataCache.SCREEN_HEIGHT = SizeUtil.getScreenHeight(this);

		lv_group.setTextFilterEnabled(true);
		tv_letter.setVisibility(View.GONE);

		sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				if (adapter == null || adapter.getDataSource() == null
						|| adapter.getDataSource().size() < 1) {
					return;
				}
				int position = searchPositionInDataSource(s);
				lv_group.setSelection(position);
				tv_letter.setText(s);
				tv_letter.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overLayThread);
				handler.postDelayed(overLayThread, 200);
			}
		});
		getData("");

	}

	private void getData(final String filter) {
		new AsyncTask<Void, Void, ArrayList<TaskGroup>>() {

			@Override
			protected ArrayList<TaskGroup> doInBackground(Void... params) {
				try {
					if (TextUtils.isEmpty(filter)) {
						return (ArrayList<TaskGroup>) getHelper()
								.getTaskGroupDao().queryForAll();
					} else {
						QueryBuilder<TaskGroup, Long> builder = getHelper()
								.getTaskGroupDao().queryBuilder();
						builder.where()
								.like("categoryName", "%" + filter + "%");
						return (ArrayList<TaskGroup>) builder.query();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<TaskGroup> result) {
				if (adapter == null) {
					adapter = new TaskGroupAdapter(
							TaskGroupManageActivity.this,
							TaskGroupManageActivity.this);
					adapter.setData(result);
					lv_group.setAdapter(adapter);
				} else {
					adapter.setData(result);
				}
			}

		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_setting:
			IntentUtil.startActivity(TaskGroupManageActivity.this,
					SettingActivity.class, false);
			break;

		case R.id.iv_add:
			curStyle = STYLE_TASK_DLG_ADD;
			showTaskDialog(null);
			break;
		case R.id.bt_cancel:
			dismissDlg(taskDlg);
			break;
		case R.id.bt_ok:
			String categoryName = et_task_group.getText().toString().trim();
			if (TextUtils.isEmpty(categoryName)) {
				showMsg("请输入分组名称");
				return;
			}
			if (curStyle == STYLE_TASK_DLG_UPDATE) {
				selectGroup.setCategoryName(categoryName);
				try {
					getHelper().getTaskGroupDao().update(selectGroup);
					adapter.getDataSource().set(selectPosition, selectGroup);
					adapter.notifyDataSetChanged();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else {
				TaskGroup taskGroup = new TaskGroup();
				taskGroup.setCategoryName(categoryName);
				taskGroup.setCreateTime(System.currentTimeMillis());
				taskGroup.setTaskCount(0);
				try {
					getHelper().getTaskGroupDao().create(taskGroup);
					adapter.addElement(taskGroup);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			dismissDlg(taskDlg);

			break;
		case R.id.bt_delete_cancel:
			dismissDlg(deleteNoticeDlg);
			break;

		case R.id.bt_delete_ok:
			dismissDlg(deleteNoticeDlg);
			try {
				List<TaskGroup> taskGroups = getSelectedGroups();
				if (taskGroups.size() < 1) {
					showMsg("请至少选择一项");
					return;
				}
				for (TaskGroup group : taskGroups) {
					DeleteBuilder<Task, Long> builder = getHelper()
							.getTaskDao().deleteBuilder();
					builder.where().eq("taskGroup_id", group.getId());
					builder.delete();
					getHelper().getTaskGroupDao().delete(group);
				}
				adapter.removeElements(taskGroups);
				cb_all_selected.setChecked(false);
				changeAllGroupState(false);
				tv_checked_num.setText(getSelectedGroupNumbers());

			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case R.id.ib_search_clear:
			et_query.setText("");
			break;
		case R.id.ib_delete:
			showDeleteDlg();
			break;

		}
	}

	/**
	 * 初始化任务分组编辑对话框
	 */
	private void initTaskDlg() {
		taskDlg = CustomDlg.createDlg(this);
		View dlgView = View.inflate(this, R.layout.dlg_task_input, null);

		tv_task_dlg_title = (TextView) dlgView
				.findViewById(R.id.tv_task_dlg_title);
		et_task_group = (EditText) dlgView.findViewById(R.id.et_task_group);
		bt_cancel = (Button) dlgView.findViewById(R.id.bt_cancel);
		bt_ok = (Button) dlgView.findViewById(R.id.bt_ok);

		bt_cancel.setOnClickListener(this);
		bt_ok.setOnClickListener(this);

		taskDlg.setContentView(dlgView);
		taskDlg.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
	}

	/**
	 * 显示任务分组编辑对话框
	 * 
	 * @param task
	 */
	private void showTaskDialog(TaskGroup group) {
		if (taskDlg != null && !taskDlg.isShowing()) {
			if (curStyle == STYLE_TASK_DLG_UPDATE && group != null) {
				et_task_group.setText(group.getCategoryName());
				et_task_group.setHint("分组名称");
				tv_task_dlg_title.setText("编辑分组");
			} else {
				et_task_group.setHint("分组名称");
				et_task_group.setText("");
				tv_task_dlg_title.setText("新建分组");
			}
		}
		taskDlg.show();
	}

	/**
	 * 关闭对话框
	 * 
	 * @param dialog
	 */
	private void dismissDlg(Dialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * 初始化删除提示对话框
	 */
	private void initDeleteNoticeDlg() {
		deleteNoticeDlg = CustomDlg.createDlg(this);
		View dlgView = View.inflate(this, R.layout.dlg_delete_group_notice,
				null);
		bt_delete_cancel = (Button) dlgView.findViewById(R.id.bt_delete_cancel);
		bt_delete_ok = (Button) dlgView.findViewById(R.id.bt_delete_ok);

		bt_delete_cancel.setOnClickListener(this);
		bt_delete_ok.setOnClickListener(this);

		deleteNoticeDlg.setContentView(dlgView);
		taskDlg.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
	}

	/**
	 * 显示删除提示对话框
	 */
	private void showDeleteDlg() {
		if (adapter == null || adapter.getDataSource().size() < 1) {
			return;
		}
		List<TaskGroup> taskGroups = getSelectedGroups();
		if (taskGroups.size() < 1) {
			if (deleteNoticeDialog == null) {
				deleteNoticeDialog = new DeleteNoticeDialog(
						TaskGroupManageActivity.this,
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								deleteNoticeDialog.dismiss();
							}
						});
			}
			deleteNoticeDialog.show();
			return;
		}

		if (deleteNoticeDlg != null && !deleteNoticeDlg.isShowing()) {
			deleteNoticeDlg.show();
		}
	}

	@Override
	protected void onDestroy() {
		if (taskDlg != null) {
			taskDlg.dismiss();
			taskDlg = null;
		}
		super.onDestroy();
	}
	
	
	@Override
	protected void onStop() {
		changeAllGroupState(false);
		tv_checked_num.setText(getSelectedGroupNumbers());
		cb_all_selected.setChecked(false);
		super.onStop();
	}

	@Override
	public void editClick(int position, TaskGroup group) {
		// 弹出编辑对话框
		if (position != -1 && group != null) {
			selectPosition = position;
			selectGroup = group;
			curStyle = STYLE_TASK_DLG_UPDATE;
			showTaskDialog(group);
		}

	}

	@Override
	public void checkClick(int position, TaskGroup group) {
		if (position != -1 && group != null) {
			group.setChecked(!group.isChecked());
			adapter.getDataSource().set(position, group);
			adapter.notifyDataSetChanged();

			List<TaskGroup> groups = getSelectedGroups();
			if (groups == null || groups.size() < 1) {
				cb_all_selected.setChecked(false);
			}
			tv_checked_num.setText(getSelectedGroupNumbers());
		}
	}

	@Override
	public void showDetailClick(TaskGroup group) {
		Intent intent = new Intent(TaskGroupManageActivity.this,
				TaskListActivity.class);
		intent.putExtra("taskGroup", group);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	private class OverLayThread implements Runnable {

		@Override
		public void run() {
			tv_letter.setVisibility(View.GONE);
		}

	}

	private int searchPositionInDataSource(String filter) {
		List<TaskGroup> taskGroups = adapter.getDataSource();
		String pinYin = null;
		if (taskGroups != null && taskGroups.size() > 0) {
			for (int i = 0; i < taskGroups.size(); i++) {
				TaskGroup group = taskGroups.get(i);
				pinYin = CharacterParser.getInstance().getSelling(
						group.getCategoryName());
				if (pinYin.startsWith(filter)) {
					return i;
				}
			}
		}
		return 0;
	}

	/**
	 * 改变所有数据源的选中状态
	 * 
	 * @param isChecked
	 */
	private void changeAllGroupState(boolean isChecked) {
		if (adapter == null || adapter.getDataSource() == null
				|| adapter.getDataSource().size() < 1) {
			return;
		}
		List<TaskGroup> taskGroups = adapter.getDataSource();
		if (taskGroups != null && taskGroups.size() > 0) {
			for (TaskGroup group : taskGroups) {
				group.setChecked(isChecked);
			}
		}
		adapter.notifyDataSetChanged();
	}

	private List<TaskGroup> getSelectedGroups() {
		List<TaskGroup> selectedGroups = new ArrayList<TaskGroup>();
		for (TaskGroup group : adapter.getDataSource()) {
			if (group.isChecked()) {
				selectedGroups.add(group);
			}
		}
		return selectedGroups;
	}

	/**
	 * 获取选中的数量
	 * 
	 * @return
	 */
	private String getSelectedGroupNumbers() {
		int count = 0;
		for (TaskGroup group : adapter.getDataSource()) {
			if (group.isChecked()) {
				count++;
			}
		}
		return "("+count+")";
	}
	
	private long[] getSelectedIds(){
		List<TaskGroup> taskGroups=getSelectedGroups();
		if (taskGroups==null||taskGroups.size()<1) {
			return null;
		}
		long[] selectedIds=new long[taskGroups.size()];
		int position=0;
		for(TaskGroup group:taskGroups){
			if (group.isChecked()) {
				selectedIds[position++]=group.getId();
			}
		}
		return selectedIds;
	}

	@Override
	protected void onResume() {
//		getData("",null);
		super.onResume();
	}
	
}
