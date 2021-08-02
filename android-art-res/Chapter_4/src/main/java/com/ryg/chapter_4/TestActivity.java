package com.ryg.chapter_4;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 获取View的宽高
 * <p>
 * <p><p> 四种方法：
 * <p>
 * 1. Activity/View#onWindowFocusChanged。
 * onWindowFocusChanged这个方法的含义是：VieW已经初始化完毕了，宽高已经准备好了，需要注意：onWindowFocusChanged会被调用多次，当Activity的窗口得到焦点和失去焦点均会被调用。
 * <p>
 * 2. view.post(runnable)。 通过post将一个runnable投递到消息队列的尾部，当Looper调用此
 * runnable的时候，View也初始化好了。
 * <p>
 * 3. ViewTreeObserver。 使用 ViewTreeObserver 的众多回调可以完成这个功能，比如 OnGlobalLayoutListener 这个接口，当View树的状态发送改变或View树内部的View的可见性发生改变时， onGlobalLayout 方法会被回调。需要注意的是，伴随着View树状态的改变， onGlobalLayout 会被回调多次。
 * <p>
 * 4. view.measure(int widthMeasureSpec,int heightMeasureSpec)。
 * match_parent： 无法measure出具体的宽高，因为不知道父容器的剩余空间，无法测量出View的大小。
 * <p>
 * 具体的数值（dp/px）:
 * int widthMeasureSpec = MeasureSpec.makeMeasureSpec(100,MeasureSpec.EXACTLY);
 * int heightMeasureSpec = MeasureSpec.makeMeasureSpec(100,MeasureSpec.EXACTLY);
 * view.measure(widthMeasureSpec,heightMeasureSpec);
 * <p>
 * wrap_content：
 * int widthMeasureSpec = MeasureSpec.makeMeasureSpec((1<<30)-1,MeasureSpec.AT_MOST);
 * //View的尺寸使用30位二进制表示，最大值30个1，在AT_MOST模式下，我们用View理论上能支持的最大值去构造MeasureSpec是合理的.
 * int heightMeasureSpec = MeasureSpec.makeMeasureSpec((1<<30)-1,MeasureSpec.AT_MOST);
 * view.measure(widthMeasureSpec,heightMeasureSpec);
 */
public class TestActivity extends Activity implements OnClickListener {

    private static final String TAG = "TestActivity";

    private Button view;
    private View mButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        measureView();
    }

    private void initView() {
        view = (Button) findViewById(R.id.button1);
        view.setOnClickListener(this);
        mButton2 = (TextView) findViewById(R.id.button2);
    }

    private void measureView() {
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec((1 << 30) - 1, MeasureSpec.AT_MOST);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "measureView, width= " + view.getMeasuredWidth() + " height= " + view.getMeasuredHeight());
    }

    @Override
    protected void onStart() {
        super.onStart();
        view.post(new Runnable() {

            @Override
            public void run() {
                int width = view.getMeasuredWidth();
                int height = view.getMeasuredHeight();
            }
        });

        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = view.getMeasuredWidth();
                int height = view.getMeasuredHeight();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            Log.d(TAG, "onWindowFocusChanged, width= " + view.getMeasuredWidth() + " height= " + view.getMeasuredHeight());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == view) {
            Log.d(TAG, "measure width= " + mButton2.getMeasuredWidth() + " height= " + mButton2.getMeasuredHeight());
            Log.d(TAG, "layout width= " + mButton2.getWidth() + " height= " + mButton2.getHeight());
        }
    }

}
