package com.feiyou.headstyle.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.FunTestInfo;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.FunTestResultService;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.view.SharePopupWindow;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by admin on 2017/9/14.
 */

public class FunTestDetailActivity extends BaseActivity {

    @BindView(R.id.back_image)
    ImageView mBackImageView;

    @BindView(R.id.iv_share_icon)
    ImageView mShareImageView;

    @BindView(R.id.tv_fun_test_title)
    TextView mFunTestTitleTextView;

    @BindView(R.id.tv_test_des)
    TextView mFunTestDesTextView;

    @BindView(R.id.tv_test_count)
    TextView mTestCountTextView;

    @BindView(R.id.tv_share_count)
    TextView mShareCountTextView;

    @BindView(R.id.iv_fun_test)
    ImageView mTestTopicImageView;

    @BindView(R.id.btn_start_test)
    Button mStartButton;

    private FunTestInfo funTestInfo;

    //分享弹出窗口
    private SharePopupWindow shareWindow;

    private UMImage image;

    private UMShareAPI mShareAPI = null;

    private String shareTitle = null;

    OKHttpRequest okHttpRequest = null;

    FunTestResultService mService = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fun_test_detail;
    }

    @Override
    protected void initVars() {
        super.initVars();
        okHttpRequest = new OKHttpRequest();
    }

    @Override
    public void initViews() {
        super.initViews();

        mService = new FunTestResultService();
        Bundle bundle = getIntent().getExtras();
        funTestInfo = (FunTestInfo) bundle.getSerializable("fun_test_data_info");
        if (funTestInfo != null) {
            HeadStyleApplication.typeId = funTestInfo.cid;
            mTestCountTextView.setText(funTestInfo.shareperson + "人参与测试");
            mShareCountTextView.setText(funTestInfo.sharetotal + "人转发");
            mFunTestTitleTextView.setText(funTestInfo.title);
            mFunTestDesTextView.setText(funTestInfo.content);
            Glide.with(this).load(funTestInfo.smallimg).into(mTestTopicImageView);
            shareTitle = funTestInfo.sharetitle;
            if (!StringUtils.isEmpty(shareTitle)) {
                if (shareTitle.length() > 11) {
                    String tShareTitle = shareTitle.substring(0, 11);
                    shareTitle = tShareTitle + "......";
                }
            }
        }

        mShareAPI = UMShareAPI.get(this);
        image = new UMImage(FunTestDetailActivity.this, R.mipmap.logo_100);

        RxView.clicks(mStartButton).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(FunTestDetailActivity.this, FunTestItemActivity.class);
                intent.putExtra("fun_test_data_info", funTestInfo);
                startActivity(intent);
            }
        });

        RxView.clicks(mBackImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });

        RxView.clicks(mShareImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) FunTestDetailActivity.this.findViewById(android.R.id.content)).getChildAt(0);

                shareWindow = new SharePopupWindow(FunTestDetailActivity.this, itemsOnClick);

                shareWindow.showAtLocation(viewGroup, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                setBackgroundAlpha(FunTestDetailActivity.this, 0.5f);
                shareWindow.setOnDismissListener(new FunTestDetailActivity.PoponDismissListener());
            }
        });
    }

    //弹出窗口监听消失
    public class PoponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            setBackgroundAlpha(FunTestDetailActivity.this, 1f);
        }
    }

    @Override
    public void loadData() {
        super.loadData();
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
                    new ShareAction(FunTestDetailActivity.this)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .setCallback(umShareListener)
                            .withTitle(shareTitle)
                            .withText(funTestInfo.sharedesc)
                            .withTargetUrl("http://gx.qqtn.com/")
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.qzone_layout:
                    new ShareAction(FunTestDetailActivity.this)
                            .setPlatform(SHARE_MEDIA.QZONE)
                            .setCallback(umShareListener)
                            .withTitle(shareTitle)
                            .withText(funTestInfo.sharedesc)
                            .withTargetUrl("http://gx.qqtn.com/")
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.wechat_layout:
                    new ShareAction(FunTestDetailActivity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .setCallback(umShareListener)
                            .withTitle(shareTitle)
                            .withText(funTestInfo.sharedesc)
                            .withTargetUrl("http://gx.qqtn.com/")
                            .withMedia(image)
                            .share();
                    if (shareWindow != null && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                    break;
                case R.id.wxcircle_layout:
                    new ShareAction(FunTestDetailActivity.this)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .setCallback(umShareListener)
                            .withTitle(shareTitle)
                            .withText(funTestInfo.sharedesc)
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
            updateShareCount();
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            Toast.makeText(FunTestDetailActivity.this, " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(FunTestDetailActivity.this, " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(FunTestDetailActivity.this, " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    public void updateShareCount() {

        final Map<String, String> params = new HashMap<String, String>();
        params.put("stype", "1");
        params.put("testid", funTestInfo.testid);
        params.put("cid", funTestInfo.cid + "");

        okHttpRequest.aget(Server.FUN_TEST_COUNT_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {

                Result result = mService.getUpdateResult(response);
                if (result != null && result.errCode == 0) {
                    Logger.e("update share count success--->");
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
