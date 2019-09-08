package com.ryg.chapter_5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.ryg.chapter_5.utils.MyConstants;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private LinearLayout mRemoteViewsContent;

    private BroadcastReceiver mRemoteViewsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RemoteViews remoteViews = intent.getParcelableExtra(MyConstants.EXTRA_REMOTE_VIEWS);
            if (remoteViews != null) {
                updateUI(remoteViews);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        updateUI(mRemoteViewsContent);
    }

    private void initView() {
        mRemoteViewsContent = findViewById(R.id.remote_views_content);
        IntentFilter filter = new IntentFilter(MyConstants.REMOTE_ACTION);
        registerReceiver(mRemoteViewsReceiver, filter);
    }

    private void updateUI(RemoteViews remoteViews) {
        //同一个应用多进程，从TestActivity中传递过来。
//        View view = remoteViews.apply(this, mRemoteViewsContent);
//        mRemoteViewsContent.addView(view);

        //不同的应用，从DemoActivity_2中传递过来。
        int layoutId = getResources().getIdentifier(
                "layout_simulated_notification", "layout", getPackageName());
        View view = getLayoutInflater().inflate(layoutId, mRemoteViewsContent, false);
        remoteViews.reapply(this, view);
        mRemoteViewsContent.addView(view);
    }

    private void updateUI(LinearLayout mRemoteViewsContent) {
        //加载另外一个应用的布局到自己的应用。视频中介绍的简单方法。
        final String pkg = "com.ryg.chapter_4";
        Resources resources = null;
        try {
            resources = getPackageManager().getResourcesForApplication(pkg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (resources != null) {
            int layoutId = resources.getIdentifier("activity_main", "layout", pkg);
            RemoteViews remoteViews = new RemoteViews(pkg, layoutId);
            View view1 = remoteViews.apply(this, mRemoteViewsContent);
            mRemoteViewsContent.addView(view1);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mRemoteViewsReceiver);
        super.onDestroy();
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.button1) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.button2) {
            Intent intent = new Intent(this, DemoActivity_2.class);
            startActivity(intent);
        }
    }

}
