package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.HeadWallAdapter;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.HeadListRet;
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

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class HeadListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindView(R.id.photo_wall)
    GridView mPhotoWall;

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    private HeadWallAdapter mAdapter;

    private int mImageThumbSize;

    private int mImageThumbSpacing;

    public List<HeadInfo> data;

    private int pageNum = 1;

    private List<HeadInfo> nextData;

    HomeService mService = null;

    OKHttpRequest okHttpRequest = null;

    private int cid;

    private int maxPage = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_type_list;
    }

    @Override
    public void initVars() {

        mService = new HomeService();
        okHttpRequest = new OKHttpRequest();
        data = new ArrayList<HeadInfo>();

        mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_spacing);
    }

    @Override
    public void initViews() {
        super.initViews();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.getInt("cid") > 0) {
            cid = bundle.getInt("cid");
        }

        Logger.e("cid---" + cid);

        if (bundle != null && !StringUtils.isEmpty(bundle.getString("titleName"))) {
            titleTv.setText(bundle.getString("titleName"));
        }

        swipeLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);
        swipeLayout.setOnRefreshListener(this);

        nextData = new ArrayList<HeadInfo>();
        mAdapter = new HeadWallAdapter(this, data);
        mPhotoWall.setAdapter(mAdapter);

        mPhotoWall.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        imageResume();

                        // 判断是否滚动到底部
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            //ToastUtils.show(getActivity(),"滚动到了底部");

                            if (pageNum <= maxPage) {
                                loadData();//加载下一页数据
                            }
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

    @Override
    public void loadData() {

        if (nextData != null && nextData.size() > 0) {
            data.addAll(nextData);
            mAdapter.addNewDatas(nextData);
            mAdapter.notifyDataSetChanged();
            pageNum++;
            getNextData();
        } else {
            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(true);
                }
            });

            final Map<String, String> params = new HashMap<String, String>();
            Logger.e("init page---" + pageNum);
            params.put("p", String.valueOf(pageNum));
            if (cid > 0) {
                params.put("cid", cid + "");
            }

            okHttpRequest.aget(Server.HOME_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    swipeLayout.setRefreshing(false);
                    //设置首页列表数据

                    HeadListRet headListRet = mService.getData(response);
                    if (headListRet != null) {
                        maxPage = headListRet.maxpage;
                        Logger.e("max-page---" + maxPage);
                        List<HeadInfo> temp = headListRet.data;
                        if (temp != null && temp.size() > 0) {
                            data.addAll(temp);
                            mAdapter.addNewDatas(temp);

                            mAdapter.notifyDataSetChanged();

                            pageNum++;
                            if (pageNum <= maxPage) {
                                getNextData();
                            }
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
    }

    /**
     * 加载下一页数据
     */
    public void getNextData() {
        final Map<String, String> params = new HashMap<String, String>();
        Logger.e("next-page---" + pageNum);
        params.put("p", String.valueOf(pageNum));
        if (cid > 0) {
            params.put("cid", cid + "");
        }

        if (pageNum <= maxPage) {

            okHttpRequest.aget(Server.HOME_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    //设置首页列表数据
                    if (nextData != null) {
                        nextData.clear();
                    }
                    nextData = mService.getHeadInfos(response);
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onBefore() {

                }
            });
        }
    }

    @OnItemClick(R.id.photo_wall)
    public void onHeadItemClick(int position) {
        Intent intent = new Intent(HeadListActivity.this, HeadShowActivity.class);

        if (data != null && data.size() > 0) {
            intent.putExtra("cid", data.get(position).cid);
            intent.putExtra("imageUrl", data.get(position).hurl);
        }

        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (nextData != null) {
            nextData.clear();
        }
        if (data != null) {
            data.clear();
        }
        mAdapter.clear();
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
}
