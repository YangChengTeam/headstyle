package com.feiyou.headstyle.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.bean.VersionInfo;
import com.feiyou.headstyle.bean.VersionRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.net.service.UpdateService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.activity.AboutActivity;
import com.feiyou.headstyle.ui.activity.KeepListActivity;
import com.feiyou.headstyle.ui.activity.MyArticleActivity;
import com.feiyou.headstyle.ui.activity.MyCreateListActivity;
import com.feiyou.headstyle.ui.activity.MyInfoActivity;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.GlideHelper;
import com.feiyou.headstyle.util.NetWorkUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;
import rx.functions.Action1;

/**
 * Created by admin on 2017/11/21.
 */

public class MyFragment1 extends BaseFragment {

    @BindView(R.id.iv_user_head)
    ImageView mUserHeadImageView;

    @BindView(R.id.iv_gender)
    ImageView mGenderImageView;

    @BindView(R.id.tv_user_name)
    TextView mUserNameTextView;

    @BindView(R.id.tv_sign)
    TextView mSignTextView;

    @BindView(R.id.tv_agree_count)
    TextView mAgreeCountTextView;

    @BindView(R.id.layout_my_keep)
    RelativeLayout mMyKeepLayout;

    @BindView(R.id.layout_my_create)
    RelativeLayout mMyCreateLayout;

    @BindView(R.id.layout_my_article)
    RelativeLayout mMyArticleLayout;

    @BindView(R.id.layout_my_message)
    RelativeLayout mMyMessageLayout;

    @BindView(R.id.layout_version_update)
    RelativeLayout mVersionUpdateLayout;

    @BindView(R.id.layout_about)
    RelativeLayout mAboutLayout;

    @BindView(R.id.layout_message_count)
    LinearLayout mMessageCountLayout;

    @BindView(R.id.tv_message_count)
    TextView mMessageCountTextView;

    @BindView(R.id.layout_agree)
    LinearLayout mAgreeLayout;

    @BindView(R.id.iv_letter_count)
    ImageView mLetterCountImageView;

    private UserService mService = null;

    private OKHttpRequest okHttpRequest = null;

    private UserInfo userInfo;

    private MaterialDialog materialDialog;

    private MaterialDialog checkDialog;

    private MaterialDialog updateDialog;

    private String downUrl;

    private UMShareAPI mShareAPI = null;

    private SHARE_MEDIA platform = null;

    private String userName;

    private String gender;

    private String imgUrl;

    private Handler handler = new Handler() {
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my1;
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

        mShareAPI = UMShareAPI.get(getActivity());

        if (AppUtils.isLogin(getActivity())) {
            userInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
        }

        if (App.isLetter && userInfo != null) {
            mLetterCountImageView.setVisibility(View.VISIBLE);
        }else{
            mLetterCountImageView.setVisibility(View.GONE);
        }

        RxView.clicks(mUserHeadImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (userInfo == null) {
                    platform = SHARE_MEDIA.QQ;
                    mShareAPI.doOauthVerify(getActivity(), platform, umAuthListener);
                } else {
                    Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                    startActivity(intent);
                }
            }
        });

        RxView.clicks(mMyKeepLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), KeepListActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mMyCreateLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), MyCreateListActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mMyArticleLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), MyArticleActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mMyMessageLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (userInfo != null) {
                    Map<String, Boolean> map = new HashMap<String, Boolean>();
                    map.put(Conversation.ConversationType.PRIVATE.getName(), false);
                    RongIM.getInstance().startConversationList(getActivity(), map);
                }else{
                    ToastUtils.show(getActivity(),"请登录后查看");
                    return;
                }
            }
        });

        RxView.clicks(mVersionUpdateLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
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
        });

        RxView.clicks(mAboutLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("onResume--->");
        getUserInfo();
    }

    public void getUserInfo() {
        userInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
        if (userInfo != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("uid", userInfo.getUid());
            //查询用户信息
            okHttpRequest.aget(Server.USER_INFO_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    UserInfo tempInfo = mService.login(response);
                    if (tempInfo != null) {
                        userInfo = tempInfo;
                        PreferencesUtils.putObject(getActivity(), Constant.USER_INFO, userInfo);
                        if (userInfo.getSex().equals("1")) {
                            mGenderImageView.setImageResource(R.mipmap.persion_boy_icon);
                        } else {
                            mGenderImageView.setImageResource(R.mipmap.persion_girl_icon);
                        }
                        mUserNameTextView.setText(userInfo.getNickName());
                        mSignTextView.setText(!StringUtils.isEmpty(userInfo.getSign()) ? userInfo.getSign() : "你还没有个性签名");
                        //个人信息点赞总数
                        if (!StringUtils.isEmpty(userInfo.getUserzan())) {
                            mAgreeCountTextView.setText(userInfo.getUserzan());
                        }

                        //原有的评论数
                        int lastCount = PreferencesUtils.getInt(getActivity(), App.AID + userInfo.uid);

                        int currentCount = !StringUtils.isBlank(userInfo.getComment()) ? Integer.parseInt(userInfo.getComment()) : 0;
                        //存储最新的评论数
                        PreferencesUtils.putInt(getActivity(), App.AID + userInfo.uid, currentCount);
                        if ((currentCount > 0 && currentCount > lastCount) || App.isMessage) {
                            mMessageCountLayout.setVisibility(View.VISIBLE);
                            App.isMessage = true;
                            //我的发帖评论总数
                            if (currentCount > 9) {
                                if (currentCount > 99) {
                                    userInfo.setComment("99+");
                                }
                                mMessageCountLayout.setBackgroundResource(R.mipmap.dual_num_icon);
                            } else {
                                mMessageCountLayout.setBackgroundResource(R.mipmap.single_num_icon);
                            }
                            mMessageCountTextView.setText(userInfo.getComment() + "");
                        } else {
                            App.isMessage = false;
                            mMessageCountLayout.setVisibility(View.GONE);
                        }


                       /* RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

                            @Override
                            public io.rong.imlib.model.UserInfo getUserInfo(String userId) {
                                Uri uri = Uri.parse(userInfo.getUserimg());
                                io.rong.imlib.model.UserInfo rUser = new io.rong.imlib.model.UserInfo(userInfo.getUid(), userInfo.getNickName(), uri);
                                return rUser;
                            }

                        }, true);*/

                        Uri uri = null;
                        if (!StringUtils.isBlank(userInfo.getUserimg())) {
                            uri = Uri.parse(userInfo.getUserimg());
                        }
                        io.rong.imlib.model.UserInfo ryUser = new io.rong.imlib.model.UserInfo(userInfo.getUid(), userInfo.getNickName(), uri);
                        RongIM.getInstance().setCurrentUserInfo(ryUser);

                        RongIM.getInstance().refreshUserInfoCache(ryUser);
                    }

                    GlideHelper.circleImageView(getActivity(), mUserHeadImageView, userInfo != null ? userInfo.getUserimg() : "", R.mipmap.user_head_def_icon);
                }

                @Override
                public void onError(Exception e) {
                    ToastUtils.show(getActivity(), R.string.get_user_info_fail);
                }

                @Override
                public void onBefore() {

                }
            });
        } else {
            mUserNameTextView.setText("请登录");
            mSignTextView.setText("你还没有个性签名");
            mAgreeCountTextView.setText("0");
            mGenderImageView.setImageResource(0);
            mMessageCountTextView.setText("");
            mMessageCountLayout.setVisibility(View.GONE);
            GlideHelper.circleImageView(getActivity(), mUserHeadImageView, userInfo != null ? userInfo.getUserimg() : "", R.mipmap.user_head_def_icon);
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

                            userInfo = mService.login(response);
                            if (userInfo != null) {
                                Logger.e("登录成功");

                                if (userInfo.getSex().equals("1")) {
                                    mGenderImageView.setImageResource(R.mipmap.persion_boy_icon);
                                } else {
                                    mGenderImageView.setImageResource(R.mipmap.persion_girl_icon);
                                }
                                mUserNameTextView.setText(!StringUtils.isEmpty(userInfo.getNickName()) ? userInfo.getNickName() : "火星用户");
                                mSignTextView.setText(!StringUtils.isEmpty(userInfo.getSign()) ? userInfo.getSign() : "你还没有个性签名");
                                if (!StringUtils.isEmpty(userInfo.getUserzan())) {
                                    mAgreeCountTextView.setText(userInfo.userzan);
                                }
                                //设置QQ头像
                                if (!StringUtils.isEmpty(imgUrl)) {

                                    GlideHelper.circleImageView(getActivity(), mUserHeadImageView, userInfo != null ? userInfo.getUserimg() : "", R.mipmap.user_default_icon);

                                    String fileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) + ".jpg";

                                    OkHttpUtils.get().url(imgUrl).build().execute(new FileCallBack(Constant.BASE_NORMAL_IMAGE_DIR, fileName)//
                                    {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                        }

                                        @Override
                                        public void onResponse(File file, int id) {
                                            String imagePath = file.getAbsolutePath();
                                            App.userImgPath = imagePath;
                                        }
                                    });
                                }
                                PreferencesUtils.putObject(getActivity(), Constant.USER_INFO, userInfo);
                                App.isLoginAuth = true;
                                getUserInfo();
                                App.connect(userInfo.getUsertoken());
                                RxBus.get().post(Constant.LOGIN_SUCCESS, "loginSuccess");
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

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.MESSAGE)
            }
    )
    public void messageHint(String result) {
        if (App.isLetter) {
            mLetterCountImageView.setVisibility(View.VISIBLE);
        }else{
            mLetterCountImageView.setVisibility(View.GONE);
        }
    }

}
