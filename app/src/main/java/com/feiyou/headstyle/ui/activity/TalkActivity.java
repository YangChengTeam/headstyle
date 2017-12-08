package com.feiyou.headstyle.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.common.Constant;
import com.hwangjr.rxbus.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/11/30.
 */

public class TalkActivity extends FragmentActivity {

    @BindView(R.id.title_text)
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        RxBus.get().register(this);
        ButterKnife.bind(this);
        titleTv.setText("我的消息");
        App.isLetter = false;
        RxBus.get().post(Constant.MESSAGE, "message");
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }
}
