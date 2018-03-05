package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.ImageItemAdapter;
import com.feiyou.headstyle.bean.SpecialInfoData;
import com.feiyou.headstyle.bean.SpecialListRet;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.SpecialService;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.view.DividerItemDecoration;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class SpecialListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindView(R.id.special_list)
    RecyclerView mSpecialRecyclerView;

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    private ImageView specialImageView;

    private TextView specialTitleTextView;

    private TextView specialDesTextView;

    private TextView specialCountTextView;

    private ImageItemAdapter mAdapter;

    private int pageNum = 1;

    SpecialService mService = null;

    OKHttpRequest okHttpRequest = null;

    private int sid;

    private int maxPage = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_special_list;
    }

    @Override
    public void initVars() {
        mService = new SpecialService();
        okHttpRequest = new OKHttpRequest();
    }

    private View getHeaderView() {
        View headView = getLayoutInflater().inflate(R.layout.special_head_view, (ViewGroup) mSpecialRecyclerView.getParent(), false);

        specialImageView = (ImageView) headView.findViewById(R.id.iv_special);

        specialTitleTextView = (TextView) headView.findViewById(R.id.tv_special_title);
        specialDesTextView = (TextView) headView.findViewById(R.id.tv_special_des);
        specialCountTextView = (TextView) headView.findViewById(R.id.tv_special_count);
        return headView;
    }

    @Override
    public void initViews() {
        super.initViews();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.getInt("sid") > 0) {
            sid = bundle.getInt("sid");
        }

        Logger.e("cid---" + sid);

        if (bundle != null && !StringUtils.isEmpty(bundle.getString("titleName"))) {
            titleTv.setText(bundle.getString("titleName"));
        }

        swipeLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);
        swipeLayout.setOnRefreshListener(this);
        mAdapter = new ImageItemAdapter(this, null);
        //添加分割线
        mSpecialRecyclerView.addItemDecoration(new DividerItemDecoration(
                this, LinearLayoutManager.HORIZONTAL, R.drawable.divider_item));
        mSpecialRecyclerView.addItemDecoration(new DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL, R.drawable.divider_item));
        mSpecialRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter.setHeaderView(getHeaderView());
        mSpecialRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mAdapter.getData() != null) {
                    Intent intent = new Intent(SpecialListActivity.this, HeadShowActivity.class);
                    intent.putExtra("cid", mAdapter.getData().get(position).getCid());
                    intent.putExtra("imageUrl", mAdapter.getData().get(position).getHurl());
                    intent.putExtra("gaoqing", mAdapter.getData().get(position).getGaoqing());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void loadData() {

        final Map<String, String> params = new HashMap<String, String>();
        params.put("p", String.valueOf(pageNum));
        params.put("num", "20");
        if (sid > 0) {
            params.put("sid", sid + "");
        }

        okHttpRequest.aget(Server.SPECIAL_LIST_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                swipeLayout.setRefreshing(false);
                //设置首页列表数据

                SpecialInfoData specialInfoData = mService.getData(response);
                if (specialInfoData != null && specialInfoData.data != null && specialInfoData.getData().size() > 0) {

                    SpecialListRet ret = specialInfoData.data.get(0);

                    specialTitleTextView.setText(ret.title);
                    specialDesTextView.setText(ret.description);
                    specialCountTextView.setText(ret.total + "张");
                    if (ret.list != null) {
                        if (Util.isOnMainThread()) {
                            if (AppUtils.isValidContext(SpecialListActivity.this)) {
                                Glide.with(SpecialListActivity.this).load(ret.list.get(0).getHurl()).into(specialImageView);
                            }
                        }
                        mAdapter.setNewData(ret.list);
                    }

                }
            }

            @Override
            public void onError(Exception e) {
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onBefore() {

            }
        });
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        maxPage = 0;
        loadData();
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.isOnMainThread()) {
            Glide.with(this).pauseRequests();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
