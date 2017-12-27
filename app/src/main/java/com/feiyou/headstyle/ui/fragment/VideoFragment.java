package com.feiyou.headstyle.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.VideoAdapter;
import com.feiyou.headstyle.bean.VideoInfoRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;

import butterknife.BindView;

/**
 * Created by admin on 2017/12/22.
 */

public class VideoFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.loading_layout)
    LinearLayout mLoadingLayout;

    @BindView(R.id.loading_iv)
    ImageView mLoadImageView;

    @BindView(R.id.iv_no_date)
    ImageView mNoDataImageView;

    @BindView(R.id.video_list)
    RecyclerView mVideoRecyclerView;

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    OKHttpRequest okHttpRequest = null;

    VideoAdapter videoAdapter;

    private int requestNum;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    public void initVars() {
        super.initVars();
    }

    @Override
    public void initViews() {
        super.initViews();
        okHttpRequest = new OKHttpRequest();
        Glide.with(getActivity()).load(R.drawable.main_loading_gif).into(mLoadImageView);
        swipeLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                R.color.refresh_select_color);
        swipeLayout.setOnRefreshListener(this);
        videoAdapter = new VideoAdapter(getActivity(), null);
        mVideoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mVideoRecyclerView.setAdapter(videoAdapter);

        videoAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mVideoRecyclerView);
    }

    @Override
    public void loadData() {
        super.loadData();
        requestNum++;
        okHttpRequest.aget(Server.VIDEO_LIST_DATA, null, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                swipeLayout.setRefreshing(false);
                //设置首页列表数据
                VideoInfoRet videoInfoRet = Constant.GSON.fromJson(response, VideoInfoRet.class);
                if (videoInfoRet != null && videoInfoRet.code == 1) {
                    mLoadingLayout.setVisibility(View.GONE);
                    if (videoInfoRet.data != null && videoInfoRet.data.lists != null && videoInfoRet.data.lists.size() > 0) {
                        if (requestNum == 1) {
                            videoAdapter.setNewData(videoInfoRet.data.lists);
                        } else {
                            videoAdapter.addData(videoInfoRet.data.lists);
                        }
                        if (videoInfoRet.data.lists.size() == 10) {
                            videoAdapter.loadMoreComplete();
                        } else {
                            videoAdapter.loadMoreEnd();
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

    @Override
    public void onRefresh() {
        requestNum = 0;
        loadData();
    }
}
