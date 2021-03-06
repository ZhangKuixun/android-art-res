package xmu.software.acbuwa.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

import xmu.software.acbuwa.R;
import xmu.software.acbuwa.callback.SizeCallBack;

public class MenuHorizontalScrollView extends HorizontalScrollView {
    private static final String TAG = "MenuHorizontalScrollView";

    /* 当前控件 */
    private MenuHorizontalScrollView me;

    /* 菜单 */
    private ListView menu;

    /* 菜单状态 */
    public static boolean menuOut;

    /* 扩展宽度 */
    private final int ENLARGE_WIDTH = 80;

    /* 菜单的宽度 */
    private int menuWidth;

    /* 手势动作最开始时的x坐标 */
    private float lastMotionX = -1;

    /* 按钮 */
    private Button menuBtn;

    /* 当前滑动的位置 */
    private int current;

    private int scrollToViewPos;

    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    // 分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;

    public MenuHorizontalScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    public MenuHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    public MenuHorizontalScrollView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {
        this.setHorizontalFadingEdgeEnabled(false);
        this.setVerticalFadingEdgeEnabled(false);
        this.me = this;
        this.me.setVisibility(View.INVISIBLE);
        menuOut = false;
    }

    public void initViews(View[] children, SizeCallBack sizeCallBack,
                          ListView menu) {
        this.menu = menu;
        ViewGroup parent = (ViewGroup) getChildAt(0);

        for (int i = 0; i < children.length; i++) {
            children[i].setVisibility(View.INVISIBLE);
            parent.addView(children[i]);
        }

        OnGlobalLayoutListener onGlLayoutistener = new MenuOnGlobalLayoutListener(
                parent, children, sizeCallBack);
        getViewTreeObserver().addOnGlobalLayoutListener(onGlLayoutistener);

    }

    /**
     * 设置按钮
     *
     * @param btn
     */
    public void setMenuBtn(Button btn) {
        this.menuBtn = btn;
    }

    public void clickMenuBtn() {

        if (!menuOut) {
            this.menuWidth = 0;
        } else {
            this.menuWidth = this.menu.getMeasuredWidth()
                    - this.menuBtn.getMeasuredWidth() - this.ENLARGE_WIDTH;
        }
        menuSlide();
    }

    /**
     * 滑动出菜单
     */
    private void menuSlide() {
        if (this.menuWidth == 0) {
            menuOut = true;
        } else {
            menuOut = false;
        }
        me.smoothScrollTo(this.menuWidth, 0);
        if (menuOut)
            this.menuBtn.setBackgroundResource(R.drawable.menu_fold);
        else
            this.menuBtn.setBackgroundResource(R.drawable.menu_unfold);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (l < (this.menu.getMeasuredWidth() - this.menuBtn.getMeasuredWidth() - this.ENLARGE_WIDTH) / 2) {
            this.menuWidth = 0;
        } else {
            this.menuWidth = this.menu.getWidth()
                    - this.menuBtn.getMeasuredWidth() - this.ENLARGE_WIDTH;
        }
        this.current = l;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                intercepted = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //外部拦截法，判断是否需要拦截滑动事件。
                final int deltaX = x - mLastXIntercept;
                final int deltaY = y - mLastYIntercept;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = false;
                break;
            }
            default:
                break;
        }
        mLastXIntercept = x;
        mLastYIntercept = y;
        mLastX = x;
        mLastY = y;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        // 当菜单滑出来以后，菜单但不能被点击，事件不能穿透 HorizontalScrollView。
        // current 表示 HorizontalScrollView 在X轴滑动的坐标。点击的范围小于 scrollToViewPos。
        if ((this.current == 0 && x < this.scrollToViewPos)) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // 当我们拦截了 ACTION_MOVE 之后，HorizontalScrollView 划不动了。因为 HorizontalScrollView
            // 内部重写了 onInterceptTouchEvent 和 onTouchEvent,就导致 HorizontalScrollView 的一些特性发生了改变。
            // 解决：ACTION_MOVE 中自己去 scrollBy
            scrollBy(mLastX - x, 0);
            mLastX = x;
            mLastY = y;
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //划出一段距离之后突然松手后，页面该朝哪边滑动
            menuSlide();
            return true;
        }
        return super.onTouchEvent(event);
    }

    /****************************************************/
    /*-												   -*/
    /*-			Class 			Area				   -*/
    /*-												   -*/

    /****************************************************/

    public class MenuOnGlobalLayoutListener implements OnGlobalLayoutListener {

        private ViewGroup parent;
        private View[] children;
        // private int scrollToViewIndex = 0;
        private SizeCallBack sizeCallBack;

        public MenuOnGlobalLayoutListener(ViewGroup parent, View[] children,
                                          SizeCallBack sizeCallBack) {

            this.parent = parent;
            this.children = children;
            this.sizeCallBack = sizeCallBack;
        }

        @Override
        public void onGlobalLayout() {
            // TODO Auto-generated method stub
            me.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            this.sizeCallBack.onGlobalLayout();
            this.parent.removeViewsInLayout(0, children.length);
            int width = me.getMeasuredWidth();
            int height = me.getMeasuredHeight();

            int[] dims = new int[2];
            scrollToViewPos = 0;

            for (int i = 0; i < children.length; i++) {
                this.sizeCallBack.getViewSize(i, width, height, dims);
                children[i].setVisibility(View.VISIBLE);

                parent.addView(children[i], dims[0], dims[1]);
                if (i == 0) {
                    scrollToViewPos += dims[0];
                }
            }
            // if(firstLoad){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    me.scrollBy(scrollToViewPos, 0);

                    /* 视图不是中间视图 */
                    me.setVisibility(View.VISIBLE);
                    menu.setVisibility(View.VISIBLE);
                }
            });
            // }

        }
    }

}
