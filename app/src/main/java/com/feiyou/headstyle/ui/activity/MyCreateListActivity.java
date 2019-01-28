package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.MyCreateAdapter;
import com.feiyou.headstyle.bean.MyCreateInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.db.greendao.DaoSession;
import com.feiyou.headstyle.db.greendao.MyCreateInfoDao;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.service.HomeService;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DbUtil;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MyCreateListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.my_create_list_layout)
    LinearLayout myCreateListLayout;

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindView(R.id.photo_wall)
    GridView mPhotoWall;

    private MyCreateAdapter mAdapter;

    private int mImageThumbSize;

    private int mImageThumbSpacing;

    public List<MyCreateInfo> data;

    HomeService mService = null;

    OKHttpRequest okHttpRequest = null;

    private UserInfo userInfo;

    @Override
    public int getLayoutId() {
        return R.layout.my_create_list;
    }

    @Override
    public void initVars() {

        mService = new HomeService();
        okHttpRequest = new OKHttpRequest();
        data = new ArrayList<MyCreateInfo>();

        mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_spacing);
    }

    @Override
    public void initViews() {
        super.initViews();
        titleTv.setText("我的制作");
        mAdapter = new MyCreateAdapter(this, data);
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
                            //loadData();//加载下一页数据
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
        if (AppUtils.isLogin(this)) {
            userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
            if (userInfo != null) {
                DaoSession daoSession = DbUtil.getSession(MyCreateListActivity.this);

                List<MyCreateInfo> temp = daoSession.getMyCreateInfoDao().queryBuilder().where(MyCreateInfoDao.Properties.Uid.eq(userInfo.uid)).list();

                if (temp != null && temp.size() > 0) {
                    data.addAll(temp);
                    mAdapter.addNewDatas(temp);
                    mAdapter.notifyDataSetChanged();
                }else{
                    ToastUtils.show(this,"暂无制作信息");
                }
            }
        }else{
            ToastUtils.show(this,"请登录后查看");
        }
    }

    @OnItemClick(R.id.photo_wall)
    public void onHeadItemClick(int position) {
        Intent intent = new Intent(MyCreateListActivity.this, HeadCreateShowActivity.class);

        if (data != null && data.size() > 0) {
            intent.putExtra("isCreateQQImage", true);
            intent.putExtra("imagePath",data.get(position).getPhoto_id());
            intent.putExtra("from_createlist",true);
        }

        startActivity(intent);
    }


    @Override
    public void onRefresh() {
        if (data != null) {
            data.clear();
        }
        mAdapter.clear();
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
