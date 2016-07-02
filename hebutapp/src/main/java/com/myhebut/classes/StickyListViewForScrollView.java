package com.myhebut.classes;

import android.content.Context;
import android.util.AttributeSet;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * 解决scrollview嵌套listview的问题
 */
public class StickyListViewForScrollView extends StickyListHeadersListView {
    public StickyListViewForScrollView(Context context) {
        super(context);
    }

    public StickyListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyListViewForScrollView(Context context, AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
