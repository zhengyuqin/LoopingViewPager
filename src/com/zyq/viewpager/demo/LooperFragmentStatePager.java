package com.zyq.viewpager.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author zyq 15-7-20
 */
public class LooperFragmentStatePager extends FragmentStatePagerAdapter {

	private List<ChildFragment> mFragments;


	public LooperFragmentStatePager(FragmentManager fm, List<ChildFragment> fragments) {
		super(fm);
		mFragments = fragments;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
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
}
