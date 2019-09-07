package com.ryg.chapter_4.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.ryg.chapter_4.R;

import java.util.ArrayList;

/**
 * 一个特殊的LinearLayout,任何放入内部的clickable元素都具有波纹效果，当它被点击的时候，
 * 为了性能，尽量不要在内部放入复杂的元素
 * note: long click listener is not supported current for fix compatible bug.
 */
public class RevealLayout extends LinearLayout implements Runnable {

    private static final String TAG = "DxRevealLayout";
    private static final boolean DEBUG = true;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mTargetWidth;
    private int mTargetHeight;
    private int mMinBetweenWidthAndHeight;
    private int mMaxBetweenWidthAndHeight;
    private int mMaxRevealRadius;
    private int mRevealRadiusGap;
    private int mRevealRadius = 0;
    private float mCenterX;
    private float mCenterY;
    private int[] mLocationInScreen = new int[2];

    private boolean mShouldDoAnimation = false;
    private boolean mIsPressed = false;
    private int INVALIDATE_DURATION = 40;

    private View mTouchTarget;
    private DispatchUpTouchEventRunnable mDispatchUpTouchEventRunnable = new DispatchUpTouchEventRunnable();

    public RevealLayout(Context context) {
        super(context);
        init();
    }

    public RevealLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RevealLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);// 我们需要用的 Draw 方法，ViewGroup 默认启动了 Will_Not_Draw
        mPaint.setColor(getResources().getColor(R.color.reveal_color));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.getLocationOnScreen(mLocationInScreen);//一个控件在其整个屏幕上的坐标位置
    }

    private void initParametersForChild(MotionEvent event, View view) {
        mCenterX = event.getX();
        mCenterY = event.getY();
        mTargetWidth = view.getMeasuredWidth();
        mTargetHeight = view.getMeasuredHeight();
        mMinBetweenWidthAndHeight = Math.min(mTargetWidth, mTargetHeight);
        mMaxBetweenWidthAndHeight = Math.max(mTargetWidth, mTargetHeight);
        mRevealRadius = 0;
        mShouldDoAnimation = true;
        mIsPressed = true;
        mRevealRadiusGap = mMinBetweenWidthAndHeight / 8;

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0] - mLocationInScreen[0];
        int transformedCenterX = (int) mCenterX - left;
        mMaxRevealRadius = Math.max(transformedCenterX, mTargetWidth - transformedCenterX);
    }

    /**
     * 在这里绘制的圆环，是在View上面。因为，在这里绘制圆环，所有的子View都已经绘制完成，所以它就会绘制在子View
     * 的上面。如果绘制在onDraw里面，圆环就会绘制在View的下面，在Layout中，onDraw方法是画自己，如果在onDraw
     * 中画圆环，子View是还没有绘制的，画出来的圆环就会被子View盖住。
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!mShouldDoAnimation || mTargetWidth <= 0 || mTouchTarget == null) {
            return;
        }

        if (mRevealRadius > mMinBetweenWidthAndHeight / 2) {
            mRevealRadius += mRevealRadiusGap * 4;
        } else {
            mRevealRadius += mRevealRadiusGap;
        }
        this.getLocationOnScreen(mLocationInScreen);
        int[] location = new int[2];
        mTouchTarget.getLocationOnScreen(location);// 3.找到点击的这个元素，它在屏幕中的位置
        Log.e(TAG, "location[0]=" + location[0] + " location[1]=" + location[1] + " mLocationInScreen[0]=" + mLocationInScreen[0] + " mLocationInScreen[1]=" + mLocationInScreen[1]);
        int left = location[0] - mLocationInScreen[0];// 4.得出相对于layout的相对位置
        int top = location[1] - mLocationInScreen[1];
        int right = left + mTouchTarget.getMeasuredWidth();
        int bottom = top + mTouchTarget.getMeasuredHeight();

        canvas.save();
        //2.裁剪。需要找到button点击的范围，先找到layout在屏幕中的位置，再找到点击的这个元素在屏幕中的位置。
        canvas.clipRect(left, top, right, bottom);
        //1.画圆
        canvas.drawCircle(mCenterX, mCenterY, mRevealRadius, mPaint);
        canvas.restore();

        //9.循环的去画水波纹
        if (mRevealRadius <= mMaxRevealRadius) {
            postInvalidateDelayed(INVALIDATE_DURATION, left, top, right, bottom);
        } else if (!mIsPressed) {
            mShouldDoAnimation = false;
            postInvalidateDelayed(INVALIDATE_DURATION, left, top, right, bottom);
        }
    }

    //为什么我们不能在onTouchEvent中去处理？因为这是一个 LinearLayout，LinearLayout#dispatchTouchEvent 默认不拦截任何事件，
    //如果我们在onTouchEvent中去处理，那么我们拦截不到任何事件。
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            //5.获取点击的View
            View touchTarget = getTouchTarget(this, x, y);

            if (touchTarget != null && touchTarget.isClickable() && touchTarget.isEnabled()) {
                mTouchTarget = touchTarget;
                initParametersForChild(event, touchTarget);
                //8.立刻刷新，一刷新就会触发View的Draw，由于是Layout，就会触发dispatchDraw。然后我们就可以循环的去画水波纹了。
                postInvalidateDelayed(INVALIDATE_DURATION);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            mIsPressed = false;
            postInvalidateDelayed(INVALIDATE_DURATION);
            mDispatchUpTouchEventRunnable.event = event;
            postDelayed(mDispatchUpTouchEventRunnable, 200);
            return true;
        } else if (action == MotionEvent.ACTION_CANCEL) {
            mIsPressed = false;
            postInvalidateDelayed(INVALIDATE_DURATION);
        }

        return super.dispatchTouchEvent(event);
    }

    //6.找到Layout里面所有可点击的元素
    private View getTouchTarget(View view, int x, int y) {
        View target = null;
        ArrayList<View> TouchableViews = view.getTouchables();
        for (View child : TouchableViews) {
            if (isTouchPointInView(child, x, y)) {
                target = child;
                break;
            }
        }

        return target;
    }

    //7.判断点击的坐标是否在View的范围内
    private boolean isTouchPointInView(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);//把这个View在屏幕中的范围取出来，然后判断点击的点在不在一个矩形里面。
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (view.isClickable() && y >= top &&
                y <= bottom && x >= left && x <= right) {//判断点击的点在不在一个矩形里面。
            return true;
        }
        return false;
    }

    /**
     * 最后，怎么延迟？ 如果我们点击了这个按钮，就立刻执行跳转，那么我们的水波纹效果就绘制不完。
     */
    //我们监听你是否点击了一个 button，如果点击了，那么我们就把这个点击的行为 postDelayed。
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        postDelayed(this, 400);//400毫秒以后，再去触发一个 Runnable，在 Runnable 中做真正的点击操作。
        return true;
    }

    @Override
    public void run() {
        super.performClick();
    }

    private class DispatchUpTouchEventRunnable implements Runnable {
        MotionEvent event;

        @Override
        public void run() {
            if (mTouchTarget == null || !mTouchTarget.isEnabled()) {
                return;
            }

            if (isTouchPointInView(mTouchTarget, (int) event.getRawX(), (int) event.getRawY())) {
                //当我们的Runnable中执行操作以后，会直接调用 performClick 去执行点击，这样我们的点击效果就完成了。
                mTouchTarget.performClick();
            }
        }
    }
}
