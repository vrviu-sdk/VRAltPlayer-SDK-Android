package com.viu.vrplayer.demo;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by VRVIU on 2018/1/4.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从栈中移除该Activity
        MyApplication.getInstance().finishActivity(this);
    }
}
