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

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

/**
 * A ViewPager subclass enabling infinte scrolling of the viewPager elements
 * <p/>
 * When used for paginating views (in opposite to fragments), no code changes
 * should be needed only change xml's from <android.support.v4.view.ViewPager>
 * to <com.zyq.viewpager.LoopViewPager>
 * <p/>
 * If "blinking" can be seen when paginating to first or last view, simply call
 * seBoundaryCaching( true ), or change DEFAULT_BOUNDARY_CASHING to true
 * <p/>
 * When using a FragmentPagerAdapter or FragmentStatePagerAdapter,
 * additional changes in the adapter must be done.
 * The adapter must be prepared to create 2 extra items e.g.:
 * <p/>
 * The original adapter creates 4 items: [0,1,2,3]
 * The modified adapter will have to create 6 items [0,1,2,3,4,5]
 * with mapping realPosition=(position-1)%count
 * [0->3, 1->0, 2->1, 3->2, 4->3, 5->0]
 */
public class AutoLoopViewPager extends ViewPager {

	private static final boolean DEBUG = true;
	private static final String TAG = "AutoLoopViewPager";

	private static final boolean DEFAULT_BOUNDARY_CASHING = true;
	public static final int DEFAULT_INTERVAL = 3000;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	OnPageChangeListener mOuterPageChangeListener;
	private LoopPagerAdapterWrapper mAdapter;
	private boolean mBoundaryCaching = DEFAULT_BOUNDARY_CASHING;

	/**
	 * do nothing when sliding at the last or first item *
	 */
	public static final int SLIDE_BORDER_MODE_NONE = 0;
	/**
	 * cycle when sliding at the last or first item *
	 */
	public static final int SLIDE_BORDER_MODE_CYCLE = 1;
	/**
	 * deliver event to parent when sliding at the last or first item *
	 */
	public static final int SLIDE_BORDER_MODE_TO_PARENT = 2;

	/**
	 * auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL} *
	 */
	private long interval = DEFAULT_INTERVAL;
	/**
	 * auto scroll direction, default is {@link #RIGHT} *
	 */
	private int direction = RIGHT;
	/**
	 * whether automatic cycle when auto scroll reaching the last or first item,
	 * default is true
	 */
	private boolean isCycle = true;
	/**
	 * whether stop auto scroll when touching, default is true *
	 */
	private boolean stopScrollWhenTouch = true;
	/**
	 * how to process when sliding at the last or first item, default is
	 * {@link #SLIDE_BORDER_MODE_NONE}
	 */
	private int slideBorderMode = SLIDE_BORDER_MODE_CYCLE;
	/**
	 * whether animating when auto scroll at the last or first item *
	 */
	private boolean isBorderAnimation = true;
	private boolean roundTrip = false;
	private Handler handler;
	private boolean isAutoScroll = false;
	private boolean isStopByTouch = false;
	private float touchX = 0f, downX = 0f;
	private CustomDurationScroller scroller = null;

	public static final int SCROLL_WHAT = 0;

	/**
	 * helper function which may be used when implementing FragmentPagerAdapter
	 *
	 * @param position
	 * @param count
	 * @return (position-1)%count
	 */
	public static int toRealPosition(int position, int count) {
		position = position - 1;
		if (position < 0) {
			position += count;
		} else {
			position = position % count;
		}
		return position;
	}

	/**
	 * If set to true, the boundary views (i.e. first and last) will never be destroyed
	 * This may help to prevent "blinking" of some views
	 *
	 * @param flag
	 */
	public void setBoundaryCaching(boolean flag) {
		mBoundaryCaching = flag;
		if (mAdapter != null) {
			mAdapter.setBoundaryCaching(flag);
		}
	}

	/**
	 * start auto scroll, first scroll delay time is {@link #getInterval()}
	 */
	public void startAutoScroll() {
		isAutoScroll = true;
		sendScrollMessage(interval);
	}

	/**
	 * start auto scroll
	 *
	 * @param delayTimeInMills first scroll delay time
	 */
	public void startAutoScroll(int delayTimeInMills) {
		isAutoScroll = true;
		sendScrollMessage(delayTimeInMills);
	}

	/**
	 * stop auto scroll
	 */
	public void stopAutoScroll() {
		isAutoScroll = false;
		handler.removeMessages(SCROLL_WHAT);
	}

	/**
	 * get auto scroll time in milliseconds, default is
	 * {@link #DEFAULT_INTERVAL}
	 *
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * set auto scroll time in milliseconds, default is
	 * {@link #DEFAULT_INTERVAL}
	 *
	 * @param interval the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		mAdapter = new LoopPagerAdapterWrapper(adapter);
		mAdapter.setBoundaryCaching(mBoundaryCaching);
		super.setAdapter(mAdapter);
		setCurrentItem(0, false);
	}

	@Override
	public PagerAdapter getAdapter() {
		return mAdapter != null ? mAdapter.getRealAdapter() : mAdapter;
	}

	@Override
	public int getCurrentItem() {
		return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
	}

	public void setCurrentItem(int item, boolean smoothScroll) {
		int realItem = mAdapter.toInnerPosition(item);
		if (DEBUG) Log.d(TAG, "currentItem:" + realItem);
		super.setCurrentItem(realItem, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		if (DEBUG) Log.d(TAG, "当前在哪个item: " + getCurrentItem());
		if (getCurrentItem() != item) {
			setCurrentItem(item, true);
		}
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOuterPageChangeListener = listener;
	}


	public AutoLoopViewPager(Context context) {
		super(context);
		init();
	}

	public AutoLoopViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		super.setOnPageChangeListener(onPageChangeListener);
		handler = new LooperHandler();
		setViewPagerScroller();
	}

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		private float mPreviousOffset = -1;
		private float mPreviousPosition = -1;

		//只有界面发生了变化,才会调用该方法,所以需要求到需要显示的位置.
		@Override
		public void onPageSelected(int arg0) {
			if (DEBUG) Log.d(TAG, "onPageSelected:  " + arg0);
//			int realPosition = mAdapter.toRealPosition(position);
////			if ((realPosition == 0 && position == mAdapter.getCount() - 1) ||
////					(realPosition == getAdapter().getCount() - 1 && position == 0)) {
////				setCurrentItem(realPosition, false);
////			}
//			//表示两次真实位置不同,说明发生了移动
//			if (mPreviousPosition != realPosition) {
//				mPreviousPosition = realPosition;
//				if (mOuterPageChangeListener != null) {
//					mOuterPageChangeListener.onPageSelected(realPosition);
//				}
//			}
			if (mAdapter != null) {
				int position = AutoLoopViewPager.super.getCurrentItem();
				if (DEBUG) Log.d(TAG, "onPageScrollStateChanged:  " + position);
				int realPosition = mAdapter.toRealPosition(position);
//				if (state == ViewPager.SCROLL_STATE_IDLE
//						&& (position == 0 || position == mAdapter.getCount() - 1)) {
//					if (DEBUG) Log.d(TAG, "到达相邻页   " + realPosition);
//					setCurrentItem(, false);
//				}

				if (DEBUG) Log.d(TAG, "到达相邻页:  " + realPosition);
				if (position == 0) {
					setCurrentItem(realPosition, false);
				} else if (position == mAdapter.getCount() - 1) {
					setCurrentItem(realPosition, false);
				}

			}
		}

		/**
		 * 这个方法会在滚动过程中会一直走,所以,setCurrentItem(realPosition,false)设立在该方法和onPageStateChanged()中
		 *
		 * @param position
		 * @param positionOffset
		 * @param positionOffsetPixels
		 */
		@Override
		public void onPageScrolled(int position, float positionOffset,
		                           int positionOffsetPixels) {
			//if (DEBUG) Log.d(TAG, "onPageScrolled: " + position);
			int realPosition = position;
			if (mAdapter != null) {
				realPosition = mAdapter.toRealPosition(position);
				/**
				 * 这里要这么做的原因:首先我们已经缓存了第一张和最后一张图了,打log发现,当我们执行了onPageStateChanged()后
				 * 我们做了setCurrentItem(),这时候重新instantiateItem,最多有三张,假设我们从最后一张图切到第一张时候,这时候,会
				 * 重绘0,1,2这三张图(0:应该指向最后一张图),若此时只有三张图1,2,3,图2不会重绘,因为它还没被销毁,这是特殊情况(可以减少重绘发生)
				 * 调试发现只要你后面(不是初始化)调用了setCurrentItem(position,boolean)这个方法,且position-1 ~ position +1,这个范围内的view被销毁的话,
				 * 就会调用instantiateItem(),之后肯定会调用onPageSelected(),然后会调用onPageScrolled(),所以就会出现下面这种情况:
				 * positionOffset = 0 ; mPreviousOffset = 0; (position == 1 || position == mAdapter.getAdapter()
				 * .getCount() - 1)
				 * 这里调用setCurrentItem(realPosition,false),主要是防止之前没被调用,其实没什么卵用.(现在还没要用到这段代码的地方 !!!!)
				 */
//				if (positionOffset == 0
//						&& mPreviousOffset == 0
//						&& (position == 1 || position == mAdapter.getAdapter().getCount() - 1)) {
//					setCurrentItem(realPosition, false);
//				}
			}

			mPreviousOffset = positionOffset;
			if (mOuterPageChangeListener != null) {
				/**
				 * 表示当前这个不是最后一张图片时候,
				 */
				if (realPosition != mAdapter.getRealCount() - 1) {
					mOuterPageChangeListener.onPageScrolled(realPosition,
							positionOffset, positionOffsetPixels);
				} else {
					/**
					 * 表示当前页是最后一页,且滑动距离超过了一半,左滑positionOffset是0-->0.99,且positionOffsetPixels 是递增的,
					 * 若是右滑表示positionOffset 0.99-->0,且positionOffsetPixels 递减,所以这里还要做判断往哪个方向,但考虑到这方法一般
					 * 不在子类重写,所以,下面这块可以再找时间改改.
					 */
					if (positionOffset > .5) {
						mOuterPageChangeListener.onPageScrolled(0, 0, 0);
					} else {
						mOuterPageChangeListener.onPageScrolled(realPosition,
								0, 0);
					}
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

			if (mOuterPageChangeListener != null) {
				mOuterPageChangeListener.onPageScrollStateChanged(state);
			}
		}
	};

	/**
	 * set ViewPager scroller to change animation duration when sliding
	 */
	private void setViewPagerScroller() {
		try {
			Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
			scrollerField.setAccessible(true);
			Field interpolatorField = ViewPager.class
					.getDeclaredField("sInterpolator");
			interpolatorField.setAccessible(true);
			scroller = new CustomDurationScroller(getContext(),
					(Interpolator) interpolatorField.get(null));
			scroller.setScrollDurationFactor(10.0);
			scrollerField.set(this, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private class LooperHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case SCROLL_WHAT:
					scrollOnce();
					sendScrollMessage(interval);
					break;
				default:
					break;
			}
		}
	}


	private void sendScrollMessage(long delayTimeInMills) {
		/** remove messages before, keeps one message is running at most **/
		handler.removeMessages(SCROLL_WHAT);
		handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
	}


	/**
	 * 当只有一张图片时候,没必要让他轮播
	 */
	public void scrollOnce() {
		PagerAdapter adapter = getAdapter();
		int currentItem = getCurrentItem();//是指真实的位置
		int totalCount = adapter.getCount();//全部的数量.

		if (DEBUG) {
			Log.d(TAG, "有多少张图片" + adapter.getCount());
		}

	/*	if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
			return;
		}*/

		int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
		if (DEBUG) {
			Log.d(TAG, "nextItem:" + nextItem + " currentItem: " + currentItem);
		}
		if (nextItem == totalCount) {//当当前跑到最后一页,(其实现在应该指向第一页)
			setCurrentItem(0, true);
		} else {
			setCurrentItem(nextItem, true);
		}
	}

	/**
	 * 如果当前是ACTION_DOWN,就停止滚动
	 * 如果是ACTION_UP,就开始自动滚动
	 *
	 * @param ev
	 * @return
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (stopScrollWhenTouch) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN && isAutoScroll) {
				isStopByTouch = true;
				stopAutoScroll();
			} else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
				startAutoScroll();
			}
		}

		if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
			touchX = ev.getX();
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				downX = touchX;
			}
			int currentItem = getCurrentItem();
			PagerAdapter adapter = getAdapter();
			int pageCount = adapter == null ? 0 : adapter.getCount();
			/**
			 * current index is first one and slide to right or current index is
			 * last one and slide to left.<br/>
			 * if slide border mode is to parent, then
			 * requestDisallowInterceptTouchEvent false.<br/>
			 * else scroll to last one when current item is first one, scroll to
			 * first one when current item is last one.
			 */
			if ((currentItem == 0 && downX <= touchX) || (currentItem == pageCount - 1 && downX >= touchX)) {
				if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					if (pageCount > 1) {
						getParent().requestDisallowInterceptTouchEvent(true);
					} else {
						getParent().requestDisallowInterceptTouchEvent(false);
					}
				}
				return super.onTouchEvent(ev);
			}
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		return super.onTouchEvent(ev);
	}
}
