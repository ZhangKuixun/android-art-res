package com.ryg.chapter_3.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 滑动冲突场景2-外部拦截
 * <p>
 * 创建一个上下胡奥的那个的数值LinearLayout，然后在他的内部分别放入一个Header和一个ListView，这样内外两层都能
 * 上下滑动，于是就形成了场景二中的滑动冲突。
 * <p>
 * StickyLayout S滑动规则：当Header隐藏时，要分情况如果ListVew滑动到顶部并且当前手势是下滑，这时需要StickyLayout
 * 拦截事件，其他情况又ListView拦截。
 */
public class StickyLayout extends LinearLayout {

    private static final String TAG = "StickyLayout";

    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    // 分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;

    public StickyLayout(Context context) {
        super(context);
        init();
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StickyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int intercepted = 0;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastXIntercept = x;
                mLastYIntercept = y;
                mLastX = x;
                mLastY = y;
                intercepted = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        Log.d(TAG, "intercepted=" + intercepted);
        return intercepted != 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                int desHeight = 0;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


}
