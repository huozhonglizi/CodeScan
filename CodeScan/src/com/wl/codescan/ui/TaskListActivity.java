package com.wl.codescan.ui;

import java.io.File;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.zxing.Result;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.wl.codescan.R;
import com.wl.codescan.adapter.TaskAdapter;
import com.wl.codescan.callback.CallBackMenuPop;
import com.wl.codescan.callback.CallBackOrderDialog;
import com.wl.codescan.callback.CallBackShareDialog;
import com.wl.codescan.callback.CallBackTaskAdapter;
import com.wl.codescan.callback.CallBackTaskDialog;
import com.wl.codescan.domain.ProductSn;
import com.wl.codescan.domain.RequestModel;
import com.wl.codescan.domain.ResponseModel;
import com.wl.codescan.domain.ShareType;
import com.wl.codescan.domain.Task;
import com.wl.codescan.domain.TaskGroup;
import com.wl.codescan.net.RequestClient;
import com.wl.codescan.util.CharacterParser;
import com.wl.codescan.util.Constant;
import com.wl.codescan.util.FileUtil;
import com.wl.codescan.util.IntentUtil;
import com.wl.codescan.util.Logger;
import com.wl.codescan.util.ShareUtil;
import com.wl.codescan.view.DeleteDialog;
import com.wl.codescan.view.InfoNoticeDialog;
import com.wl.codescan.view.MenuPopupWindow;
import com.wl.codescan.view.OrderNumberDialog;
import com.wl.codescan.view.ShareDialog;
import com.wl.codescan.view.Sidebar;
import com.wl.codescan.view.Sidebar.OnTouchingLetterChangedListener;
import com.wl.codescan.view.TaskDialog;

/**
 * 
 * @类描述:任务列表界面
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class TaskListActivity extends BaseActivity {
	private Button bt_left;
	private ImageView iv_menu;
	private ListView lv_task;
	private CheckBox cb_all_selected;
	private Button bt_tuihuanhuo;
	private Button bt_shouhuo;
	private LinearLayout rl_scan;
	private TextView tv_title;
	private TextView tv_checked_num;

	// ---------------
	private EditText et_query;
	private ImageButton ib_search_clear;
	private TextView tv_letter;
	private Sidebar sidebar;

	// ------应用逻辑数据保存----
	private TaskGroup mTaskGroup;
	private TaskAdapter adapter;

	private TaskDialog taskDialog;
	private MenuPopupWindow menuPopupWindow;

	private ShareDialog shareDialog;

	private OrderNumberDialog orderCommitDialog; // 输入订单号对话框
	private OrderNumberDialog orderChangeDialog;
	private OrderNumberDialog serialInputDialog; // 输入序列号对话框

	private DeleteDialog deleteDialog; // 删除提示对话框

	private InfoNoticeDialog infoNoticeDialog; // 信息提示框

	private Handler handler = new Handler();
	private OverLayThread overLayThread = new OverLayThread();

	private List<Task> selectedTasks; // 保存选中的任务
	private boolean isNeedReset=true;	//是否需要重置状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_list);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {
		bt_left = (Button) findViewById(R.id.bt_left);
		iv_menu = (ImageView) findViewById(R.id.iv_menu);
		lv_task = (ListView) findViewById(R.id.lv_task);
		cb_all_selected = (CheckBox) findViewById(R.id.cb_all_selected);
		bt_tuihuanhuo = (Button) findViewById(R.id.bt_tuihuanhuo);
		bt_shouhuo = (Button) findViewById(R.id.bt_shouhuo);
		rl_scan = (LinearLayout) findViewById(R.id.rl_scan);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_checked_num = (TextView) findViewById(R.id.tv_checked_num);

		et_query = (EditText) findViewById(R.id.et_query);
		ib_search_clear = (ImageButton) findViewById(R.id.ib_search_clear);
		tv_letter = (TextView) findViewById(R.id.tv_letter);
		sidebar = (Sidebar) findViewById(R.id.sidebar);
	}

	@Override
	public void setListener() {
		bt_left.setOnClickListener(this);
		iv_menu.setOnClickListener(this);
		rl_scan.setOnClickListener(this);
		bt_tuihuanhuo.setOnClickListener(this);
		bt_shouhuo.setOnClickListener(this);
		ib_search_clear.setOnClickListener(this);

		cb_all_selected
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						changeAllTaskStatus(isChecked);
						tv_checked_num.setText(getSelectedTaskNumbers());
					}
				});

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
					if (!isNeedReset) {
						getData("",true);
						isNeedReset=true;
					}else {
						getData("");
					}
				}
			}
		});

	}

	@Override
	public void initData() {
		mTaskGroup = (TaskGroup) getIntent().getSerializableExtra("taskGroup");
		if (mTaskGroup != null) {
			tv_title.setText(mTaskGroup.getCategoryName());
			lv_task.setTextFilterEnabled(true);
			tv_letter.setVisibility(View.GONE);

			sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

				@Override
				public void onTouchingLetterChanged(String s) {
					if (adapter == null || adapter.getDataSource() == null
							|| adapter.getDataSource().size() < 1) {
						return;
					}
					int position = searchPositionInDataSource(s);
					lv_task.setSelection(position);
					tv_letter.setText(s);
					tv_letter.setVisibility(View.VISIBLE);
					handler.removeCallbacks(overLayThread);
					handler.postDelayed(overLayThread, 200);
				}
			});
			getData("");
		}

	}

	public void getData(String filter) {
		getData(filter, false);
	}

	@Override
	protected void onResume() {
		getData("", true);
		et_query.setText("");
		super.onResume();
	}

	@Override
	protected void onStop() {
		if (isNeedReset) {
			 changeAllTaskStatus(false);
			 tv_checked_num.setText(getSelectedTaskNumbers());
			 cb_all_selected.setChecked(false);
		}else {
			selectedTasks = getSelectedTasks();
		}
		super.onStop();
	}

	private String getSelectedTaskNumbers() {
		int count = 0;
		if (adapter == null || adapter.getDataSource() == null
				|| adapter.getDataSource().size() < 1) {
			return "(" + count + ")";
		}
		List<Task> tasks = adapter.getDataSource();
		for (Task task : tasks) {
			if (task.isChecked()) {
				count++;
			}
		}
		return "(" + count + ")";
	}

	/**
	 * 判断当前id是否被选中
	 * 
	 * @param id
	 * @return
	 */
	private boolean isBelong(Long id) {
		for (Task task : selectedTasks) {
			if (task.getId() == id) {
				return true;
			}
		}
		return false;
	}

	private void getData(final String filter, final boolean isNeedUpdate) {
		new AsyncTask<Void, Void, ArrayList<Task>>() {

			@Override
			protected ArrayList<Task> doInBackground(Void... params) {

				try {
					if (TextUtils.isEmpty(filter)) {
						return (ArrayList<Task>) getHelper().getTaskDao()
								.queryBuilder().where()
								.eq("taskGroup_id", mTaskGroup.getId()).query();
					} else {
						return (ArrayList<Task>) getHelper().getTaskDao()
								.queryBuilder().where()
								.eq("taskGroup_id", mTaskGroup.getId()).and()
								.like("taskResult", "%" + filter + "%").query();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<Task> result) {
				super.onPostExecute(result);
				if (isNeedUpdate) {
					if (selectedTasks != null && selectedTasks.size() > 0
							&& result != null && result.size() > 0) {
						for (Task task : result) {
							if (isBelong(task.getId())) {
								task.setChecked(true);
							}
						}
					}
				}
				if (adapter == null) {
					adapter = new TaskAdapter(TaskListActivity.this,
							new CallBackTaskAdapter() {

								@Override
								public void editClick(final int position,
										final Task task) {
									if (task.getStatus() == 1
											|| task.getStatus() == 2) {
										return;
									}
									taskDialog = new TaskDialog(
											TaskListActivity.this,
											new CallBackTaskDialog() {

												@Override
												public void clickOK(
														String result) {
													taskDialog.dismiss();
													if (TextUtils
															.isEmpty(result)) {
														showMsg("不能为空");
														return;
													}
													try {
														QueryBuilder<Task, Long> builder = getHelper()
																.getTaskDao()
																.queryBuilder();
														builder.where()
																.eq("taskGroup_id",
																		mTaskGroup
																				.getId())
																.and()
																.eq("taskResult",
																		result);
														List<Task> tasks = builder
																.query();
														if (tasks != null
																&& tasks.size() > 0) {
															showRedMsg("条码重复");
															return;
														}

														task.setTaskResult(result);

														getHelper()
																.getTaskDao()
																.update(task);
													} catch (SQLException e) {
														e.printStackTrace();
													}
													adapter.notifyDataSetChanged();
												}

												@Override
												public void clickCancel() {
													taskDialog.dismiss();
													taskDialog = null;
												}
											});
									taskDialog.setTitle("编辑序列号");
									taskDialog.setText(task.getTaskResult());
									taskDialog.show();

								}

								@Override
								public void checkClick(int position, Task task) {
									task.setChecked(!task.isChecked());
									List<Task> tasks = getSelectedTasks();
									if (tasks == null || tasks.size() < 1) {
										cb_all_selected.setChecked(false);
									}
									tv_checked_num
											.setText(getSelectedTaskNumbers());
									adapter.notifyDataSetChanged();
								}

								@Override
								public void detailClick(Task task) {
									// 进入详情界面
									isNeedReset=false;
									Map<String, Serializable> params = new HashMap<String, Serializable>();
									params.put("task", task);
									IntentUtil.startActivity(
											TaskListActivity.this,
											TaskDetailActivity.class, params,
											false);
								}
							});
					adapter.setData(result);
					lv_task.setAdapter(adapter);
				} else {
					adapter.setData(result);
				}
			}

		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			IntentUtil.finishActivity(TaskListActivity.this);
			break;
		case R.id.iv_menu:
			showMenuPopWindow();
			break;
		case R.id.bt_tuihuanhuo:
			final List<Task> selectedTasks = getSelectedTasks();
			if (selectedTasks == null || selectedTasks.size() < 1) {
				showRedMsg("未选择条码");
				return;
			}
//			int count = 0;
//			for (Task task : selectedTasks) {
//				if (task.getStatus() != 2) {
//					count++;
//				}
//			}
//			if (count < 1) {
//				showRedMsg("没有选择未退货的订单");
//				return;
//			}
			orderChangeDialog = new OrderNumberDialog(TaskListActivity.this,
					new CallBackOrderDialog() {

						@Override
						public void clickOK(String result) {
							if (TextUtils.isEmpty(result)) {
								showRedMsg("订单号不能为空");
								return;
							}
							orderChangeDialog.dismiss();
							orderChangeDialog = null;
							try {
								changeOrders(selectedTasks, result);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void clickCancel() {
							orderChangeDialog.dismiss();
							orderChangeDialog = null;
						}
					});
			orderChangeDialog.clearData();
			orderChangeDialog.show();
			break;
		case R.id.bt_shouhuo:
			final List<Task> tasks = getSelectedTasks();
			if (tasks == null || tasks.size() < 1) {
				showRedMsg("未选择条码");
				return;
			}
			final List<ProductSn> productSns = new ArrayList<ProductSn>();
			final List<Long> ids = new ArrayList<Long>();
			for (Task task : tasks) {
					productSns.add(new ProductSn(task.getTaskResult()));
					ids.add(task.getId());
			}
//			if (productSns.size() < 1) {
//				showRedMsg("没有选择未提交的订单");
//				return;
//			}
			orderCommitDialog = new OrderNumberDialog(TaskListActivity.this,
					new CallBackOrderDialog() {

						@Override
						public void clickOK(String result) {
							if (TextUtils.isEmpty(result)) {
								showRedMsg("订单号不能为空");
								return;
							}
							orderCommitDialog.dismiss();
							orderCommitDialog = null;
							try {
								commitOrders(tasks, productSns, ids, result);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void clickCancel() {
							orderCommitDialog.dismiss();
							orderCommitDialog = null;
						}
					});
			orderCommitDialog.clearData();
			orderCommitDialog.show();
			break;
		case R.id.rl_scan:
			Intent intent = new Intent(TaskListActivity.this,
					CaptureActivity.class);
			intent.putExtra("taskGroup", mTaskGroup);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.ib_search_clear:
			et_query.setText("");
			break;

		}
	}

	/**
	 * 改变所有选中任务的状态
	 * 
	 * @param isChecked
	 */
	private void changeAllTaskStatus(boolean isChecked) {
		if (adapter == null || adapter.getDataSource() == null
				|| adapter.getDataSource().size() < 1) {
			return;
		}
		List<Task> tasks = adapter.getDataSource();
		if (tasks != null && tasks.size() > 0) {
			for (Task task : tasks) {
				task.setChecked(isChecked);
			}
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 显示菜单
	 */
	private void showMenuPopWindow() {
		if (menuPopupWindow == null) {
			menuPopupWindow = new MenuPopupWindow(this, new CallBackMenuPop() {

				@Override
				public void shareClick() {
					menuPopupWindow.dismiss();
					if (adapter == null || adapter.getDataSource().size() < 1) {
						showRedMsg("没有数据可供分享");
						return;
					}
					List<Task> selectedTasks = getSelectedTasks();
					Logger.i("选中数量:" + selectedTasks.size());
					if (selectedTasks.size() < 1) {
						showRedMsg("请至少选择一个条码");
						return;
					}
					// 分享
					showShareDialog();
				}

				@Override
				public void deleteClick() {
					menuPopupWindow.dismiss();
					if (adapter == null || adapter.getDataSource().size() < 1) {
						showRedMsg("没有数据可供删除");
						return;
					}
					final List<Task> selectedTasks = getSelectedTasks();
					if (selectedTasks.size() < 1) {
						showRedMsg("请至少选择一个条码");
						return;
					}
					// 删除
					// TODO 新增删除提示
					deleteDialog = new DeleteDialog(TaskListActivity.this,
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									try {
										deleteDialog.dismiss();
										for (Task task : selectedTasks) {
											getHelper().getTaskDao().delete(
													task);
											adapter.removeElement(task);
										}
										showMsg("删除成功");
										cb_all_selected.setChecked(false);
										changeAllTaskStatus(false);
										tv_checked_num
												.setText(getSelectedTaskNumbers());
									} catch (Exception e) {
									}
								}
							});
					deleteDialog.setTitle("是否确认删除选中条码？");
					deleteDialog.show();

				}

				@Override
				public void addClick() {
					menuPopupWindow.dismiss();
					serialInputDialog = new OrderNumberDialog(
							TaskListActivity.this, new CallBackOrderDialog() {

								@Override
								public void clickOK(String result) {
									if (TextUtils.isEmpty(result)) {
										showRedMsg("录入的序列号为空");
										return;
									}
									QueryBuilder<Task, Long> builder = getHelper()
											.getTaskDao().queryBuilder();

									try {
										builder.where()
												.eq("taskGroup_id",
														mTaskGroup.getId())
												.and().eq("taskResult", result);
										List<Task> tasks = builder.query();
										if (tasks != null && tasks.size() > 0) {
											showRedMsg("重复录入");
											serialInputDialog.clearData();
											return;
										} else {
											Task task2 = new Task();
											task2.setImgUrl(null);
											task2.setScanTime(System
													.currentTimeMillis());
											task2.setTaskGroup(mTaskGroup);
											task2.setTaskResult(result);
											getHelper().getTaskDao().create(
													task2);
										}
										serialInputDialog.dismiss();
										serialInputDialog = null;
										cb_all_selected.setChecked(false);
										changeAllTaskStatus(false);
										tv_checked_num
												.setText(getSelectedTaskNumbers());
										getData("");
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void clickCancel() {
									serialInputDialog.dismiss();
									serialInputDialog = null;
									cb_all_selected.setChecked(false);
									changeAllTaskStatus(false);
									tv_checked_num
											.setText(getSelectedTaskNumbers());
								}
							});
					serialInputDialog.setTitle("输入序列号");
					serialInputDialog.setHintText("序列号");
					serialInputDialog.clearData();
					serialInputDialog.show();
				}
			});
		}
		menuPopupWindow.showAsDropDown(iv_menu);
	}

	/**
	 * 获取当前被选中的任务
	 * 
	 * @return
	 */
	private List<Task> getSelectedTasks() {
		if (adapter == null || adapter.getDataSource() == null
				|| adapter.getDataSource().size() < 1) {
			return null;
		}
		List<Task> selectedTasks = new ArrayList<Task>();
		for (Task task : adapter.getDataSource()) {
			if (task.isChecked()) {
				selectedTasks.add(task);
			}
		}
		return selectedTasks;
	}

	/**
	 * 显示分享对话框
	 */
	private void showShareDialog() {
		if (shareDialog == null) {
			shareDialog = new ShareDialog(this, new CallBackShareDialog() {

				@Override
				public void share(ShareType shareType) {
					shareDialog.dismiss();
					switch (shareType) {
					case SMS:
						Intent intent = new Intent();
						intent.setClass(TaskListActivity.this,
								SmsShareActivity.class);
						intent.putExtra("content", getShareContent());
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						break;
					case QQ:
						shareToQQ(getShareContent());
						break;
					case EMAIL:
						mailShare(getSelectedTasks());
						break;
					case WEIXIN:
						if (!FileUtil.isInstallAPP(TaskListActivity.this,
								"com.tencent.mm")) {
							showRedMsg("请安装微信客户端");
							return;
						}
						Intent wxIntent = new Intent();
						ComponentName componentName = new ComponentName(
								"com.tencent.mm",
								"com.tencent.mm.ui.tools.ShareImgUI");
						wxIntent.setComponent(componentName);
						wxIntent.setAction(Intent.ACTION_SEND);
						wxIntent.setType("*/*");
						wxIntent.putExtra(Intent.EXTRA_TEXT, getShareContent());
						startActivity(wxIntent);
						break;

					}
				}
			});
		}
		shareDialog.show();
	}

	/**
	 * 分享到qq
	 * 
	 * @param content
	 */
	private void shareToQQ(String content) {
		ShareUtil.shareToQQ(TaskListActivity.this, "扫描信息", content,
				"http://www.baidu.com", new IUiListener() {

					@Override
					public void onError(UiError error) {
						showMsg("分享失败" + error.errorMessage);
					}

					@Override
					public void onComplete(Object arg0) {
						showMsg("分享完成");
					}

					@Override
					public void onCancel() {
						showMsg("分享取消");
					}
				});
	}

	/**
	 * 提交订单
	 * 
	 * @param ids
	 * 
	 */
	private void commitOrders(List<Task> tasks, List<ProductSn> productSns,
			final List<Long> ids, final String orderNumber) throws Exception {
		final RequestModel requestModel = new RequestModel();
		requestModel.setApiusername("ruijieorder");
		requestModel.setApipwd("ruijieorder");
		requestModel.setOrdercode(orderNumber);
		requestModel.setProductSns(productSns);

		RequestParams params = new RequestParams();
		params.put("param", com.alibaba.fastjson.JSONObject
				.toJSON(requestModel).toString());
		RequestClient.post(Constant.COMMIT_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						ResponseModel model = JSON.parseObject(
								response.toString(), ResponseModel.class);
						if ("1".equals(model.getApi().getStatus())
								&& "1".equals(model.getProduct().getStatus())) {
							showInfoDlg("订单提交成功", 20);

							// 更新到数据库中
							UpdateBuilder<Task, Long> updateBuilder = getHelper()
									.getTaskDao().updateBuilder();
							try {
								updateBuilder.where().in("id", ids);
								updateBuilder.updateColumnValue("status", 1);
								updateBuilder.updateColumnValue(
										"submitOrderNumber", orderNumber);
								int count = updateBuilder.update();
								tv_checked_num.setText("(" + 0 + ")");
								getData("");
							} catch (SQLException e) {
								e.printStackTrace();
							}

						} else {
							if ("0".equals(model.getApi().getStatus())) {
								showInfoDlg(model.getApi().getMessage(), 15);
							} else {
								showInfoDlg(model.getProduct().getMessage(), 15);
							}
						}
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						if (throwable instanceof HttpHostConnectException
								|| throwable instanceof SocketTimeoutException) {
							showInfoDlg("网络发生异常，请重新提交", 20);
						} else {
							showInfoDlg("提交订单失败", 20);
						}
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						if (throwable instanceof HttpHostConnectException
								|| throwable instanceof SocketTimeoutException) {
							showInfoDlg("网络发生异常，请重新提交", 20);
						} else {
							showInfoDlg("提交订单失败", 20);
						}
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});

	}

	/**
	 * 退换货
	 */
	private void changeOrders(List<Task> tasks, final String orderNumber)
			throws Exception {
		List<ProductSn> productSns = new ArrayList<ProductSn>();
		final List<Long> ids = new ArrayList<Long>();
		for (Task task : tasks) {
			productSns.add(new ProductSn(task.getTaskResult()));
			ids.add(task.getId());
		}

		final RequestModel requestModel = new RequestModel();
		requestModel.setApiusername("ruijieorder");
		requestModel.setApipwd("ruijieorder");
		requestModel.setOrdercode(orderNumber);
		requestModel.setProductSns(productSns);

		RequestParams params = new RequestParams();
		params.put("param", com.alibaba.fastjson.JSONObject
				.toJSON(requestModel).toString());
		RequestClient.post(Constant.CHANGE_URL, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						ResponseModel model = JSON.parseObject(
								response.toString(), ResponseModel.class);
						if ("1".equals(model.getApi().getStatus())
								&& "1".equals(model.getProduct().getStatus())) {
							showInfoDlg("退货成功", 20);

							// 更新到数据库中
							UpdateBuilder<Task, Long> updateBuilder = getHelper()
									.getTaskDao().updateBuilder();
							try {
								updateBuilder.where().in("id", ids);
								updateBuilder.updateColumnValue("status", 2);
								updateBuilder.updateColumnValue(
										"changeOrderNumber", orderNumber);
								updateBuilder.update();
								tv_checked_num.setText("(" + 0 + ")");
								getData("");
							} catch (SQLException e) {
								e.printStackTrace();
							}
						} else {
							if ("0".equals(model.getApi().getStatus())) {
								showInfoDlg(model.getApi().getMessage(), 15);
							} else {
								showInfoDlg(model.getProduct().getMessage(), 15);
							}
						}
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						if (throwable instanceof HttpHostConnectException
								|| throwable instanceof SocketTimeoutException) {
							showInfoDlg("网络发生异常，请重新提交", 20);
						} else {
							showInfoDlg("退货失败", 20);
						}
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						if (throwable instanceof HttpHostConnectException
								|| throwable instanceof SocketTimeoutException) {
							showInfoDlg("网络发生异常，请重新提交", 20);
						} else {
							showInfoDlg("退货失败", 20);
						}
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});

	}

	/**
	 * 显示信息提示框
	 * 
	 * @param info
	 */
	private void showInfoDlg(String info, int textSize) {
		if (infoNoticeDialog == null) {
			infoNoticeDialog = new InfoNoticeDialog(this);
		}
		infoNoticeDialog.setInfo(info, textSize);
		infoNoticeDialog.show();
	}

	private class OverLayThread implements Runnable {

		@Override
		public void run() {
			tv_letter.setVisibility(View.GONE);
		}

	}

	private int searchPositionInDataSource(String filter) {
		if (adapter == null || adapter.getDataSource().size() < 1) {
			return 0;
		}
		List<Task> tasks = adapter.getDataSource();
		String pinYin = null;
		if (tasks != null && tasks.size() > 0) {
			for (int i = 0; i < tasks.size(); i++) {
				Task task = tasks.get(i);
				pinYin = CharacterParser.getInstance().getSelling(
						task.getTaskResult());
				if (pinYin.startsWith(filter)) {
					return i;
				}
			}
		}
		return 0;
	}

	/**
	 * 组装分享的文字内容
	 * 
	 * @return
	 */
	private String getShareContent() {
		StringBuilder builder = new StringBuilder();
		List<Task> tasks = getSelectedTasks();
		if (tasks != null && tasks.size() > 0) {
			builder.append("当前分组:");
			builder.append(mTaskGroup.getCategoryName());
			builder.append("\r\n");
			for (Task task : tasks) {
				builder.append(task.getTaskResult());
				builder.append(",");
				builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date(task.getScanTime())));
				builder.append(",");
				if (task.getStatus() == 1) {
					builder.append("已收货");
				} else if (task.getStatus() == 2) {
					builder.append("已退货");
				} else {
					builder.append("未提交");
				}
				builder.append(",订单号：");
				String submitNumber = task.getSubmitOrderNumber();
				String changeNumber = task.getChangeOrderNumber();
				if (!TextUtils.isEmpty(submitNumber)
						&& !TextUtils.isEmpty(changeNumber)) {
					builder.append(submitNumber);
					builder.append("[收货]");
					builder.append("\t    ");
					builder.append(changeNumber);
					builder.append("[换货]");
				} else {
					if (!TextUtils.isEmpty(submitNumber)) {
						builder.append(submitNumber);
						builder.append("[收货]");
					} else if (!TextUtils.isEmpty(changeNumber)) {
						builder.append(changeNumber);
						builder.append("[换货]");
					} else {
						builder.append("无");
					}
				}
				builder.append("\r\n");
			}
		}

		return builder.toString();
	}

	/**
	 * 邮箱分享
	 */
	private void mailShare(List<Task> selectedTasks) {
		if (selectedTasks == null || selectedTasks.size() < 1) {
			showRedMsg("请选择至少一个条码");
			return;
		}
		Intent mailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		ArrayList<Uri> uris = new ArrayList<Uri>();
		StringBuilder builder = new StringBuilder();
		builder.append("当前分组:");
		builder.append(mTaskGroup.getCategoryName());
		builder.append("\r\n");
		for (Task task : selectedTasks) {
			if (!TextUtils.isEmpty(task.getImgUrl())) {
				File file = new File(task.getImgUrl());
				if (file.exists()) {
					uris.add(Uri.fromFile(file));
				}
			}
			builder.append("序列号:");
			builder.append(task.getTaskResult());
			builder.append(",扫码时间：");
			builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date(task.getScanTime())));
			builder.append(",状态：");
			if (task.getStatus() == 1) {
				builder.append("已收货");
			} else if (task.getStatus() == 2) {
				builder.append("已退货");
			} else {
				builder.append("未提交");
			}
			builder.append(",订单号：");
			String submitNumber = task.getSubmitOrderNumber();
			String changeNumber = task.getChangeOrderNumber();
			if (!TextUtils.isEmpty(submitNumber)
					&& !TextUtils.isEmpty(changeNumber)) {
				builder.append(submitNumber);
				builder.append("[收货]");
				builder.append("\t    ");
				builder.append(changeNumber);
				builder.append("[换货]");
			} else {
				if (!TextUtils.isEmpty(submitNumber)) {
					builder.append(submitNumber);
					builder.append("[收货]");
				} else if (!TextUtils.isEmpty(changeNumber)) {
					builder.append(changeNumber);
					builder.append("[换货]");
				} else {
					builder.append("无");
				}
			}
			builder.append("\r\n");
		}

		mailIntent.putExtra(Intent.EXTRA_SUBJECT, "收货助手数据");
		mailIntent.setType("*/*");
		mailIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
		mailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		startActivity(Intent.createChooser(mailIntent, "选择..."));

	}

	@Override
	protected void onDestroy() {
		if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
			menuPopupWindow.dismiss();
		}
		super.onDestroy();
	}

}
