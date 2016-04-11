package com.myhebut.classes;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 取消viewpager的左右滑动
 */
public class NoScrollViewPage extends ViewPager {

	public NoScrollViewPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public NoScrollViewPage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 不让自己拦截
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

}
