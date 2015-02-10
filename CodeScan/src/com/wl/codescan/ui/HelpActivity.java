package com.wl.codescan.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.wl.codescan.R;
import com.wl.codescan.util.IntentUtil;
import com.wl.codescan.util.SharedPreUtil;
import com.wl.codescan.view.JazzyViewPager;
import com.wl.codescan.view.JazzyViewPager.TransitionEffect;

/**
 * 
 * @类描述:帮助界面
 * @创建人:wanglei
 * @创建时间: 2014-11-27
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class HelpActivity extends BaseActivity {
	private JazzyViewPager vp_guide;
	private List<Integer> imageIds; // 图片的资源id
	private LayoutInflater inflater;
	private GuidePagerAdapter adapter;

	private int cuurentPosition;
	private GestureDetector gestureDetector; // 用户滑动
	/** 记录当前分页ID */
	private int flaggingWidth;// 互动翻页所需滚动的长度是当前屏幕宽度的1/3
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_help);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void findViews() {
		vp_guide = (JazzyViewPager) findViewById(R.id.vp_guide);
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {
		gestureDetector = new GestureDetector(new GuideViewTouch());  
        // 获取分辨率  
        DisplayMetrics dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        flaggingWidth = dm.widthPixels / 3;
        
        
		vp_guide.setTransitionEffect(TransitionEffect.CubeOut);
		imageIds = new ArrayList<Integer>();
		imageIds.add(R.drawable.guide1);
		imageIds.add(R.drawable.guide2);
		imageIds.add(R.drawable.guide3);
		imageIds.add(R.drawable.guide4);

		inflater = LayoutInflater.from(this);

		adapter = new GuidePagerAdapter();
		vp_guide.setAdapter(adapter);
		vp_guide.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				cuurentPosition = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	private class GuideViewTouch extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (cuurentPosition == imageIds.size() - 1) {
				if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY()
						- e2.getY())
						&& (e1.getX() - e2.getX() <= (-flaggingWidth) || e1
								.getX() - e2.getX() >= flaggingWidth)) {
					if (e1.getX() - e2.getX() >= flaggingWidth) {
						loadMain();
						return true;
					}
				}
			}
			return false;
		}
	}
	
	@Override  
    public boolean dispatchTouchEvent(MotionEvent event) {  
        if (gestureDetector.onTouchEvent(event)) {  
            event.setAction(MotionEvent.ACTION_CANCEL);  
        }  
        return super.dispatchTouchEvent(event);  
    } 
	

	@Override
	public void onClick(View v) {

	}

	private class GuidePagerAdapter extends PagerAdapter {
		GuidePagerAdapter() {

		}

		@Override
		public int getCount() {
			return imageIds.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		/**
		 * 最重要的方法
		 */
		@Override
		public Object instantiateItem(View container, int position) {

			View view = inflater.inflate(R.layout.guide_item, null);
			ImageView iv = (ImageView) view.findViewById(R.id.iv_guide_item);

			Button bt_guide_enter_main = (Button) view
					.findViewById(R.id.bt_main);

			iv.setBackgroundResource(imageIds.get(position));

			bt_guide_enter_main.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					loadMain();
				}
			});

			// if (position!=3) {
			// bt_guide_enter_main.setVisibility(View.GONE);
			// }else {
			// bt_guide_enter_main.setVisibility(View.VISIBLE);
			// }

			((ViewPager) container).addView(view);
			vp_guide.setObjectForPosition(view, position);
			return view;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

	}

	private void LoadMainUi() {
		IntentUtil.startActivity(HelpActivity.this,
				TaskGroupManageActivity.class, true);

	}

	private void loadMain() {
		SharedPreUtil.getInstance(HelpActivity.this).setBoolData(
				SharedPreUtil.IS_FIRST_IN, false);
		LoadMainUi(); // 进入主界面
	}
}
