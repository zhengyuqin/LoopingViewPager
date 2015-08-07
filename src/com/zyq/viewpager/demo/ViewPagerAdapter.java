package com.zyq.viewpager.demo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author zyq 15-7-16
 */
public class ViewPagerAdapter extends PagerAdapter {

	private static final boolean DEBUG = true;
	private Context mContext;
	private List<View> mViews;

	public ViewPagerAdapter(Context context, List<View> list) {
		mContext = context;
		mViews = list;
	}

	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public int getItemPosition(Object object) {
		if (DEBUG) Log.d("catrosel", "getItemPosition()-->object:  " + object);
		return super.getItemPosition(object);
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (DEBUG) Log.d("catrosel", "instantiateItem()-->position:  " + position);
		container.addView(mViews.get(position));
		return mViews.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (DEBUG) Log.d("catrosel", "destoryItem()-->position:  " + position);
		container.removeView((View) object);
	}
}
