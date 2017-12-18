package com.feiyou.headstyle.ui.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.SharePopupWindow;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.about_text)
    String titleTextValue;

    @BindView(R.id.about_head_tv)
    TextView aboutHeadTv;

    @BindView(R.id.tv_version_name)
    TextView mVersionNameTextView;

    @BindView(R.id.iv_head_weixin_code)
    ImageView mWeiXinImageView;

    //分享弹出窗口
    private SharePopupWindow shareWindow;

    private UMImage image;

    private UMShareAPI mShareAPI = null;

    private MaterialDialog loginDialog;

    private Handler handler = new Handler();

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initViews() {
        super.initViews();
        titleTv.setText(titleTextValue);
        mVersionNameTextView.setText(getResources().getString(R.string.app_name) + "V" + AppUtils.getVersionName(this));
    }

    @Override
    public void loadData() {
        String htmlHead = "<p>专为年轻人打造的头像APP </p>" +
                "<p>海量头像素材，任你挑选</p>" +
                "<p>专属定制头像，由你做主</p>" +
                "<p>个性头像，让生活更精彩!</p>";
        aboutHeadTv.setText(Html.fromHtml(htmlHead));
        mShareAPI = UMShareAPI.get(this);
        image = new UMImage(AboutActivity.this, R.mipmap.logo_100);
    }

    /****************
     * 发起添加群流程。群号：个性头像(302826961) 的 key 为： KFPYG3jYKt9Z482w3KquNogfuz7ioPey
     * 调用 joinQQGroup(KFPYG3jYKt9Z482w3KquNogfuz7ioPey) 即可发起手Q客户端申请加群 个性头像(302826961)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    @OnClick(R.id.join_tv)
    public void joinQQ() {
        joinQQGroup("KFPYG3jYKt9Z482w3KquNogfuz7ioPey");
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

    @OnClick(R.id.share_btn)
    public void shareApp() {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        shareWindow = new SharePopupWindow(this, itemsOnClick);

        shareWindow.showAtLocation(viewGroup, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        setBackgroundAlpha(AboutActivity.this, 0.5f);
        shareWindow.setOnDismissListener(new PoponDismissListener());
    }

    //弹出窗口监听消失
    public class PoponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            setBackgroundAlpha(AboutActivity.this, 1f);
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
                    new ShareAction(AboutActivity.this)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .setCallback(umShareListener)
                            .withTitle("个性头像")
                            .withText("换个头像，换种心情")
                            .withTargetUrl("http://gx.qqtn.com/")
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.qzone_layout:
                    new ShareAction(AboutActivity.this)
                            .setPlatform(SHARE_MEDIA.QZONE)
                            .setCallback(umShareListener)
                            .withTitle("个性头像")
                            .withText("换个头像，换种心情")
                            .withTargetUrl("http://gx.qqtn.com/")
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.wechat_layout:
                    new ShareAction(AboutActivity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .setCallback(umShareListener)
                            .withTitle("个性头像")
                            .withText("换个头像，换种心情")
                            .withTargetUrl("http://gx.qqtn.com/")
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.wxcircle_layout:
                    new ShareAction(AboutActivity.this)
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
            Toast.makeText(AboutActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(AboutActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(AboutActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }

    @OnClick(R.id.iv_head_weixin_code)
    public void weixinCode() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(ClipData.newPlainText(null, "gexing-app"));
        ToastUtils.show(AboutActivity.this, "复制成功，可以加微信了");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //弹出QQ登录询问框
                loginDialog = DialogUtils.createWeiXinDialog(AboutActivity.this, R.string.confirm_text, R.string.cancel_login_text, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (AppUtils.appInstalled(AboutActivity.this, "com.tencent.mm")) {
                            AppUtils.launchApp(AboutActivity.this, "com.tencent.mm", 1);
                        } else {
                            ToastUtils.show(AboutActivity.this, "未安装微信");
                        }
                    }
                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
                loginDialog.show();
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (loginDialog != null && loginDialog.isShowing()) {
            loginDialog.dismiss();
        }
        finish();
    }
}
