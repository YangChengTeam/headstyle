package com.feiyou.headstyle.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.bean.UserKeepInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.ImgUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
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
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import okhttp3.Call;

public class HeadShowActivity extends BaseActivity {

    private static final int REQUEST_SET_AVATAR = 2;

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.head_show_text)
    String titleTextValue;

    @BindView(R.id.square_head_image)
    SimpleDraweeView squareHeadImage;

    @BindView(R.id.circle_head_image)
    SimpleDraweeView circleHeadImage;

    @BindView(R.id.high_square_head_image)
    SimpleDraweeView highSquareHeadImage;

    @BindView(R.id.high_circle_head_image)
    SimpleDraweeView highCircleHeadImage;

    @BindView(R.id.high_image)
    SimpleDraweeView highImage;

    @BindView(R.id.low_image_layout)
    LinearLayout lowImageLayout;

    @BindView(R.id.high_image_layout)
    LinearLayout highImageLayout;

    @BindView(R.id.keep_head_img)
    TextView keepHeadImg;

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    private String cid;

    private String imageUrl;

    private int isGaoQing = 0;

    /**
     * 是否收藏, 0:为收藏，1：已收藏
     */
    private String isKeep = "0";

    private Tencent mTencent;

    UserService mService = null;

    OKHttpRequest okHttpRequest = null;

    private UserInfo userInfo;

    /**
     * 是否是设置QQ头像的操作
     */
    private boolean isSetQQImage = false;

    //分享弹出窗口
    private SharePopupWindow shareWindow;

    private UMImage image;

    MaterialDialog materialDialog;

    private MaterialDialog loginDialog;

    private String imagePath;

    String userName;

    String gender;

    String imgUrl;

    // 1，设置QQ头像,2，添加到我的收藏
    private int operation = 1;

    private String fileName = "";

    private String savePath = "";

    private File saveFile;

    private boolean isSetting = false;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtils.show(HeadShowActivity.this, "图片已保存到图库");

                    // 最后通知图库更新
                    if (saveFile.exists()) {
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(saveFile);
                        intent.setData(uri);
                        sendBroadcast(intent);
                    }

                    break;
                case 1:
                    ToastUtils.show(HeadShowActivity.this, "图片保存失败");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_head_show;
    }

    @Override
    public void initViews() {
        super.initViews();
        mService = new UserService();
        okHttpRequest = new OKHttpRequest();
        mTencent = Tencent.createInstance("1105592461", this.getApplicationContext());
        titleTv.setText(titleTextValue);
        isSetting = false;
    }

    @Override
    public void loadData() {
        image = new UMImage(HeadShowActivity.this, R.drawable.logo);
        mShareAPI = UMShareAPI.get(this);
        platform = SHARE_MEDIA.QQ;

        fileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) + ".jpg";
        savePath = Constant.BASE_NORMAL_SAVE_IMAGE_DIR + File.separator + "DCIM" + File.separator + "camera";

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.getString("cid") != null) {
            cid = bundle.getString("cid");
        }

        if (bundle != null && bundle.getString("gaoqing") != null) {
            isGaoQing = Integer.parseInt(bundle.getString("gaoqing"));
        }

        if (bundle != null && bundle.getString("imageUrl") != null) {
            imageUrl = bundle.getString("imageUrl");

            setImage();

            OkHttpUtils.get().url(imageUrl).build().execute(new FileCallBack(savePath, fileName)//
            {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(File file, int id) {
                    saveFile = file;
                    imagePath = file.getAbsolutePath();
                    Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath);
                    if (tempBitmap != null) {
                        tempBitmap = ImgUtils.compressImage(tempBitmap, 100);
                        image = new UMImage(HeadShowActivity.this, tempBitmap);
                    }
                }
            });

        }

        if (AppUtils.isLogin(this)) {
            userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        }

        if (userInfo != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("openid", userInfo.openid);
            params.put("uid", userInfo.uid);
            params.put("imgid", cid);

            okHttpRequest.aget(Server.HEAD_KEEP_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    UserKeepInfo userKeepInfo = mService.isKeepByUser(response);
                    if (userKeepInfo != null) {
                        if (userKeepInfo.iskeep.equals("1")) {

                            Drawable drawable = ContextCompat.getDrawable(HeadShowActivity.this, R.mipmap.keep_success_icon);
                            /// 这一步必须要做,否则不会显示.
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            keepHeadImg.setCompoundDrawables(null, drawable, null, null);
                            isKeep = "1";
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    Logger.e("获取用户收藏信息失败");
                }

                @Override
                public void onBefore() {

                }
            });

        } else {
            //初始化登录框
            initLoginDialog();
            Drawable drawable = ContextCompat.getDrawable(HeadShowActivity.this, R.mipmap.keep_normal_icon);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            keepHeadImg.setCompoundDrawables(null, drawable, null, null);
            isKeep = "0";
        }

        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
    }


    public void setImage() {
        if (!StringUtils.isEmpty(imageUrl)) {
            Uri uri = Uri.parse(imageUrl);

            if (isGaoQing == 2) {
                highImageLayout.setVisibility(View.VISIBLE);
                lowImageLayout.setVisibility(View.GONE);
                highSquareHeadImage.setImageURI(uri);
                highCircleHeadImage.setImageURI(uri);
                highImage.setImageURI(uri);
            } else {
                lowImageLayout.setVisibility(View.VISIBLE);
                highImageLayout.setVisibility(View.GONE);
                squareHeadImage.setImageURI(uri);
                circleHeadImage.setImageURI(uri);
            }
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
                mShareAPI.doOauthVerify(HeadShowActivity.this, platform, umAuthListener);
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.keep_head_img)
    public void headKeep(View view) {
        operation = 2;
        if (AppUtils.isLogin(this)) {
            addKeep();
        } else {
            loginDialog.show();
        }
    }

    public void addKeep() {
        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        if (userInfo != null && cid != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("openid", userInfo.openid);
            params.put("uid", userInfo.uid);
            params.put("imgid", cid);
            if (isKeep.equals("0")) {
                params.put("operate", "add");
            } else {
                params.put("operate", "del");
            }

            okHttpRequest.aget(Server.HEAD_KEEP_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Result result = mService.headKeep(response);
                    if (result != null) {
                        if (result.errCode == Constant.RESULT_SUCCESS) {
                            if (isKeep.equals("0")) {
                                ToastUtils.show(HeadShowActivity.this, R.string.head_keep_success);

                                Drawable drawable = ContextCompat.getDrawable(HeadShowActivity.this, R.mipmap.keep_success_icon);
                                /// 这一步必须要做,否则不会显示.
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                keepHeadImg.setCompoundDrawables(null, drawable, null, null);

                                isKeep = "1";
                            } else {
                                ToastUtils.show(HeadShowActivity.this, R.string.head_unkeep_success);

                                Drawable drawable = ContextCompat.getDrawable(HeadShowActivity.this, R.mipmap.keep_normal_icon);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                keepHeadImg.setCompoundDrawables(null, drawable, null, null);

                                isKeep = "0";
                            }
                        }
                        if (result.errCode == Constant.RESULT_FAIL) {
                            if (isKeep.equals("0")) {
                                ToastUtils.show(HeadShowActivity.this, "error1");
                            } else {
                                ToastUtils.show(HeadShowActivity.this, "error2");
                            }
                        }
                    } else {
                        if (isKeep.equals("0")) {
                            ToastUtils.show(HeadShowActivity.this, "error3");
                        } else {
                            ToastUtils.show(HeadShowActivity.this, "error4");
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (isKeep.equals("0")) {
                        ToastUtils.show(HeadShowActivity.this, "error5");
                    } else {
                        ToastUtils.show(HeadShowActivity.this, "error6");
                    }
                }

                @Override
                public void onBefore() {

                }
            });
        } else {
            ToastUtils.show(HeadShowActivity.this, R.string.head_keep_fail);
        }
    }

    @OnClick(R.id.iv_set_qq_head)
    public void toAuth(View view) {
        operation = 1;
        //已经登录授权过，无需再次授权
        if (App.isLoginAuth) {
            userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
            setUseCount(userInfo.uid);
            if (imagePath != null && imagePath.length() > 0) {
                isSetting = true;
                Uri uri = Uri.parse("file:///" + imagePath);
                doSetAvatar(uri);
            } else {
                ToastUtils.show(HeadShowActivity.this, "图片地址有误，不能设置QQ头像");
            }
        } else {
            mShareAPI.doOauthVerify(HeadShowActivity.this, platform, umAuthListener);
        }
    }

    @OnClick(R.id.iv_down_head)
    public void downImg(View view) {
        saveImageToGallery();
    }

    // 其次把文件插入到系统图库
    public void saveImageToGallery() {
        try {
            if (!StringUtils.isEmpty(imagePath)) {
                /*MediaStore.Images.Media.insertImage(getContentResolver(),
                        imagePath, imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length()), null);

                MediaScannerConnection.scanFile(HeadShowActivity.this, new String[]{imagePath}, null, null);*/

                File saveFile = new File(imagePath);
                Logger.e("saveFile--Path---" + imagePath);

                Message message = new Message();
                message.what = 0;
                if (!saveFile.exists()) {
                    message.what = 1;
                }
                handler.sendMessage(message);
            } else {
                OkHttpUtils.get().url(imageUrl).build().execute(new FileCallBack(savePath, fileName) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(File file, int id) {
                        saveFile = file;
                        imagePath = file.getAbsolutePath();
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();

            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSetting) {
            shareHead();
        }
    }

    @OnClick(R.id.right_image)
    public void shareHead() {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        shareWindow = new SharePopupWindow(this, itemsOnClick);

        shareWindow.showAtLocation(viewGroup, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        setBackgroundAlpha(HeadShowActivity.this, 0.5f);
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
            setBackgroundAlpha(HeadShowActivity.this, 1f);
            isSetting = false;
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
                        isSetting = false;
                    }
                    break;
                case R.id.qq_layout:
                    ShareAction shareAction = new ShareAction(HeadShowActivity.this)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .setCallback(umShareListener)
                            .withMedia(image);

                    if(isSetting){
                        shareAction.withTitle("个性头像")
                        .withText("换个头像，换种心情")
                        .withTargetUrl("http://gx.qqtn.com/");
                    }
                    shareAction.share();

                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                        isSetting = false;
                    }
                    break;
                case R.id.qzone_layout:
                    ShareAction shareActionqz = new ShareAction(HeadShowActivity.this)
                            .setPlatform(SHARE_MEDIA.QZONE)
                            .setCallback(umShareListener)
                            .withTitle("个性头像")
                            .withText("换个头像，换种心情")
                            .withMedia(image);
                    if(isSetting){
                        shareActionqz.withTargetUrl("http://gx.qqtn.com/");
                    }
                    shareActionqz.share();

                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                        isSetting = false;
                    }
                    break;
                case R.id.wechat_layout:
                   ShareAction shareActionwc = new ShareAction(HeadShowActivity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .setCallback(umShareListener)
                            .withMedia(image);
                    if(isSetting){
                        shareActionwc.withTitle("个性头像")
                                .withText("换个头像，换种心情")
                                .withTargetUrl("http://gx.qqtn.com/");
                    }
                    shareActionwc.share();

                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                        isSetting = false;
                    }
                    break;
                case R.id.wxcircle_layout:
                    ShareAction shareActioncr = new ShareAction(HeadShowActivity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .setCallback(umShareListener)
                            .withTitle("个性头像")
                            .withText("换个头像，换种心情")
                            .withMedia(image);
                    if(isSetting){
                        shareActioncr.withTargetUrl("http://www.qqtn.com/");
                    }
                    shareActioncr.share();

                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                        isSetting = false;
                    }
                    break;
                default:
                    isSetting = false;
                    break;
            }
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            Toast.makeText(HeadShowActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(HeadShowActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(HeadShowActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
            mShareAPI.getPlatformInfo(HeadShowActivity.this, platform, umAuthUserInfoListener);
            materialDialog = new MaterialDialog.Builder(HeadShowActivity.this)
                    .content("获取用户信息，请稍后...")
                    .progress(true, 0)
                    .backgroundColorRes(R.color.white)
                    .contentColorRes(R.color.dialog_content_color).build();
            materialDialog.show();
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

                                PreferencesUtils.putObject(HeadShowActivity.this, Constant.USER_INFO, userInfo);
                                App.isLoginAuth = true;

                                if (operation == 1) {
                                    if (imagePath != null && imagePath.length() > 0) {
                                        isSetting = true;
                                        Uri uri = Uri.parse("file:///" + imagePath);
                                        doSetAvatar(uri);
                                    } else {
                                        ToastUtils.show(HeadShowActivity.this, "图片地址有误，不能设置QQ头像");
                                    }
                                    setUseCount(userInfo.uid);
                                } else {
                                    addKeep();
                                }
                                App.connect(userInfo.getUsertoken());
                                Uri uri = null;
                                if (!StringUtils.isBlank(userInfo.getUserimg())) {
                                    uri = Uri.parse(userInfo.getUserimg());
                                }
                                io.rong.imlib.model.UserInfo ryUser = new io.rong.imlib.model.UserInfo(userInfo.getUid(), userInfo.getNickName(), uri);
                                RongIM.getInstance().setCurrentUserInfo(ryUser);

                                RongIM.getInstance().refreshUserInfoCache(ryUser);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(HeadShowActivity.this, R.string.get_user_info_fail);
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

    public void setUseCount(String uid) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("imgid", cid);
        params.put("uid", uid);
        Logger.e("imgid--->" + cid + "uid--->" + uid);
        okHttpRequest.aget(Server.USE_COUNT_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                Logger.e("使用次数增加...");
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onBefore() {

            }
        });
    }


    public boolean ready(Context context) {
        if (mTencent == null) {
            return false;
        }
        boolean ready = mTencent.isSessionValid()
                && mTencent.getQQToken().getOpenId() != null;
        if (!ready) {
            Toast.makeText(context, "login and get openId first, please!",
                    Toast.LENGTH_SHORT).show();
        }
        return ready;
    }

    private void doSetAvatar(Uri uri) {
        //ToastUtils.show(HeadShowActivity.this, R.string.qq_img);
        QQAvatar qqAvatar = new QQAvatar(mTencent.getQQToken());
        qqAvatar.setAvatar(this, uri, new BaseUIListener(this), R.anim.zoomout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).HandleQQError(HeadShowActivity.this, requestCode, umAuthListener);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }
}
