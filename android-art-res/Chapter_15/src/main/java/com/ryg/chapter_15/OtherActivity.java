package com.ryg.chapter_15;

import android.app.Activity;
import android.os.Bundle;

import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;

public class OtherActivity extends Activity {

    private static ArrayList<Activity> mActivities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other);
        mActivities.add(OtherActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = LeakApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
