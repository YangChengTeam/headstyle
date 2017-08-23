package com.feiyou.headstyle.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.ArticleListRet;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.bean.VersionInfo;
import com.feiyou.headstyle.bean.VersionRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.net.service.UpdateService;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.activity.AboutActivity;
import com.feiyou.headstyle.ui.activity.KeepListActivity;
import com.feiyou.headstyle.ui.activity.MyArticleActivity;
import com.feiyou.headstyle.ui.activity.MyCreateListActivity;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.NetWorkUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends BaseFragment {

    @BindView(R.id.qq_login_layout)
    RelativeLayout logonLayout;

    @BindView(R.id.qq_login_icon)
    SimpleDraweeView userImg;

    @BindView(R.id.login_tip_tv)
    TextView loginTipTv;

    @BindView(R.id.user_info_layout)
    RelativeLayout usreInfoLayout;

    @BindView(R.id.user_name_tv)
    TextView userNameTv;

    @BindView(R.id.user_gender_icon)
    ImageView userGenderImg;

    @BindView(R.id.login_out_btn)
    Button loginOutBtn;

    @BindView(R.id.message_red_point)
    ImageView messageRedImage;

    UserService mService = null;

    ArticleService articleService = null;

    OKHttpRequest okHttpRequest = null;

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    String userName;

    String gender;

    String imgUrl;

    // ProgressDialog dialogLogin;

    MaterialDialog materialDialog;

    MaterialDialog checkDialog;

    MaterialDialog updateDialog;

    private String downUrl;

    private UserInfo loginUserInfo;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //设置"我的发帖"未读消息提醒
                    if (HeadStyleApplication.isMessage) {
                        messageRedImage.setVisibility(View.VISIBLE);
                    } else {
                        messageRedImage.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initVars() {
        super.initVars();
        mService = new UserService();
        articleService = new ArticleService();
        okHttpRequest = new OKHttpRequest();
    }

    @Override
    public void initViews() {
        super.initViews();
    }

    @Override
    public void loadData() {
        super.loadData();
        mShareAPI = UMShareAPI.get(getActivity());

        /*if (AppUtils.isLogin(getActivity())) {
            loginUserInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
            if (loginUserInfo != null) {

                loginTipTv.setVisibility(View.GONE);
                usreInfoLayout.setVisibility(View.VISIBLE);
                userNameTv.setText(loginUserInfo.nickName);

                gender = loginUserInfo.sex;
                imgUrl = loginUserInfo.userimg;

                //设置性别
                if (gender.equals("1")) {
                    userGenderImg.setImageResource(R.mipmap.boy_icon);
                } else {
                    userGenderImg.setImageResource(R.mipmap.girl_icon);
                }

                //设置QQ头像
                if (!StringUtils.isEmpty(imgUrl)) {
                    Uri uri = Uri.parse(imgUrl);
                    userImg.setImageURI(uri);
                }
            }
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppUtils.isLogin(getActivity())) {
            loginUserInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
            if (loginUserInfo != null) {

                loginTipTv.setVisibility(View.GONE);
                usreInfoLayout.setVisibility(View.VISIBLE);
                userNameTv.setText(loginUserInfo.nickName);

                gender = loginUserInfo.sex;
                imgUrl = loginUserInfo.userimg;

                //设置性别
                if (gender.equals("1")) {
                    userGenderImg.setImageResource(R.mipmap.boy_icon);
                } else {
                    userGenderImg.setImageResource(R.mipmap.girl_icon);
                }

                //设置QQ头像
                if (HeadStyleApplication.userImgPath != null) {
                    Uri uri = Uri.parse("file:///" + HeadStyleApplication.userImgPath);
                    userImg.setImageURI(uri);
                } else if (imgUrl != null) {
                    Uri uri = Uri.parse(imgUrl);
                    userImg.setImageURI(uri);
                } else {
                    Uri uri = Uri.parse("res://mipmap/" + R.mipmap.user_default_icon);
                    userImg.setImageURI(uri);
                }

                initCommentCount();
            }
        }

        //messageRedImage.setVisibility(View.VISIBLE);
    }

    /**
     * 获取我的发帖的评论数
     */
    public void initCommentCount() {
        loginUserInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
        if (loginUserInfo != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("uid", loginUserInfo.uid);
            params.put("num", "10000");//设置一个分页每页的条数，目的是查出所有记录
            okHttpRequest.aget(Server.MY_ARTICLE_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {

                    ArticleListRet articleListRet = articleService.getData(response);
                    int commentCount = 0;
                    if (articleListRet != null) {
                        List<ArticleInfo> temp = articleListRet.data;
                        if (temp != null) {
                            for (int i = 0; i < temp.size(); i++) {
                                commentCount += temp.get(i).comment;
                            }
                        }
                    }

                    int lastComment = PreferencesUtils.getInt(getActivity(), loginUserInfo.uid);

                    //ToastUtils.show(getActivity(),"个人页--->"+lastComment +"-->"+commentCount);

                    PreferencesUtils.putInt(getActivity(), loginUserInfo.uid, commentCount);

                    if (commentCount > 0 && commentCount > lastComment) {
                        if(lastComment > -1){
                            HeadStyleApplication.isMessage = true;
                            Message mes = new Message();
                            mes.what = 0;
                            handler.sendMessage(mes);
                        }
                    }else{
                        Message mes = new Message();
                        mes.what = 0;
                        handler.sendMessage(mes);
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

    @OnClick(R.id.qq_login_layout)
    void toLogin() {
        loginUserInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
        if (loginUserInfo == null) {
            platform = SHARE_MEDIA.QQ;
            mShareAPI.doOauthVerify(getActivity(), platform, umAuthListener);
        }
    }

    @OnClick(R.id.login_out_btn)
    void loginOut(View view) {
        loginUserInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
        if (loginUserInfo != null) {
            loginTipTv.setVisibility(View.VISIBLE);
            usreInfoLayout.setVisibility(View.GONE);
            userImg.setImageResource(R.mipmap.qq_login_select_icon);
            PreferencesUtils.putObject(getActivity(), Constant.USER_INFO, null);
            HeadStyleApplication.isLoginAuth = false;
        }
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
                                //ToastUtils.show(getActivity(),R.string.get_user_info_success);
                                Logger.e("登录成功");

                                loginTipTv.setVisibility(View.GONE);
                                usreInfoLayout.setVisibility(View.VISIBLE);
                                userNameTv.setText(userName);

                                //设置性别
                                if (gender.equals("1")) {
                                    userGenderImg.setImageResource(R.mipmap.boy_icon);
                                } else {
                                    userGenderImg.setImageResource(R.mipmap.girl_icon);
                                }

                                //设置QQ头像
                                if (!StringUtils.isEmpty(imgUrl)) {

                                    Uri uriShow = Uri.parse(imgUrl);
                                    userImg.setImageURI(uriShow);

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

                                //刷新消息评论
                                initCommentCount();
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

    @OnClick(R.id.my_keep_layout)
    public void toKeepList(View view) {
        Intent intent = new Intent(getActivity(), KeepListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.my_article_layout)
    public void toArticleList(View view) {
        Intent intent = new Intent(getActivity(), MyArticleActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.my_create_layout)
    public void toCreateList(View view) {
        Intent intent = new Intent(getActivity(), MyCreateListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.clear_layout)
    public void clear() {
        clearCacheFolder(new File(Constant.BASE_SD_DIR), System.currentTimeMillis());
        ToastUtils.show(getActivity(), "缓存已清除");
    }

    @OnClick(R.id.about_layout)
    public void toAbout(View view) {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }

    public int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }
                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    @OnClick(R.id.update_layout)
    public void updateVersion(View view) {

        if (!AppUtils.isServiceWork(getActivity(), "com.feiyou.headstyle.net.service.UpdateService")) {
            checkDialog = new MaterialDialog.Builder(getActivity())
                    .content("正在检测新版本")
                    .progress(true, 0)
                    .backgroundColorRes(R.color.white)
                    .contentColorRes(R.color.dialog_content_color).build();

            checkDialog.show();
            //速度太快，避免弹出窗口消失太快,2秒后执行检测方法
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    versionCheck();
                }
            }, 1500);
        } else {
            ToastUtils.show(getActivity(), "正在下载新版本");
        }
    }

    //版本检测更新
    public void versionCheck() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("channel", AppUtils.getMetaDataValue(getActivity(), "UMENG_CHANNEL"));

        okHttpRequest.aget(Server.CHECK_VERSION_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                if (checkDialog != null && checkDialog.isShowing()) {
                    checkDialog.dismiss();
                }

                VersionRet versionRet = mService.checkVersion(response);
                if (versionRet != null && versionRet.data != null) {
                    VersionInfo versionInfo = versionRet.data;
                    if (versionInfo != null) {
                        if (versionInfo.version.equals(AppUtils.getVersionName(getActivity()))) {
                            ToastUtils.show(getActivity(), "当前已是最新版本");
                        } else {
                            if (versionInfo.versionCode > AppUtils.getVersionCode(getActivity())) {
                                if (!AppUtils.checkSdCardExist()) {
                                    ToastUtils.show(getActivity(), "未检测到SD卡，暂时无法更新");
                                } else {
                                    downUrl = versionInfo.download;

                                    updateDialog = new MaterialDialog.Builder(getActivity())
                                            .titleColorRes(R.color.dialog_title_color)
                                            .contentColorRes(R.color.dialog_content_color)
                                            .backgroundColorRes(R.color.white)
                                            .positiveColorRes(R.color.colorPrimary)
                                            .negativeColorRes(R.color.quick_text_color)
                                            .title(R.string.version_update_text)
                                            .content(R.string.version_update_content_text)
                                            .positiveText(R.string.confirm_update_text)
                                            .negativeText(R.string.cancel_text)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    if (downUrl != null && downUrl.length() > 0) {
                                                        new DownAsyncTask().execute();
                                                    } else {
                                                        ToastUtils.show(getActivity(), "下载地址有误，请稍后重试");
                                                    }
                                                }
                                            })
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .iconRes(R.drawable.logo)
                                            .maxIconSize(80).build();
                                    updateDialog.show();
                                }
                            } else {
                                ToastUtils.show(getActivity(), "当前已是最新版本");
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                if (materialDialog != null && materialDialog.isShowing()) {
                    materialDialog.dismiss();
                }
            }

            @Override
            public void onBefore() {

            }
        });
    }


    public class DownAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            return NetWorkUtils.is404NotFound(downUrl);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                Intent intent = new Intent(getActivity(), UpdateService.class);
                intent.putExtra("downUrl", downUrl);
                getActivity().startService(intent);
            } else {
                ToastUtils.show(getActivity(), "下载地址有误，请稍后重试");
            }
        }
    }

}