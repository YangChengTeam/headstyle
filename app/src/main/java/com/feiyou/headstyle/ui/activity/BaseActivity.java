package com.feiyou.headstyle.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hwangjr.rxbus.RxBus;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * Created by admin on 2016/9/2.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        RxBus.get().register(this);
        initVars();
        initViews();
        loadData();
    }

    protected void initVars() {

    }

    public void initViews() {
        ButterKnife.bind(this);
        ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View parentView = contentFrameLayout.getChildAt(0);
        if (parentView != null && Build.VERSION.SDK_INT >= 14) {
            //parentView.setFitsSystemWindows(true);
        }
    }

    public void loadData() {

    }

    public abstract int getLayoutId();

    // 暂停图片请求
    public static void imagePause() {
        Fresco.getImagePipeline().pause();
    }

    // 恢复图片请求
    public static void imageResume() {
        Fresco.getImagePipeline().resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}
