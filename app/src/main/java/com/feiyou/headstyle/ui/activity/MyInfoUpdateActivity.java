package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.feiyou.headstyle.util.KeyboardUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by admin on 2017/11/23.
 */

public class MyInfoUpdateActivity extends BaseActivity {

    @BindView(R.id.title_text)
    TextView mTitleTextView;

    @BindView(R.id.iv_delete)
    ImageView mDeleteImageView;

    @BindView(R.id.et_update_value)
    EditText mUpdateEditText;

    @BindView(R.id.tv_save)
    TextView mSaveTextView;

    private int updateType = 1;//1修改昵称2修改签名3修改qq4修改微信5修改学习6修改地址

    private String titleName;

    OKHttpRequest okHttpRequest = null;

    private UserService mService = null;

    MaterialDialog materialDialog;

    private UserInfo userInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_info_update;
    }

    @Override
    public void initViews() {
        super.initViews();

        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            updateType = bundle.getInt("update_type", 1);
            switch (updateType) {
                case 1:
                    titleName = "修改昵称";
                    mUpdateEditText.setText(userInfo != null ? userInfo.getNickName() : "");
                    break;
                case 2:
                    titleName = "修改签名";
                    mUpdateEditText.setText(userInfo != null ? userInfo.getSign() : "");
                    mUpdateEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                    break;
                case 3:
                    titleName = "修改QQ";
                    mUpdateEditText.setText(userInfo != null ? userInfo.getQq() : "");
                    break;
                case 4:
                    titleName = "修改微信";
                    mUpdateEditText.setText(userInfo != null ? userInfo.getWeixin() : "");
                    break;
                case 5:
                    titleName = "修改学校";
                    mUpdateEditText.setText(userInfo != null ? userInfo.getSchool() : "");
                    break;
                case 6:
                    titleName = "修改地址";
                    mUpdateEditText.setText(userInfo != null ? userInfo.getAddress() : "");
                    break;
                default:
                    titleName = "修改";
                    break;
            }
            mTitleTextView.setText(titleName);
        }

        mService = new UserService();
        okHttpRequest = new OKHttpRequest();

        materialDialog = new MaterialDialog.Builder(this)
                .content("正在修改，请稍后...")
                .progress(true, 0)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.dialog_content_color).build();

        RxView.clicks(mDeleteImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mUpdateEditText.setText("");
            }
        });

        RxView.clicks(mSaveTextView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if(StringUtils.isEmpty(mUpdateEditText.getText())){
                    ToastUtils.show(MyInfoUpdateActivity.this,"请输入修改值");
                    return;
                }

                KeyboardUtils.hideSoftInput(MyInfoUpdateActivity.this);

                updateInfo(updateType);
            }
        });
    }


    public void updateInfo(int type) {
        if (userInfo != null) {

            Map<String, String> params = new HashMap<String, String>();
            params.put("action", "userinfo");
            params.put("openid", userInfo.getOpenid());
            params.put("stype", "edit");
            params.put("uid", userInfo.getUid());

            switch (updateType) {
                case 1:
                    params.put("nickName",mUpdateEditText.getText().toString());
                    break;
                case 2:
                    params.put("sign",mUpdateEditText.getText().toString());
                    break;
                case 3:
                    params.put("qq",mUpdateEditText.getText().toString());
                    break;
                case 4:
                    params.put("weixin",mUpdateEditText.getText().toString());
                    break;
                case 5:
                    params.put("school",mUpdateEditText.getText().toString());
                    break;
                case 6:
                    params.put("address",mUpdateEditText.getText().toString());
                    break;
                default:
                    break;
            }

            okHttpRequest.apost(Server.UPDATE_USER_INFO_DATA, params,new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    if (materialDialog != null && materialDialog.isShowing()) {
                        materialDialog.dismiss();
                    }
                    UserInfoRet tempInfo = mService.update(response);
                    if (tempInfo != null) {
                        if(tempInfo != null && tempInfo.errCode == Constant.RESULT_SUCCESS) {
                            ToastUtils.show(MyInfoUpdateActivity.this, "修改成功");
                            Logger.e("修改成功");
                            finish();
                        }else{
                            ToastUtils.show(MyInfoUpdateActivity.this, tempInfo.message);
                            Logger.e("修改失败--->" + tempInfo.message);
                        }
                    } else {
                        ToastUtils.show(MyInfoUpdateActivity.this, "修改失败,请稍后重试");
                        Logger.e("修改失败");
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (materialDialog != null && materialDialog.isShowing()) {
                        materialDialog.dismiss();
                    }
                    ToastUtils.show(MyInfoUpdateActivity.this, "修改失败,请稍后重试");
                    Logger.e("修改失败");
                }

                @Override
                public void onBefore() {

                }
            });

        }
    }


}
