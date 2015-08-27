/*
 * Copyright (C) 2013 Leszek Mzyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zyq.viewpager;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * A PagerAdapter wrapper responsible for providing a proper page to
 * LoopViewPager
 * <p/>
 * <p/>
 * This class shouldn't be used directly
 */
public class LoopPagerAdapterWrapper extends PagerAdapter {

	private static final boolean DEBUG = true;
	private static final String TAG = "LoopPagerAdapterWrapper";
	private PagerAdapter mAdapter;

	private SparseArray<ToDestroy> mToDestroy = new SparseArray<ToDestroy>();

	private boolean mBoundaryCaching = true;

	void setBoundaryCaching(boolean flag) {
		mBoundaryCaching = flag;
	}

	LoopPagerAdapterWrapper(PagerAdapter adapter) {
		this.mAdapter = adapter;
	}

	@Override
	public void notifyDataSetChanged() {
		mToDestroy = new SparseArray<ToDestroy>();
		super.notifyDataSetChanged();
	}

	int toRealPosition(int position) {
		int realCount = getRealCount();
		if (realCount == 0)
			return 0;
		int realPosition = (position - 1) % realCount;
		if (realPosition < 0)
			realPosition += realCount;

		return realPosition;
	}

	public int toInnerPosition(int realPosition) {
		int position = (realPosition + 1);
		return position;
	}

	private int getRealFirstPosition() {
		return 1;
	}

	private int getRealLastPosition() {
		return getRealFirstPosition() + getRealCount() - 1;
	}

	@Override
	public int getCount() {
		return mAdapter.getCount() + 2;
	}

	public int getRealCount() {
		return mAdapter.getCount();
	}

	public PagerAdapter getRealAdapter() {
		return mAdapter;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		int realPosition = toRealPosition(position);
		if (DEBUG) Log.d(TAG, "这时候要绘制哪个界面呢:  " + realPosition);

		/**
		 *  考虑到切换到realPosition = realFirst || realPosition = readLast 时候,这时候,图片已经被回收了,FragmentStatePagerAdapter
		 *  会保存 当前页,和前后一页,所以很有可能会被回收,所以这里的处理策略是用保存第一页和最后一页,这样onPageSe
		 */
//		if (mBoundaryCaching) {
//			ToDestroy toDestroy = mToDestroy.get(position);
//			if (toDestroy != null) {
//				mToDestroy.remove(position);
//				return toDestroy.object;
//			}
//		}
		return mAdapter.instantiateItem(container, realPosition);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (DEBUG) Log.d(TAG, "destroyItem:" + position + "object:" + object);
		int realFirst = getRealFirstPosition();
		int realLast = getRealLastPosition();
		int realPosition = toRealPosition(position);

		if (mBoundaryCaching && (position == 0 || position == getCount() - 1)) {
			mToDestroy.put(position, new ToDestroy(container, realPosition,
					object));
		} else {
			if (DEBUG) Log.d(TAG, "让chidlAdpater自己destory");
			mAdapter.destroyItem(container, realPosition, object);
		}

		//mAdapter.destroyItem(container,realPosition,object);
	}

    /*
     * Delegate rest of methods directly to the inner adapter.
     */

	@Override
	public void finishUpdate(ViewGroup container) {
		mAdapter.finishUpdate(container);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return mAdapter.isViewFromObject(view, object);
	}

	@Override
	public void restoreState(Parcelable bundle, ClassLoader classLoader) {
		mAdapter.restoreState(bundle, classLoader);
	}

	@Override
	public Parcelable saveState() {
		return mAdapter.saveState();
	}

	@Override
	public void startUpdate(ViewGroup container) {
		mAdapter.startUpdate(container);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mAdapter.setPrimaryItem(container, position, object);
	}

	public PagerAdapter getAdapter() {
		return mAdapter;
	}

    /*
     * End delegation
     */

	/**
	 * Container class for caching the boundary views
	 */
	static class ToDestroy {
		ViewGroup container;
		int position;
		Object object;

		public ToDestroy(ViewGroup container, int position, Object object) {
			this.container = container;
			this.position = position;
			this.object = object;
		}
	}

}