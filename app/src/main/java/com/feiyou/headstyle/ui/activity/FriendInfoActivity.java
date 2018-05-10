package com.feiyou.headstyle.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.bean.UserInfoRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.GlideHelper;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import rx.functions.Action1;

/**
 * Created by admin on 2017/11/21.
 */

public class FriendInfoActivity extends BaseActivity {

    private static final int TAKE_BIG_PICTURE = 1000;

    private final int REQUEST_CODE_GALLERY = 1001;

    private static final int CROP_SMALL_PICTURE = 1003;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv_user_head)
    ImageView mUserHeadImageView;

    @BindView(R.id.tv_user_name)
    TextView mNickNameTextView;

    @BindView(R.id.tv_auto_id)
    TextView mAutoIdTextView;

    @BindView(R.id.tv_gender)
    TextView mGenderTextView;

    @BindView(R.id.tv_sign)
    TextView mSignTextView;

    @BindView(R.id.tv_bir_date)
    TextView mBirDateTextView;

    @BindView(R.id.tv_star)
    TextView mStarTextView;

    @BindView(R.id.tv_qq)
    TextView mQQTextView;

    @BindView(R.id.tv_weixin)
    TextView mWeiXinTextView;

    @BindView(R.id.tv_school)
    TextView mSchoolTextView;

    @BindView(R.id.tv_address)
    TextView mAddressTextView;

    @BindView(R.id.layout_agree)
    LinearLayout mAgreeLayout;

    @BindView(R.id.tv_agree_count)
    TextView mAgreeCountTextView;

    @BindView(R.id.layout_letter)
    LinearLayout mLetterLayout;

    private UserInfo currentUserInfo;

    private UserInfo friendUserInfo;

    OKHttpRequest okHttpRequest = null;

    private UserService mService = null;

    private String[] stars;

    private String fuid;

    MaterialDialog materialDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_friend_info;
    }

    @Override
    public void initViews() {
        super.initViews();
        mService = new UserService();
        okHttpRequest = new OKHttpRequest();

        materialDialog = new MaterialDialog.Builder(this)
                .content("正在点赞，请稍后...")
                .progress(true, 0)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.dialog_content_color).build();

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendInfoActivity.this.finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("fuid") != null) {
                fuid = bundle.getString("fuid");
            }
        }

        stars = new String[]{"白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "人马座", "摩羯座", "水瓶座", "双鱼座"};

        RxView.clicks(mAgreeLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (friendUserInfo != null) {
                    if (friendUserInfo.iszan.equals("0")) {
                        if (StringUtils.isEmpty(fuid)) {
                            ToastUtils.show(FriendInfoActivity.this, "用户信息错误");
                            return;
                        }
                        addAgree();
                    } else {
                        ToastUtils.show(FriendInfoActivity.this, "你已经点过赞");
                    }
                }
            }
        });

        //私信
        RxView.clicks(mLetterLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (friendUserInfo != null) {
                    RongIM.getInstance().startPrivateChat(FriendInfoActivity.this, friendUserInfo.getUid(), "测试标题");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void getUserInfo() {

        if (!StringUtils.isEmpty(fuid)) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("uid", fuid);
            params.put("cuid", currentUserInfo != null ? currentUserInfo.getUid() : "");
            //查询好友用户信息
            okHttpRequest.aget(Server.USER_INFO_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    UserInfo tempInfo = mService.login(response);
                    if (tempInfo != null) {
                        friendUserInfo = tempInfo;

                        GlideHelper.circleImageView(FriendInfoActivity.this, mUserHeadImageView, friendUserInfo.getUserimg(), R.mipmap.user_head_def_icon);
                        mNickNameTextView.setText(!StringUtils.isEmpty(friendUserInfo.getNickName()) ? friendUserInfo.getNickName() : "");
                        mAutoIdTextView.setText(friendUserInfo.getUid() + "");
                        mGenderTextView.setText(friendUserInfo.getSex().equals("1") ? "男" : "女");
                        mSignTextView.setText(!StringUtils.isEmpty(friendUserInfo.getSign()) ? friendUserInfo.getSign() : "你还没有个性签名");
                        mBirDateTextView.setText(friendUserInfo.getBirthday());
                        mQQTextView.setText(friendUserInfo.getQq());
                        mWeiXinTextView.setText(friendUserInfo.getWeixin());
                        mSchoolTextView.setText(friendUserInfo.getSchool());
                        mAddressTextView.setText(friendUserInfo.getAddress());
                        mAgreeCountTextView.setText(friendUserInfo.getUserzan());
                        if (!StringUtils.isBlank(friendUserInfo.getStar())) {
                            if (isNumeric(friendUserInfo.getStar())) {
                                int starPosition = Integer.parseInt(friendUserInfo.getStar());
                                mStarTextView.setText(stars[starPosition]);
                            }
                        }
                    }
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onBefore() {

                }
            });
        } else {
            ToastUtils.show(this, "获取用户信息失败");
        }
    }


    public void addAgree() {
        currentUserInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);

        if (currentUserInfo != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("openid", currentUserInfo.getOpenid());
            params.put("stype", "zan");
            params.put("uid", currentUserInfo.getUid());
            params.put("zanuid", fuid);
            params.put("iszan", "1");

            okHttpRequest.aget(Server.USER_INFO_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    if (materialDialog != null && materialDialog.isShowing()) {
                        materialDialog.dismiss();
                    }
                    UserInfoRet tempInfo = mService.update(response);
                    if (tempInfo != null && tempInfo.errCode == Constant.RESULT_SUCCESS) {
                        ToastUtils.show(FriendInfoActivity.this, "点赞成功");
                        Logger.e("点赞成功");
                        if (!StringUtils.isBlank(friendUserInfo.getUserzan())) {
                            int temp = Integer.parseInt(friendUserInfo.getUserzan()) + 1;
                            mAgreeCountTextView.setText(temp + "");
                        }
                    } else if (tempInfo.errCode == Constant.OPERATION_CODE) {
                        ToastUtils.show(FriendInfoActivity.this, "已经点赞过了");
                        Logger.e("已经点赞过了");
                    } else {
                        ToastUtils.show(FriendInfoActivity.this, "点赞失败,请稍后重试");
                        Logger.e("点赞失败");
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (materialDialog != null && materialDialog.isShowing()) {
                        materialDialog.dismiss();
                    }
                    ToastUtils.show(FriendInfoActivity.this, "点赞失败,请稍后重试");
                    Logger.e("点赞失败");
                }

                @Override
                public void onBefore() {

                }
            });
        } else {
            ToastUtils.show(FriendInfoActivity.this, "请先登录");
        }
    }
}
