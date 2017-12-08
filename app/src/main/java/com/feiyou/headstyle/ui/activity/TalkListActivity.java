package com.feiyou.headstyle.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.feiyou.headstyle.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/11/30.
 */

public class TalkListActivity extends FragmentActivity {

    @BindView(R.id.title_text)
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_list);
        ButterKnife.bind(this);
        titleTv.setText("消息列表");
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }
}
