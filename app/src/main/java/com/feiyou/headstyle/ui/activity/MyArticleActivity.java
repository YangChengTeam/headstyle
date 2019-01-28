package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.ArticleNewListAdapter;
import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.ArticleListRet;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;

public class MyArticleActivity extends BaseActivity implements ArticleNewListAdapter.LoginShowListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.my_article_text)
    String titleTextValue;

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.article_recyclerview_list)
    RecyclerView mRecyclerView;

    private ArticleNewListAdapter mAdapter;

    private List<ArticleInfo> articleInfoList;

    private int pageNum = 1;

    OKHttpRequest okHttpRequest = null;

    ArticleService articleService = null;

    private MaterialDialog loginDialog;

    MaterialDialog materialDialog;

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    String userName;

    String gender;

    String imgUrl;

    UserService mService = null;

    private UserInfo userInfo;

    public String showType = "0";

    public int maxPage;

    public int pageSize = 10;

    @Override
    public int getLayoutId() {
        return R.layout.activity_article;
    }

    @Override
    public void initVars() {
        super.initVars();
        articleInfoList = new ArrayList<ArticleInfo>();
    }

    @Override
    public void initViews() {
        super.initViews();
        titleTv.setText(titleTextValue);
        swipeLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);
        swipeLayout.setOnRefreshListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mService = new UserService();
        articleService = new ArticleService();
        okHttpRequest = new OKHttpRequest();

        mAdapter = new ArticleNewListAdapter(this, articleInfoList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setShowListener(this);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent(MyArticleActivity.this, ArticleDetailActivity.class);
                intent.putExtra("show_type", 0);
                intent.putExtra("sid", articleInfoList.get(position).sid);
                startActivity(intent);
            }
        });

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                pageNum++;
                getNextData();
            }
        },mRecyclerView);

        mShareAPI = UMShareAPI.get(this);
        initLoginDialog();

        if(App.isMessage){
            App.isMessage = false;
        }
    }

    /**
     * 初始化登录框
     */
    public void initLoginDialog() {
        //弹出QQ登录询问框
        loginDialog = DialogUtils.createDialog(this, R.string.to_login_text, R.string.cancel_login_text, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                //直接登录QQ
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(MyArticleActivity.this, platform, umAuthListener);
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    public Map<String, String> setParams() {
        Map<String, String> params = new HashMap<String, String>();
        Logger.e("init page---" + pageNum);
        params.put("p", String.valueOf(pageNum));
        params.put("t", showType);
        params.put("num", "10");//每页条数
        return params;
    }

    @Override
    public void loadData() {
        super.loadData();

        final Map<String, String> params = setParams();

        if (AppUtils.isLogin(this)) {

            userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);

            if (userInfo != null) {
                params.put("uid", userInfo.uid);
            }

            okHttpRequest.aget(Server.MY_ARTICLE_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    swipeLayout.setRefreshing(false);
                    //设置首页列表数据

                    ArticleListRet articleListRet = articleService.getData(response);
                    if (articleListRet != null) {
                        maxPage = articleListRet.maxpage;
                        List<ArticleInfo> temp = articleListRet.data;
                        if (temp != null && temp.size() > 0) {
                            if (articleInfoList != null && articleInfoList.size() > 0) {
                                articleInfoList.clear();
                            }
                            //articleInfoList.addAll(temp);
                            mAdapter.addNewDatas(temp);
                            mAdapter.notifyDataSetChanged();
                        }
                        if(temp.size() <= 10){
                            mAdapter.loadMoreComplete();
                            mAdapter.loadMoreEnd();
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
        } else {
            loginDialog.show();
        }
    }

    public void getNextData() {
        final Map<String, String> params = setParams();

        if (userInfo != null) {
            params.put("uid", userInfo.uid);
        }

        if (pageNum <= maxPage) {
            okHttpRequest.aget(Server.MY_ARTICLE_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    //设置首页列表数据
                    List<ArticleInfo> temp = articleService.getData(response).data;
                    if (temp != null && temp.size() > 0) {
                        if(temp.size() == pageSize){
                            mAdapter.loadMoreComplete();
                        }
                        if(temp.size() < pageSize){
                            mAdapter.loadMoreEnd();
                        }
                        mAdapter.addNewDatas(temp);

                        mAdapter.notifyDataSetChanged();
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
     * 判断是否滚动到底部
     *
     * @param recyclerView
     * @return
     */
    public static boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //Toast.makeText(this, "Authorize succeed", Toast.LENGTH_SHORT).show();

            ToastUtils.show(MyArticleActivity.this, "授权成功");

            mShareAPI.getPlatformInfo(MyArticleActivity.this, platform, umAuthUserInfoListener);

            materialDialog = new MaterialDialog.Builder(MyArticleActivity.this)
                    .content("获取用户信息，请稍后...")
                    .progress(true, 0)
                    .backgroundColorRes(R.color.white)
                    .contentColorRes(R.color.dialog_content_color).build();
            materialDialog.show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(MyArticleActivity.this, "登录授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(MyArticleActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthUserInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            Log.d("user info", "user info:" + data.get("screen_name").toString());//用户名
            Log.d("user info", "user info:" + data.get("gender").toString());//性别
            Log.d("user info", "user info:" + data.get("profile_image_url").toString());//头像

            userName = data.get("screen_name");
            gender = data.get("gender");

            if (!StringUtils.isEmpty(gender)) {
                if (gender.equals("男")) {
                    gender = "1";
                } else {
                    gender = "2";
                }
            } else {
                gender = "1";
            }

            imgUrl = data.get("profile_image_url");

            if (!StringUtils.isEmpty(userName)) {

                if (!StringUtils.isEmpty(data.get("openid"))) {

                    final Map<String, String> params = new HashMap<String, String>();
                    params.put("openid", data.get("openid"));
                    params.put("gender", gender);
                    params.put("userage", "0");
                    params.put("nickname", userName);
                    params.put("userimg", imgUrl != null ? imgUrl : "null");

                    okHttpRequest.aget(Server.LOGIN_DATA, params, new OnResponseListener() {
                        @Override
                        public void onSuccess(String response) {

                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }

                            UserInfo tempUserInfo = mService.login(response);
                            if (tempUserInfo != null) {

                                ToastUtils.show(MyArticleActivity.this, "登录成功");

                                PreferencesUtils.putObject(MyArticleActivity.this, Constant.USER_INFO, tempUserInfo);
                                App.isLoginAuth = true;

                                pageNum = 1;
                                loadData();
                                App.connect(tempUserInfo.getUsertoken());

                                Uri uri = null;
                                if (!StringUtils.isBlank(tempUserInfo.getUserimg())) {
                                    uri = Uri.parse(tempUserInfo.getUserimg());
                                }
                                io.rong.imlib.model.UserInfo ryUser = new io.rong.imlib.model.UserInfo(tempUserInfo.getUid(), tempUserInfo.getNickName(), uri);
                                RongIM.getInstance().setCurrentUserInfo(ryUser);

                                RongIM.getInstance().refreshUserInfoCache(ryUser);
                                RxBus.get().post(Constant.LOGIN_SUCCESS,"loginSuccess");
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(MyArticleActivity.this, R.string.get_user_info_fail);
                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }
                            ToastUtils.show(MyArticleActivity.this, "登录失败，请稍后重试");
                        }

                        @Override
                        public void onBefore() {

                        }
                    });
                }
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ToastUtils.show(MyArticleActivity.this, R.string.get_user_info_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show(MyArticleActivity.this, R.string.get_user_info_cancel);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loginShow(String sid, int iszan) {
        loginDialog.show();
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        loadData();
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }
}
