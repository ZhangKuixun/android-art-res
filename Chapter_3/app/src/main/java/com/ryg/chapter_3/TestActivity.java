package com.ryg.chapter_3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends Activity implements OnClickListener,
        OnLongClickListener {

    private static final String TAG = "TestActivity";

    private static final int MESSAGE_SCROLL_TO = 1;
    private static final int FRAME_COUNT = 30;
    private static final int DELAYED_TIME = 33;

    private Button mButton1;
    private View mButton2;

    private int mCount = 0;

    //3.3.3 使用延时策略
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SCROLL_TO: {
                    mCount++;
                    if (mCount <= FRAME_COUNT) {
                        float fraction = mCount / (float) FRAME_COUNT;
                        int scrollX = (int) (fraction * 100);
                        mButton1.scrollTo(scrollX, 0);
                        mHandler.sendEmptyMessageDelayed(MESSAGE_SCROLL_TO, DELAYED_TIME);
                    }
                    break;
                }

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        mButton1 = findViewById(R.id.button1);
        mButton1.setOnClickListener(this);
        mButton2 = findViewById(R.id.button2);
        mButton2.setOnLongClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.d(TAG, "button1.left=" + mButton1.getLeft());
            Log.d(TAG, "button1.x=" + mButton1.getX());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mButton1) {
//             mButton1.setTranslationX(100);

//            //移动的动画  改变布局参数
//            Log.d(TAG, "button1.left=" + mButton1.getLeft());
//            Log.d(TAG, "button1.x=" + mButton1.getX());
//            ObjectAnimator.ofFloat(mButton1, "translationX", 0, 100).setDuration(1000).start();
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mButton1.getLayoutParams();
//            params.width += 100;
//            params.leftMargin += 100;
//            mButton1.requestLayout();
//            mButton1.setLayoutParams(params);

            //3.3.2 通过动画
//            final int startX = 0;
//            final int deltaX = 100;
//            ValueAnimator animator = ValueAnimator.ofInt(0, 1).setDuration(1000);
//            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animator) {
//                    float fraction = animator.getAnimatedFraction();
//                    mButton1.scrollTo(startX + (int) (deltaX * fraction), 0);
//                }
//            });
//            animator.start();

            mHandler.sendEmptyMessageDelayed(MESSAGE_SCROLL_TO, DELAYED_TIME);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, "long click", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }
}
