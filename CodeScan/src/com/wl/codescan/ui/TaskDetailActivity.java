package com.wl.codescan.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wl.codescan.R;
import com.wl.codescan.domain.Task;
import com.wl.codescan.util.IntentUtil;

public class TaskDetailActivity extends BaseActivity {
	private Button bt_left;
	private TextView tv_title;
	private TextView tv_result;
	private TextView tv_scanTime;
	private TextView tv_status;
	private TextView tv_orderNumber;
	private ImageView iv_img;

	private Task task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_detail);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {
		bt_left = (Button) findViewById(R.id.bt_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_result = (TextView) findViewById(R.id.tv_result);
		tv_scanTime = (TextView) findViewById(R.id.tv_scanTime);
		tv_status = (TextView) findViewById(R.id.tv_status);
		iv_img = (ImageView) findViewById(R.id.iv_img);
		tv_orderNumber = (TextView) findViewById(R.id.tv_orderNumber);
	}

	@Override
	public void setListener() {
		bt_left.setOnClickListener(this);
	}

	@Override
	public void initData() {
		if (getIntent().hasExtra("task")) {
			task = (Task) getIntent().getSerializableExtra("task");
			tv_title.setText(task.getTaskResult());

			String imagePath = task.getImgUrl();
			if (!TextUtils.isEmpty(imagePath)) {
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				if (bitmap != null) {
					iv_img.setImageBitmap(bitmap);
				}
			}

			tv_result.setText(task.getTaskResult());
			tv_scanTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date(task.getScanTime())));
			if (task.getStatus()==1) {
				tv_status.setText("已收货");
			} else if (task.getStatus()==2) {
				tv_status.setText("已退货");
			}else{
				tv_status.setText("未提交");
			}
			
			String submitNumber=task.getSubmitOrderNumber();
			String changeNumber=task.getChangeOrderNumber();
			if (!TextUtils.isEmpty(submitNumber)&&!TextUtils.isEmpty(changeNumber)) {
				Spanned spanned=Html.fromHtml("<font color='red'>"+submitNumber+"</font><font color='black'>[收货]</font><br><font color='red'>"+changeNumber+"</font><font color='black'>[退货]</font>");
				tv_orderNumber.setText(spanned);
			}else {
				if (!TextUtils.isEmpty(submitNumber)) {
					Spanned spanned=Html.fromHtml("<font color='red'>"+submitNumber+"</font><font color='black'>[收货]</font>");
					tv_orderNumber.setText(spanned);
				}else if (!TextUtils.isEmpty(changeNumber)) {
					Spanned spanned=Html.fromHtml("<font color='red'>"+changeNumber+"</font><font color='black'>[退货]</font>");
					tv_orderNumber.setText(spanned);
				}else {
					tv_orderNumber.setText("无");
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			IntentUtil.finishActivity(this);
			break;
		}
	}

}
