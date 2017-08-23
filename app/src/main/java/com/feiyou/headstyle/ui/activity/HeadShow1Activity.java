package com.feiyou.headstyle.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.GalleryImageAdapter;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.bean.UserKeepInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.HomeService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.ImageUtils;
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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class HeadShow1Activity extends BaseActivity {

    private static final int REQUEST_SET_AVATAR = 2;

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.head_show_text)
    String titleTextValue;

    @BindView(R.id.qq_auth_login_btn)
    TextView qqAuthImageBtn;

    @BindView(R.id.keep_head_img)
    TextView keepHeadImg;

    @BindView(R.id.down_img)
    TextView downImg;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    private String cid;

    private String imageUrl;

    /**
     * 是否收藏, 0:为收藏，1：已收藏
     */
    private String isKeep = "0";

    private Tencent mTencent;

    private UserService uService = null;

    private HomeService mService = null;

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

    GalleryImageAdapter galleryImageAdapter;

    private List<HeadInfo> images;

    private int pageNum = 1;

    private int currentSelectPosition;

    private Dialog tipDialog;

    private View dialogView;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtils.show(HeadShow1Activity.this, "图片已保存到图库");
                    break;
                case 1:
                    ToastUtils.show(HeadShow1Activity.this, "图片保存失败");
                    break;
                case 2:
                    galleryImageAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_head_show2;
    }

    @Override
    protected void initVars() {
        super.initVars();
        images = new ArrayList<HeadInfo>();
    }

    @Override
    public void initViews() {
        super.initViews();
        uService = new UserService();
        mService = new HomeService();
        okHttpRequest = new OKHttpRequest();
        mTencent = Tencent.createInstance("1105592461", this.getApplicationContext());
        titleTv.setText(titleTextValue);

        galleryImageAdapter = new GalleryImageAdapter(this, images);
        viewPager.setAdapter(galleryImageAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentSelectPosition = position;

                if(currentSelectPosition > galleryImageAdapter.getCount() - 5){
                    loadGalleryData(pageNum);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //首次加载制图页面时，提示用户如何操作
        tipDialog = new Dialog(this, R.style.Dialog_Fullscreen);
        dialogView = LayoutInflater.from(this).inflate(R.layout.head_dialog_tip, null);
        tipDialog.setContentView(dialogView);

        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tipDialog != null && tipDialog.isShowing()){
                    tipDialog.dismiss();
                }
            }
        });

        boolean isShowTip = PreferencesUtils.getBoolean(this,Constant.IS_SHOW_TIP,false);
        if(!isShowTip){
            tipDialog.show();
            PreferencesUtils.putBoolean(this,Constant.IS_SHOW_TIP,true);
        }
    }

    @Override
    public void loadData() {
        image = new UMImage(HeadShow1Activity.this, R.drawable.logo);
        mShareAPI = UMShareAPI.get(this);
        platform = SHARE_MEDIA.QQ;
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.getString("cid") != null) {
            cid = bundle.getString("cid");
        }
        if (bundle != null && bundle.getString("imageUrl") != null) {
            imageUrl = bundle.getString("imageUrl");
            Uri uri = Uri.parse(imageUrl);

            HeadInfo firstHeadInfo = new HeadInfo();
            firstHeadInfo.hurl = imageUrl;

            galleryImageAdapter.addNewData(firstHeadInfo);

            galleryImageAdapter.notifyDataSetChanged();

            String fileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) + ".jpg";
            OkHttpUtils.get().url(imageUrl).build().execute(new FileCallBack(Constant.BASE_NORMAL_IMAGE_DIR, fileName)//
            {
                @Override
                public void onError(Call call, Exception e, int id) {
                }

                @Override
                public void onResponse(File file, int id) {
                    imagePath = file.getAbsolutePath();
                    Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath);
                    if (tempBitmap != null) {
                        tempBitmap = ImageUtils.compressImage(tempBitmap, 100);
                        image = new UMImage(HeadShow1Activity.this, tempBitmap);
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
                    UserKeepInfo userKeepInfo = uService.isKeepByUser(response);
                    if (userKeepInfo != null) {
                        if (userKeepInfo.iskeep.equals("1")) {

                            Drawable drawable = ContextCompat.getDrawable(HeadShow1Activity.this, R.mipmap.keep_success_icon);
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
            Drawable drawable = ContextCompat.getDrawable(HeadShow1Activity.this, R.mipmap.keep_normal_icon);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            keepHeadImg.setCompoundDrawables(null, drawable, null, null);
            isKeep = "0";
        }

        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }

        loadGalleryData(pageNum);
    }

    /**
     * 预加载数据
     *
     * @param page
     */
    public void loadGalleryData(int page) {
        final Map<String, String> params = new HashMap<String, String>();
        Logger.e("page---" + page);
        params.put("p", String.valueOf(page));

        okHttpRequest.aget(Server.PRE_LOAD_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {

                //设置首页列表数据
                List<HeadInfo> temp = mService.getHeadInfos(response);
                if (temp != null && temp.size() > 0) {

                    Logger.e("temp--size---"+temp.size());

                    //images.addAll(temp);
                    galleryImageAdapter.addNewDataList(temp);
                    galleryImageAdapter.notifyDataSetChanged();

                    pageNum++;
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


    /**
     * 初始化登录框
     */
    public void initLoginDialog() {
        //弹出QQ登录询问框
        loginDialog = DialogUtils.createDialog(this, R.string.to_login_text, R.string.cancel_login_text, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                //直接登录QQ
                mShareAPI.doOauthVerify(HeadShow1Activity.this, platform, umAuthListener);
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
                    Result result = uService.headKeep(response);
                    if (result != null) {
                        if (result.errCode == Constant.RESULT_SUCCESS) {
                            if (isKeep.equals("0")) {
                                ToastUtils.show(HeadShow1Activity.this, R.string.head_keep_success);

                                Drawable drawable = ContextCompat.getDrawable(HeadShow1Activity.this, R.mipmap.keep_success_icon);
                                /// 这一步必须要做,否则不会显示.
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                keepHeadImg.setCompoundDrawables(null, drawable, null, null);

                                isKeep = "1";
                            } else {
                                ToastUtils.show(HeadShow1Activity.this, R.string.head_unkeep_success);

                                Drawable drawable = ContextCompat.getDrawable(HeadShow1Activity.this, R.mipmap.keep_normal_icon);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                keepHeadImg.setCompoundDrawables(null, drawable, null, null);

                                isKeep = "0";
                            }
                        }
                        if (result.errCode == Constant.RESULT_FAIL) {
                            if (isKeep.equals("0")) {
                                ToastUtils.show(HeadShow1Activity.this, "error1");
                            } else {
                                ToastUtils.show(HeadShow1Activity.this, "error2");
                            }
                        }
                    } else {
                        if (isKeep.equals("0")) {
                            ToastUtils.show(HeadShow1Activity.this, "error3");
                        } else {
                            ToastUtils.show(HeadShow1Activity.this, "error4");
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (isKeep.equals("0")) {
                        ToastUtils.show(HeadShow1Activity.this, "error5");
                    } else {
                        ToastUtils.show(HeadShow1Activity.this, "error6");
                    }
                }

                @Override
                public void onBefore() {

                }
            });
        } else {
            ToastUtils.show(HeadShow1Activity.this, R.string.head_keep_fail);
        }
    }


    @OnClick(R.id.qq_auth_login_btn)
    public void toAuth(View view) {
        operation = 1;
        //已经登录授权过，无需再次授权
        if (HeadStyleApplication.isLoginAuth) {
            setUseCount();
            if (imagePath != null && imagePath.length() > 0) {
                Uri uri = Uri.parse("file:///" + imagePath);
                doSetAvatar(uri);
            } else {
                ToastUtils.show(HeadShow1Activity.this, "图片地址有误，不能设置QQ头像");
            }
        } else {
            mShareAPI.doOauthVerify(HeadShow1Activity.this, platform, umAuthListener);
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

                MediaScannerConnection.scanFile(HeadShow1Activity.this, new String[]{imagePath}, null, null);
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


    @OnClick(R.id.right_image)
    public void shareHead(View view) {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        shareWindow = new SharePopupWindow(this, itemsOnClick);
        shareWindow.showAtLocation(viewGroup, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        setBackgroundAlpha(HeadShow1Activity.this, 0.5f);
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
            setBackgroundAlpha(HeadShow1Activity.this, 1f);
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
                    new ShareAction(HeadShow1Activity.this)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .setCallback(umShareListener)
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.qzone_layout:
                    new ShareAction(HeadShow1Activity.this)
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
                    new ShareAction(HeadShow1Activity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .setCallback(umShareListener)
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.wxcircle_layout:
                    new ShareAction(HeadShow1Activity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
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
                default:
                    break;
            }
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            Toast.makeText(HeadShow1Activity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(HeadShow1Activity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(HeadShow1Activity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
            mShareAPI.getPlatformInfo(HeadShow1Activity.this, platform, umAuthUserInfoListener);
            materialDialog = new MaterialDialog.Builder(HeadShow1Activity.this)
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

                            UserInfo userInfo = uService.login(response);
                            if (userInfo != null) {
                                //ToastUtils.show(getActivity(),R.string.get_user_info_success);
                                Logger.e("登录成功");

                                PreferencesUtils.putObject(HeadShow1Activity.this, Constant.USER_INFO, userInfo);
                                HeadStyleApplication.isLoginAuth = true;

                                if (operation == 1) {
                                    if (imagePath != null && imagePath.length() > 0) {
                                        Uri uri = Uri.parse("file:///" + imagePath);
                                        doSetAvatar(uri);
                                    } else {
                                        ToastUtils.show(HeadShow1Activity.this, "图片地址有误，不能设置QQ头像");
                                    }
                                    setUseCount();
                                } else {
                                    addKeep();
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(HeadShow1Activity.this, R.string.get_user_info_fail);
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

    public void setUseCount() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("imgid", cid);
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
        UMShareAPI.get(this).HandleQQError(HeadShow1Activity.this, requestCode, umAuthListener);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (galleryImageAdapter != null) {
            galleryImageAdapter.clearDatas();
        }
        if (images != null && images.size() > 0) {
            images.clear();
        }
    }
}
