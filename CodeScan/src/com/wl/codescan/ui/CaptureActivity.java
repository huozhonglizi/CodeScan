package com.wl.codescan.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.j256.ormlite.stmt.QueryBuilder;
import com.wl.codescan.R;
import com.wl.codescan.domain.Task;
import com.wl.codescan.domain.TaskGroup;
import com.wl.codescan.util.BitmapUtil;
import com.wl.codescan.util.IntentUtil;
import com.wl.codescan.util.SharedPreUtil;
import com.wl.codescan.zxing.camera.CameraManager;
import com.wl.codescan.zxing.decoding.CaptureActivityHandler;
import com.wl.codescan.zxing.decoding.DecodeFormatManager;
import com.wl.codescan.zxing.decoding.InactivityTimer;
import com.wl.codescan.zxing.decoding.RGBLuminanceSource;
import com.wl.codescan.zxing.view.ViewfinderView;

/**
 * 
 * @类描述:扫描界面
 * @创建人:wanglei
 * @创建时间: 2014-11-21
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class CaptureActivity extends BaseActivity implements
		SurfaceHolder.Callback {
	private Button bt_left;
	private TextView tv_title;
	private ImageView bt_right;
	private SurfaceView sv_preview;
	private ViewfinderView viewFinder;
	private TextView tv_result1;
	private TextView tv_result2;
	private TextView tv_result3;

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 1.0f;
	private boolean vibrate;

	private static final int REQUEST_CODE = 100;
	private static final int PARSE_BARCODE_SUC = 300;
	private static final int PARSE_BARCODE_FAIL = 303;
	private static final int RESTART_CAMERA = 304;
	private ProgressDialog progressDialog;
	private String photoPath;
	private Bitmap scanBitmap;

	private static final long VIBRATE_DURATION = 1000L;

	private TaskGroup taskGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 屏幕长亮
		setContentView(R.layout.activity_capture);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {
		bt_left = (Button) findViewById(R.id.bt_left);
		bt_right = (ImageView) findViewById(R.id.bt_right);
		sv_preview = (SurfaceView) findViewById(R.id.sv_preview);
		viewFinder = (ViewfinderView) findViewById(R.id.viewFinder);
		tv_result1 = (TextView) findViewById(R.id.tv_result1);
		tv_result2 = (TextView) findViewById(R.id.tv_result2);
		tv_result3 = (TextView) findViewById(R.id.tv_result3);
		tv_title = (TextView) findViewById(R.id.tv_title);

	}

	@Override
	public void setListener() {
		bt_left.setOnClickListener(this);
		bt_right.setOnClickListener(this);
		tv_title.setOnClickListener(this);
	}

	@Override
	public void initData() {
		CameraManager.init(getApplication());
		inactivityTimer = new InactivityTimer(this);

		taskGroup = (TaskGroup) getIntent().getSerializableExtra("taskGroup");
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceHolder surfaceHolder = sv_preview.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultStr = result.getText();
		if (TextUtils.isEmpty(resultStr)) {
			showRedMsg("扫描失败，没有任何可用信息");
			// 用户可输入

		} else {
			// TODO 检查数据库中有没有相应的记录，若有，则提示重复，否则，写入数据库，连扫
			try {
				QueryBuilder<Task, Long> builder = getHelper().getTaskDao()
						.queryBuilder();
				builder.where().eq("taskGroup_id", taskGroup.getId()).and()
						.eq("taskResult", resultStr);
				List<Task> tasks = builder.query();
				if (tasks != null && tasks.size() > 0) {
					showRedMsg("重复扫码");
//					for (Task task : tasks) {
//						if (!task.isSubmit()) {
//							return;
//						}
//						saveAndUpdate(barcode, resultStr);
//					}
				} else {
					saveAndUpdate(barcode, resultStr);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mHandler.sendMessageDelayed(
						mHandler.obtainMessage(RESTART_CAMERA), 750); // 两秒后开始连扫
			}

		}

	}

	private void saveAndUpdate(Bitmap barcode, String resultStr)
			throws FileNotFoundException, SQLException {
		showMsg(resultStr);
		// 保存图片
		String imagePath = BitmapUtil.getImageName(resultStr);
		File file = new File(imagePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		barcode.compress(CompressFormat.JPEG, 100, new FileOutputStream(
				new File(imagePath)));
		Task task = new Task();
		task.setScanTime(System.currentTimeMillis());
		task.setImgUrl(imagePath);
		task.setTaskGroup(taskGroup);
		task.setTaskResult(resultStr);
		getHelper().getTaskDao().create(task);

		taskGroup = getHelper().getTaskGroupDao().queryForId(taskGroup.getId());
		taskGroup.setTaskCount(taskGroup.getTaskCount() + 1);
		getHelper().getTaskGroupDao().update(taskGroup);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewFinder;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewFinder.drawViewfinder();

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private void playBeepSoundAndVibrate() {
		boolean isSound = SharedPreUtil.getInstance(this).getBoolData(
				SharedPreUtil.IS_SOUND, true);
		boolean isVibrate = SharedPreUtil.getInstance(this).getBoolData(
				SharedPreUtil.IS_VIBRATE, true);
		if (playBeep && mediaPlayer != null) {
			if (isSound) {
				mediaPlayer.start();
			}
		}
		if (vibrate) {
			if (isVibrate) {
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				vibrator.vibrate(VIBRATE_DURATION);
			}
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			IntentUtil.finishActivity(CaptureActivity.this);
			break;
		case R.id.bt_right:
			// 打开手机中的相册
			Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			innerIntent.setType("image/*");
			Intent wrapperIntent = Intent.createChooser(innerIntent, "选择一维码码图片");
			this.startActivityForResult(wrapperIntent, REQUEST_CODE);
			break;
		case R.id.tv_title:
			if (handler != null) {
				handler.restartPreviewAndDecode();
			}
			break;

		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				progressDialog.dismiss();
				handleDecode((Result) msg.obj, scanBitmap);
				break;
			case PARSE_BARCODE_FAIL:
				progressDialog.dismiss();
				Toast.makeText(CaptureActivity.this, (String) msg.obj,
						Toast.LENGTH_LONG).show();
				break;

			case RESTART_CAMERA:
				if (handler != null) {
					handler.restartPreviewAndDecode();
				}
				break;

			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE:
				// 获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(),
						null, null, null, null);
				if (cursor.moveToFirst()) {
					photoPath = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
				}
				cursor.close();

				progressDialog = new ProgressDialog(CaptureActivity.this);
				progressDialog.setMessage("正在扫描...");
				progressDialog.setCancelable(false);
				progressDialog.show();

				new Thread(new Runnable() {
					@Override
					public void run() {
						Result result = scanningImage(photoPath);
						if (result != null) {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_SUC;
							m.obj = result;
							mHandler.sendMessage(m);
						} else {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_FAIL;
							m.obj = "扫码失败";
							mHandler.sendMessage(m);
						}
					}
				}).start();

				break;

			}
		}
	}

	/**
	 * 扫描(一)二维码图片的方法
	 * 
	 * @param path
	 * @return
	 */
	public Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置编码
		
		List<BarcodeFormat> formats=new ArrayList<BarcodeFormat>();
		formats.addAll(DecodeFormatManager.ONE_D_FORMATS);
		formats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
		formats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		
//		formats.add(BarcodeFormat.UPC_A);
//		formats.add(BarcodeFormat.UPC_E);
//		formats.add(BarcodeFormat.EAN_13);
//		formats.add(BarcodeFormat.EAN_8);
//		formats.add(BarcodeFormat.RSS14);
//		formats.add(BarcodeFormat.CODE_39);
//		formats.add(BarcodeFormat.CODE_93);
//		formats.add(BarcodeFormat.CODE_128);
//		formats.add(BarcodeFormat.ITF);
//		formats.add(BarcodeFormat.DATA_MATRIX);
		hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		MultiFormatReader reader = new MultiFormatReader();
		try {
//			return reader.decode(bitmap1, hints);
			return reader.decodeWithState(bitmap1);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
