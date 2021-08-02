package com.ryg.chapter_7;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class DemoActivity_1 extends Activity {
    private static final String TAG = "DemoActivity_1";
    private Button mButton;
    private ObjectAnimator mColorAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_1);
        initView();
    }

    private void initView() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //为什么要在这调用动画？因为在这里所有的View已经初始化好了。
        if (hasFocus) {
            mButton = findViewById(R.id.button1);

            //让这个button的宽度从当前的宽度增加到500。

//            performAnimate(mButton, mButton.getWidth(), 500);

            //wrapper
//            performAnimateByWrapper();

            //内存泄漏
            leakedAnimator();
        }
    }


    //第三种方式：ValueAnimator
    private void performAnimate(final View target, final int start, final int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

            // 持有一个IntEvaluator对象，方便下面估值的时候使用
            private IntEvaluator mEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                // 获得当前动画的进度值，整型，1-100之间
                int currentValue = (Integer) animator.getAnimatedValue();
                Log.d(TAG, "current value: " + currentValue);

                // 获得当前进度占整个动画过程的比例，浮点型，0-1之间
                float fraction = animator.getAnimatedFraction();
                // 直接调用整型估值器通过比例计算出宽度，然后再设给Button
                target.getLayoutParams().width = mEvaluator.evaluate(fraction, start, end);
                target.requestLayout();
            }
        });

        valueAnimator.setDuration(5000).start();
    }

    private void performAnimateByWrapper() {
        ViewWrapper wrapper = new ViewWrapper(mButton);
        ObjectAnimator.ofInt(wrapper, "width", 300).setDuration(5000).start();
    }

    //第二种方式: Wrapper
    private static class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View target) {
            mTarget = target;
        }

        public int getWidth() {
            //动画播放的很怪。因为在xml布局中的宽写的是 wrap_content, wrap_content=-2。
//            return mTarget.getLayoutParams().width;

            //我们不拿LayoutParams里面的宽度了,直接拿他的宽度。
            return mTarget.getWidth();
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }
    }


    //属性动画的坑：内存泄漏

    //一直改变button的背景颜色
    private void leakedAnimator() {
        mColorAnim = ObjectAnimator.ofInt(
                mButton, "backgroundColor", 0xffff8080, 0xff8080ff);
        mColorAnim.setDuration(2000);
        mColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        mColorAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Log.d(TAG, "onAnimator in leakedAnimator");
            }
        });
        mColorAnim.start();
    }

    //必须要在页面关闭的时候把动画 cancel 掉。
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mColorAnim.cancel();
    }
}
