package com.zyq.viewpager.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyq.viewpager.R;

/**
 * @author zyq 15-7-20
 */
public class ChildFragment extends Fragment {

	private static final boolean DEBUG = true;
	private static final String TAG = "fragment";
	private static final String TEXT = "text";
	private TextView mTvContent;
	private String mContent;

	public static ChildFragment newInstance(String content) {
		ChildFragment childFragment = new ChildFragment();
		Bundle args = new Bundle();
		args.putString(TEXT, content);
		childFragment.setArguments(args);
		return childFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DEBUG) Log.d(TAG, "onCreate()" + mContent);


			/*Fragment.SavedState savedState = getActivity().getSupportFragmentManager().saveFragmentInstanceState
			(this);
			if (savedState != null) {
				this.setInitialSavedState(savedState);
				return;
			}*/

		/*if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(TEXT)) {
				if (DEBUG) Log.d(TAG, "onCreate()-->savedInstanceState");
				mContent = savedInstanceState.getString(TEXT);
			}
		} else {*/
		if (getArguments() != null) {
			if (DEBUG) Log.d(TAG, "onCreate()-->getArguments()");
			mContent = getArguments().getString(TEXT);
		}
		//}

		if (DEBUG) Log.d(TAG, "onCreate()" + mContent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (DEBUG) Log.d(TAG, "onCreateView()" + mContent);
		View view = inflater.inflate(R.layout.fragment_child_layout, null);
		mTvContent = (TextView) view.findViewById(R.id.tv_content);
		mTvContent.setText(mContent);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (DEBUG) Log.d(TAG, "onViewCreated()");
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (DEBUG) Log.d(TAG, "onSaveInstanceState()" + mContent);
		outState.putString(TEXT, mContent);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (DEBUG) Log.d(TAG, "onDestory()-->" + mContent);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (DEBUG) Log.d(TAG, "onDestroyView()-->" + mContent);
	}
}
