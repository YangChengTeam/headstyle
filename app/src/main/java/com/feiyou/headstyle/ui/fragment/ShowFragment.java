package com.feiyou.headstyle.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.interfaces.CustomWebViewDelegate;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.activity.MainActivity;
import com.feiyou.headstyle.ui.activity.ShowAddActivity;
import com.feiyou.headstyle.ui.activity.ShowDetailActivity;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.CustomWebView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowFragment extends BaseFragment implements CustomWebViewDelegate, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.user_img)
    SimpleDraweeView userImg;

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.show_add_icon)
    ImageView showAddIv;

    @BindView(R.id.loading_view)
    SpinKitView loadingView;

    @BindView(R.id.webview)
    CustomWebView customWebView;

    private UserInfo userInfo;

    private String uid = "";

    private String oid = "";

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    private MaterialDialog loginDialog;

    MaterialDialog materialDialog;

    String userName;

    String gender;

    String imgUrl;

    UserService mService = null;

    OKHttpRequest okHttpRequest = null;

    private boolean isAdd = false;

    public ShowFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_show;
    }

    @Override
    public void initVars() {
        super.initVars();
        mService = new UserService();
        okHttpRequest = new OKHttpRequest();
    }

    @Override
    public void initViews() {
        super.initViews();
        swipeLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void loadData() {
        super.loadData();
        customWebView.delegate = this;
        mShareAPI = UMShareAPI.get(getActivity());
        initLoginDialog();

        /*if (AppUtils.isLogin(getActivity())) {
            userInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
            if (userInfo != null) {
                if (HeadStyleApplication.userImgPath != null) {
                    Uri uri = Uri.parse("file:///" + HeadStyleApplication.userImgPath);
                    userImg.setImageURI(uri);
                } else if (userInfo.userimg != null) {
                    Uri uri = Uri.parse(userInfo.userimg);
                    userImg.setImageURI(uri);
                } else {
                    GenericDraweeHierarchy hierarchy = userImg.getHierarchy();
                    hierarchy.setPlaceholderImage(R.mipmap.user_default_icon);
                    userImg.setHierarchy(hierarchy);
                }
            }
        } else {
            GenericDraweeHierarchy hierarchy = userImg.getHierarchy();
            hierarchy.setPlaceholderImage(R.mipmap.user_default_icon);
            userImg.setHierarchy(hierarchy);
        }*/
        updateValsByUserInfo();
        customWebView.loadUrl("http://m.qqtn.com/txapp/community/myshow.asp?a=0&uid=" + uid + "&oid=" + oid);
    }

    @Override
    public void onResume() {
        super.onResume();
        isAdd = false;
        updateValsByUserInfo();
    }

    /**
     * 初始化登录框
     */
    public void initLoginDialog() {
        //弹出QQ登录询问框
        loginDialog = DialogUtils.createDialog(getActivity(), R.string.to_login_text, R.string.cancel_login_text, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                //直接登录QQ
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(getActivity(), platform, umAuthListener);
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.show_add_icon)
    void toShowAdd(View view) {
        isAdd = true;
        if (AppUtils.isLogin(getActivity())) {
            Intent intent = new Intent(getActivity(), ShowAddActivity.class);
            startActivity(intent);
        } else {
            loginDialog.show();
        }
    }

    @OnClick(R.id.user_img)
    public void userInfo() {
        ((MainActivity) getActivity()).setCurrentTab(3);
    }

    @Override
    public void initWithUrl(String url) {
        swipeLayout.setRefreshing(false);

        if (loadingView.getVisibility() == View.VISIBLE) {
            loadingView.setVisibility(View.GONE);
        }
        if (swipeLayout.getVisibility() == View.GONE) {
            swipeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void reload() {
        customWebView.reload();
    }

    @Override
    public void search(String searchKey) {
    }

    @Override
    public void showDetail(String serverUid, String serverOid, String sid) {
        Intent intent = new Intent(getActivity(), ShowDetailActivity.class);

        String tempUid;
        String tempOid;
        if (serverUid == null || serverUid.equals("") || serverUid.equals("0")) {
            tempUid = uid;
        } else {
            tempUid = serverUid;
        }

        if (serverOid == null || serverOid.equals("") || serverOid.equals("0")) {
            tempOid = oid;
        } else {
            tempOid = serverOid;
        }


        intent.putExtra("sid", sid == null ? "1" : sid);

        startActivity(intent);
    }

    /**
     * 获取最新的用户登录信息
     */
    public void updateValsByUserInfo() {

        if (AppUtils.isLogin(getActivity())) {

            userInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
            if (userInfo != null) {
                if (!StringUtils.isEmpty(userInfo.uid)) {
                    uid = userInfo.uid;
                }
                if (!StringUtils.isEmpty(userInfo.openid)) {
                    oid = userInfo.openid;
                }

                if (HeadStyleApplication.userImgPath != null) {
                    Uri uri = Uri.parse("file:///" + HeadStyleApplication.userImgPath);
                    userImg.setImageURI(uri);
                } else if (userInfo.userimg != null) {
                    Uri uri = Uri.parse(userInfo.userimg);
                    userImg.setImageURI(uri);
                } else {
                    Uri uri = Uri.parse("res://mipmap/"+R.mipmap.user_default_icon);
                    userImg.setImageURI(uri);
                }
            } else {
                uid = "";
                oid = "";
            }
        } else {
            Uri uri = Uri.parse("res://mipmap/"+R.mipmap.user_default_icon);
            userImg.setImageURI(uri);

            uid = "";
            oid = "";
        }
    }

    @Override
    public void showImage(String imageList, String position) {

    }

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //Toast.makeText(getActivity(), "Authorize succeed", Toast.LENGTH_SHORT).show();

            mShareAPI.getPlatformInfo(getActivity(), platform, umAuthUserInfoListener);

            materialDialog = new MaterialDialog.Builder(getActivity())
                    .content("获取用户信息，请稍后...")
                    .progress(true, 0)
                    .backgroundColorRes(R.color.white)
                    .contentColorRes(R.color.dialog_content_color).build();
            materialDialog.show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getActivity(), "登录授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getActivity(), "授权取消", Toast.LENGTH_SHORT).show();
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

                            UserInfo userInfo = mService.login(response);
                            if (userInfo != null) {
                                Logger.e("我秀-登录成功");

                                //根据头像的地址下载用户头像到本地
                                if (!StringUtils.isEmpty(imgUrl)) {
                                    String fileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) + ".jpg";
                                    OkHttpUtils.get().url(imgUrl).build().execute(new FileCallBack(Constant.BASE_NORMAL_IMAGE_DIR, fileName)//
                                    {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {

                                        }

                                        @Override
                                        public void onResponse(File file, int id) {
                                            String imagePath = file.getAbsolutePath();
                                            HeadStyleApplication.userImgPath = imagePath;
                                        }
                                    });
                                }

                                PreferencesUtils.putObject(getActivity(), Constant.USER_INFO, userInfo);
                                HeadStyleApplication.isLoginAuth = true;
                                if (isAdd) {
                                    Intent intent = new Intent(getActivity(), ShowAddActivity.class);
                                    startActivity(intent);
                                } else {
                                    onResume();
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(getActivity(), R.string.get_user_info_fail);
                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }
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
            ToastUtils.show(getActivity(), R.string.get_user_info_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show(getActivity(), R.string.get_user_info_cancel);
        }
    };

    @Override
    public void isNotLogin() {
        loginDialog.show();
    }

    @Override
    public void onRefresh() {
        updateValsByUserInfo();
        customWebView.loadUrl("http://m.qqtn.com/txapp/community/myshow.asp?a=0&uid=" + uid + "&oid=" + oid);
    }

    @Override
    public void addArticle() {

    }
}
