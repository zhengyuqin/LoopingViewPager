package com.zyq.viewpager.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.zyq.viewpager.AutoLoopViewPager;

import java.util.List;

/**
 * @author zyq 15-7-20
 */
public class LooperFragmentStatePager extends FragmentStatePagerAdapter {

	private final boolean DEBUG = true;
	private final String TAG = "飞";
	private List<ChildFragment> mFragments;
	private AutoLoopViewPager mViewPager;


	public LooperFragmentStatePager(FragmentManager fm, List<ChildFragment> fragments, AutoLoopViewPager viewPager) {
		super(fm);
		mFragments = fragments;
		mViewPager = viewPager;
	}


	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if (DEBUG) {
			Log.d(TAG, "setPrimaryItem   " + position);
		}
		super.setPrimaryItem(container, position, object);

	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (DEBUG) {
			Log.d(TAG, "instantiateItem  是否已经存在:   " + mFragments.get(position).isAdded());
		}
		return super.instantiateItem(container, position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (DEBUG) {
			Log.d(TAG, "destroyItem删掉哪个:  " + position);
		}
		if (!mFragments.get(position).isAdded()) {
			if (DEBUG) Log.d(TAG, "本身不存在Fragments中:  " + position);
			return;
		}
		super.destroyItem(container, position, object);
	}

	@Override
	public Fragment getItem(int i) {
		return mFragments.get(i);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (DEBUG) Log.d(TAG, "finishUpdate  ");
		super.finishUpdate(container);
	}
}
