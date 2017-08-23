package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.interfaces.CustomWebViewDelegate;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.CustomWebView;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class ShowDetailActivity extends BaseActivity {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.show_detail_text)
    String titleTextValue;

    @BindView(R.id.article_user_img)
    SimpleDraweeView userImg;

    @BindView(R.id.article_user_name)
    TextView userNameTv;

    @BindView(R.id.top_tv)
    TextView topTv;//置顶

    @BindView(R.id.user_gender_icon)
    ImageView userGender;

    @BindView(R.id.article_send_time)
    TextView articleSendTimeTv;

    @BindView(R.id.article_title)
    TextView articleTitleTv;

    @BindView(R.id.article_photo_list)
    GridView articlePhotoGridView;

    @BindView(R.id.comment_count_tv)
    TextView commentCountTv;

    @BindView(R.id.praise_count_tv)
    TextView praiseCountTv;

    private String uid = "0";

    private String oid = "0";

    private String sid = "1";

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    private MaterialDialog loginDialog;

    MaterialDialog materialDialog;

    String userName;

    String gender;

    String imgUrl;

    UserService mService = null;

    ArticleService articleService = null;

    OKHttpRequest okHttpRequest = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_detail;
    }

    @Override
    protected void initVars() {
        super.initVars();

        Bundle bundle = this.getIntent().getExtras();
       /* if (bundle != null && bundle.getString("uid") != null) {
            uid = bundle.getString("uid");
        }
        if (bundle != null && bundle.getString("oid") != null) {
            oid = bundle.getString("oid");
        }*/
        if (bundle != null && bundle.getString("sid") != null) {
            sid = bundle.getString("sid");
        }
        mService = new UserService();
        articleService = new ArticleService();
        okHttpRequest = new OKHttpRequest();
    }

    @Override
    public void initViews() {
        super.initViews();
        titleTv.setText(titleTextValue);
    }

    @Override
    public void loadData() {
        mShareAPI = UMShareAPI.get(ShowDetailActivity.this);
        initLoginDialog();

        final Map<String, String> params = new HashMap<String, String>();
        params.put("sid", sid);

        okHttpRequest.aget(Server.ARTICLE_DETAIL_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {

                if (materialDialog != null && materialDialog.isShowing()) {
                    materialDialog.dismiss();
                }

                ArticleInfo articleInfo = articleService.getArticleInfoBySID(response);

            }

            @Override
            public void onBefore() {

            }

            @Override
            public void onError(Exception e) {

            }
        });
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
                mShareAPI.doOauthVerify(ShowDetailActivity.this, platform, umAuthListener);
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }

    /**
     * 图片预览
     *
     * @param imageList 图片地址
     * @param position  索引位置
     */

    public void showImage(String imageList, String position) {
        Intent intent = new Intent(this, ShowImageListActivity.class);

        if (imageList != null && position != null) {
            intent.putExtra("imageList", imageList);
            intent.putExtra("position", position);
        }

        startActivity(intent);

        //进入图片浏览时的动画
        overridePendingTransition(R.anim.image_show_in, R.anim.image_show_out);
    }

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //Toast.makeText(this, "Authorize succeed", Toast.LENGTH_SHORT).show();

            mShareAPI.getPlatformInfo(ShowDetailActivity.this, platform, umAuthUserInfoListener);

            materialDialog = new MaterialDialog.Builder(ShowDetailActivity.this)
                    .content("获取用户信息，请稍后...")
                    .progress(true, 0)
                    .backgroundColorRes(R.color.white)
                    .contentColorRes(R.color.dialog_content_color).build();
            materialDialog.show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(ShowDetailActivity.this, "登录授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(ShowDetailActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
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

                                PreferencesUtils.putObject(ShowDetailActivity.this, Constant.USER_INFO, userInfo);
                                HeadStyleApplication.isLoginAuth = true;
                                uid = userInfo.uid;
                                oid = userInfo.openid;
                                loadData();
                                ToastUtils.show(ShowDetailActivity.this, "登录成功");
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(ShowDetailActivity.this, R.string.get_user_info_fail);
                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }
                            ToastUtils.show(ShowDetailActivity.this, "登录失败，请稍后重试");
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
            ToastUtils.show(ShowDetailActivity.this, R.string.get_user_info_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show(ShowDetailActivity.this, R.string.get_user_info_cancel);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

}
