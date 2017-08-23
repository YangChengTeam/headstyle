package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.interfaces.CustomWebViewDelegate;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.CustomWebView;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity implements CustomWebViewDelegate {

    @BindView(R.id.title_left_iv)
    ImageView leftIv;

    @BindView(R.id.search_key)
    EditText searchKeyEv;

    @BindView(R.id.search_btn)
    TextView searchBtn;

    @BindView(R.id.webview)
    CustomWebView customWebView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void initViews() {
        super.initViews();
    }

    @Override
    public void loadData() {
        customWebView.delegate = this;
        customWebView.loadUrl("file:///android_asset/search.html");
    }

    @OnClick(R.id.search_btn)
    public void toSearchList(View view){
        if(searchKeyEv.getText()==null ||searchKeyEv.getText().toString().length()==0){
            ToastUtils.show(SearchActivity.this,R.string.search_key_tip_text);
            return;
        }

        Intent intent = new Intent(SearchActivity.this, SearchListActivity.class);
        intent.putExtra("searchKey", searchKeyEv.getText().toString());
        startActivity(intent);
    }

    @OnClick(R.id.title_left_iv)
    public void finishActivity() {
        finish();
    }

    @Override
    public void initWithUrl(String url) {
        customWebView.loadUrl("javascript:template2();");
    }

    @Override
    public void reload() {
    }

    @Override
    public void search(String searchKey) {

        Logger.e("searchKey---"+searchKey);

        String temp = searchKey.toString();
        if(StringUtils.isEmpty(searchKey)){
            ToastUtils.show(SearchActivity.this,R.string.search_key_tip_text);
            return;
        }

        //TODO 此处代码有问题，搜索的关键字不能加载到搜索历史中，待调整
        customWebView.loadUrl("javascript:addHotWord('"+temp+"');");

        Intent intent = new Intent(SearchActivity.this, SearchListActivity.class);
        intent.putExtra("searchKey", searchKey.toString());
        startActivity(intent);
    }

    @Override
    public void showDetail(String uid, String oid, String sid) {

    }

    @Override
    public void showImage(String imageList, String position) {

    }

    @Override
    public void isNotLogin() {

    }
    @Override
    public void addArticle() {

    }
}
