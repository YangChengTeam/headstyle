package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.HeadWallAdapter;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.PreferencesUtils;
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

public class KeepListActivity extends BaseActivity{

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.my_keep_text)
    String titleTextValue;

    @BindString(R.string.not_login_text)
    String notLoginTextValue;

    @BindString(R.string.no_more_keep_text)
    String noMoreValue;

    @BindView(R.id.photo_wall)
    GridView mPhotoWall;

    @BindView(R.id.no_more_text)
    TextView noMoreTv;

    private HeadWallAdapter mAdapter;

    private int mImageThumbSize;

    private int mImageThumbSpacing;

    public List<HeadInfo> data;

    private int pageNum = 1;

    private List<HeadInfo> nextData;

    UserService mService = null;

    OKHttpRequest okHttpRequest = null;

    private UserInfo userInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_keep_list;
    }

    @Override
    public void initVars() {
        mService = new UserService();
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

        titleTv.setText(titleTextValue);

        nextData = new ArrayList<HeadInfo>();
        mAdapter = new HeadWallAdapter(this, data);
        mPhotoWall.setAdapter(mAdapter);

        mPhotoWall.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch(scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        imageResume();
                        // 判断是否滚动到底部
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            //ToastUtils.show(getActivity(),"滚动到了底部");
                            getDataByPage();//加载下一页数据
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
        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        if (userInfo != null) {
            getDataByPage();
        } else {
            noMoreTv.setText(notLoginTextValue);
            noMoreTv.setVisibility(View.VISIBLE);
        }
    }

    public void getDataByPage(){
        if (nextData != null && nextData.size() > 0) {
            data.addAll(nextData);
            mAdapter.addNewDatas(nextData);
            mAdapter.notifyDataSetChanged();
            pageNum++;

            getNextData();
        } else {
            final Map<String, String> params = new HashMap<String, String>();
            Logger.e("page---" + pageNum);
            params.put("p", String.valueOf(pageNum));

            if (!StringUtils.isEmpty(userInfo.openid)) {
                params.put("openid", userInfo.openid);
            }

            if (!StringUtils.isEmpty(userInfo.uid)) {
                params.put("uid", userInfo.uid);
            }
            okHttpRequest.aget(Server.KEEP_LIST_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    //设置首页列表数据
                    List<HeadInfo> temp = mService.getKeepHeadInfos(response);

                    if (temp != null && temp.size() > 0) {
                        data.addAll(temp);
                        mAdapter.addNewDatas(temp);
                        mAdapter.notifyDataSetChanged();

                        pageNum++;
                        getNextData();
                    } else {
                        noMoreTv.setText(noMoreValue);
                        noMoreTv.setVisibility(View.VISIBLE);
                    }
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

    /**
     * 加载下一页数据
     */
    public void getNextData() {
        if (userInfo != null) {
            final Map<String, String> params = new HashMap<String, String>();
            Logger.e("page---" + pageNum);
            params.put("p", String.valueOf(pageNum));
            if (!StringUtils.isEmpty(userInfo.openid)) {
                params.put("openid", userInfo.openid);
            }

            if (!StringUtils.isEmpty(userInfo.uid)) {
                params.put("uid", userInfo.uid);
            }
            okHttpRequest.aget(Server.HOME_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    //设置首页列表数据
                    if (nextData != null) {
                        nextData.clear();
                    }
                    nextData = mService.getKeepHeadInfos(response);
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
        Intent intent = new Intent(KeepListActivity.this, HeadShowActivity.class);

        if (data != null && data.size() > 0) {
            intent.putExtra("cid", data.get(position).getCid());
            intent.putExtra("imageUrl", data.get(position).getHurl());
        }

        startActivity(intent);
    }

    @OnClick(R.id.back_image)
    public void finishActivity(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
