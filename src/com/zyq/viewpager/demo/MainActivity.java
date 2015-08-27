package com.zyq.viewpager.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyq.viewpager.AutoLoopViewPager;
import com.zyq.viewpager.R;

import java.util.ArrayList;

public class MainActivity extends Activity implements ViewPager.OnPageChangeListener {

	private static final boolean DEBUG = true;
	private static final int POINT_LENGTH = 2;
	private static final int FIRST_ITEM_INDEX = 1;
	private static final String TAG = "main";
	private AutoLoopViewPager mViewPager;
	private ViewGroup mPointViewGroup;
	private ViewPagerAdapter mViewPagerAdapter;
	private ArrayList<View> mViewArrayList;
	private boolean mIsChanged = false;
	private int mCurrentPagePosition = 1;
	private int mCurrentIndex;
	private String mCurrentScrollState;
	private float mPreviousOffset;
	private FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mViewPager = (AutoLoopViewPager) findViewById(R.id.vp_content);
		mViewArrayList = new ArrayList<>();

		//addTextView(POINT_LENGTH - 1);
		for (int i = 0; i < 3; i++) {
			addTextView(i);
		}
		//addTextView(0);

		mViewPagerAdapter = new ViewPagerAdapter(this, mViewArrayList);
		mViewPager.setAdapter(mViewPagerAdapter);

		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(1, false);

	}

	private void addTextView(int pIndex) {
		TextView textview = new TextView(this);
		textview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
				.LayoutParams.MATCH_PARENT));
		textview.setGravity(Gravity.CENTER);
		textview.setText("这是第" + (pIndex) + "个页面");
		textview.setTextSize(50);

		mViewArrayList.add(textview);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//arg0A在有些情况不等于onPageSelected()arg0B
	//当手指左滑时候,arg0A = arg0B 只有在完全positionOffset 接近 1时候,arg2增高接近屏幕宽度时候,才相等.
	//当手指右滑时候,情况有不一样了,arg0A =
	@Override
	public void onPageScrolled(int arg0, float positionOffset, int arg2) {
		if (DEBUG)
			Log.d(TAG, "onPageScrolled()--> 当前的位置为:  " + arg0 + "  滚动了多少:  " + positionOffset + "  偏移量为:  " +
					arg2);
//		if (positionOffset == 0
//				&& mPreviousOffset == 0
//				&& (arg0 == 1 || arg0 == 2)) {
//			if (DEBUG) Log.d(TAG, "onPageScrolled()-->要调用mViewPager.setCurrentItem()-->会不会重绘界面呢");
//			mViewPager.setCurrentItem(mCurrentPagePosition, false);
//		}
//		mPreviousOffset = positionOffset;
	}

	@Override
	public void onPageSelected(int pPosition) {
//		mIsChanged = true;
//		if (pPosition > POINT_LENGTH) {
//			mCurrentPagePosition = FIRST_ITEM_INDEX;
//		} else if (pPosition < FIRST_ITEM_INDEX) {
//			mCurrentPagePosition = POINT_LENGTH;
//		} else {
//			mCurrentPagePosition = pPosition;
//		}
//		if (DEBUG) Log.d(TAG, "onPageSelected()-->当前是哪个位置:  " + pPosition + "  当前应该是哪个view:  " + mCurrentPagePosition);
		if (DEBUG) Log.d(TAG, "onPageSelected()-->当前是哪个位置:  " + pPosition);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (arg0 == ViewPager.SCROLL_STATE_SETTLING) {
			mCurrentScrollState = "完成滚动";
		} else if (arg0 == ViewPager.SCROLL_STATE_DRAGGING) {
			mCurrentScrollState = "正在滚动";
		} else {
			mCurrentScrollState = "静止状态";
		}
		if (DEBUG) {
			Log.d(TAG, "onPageScrollStateChanged()-->当前状态: " + mCurrentScrollState);
		}
//			Log.d(TAG, "onPageScrollStateChanged()-->当前状态: " + mCurrentScrollState + "   当前真实位置:  " +
//					mCurrentPagePosition);
//		if (ViewPager.SCROLL_STATE_IDLE == arg0) {
//			if (mIsChanged) {
//				mIsChanged = false;
//				if (DEBUG)
//					Log.d(TAG, "onPageScrollStateChanged()-->这时候要切换到mCurrentPagePosition:  " + mCurrentPagePosition);
//				mViewPager.setCurrentItem(mCurrentPagePosition, false);
//			}
//		}

	}
}
