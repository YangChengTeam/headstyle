package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Handler;

import com.feiyou.headstyle.R;

public class SplashActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    public void initViews() {
        super.initViews();
    }

    @Override
    public void loadData() {
        //Logger.e("mx5==="+getResources().getDisplayMetrics().density);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 1000);
    }
}
