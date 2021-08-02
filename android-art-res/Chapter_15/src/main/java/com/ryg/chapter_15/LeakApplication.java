package com.ryg.chapter_15;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class LeakApplication extends Application {

    private RefWatcher mRefWatcher;



    @Override
    public void onCreate() {
        super.onCreate();

        //默认只检测activity，一般activity是内存泄漏的大头
        mRefWatcher = LeakCanary.install(this);

        //检查其他对象
//        mRefWatcher.watch(this);
    }

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        LeakApplication leakApplication = (LeakApplication) context.getApplicationContext();
        return leakApplication.mRefWatcher;
    }
}