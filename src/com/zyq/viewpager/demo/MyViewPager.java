package com.zyq.viewpager.demo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;


/**
 * @author zyq 15-8-12
 */
public class MyViewPager extends ViewPager {

	private final boolean DEBUG = true;
	private final String TAG = "viewpager";
	private ScrollView mScrollView;

	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (DEBUG) Log.d(TAG, "ACTION_DOWN");
				break;
			case MotionEvent.ACTION_UP:
				if (DEBUG) Log.d(TAG, "ACTION_UP");
				break;
			case MotionEvent.ACTION_CANCEL:
				if (DEBUG) Log.d(TAG, "ACTION_CANCEL");
				break;
			case MotionEvent.ACTION_MOVE:
				if (DEBUG) Log.d(TAG, "移动过程中是否是fakeDrag:  " + isFakeDragging());
				break;
		}
		return super.onTouchEvent(ev);
	}


	@Override
	public void computeScroll() {
		if (DEBUG) Log.d(TAG, "移动过程中是不是经常要重写该方法:computeScroll()");
		super.computeScroll();
	}
}
