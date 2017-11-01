package com.feiyou.headstyle.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.MyCreateInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.db.greendao.DaoSession;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DbUtil;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.ImageUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.SharePopupWindow;
import com.feiyou.headstyle.view.qqhead.BaseUIListener;
import com.orhanobut.logger.Logger;
import com.tencent.connect.avatar.QQAvatar;
import com.tencent.tauth.Tencent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class HeadCreateShowActivity extends BaseActivity {

    private static final int REQUEST_SET_AVATAR = 2;

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.head_show_text)
    String titleTextValue;

    @BindView(R.id.square_head_image)
    SimpleDraweeView squareHeadImage;

    @BindView(R.id.circle_head_image)
    SimpleDraweeView circleHeadImage;

    @BindView(R.id.qq_auth_login_btn)
    TextView qqAuthImageBtn;

    @BindView(R.id.down_img)
    TextView downImg;

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    private String imagePath;

    private Tencent mTencent;

    UserService mService = null;

    OKHttpRequest okHttpRequest = null;

    private UserInfo userInfo;

    //分享弹出窗口
    private SharePopupWindow shareWindow;

    private UMImage image;

    String userName;

    String gender;

    String imgUrl;

    MaterialDialog materialDialog;

    private MaterialDialog loginDialog;

    // 1，设置QQ头像,2，添加到我的制作
    private int operation = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtils.show(HeadCreateShowActivity.this, "图片已保存到图库");
                    break;
                case 1:
                    ToastUtils.show(HeadCreateShowActivity.this, "图片保存失败");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_head_create_show;
    }

    @Override
    public void initViews() {
        super.initViews();
        mService = new UserService();
        okHttpRequest = new OKHttpRequest();
        mTencent = Tencent.createInstance("1105592461", this.getApplicationContext());
        titleTv.setText(titleTextValue);
    }

    @Override
    public void loadData() {
        image = new UMImage(HeadCreateShowActivity.this, R.drawable.logo);
        mShareAPI = UMShareAPI.get(this);
        platform = SHARE_MEDIA.QQ;

        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && bundle.getBoolean("isCreateQQImage", false)) {
            if (bundle != null && bundle.getString("imagePath") != null) {
                imagePath = bundle.getString("imagePath");
            }
            Uri uri = Uri.parse("file:///" + imagePath);
            squareHeadImage.setImageURI(uri);
            circleHeadImage.setImageURI(uri);

            Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath);
            if(tempBitmap != null){
                tempBitmap = ImageUtils.compressImage(tempBitmap,150);
                image = new UMImage(HeadCreateShowActivity.this, tempBitmap);
            }
        }

        if (AppUtils.isLogin(this)) {
            userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        } else {
            initLoginDialog();
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
                mShareAPI.doOauthVerify(HeadCreateShowActivity.this, platform, umAuthListener);
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.add_article_img)
    public void addArticleImg(View view) {
        operation = 2;
        if (AppUtils.isLogin(this)) {
            addArticle();
        } else {
            loginDialog.show();
        }
    }

    public void addArticle() {
        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        if (userInfo != null) {
            DaoSession daoSession = DbUtil.getSession(this);
            MyCreateInfo myCreateInfo = new MyCreateInfo();
            myCreateInfo.setUid(userInfo.uid);

            if (imagePath != null && imagePath.length() > 0) {
                //String photoId = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
                myCreateInfo.setPhoto_id(imagePath);
            }

            daoSession.insert(myCreateInfo);
            ToastUtils.show(this, "已添加到我的制作");
        } else {
            ToastUtils.show(this, "操作失败，请稍后重试");
        }
    }

    @OnClick(R.id.down_img)
    public void downImg(View view) {
        Message message = new Message();
        if (saveImageToGallery()) {
            message.what = 0;
        } else {
            message.what = 1;
        }
        handler.sendMessage(message);
    }

    // 其次把文件插入到系统图库
    public boolean saveImageToGallery() {
        boolean flag = true;
        try {
            if (imagePath != null && imagePath.length() > 0) {
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        imagePath, imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length()), null);

                MediaScannerConnection.scanFile(HeadCreateShowActivity.this, new String[]{imagePath}, null, null);
                // 最后通知图库更新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imagePath)));
            } else {
                flag = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    @OnClick(R.id.qq_auth_login_btn)
    public void toAuth(View view) {
        operation = 1;
        //已经登录授权过，无需再次授权
        if (HeadStyleApplication.isLoginAuth) {
            if (imagePath != null && imagePath.length() > 0) {
                Uri uri = Uri.parse("file:///" + imagePath);
                doSetAvatar(uri);
            } else {
                ToastUtils.show(HeadCreateShowActivity.this, "图片地址有误，不能设置QQ头像");
            }
        } else {
            mShareAPI.doOauthVerify(HeadCreateShowActivity.this, platform, umAuthListener);
        }
    }

    @OnClick(R.id.right_image)
    public void shareHead(View view) {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        shareWindow = new SharePopupWindow(this, itemsOnClick);
        shareWindow.showAtLocation(viewGroup, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        setBackgroundAlpha(HeadCreateShowActivity.this, 0.5f);
        shareWindow.setOnDismissListener(new PoponDismissListener());
    }

    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

    //弹出窗口监听消失
    public class PoponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            setBackgroundAlpha(HeadCreateShowActivity.this, 1f);
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancel_layout:
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.qq_layout:
                    new ShareAction(HeadCreateShowActivity.this)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .setCallback(umShareListener)
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.qzone_layout:
                    new ShareAction(HeadCreateShowActivity.this)
                            .setPlatform(SHARE_MEDIA.QZONE)
                            .setCallback(umShareListener)
                            .withTitle("个性头像")
                            .withText("换个头像，换种心情")
                            .withTargetUrl("http://www.qqtn.com/tx/")
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.wechat_layout:
                    new ShareAction(HeadCreateShowActivity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .setCallback(umShareListener)
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.wxcircle_layout:
                    new ShareAction(HeadCreateShowActivity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .setCallback(umShareListener)
                            .withTitle("个性头像")
                            .withText("超萌的图像，是否想尝试一下呢？")
                            .withTargetUrl("http://www.qqtn.com/tx/")
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            Toast.makeText(HeadCreateShowActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(HeadCreateShowActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(HeadCreateShowActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
            mShareAPI.getPlatformInfo(HeadCreateShowActivity.this, platform, umAuthUserInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "取消授权", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthUserInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            //ToastUtils.show(HeadCreateShowActivity.this, "已授权登录,设置QQ头像权限审核中");

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

                                PreferencesUtils.putObject(HeadCreateShowActivity.this, Constant.USER_INFO, userInfo);
                                HeadStyleApplication.isLoginAuth = true;


                                if (operation == 1) {
                                    if (imagePath != null && imagePath.length() > 0) {
                                        Uri uri = Uri.parse("file:///" + imagePath);
                                        doSetAvatar(uri);
                                    } else {
                                        ToastUtils.show(HeadCreateShowActivity.this, "图片地址有误，不能设置QQ头像");
                                    }
                                } else {
                                    addArticle();
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(HeadCreateShowActivity.this, R.string.get_user_info_fail);
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
            Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "取消授权", Toast.LENGTH_SHORT).show();
        }
    };

    private void doSetAvatar(Uri uri) {
        //ToastUtils.show(HeadCreateShowActivity.this, R.string.qq_img);
        QQAvatar qqAvatar = new QQAvatar(mTencent.getQQToken());
        qqAvatar.setAvatar(this, uri, new BaseUIListener(this), R.anim.zoomout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);

    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }
}
