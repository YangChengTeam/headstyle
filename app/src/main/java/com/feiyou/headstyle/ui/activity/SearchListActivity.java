package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.HeadWallAdapter;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.HomeService;
import com.feiyou.headstyle.util.StringUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class SearchListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.search_result_text)
    String titleTextValue;

    @BindView(R.id.photo_wall)
    GridView mPhotoWall;

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.no_more_text)
    TextView noMoreTv;

    private HeadWallAdapter mAdapter;

    private int mImageThumbSize;

    private int mImageThumbSpacing;

    public List<HeadInfo> data;

    private int pageNum = 1;

    HomeService mService = null;

    OKHttpRequest okHttpRequest = null;

    private String searchKey;

    private int maxPage = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_list;
    }

    public void initVars() {
        mService = new HomeService();
        okHttpRequest = new OKHttpRequest();
        data = new ArrayList<HeadInfo>();

        mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_spacing);
    }

    public void initViews() {
        super.initViews();

        titleTv.setText(titleTextValue);

        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && !StringUtils.isEmpty(bundle.getString("searchKey"))) {
            searchKey = bundle.getString("searchKey");
        }

        mAdapter = new HeadWallAdapter(this, data);
        mPhotoWall.setAdapter(mAdapter);

        swipeLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);
        swipeLayout.setOnRefreshListener(this);


        mPhotoWall.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        imageResume();
                        // 判断是否滚动到底部
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            //ToastUtils.show(getActivity(),"滚动到了底部");
                            loadData();//加载下一页数据
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滚动状态
                        imagePause();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @OnItemClick(R.id.photo_wall)
    public void onHeadItemClick(int position) {
        Intent intent = new Intent(this, HeadShowActivity.class);

        if (data != null && data.size() > 0) {
            intent.putExtra("cid", data.get(position).getCid());
            intent.putExtra("imageUrl", data.get(position).getHurl());
        }

        startActivity(intent);
    }

    @Override
    public void loadData() {

        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });

        final Map<String, String> params = new HashMap<String, String>();
        Logger.e("page---" + pageNum + "--maxpage---" + maxPage);
        params.put("p", String.valueOf(pageNum));

        if (!StringUtils.isEmpty(searchKey)) {
            params.put("keyword", searchKey);
        }

        if (pageNum <= maxPage || maxPage == 0) {
            okHttpRequest.aget(Server.SEARCH_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    swipeLayout.setRefreshing(false);
                    //设置首页列表数据
                    List<HeadInfo> temp = mService.getHeadInfos(response);

                    int maxPageValue = mService.getMaxPage(response);
                    if (maxPageValue > 0) {
                        maxPage = maxPageValue;
                    }

                    if (temp != null && temp.size() > 0) {
                        data.addAll(temp);
                        mAdapter.addNewDatas(temp);

                        mAdapter.notifyDataSetChanged();

                        pageNum++;
                    } else {
                        swipeLayout.setVisibility(View.GONE);
                        noMoreTv.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onRefresh() {
        if (data != null) {
            data.clear();
        }
        mAdapter.clear();
        pageNum = 1;
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
}
