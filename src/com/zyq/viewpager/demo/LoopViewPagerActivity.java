package com.zyq.viewpager.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.zyq.viewpager.AutoLoopViewPager;
import com.zyq.viewpager.R;

import java.util.ArrayList;
import java.util.List;

public class LoopViewPagerActivity extends FragmentActivity {

	private List<ChildFragment> mChildFragments;
	private AutoLoopViewPager mAutoLoopViewPager;
	private LooperFragmentStatePager mLooperFragmentStatePager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loopviewpager_view);
		mAutoLoopViewPager = (AutoLoopViewPager) findViewById(R.id.vp_looper);
		mChildFragments = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			mChildFragments.add(ChildFragment.newInstance(String.valueOf(i)));
		}
		mLooperFragmentStatePager = new LooperFragmentStatePager(getSupportFragmentManager(), mChildFragments);
		mAutoLoopViewPager.setAdapter(mLooperFragmentStatePager);
		mAutoLoopViewPager.startAutoScroll();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_loop_view_pager, menu);
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
}
