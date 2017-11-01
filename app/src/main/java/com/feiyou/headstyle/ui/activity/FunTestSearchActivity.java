package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by admin on 2017/9/22.
 */

public class FunTestSearchActivity extends BaseActivity {

    @BindView(R.id.title_left_iv)
    ImageView leftIv;

    @BindView(R.id.search_key)
    EditText searchKeyEv;

    @BindView(R.id.search_btn)
    TextView searchBtn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fun_test_search;
    }

    @Override
    protected void initVars() {
        super.initVars();
    }

    @Override
    public void initViews() {
        super.initViews();

        RxView.clicks(leftIv).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });

        RxView.clicks(searchBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (StringUtils.isEmpty(searchKeyEv.getText())) {
                    ToastUtils.show(FunTestSearchActivity.this, R.string.search_key_tip_text);
                    return;
                }

                Intent intent = new Intent(FunTestSearchActivity.this, FunTestSearchResultActivity.class);
                intent.putExtra("key_word", searchKeyEv.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void loadData() {
        super.loadData();
    }
}
