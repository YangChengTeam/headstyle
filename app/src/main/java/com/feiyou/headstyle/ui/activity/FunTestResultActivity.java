package com.feiyou.headstyle.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.FunMoreTestAdapter;
import com.feiyou.headstyle.adapter.FunTestCommentAdapter;
import com.feiyou.headstyle.bean.FunTestAddCommentInfo;
import com.feiyou.headstyle.bean.FunTestAddCommentInfoRet;
import com.feiyou.headstyle.bean.FunTestCommentInfo;
import com.feiyou.headstyle.bean.FunTestCommentInfoRet;
import com.feiyou.headstyle.bean.FunTestInfo;
import com.feiyou.headstyle.bean.FunTestInfoData;
import com.feiyou.headstyle.bean.FunTestResultInfo;
import com.feiyou.headstyle.bean.FunTestResultListRet;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.FunTestResultService;
import com.feiyou.headstyle.service.FunTestService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.ScreenUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.SharePopupWindow;
import com.hwangjr.rxbus.RxBus;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import okhttp3.Call;
import rx.functions.Action1;

/**
 * Created by admin on 2017/9/14.
 */

public class FunTestResultActivity extends BaseActivity implements FunTestCommentAdapter.AgreeListener {

    @BindView(R.id.back_image)
    ImageView mBackImageView;

    @BindView(R.id.title_text)
    TextView mTitleTextView;

    @BindView(R.id.all_test_comment_list)
    RecyclerView mTestCommentRecyclerView;

    RecyclerView mMoreTestRecyclerView;

    @BindView(R.id.et_comment_content)
    EditText mCommentEditView;

    @BindView(R.id.tv_send_comment)
    TextView mSendCommentTextView;

    LinearLayout mMoreTestLayout;

    TextView mTestResultTextView;

    TextView mFunTestTitleTextView;

    TextView mTestCountTextView;

    TextView mShareCountTextView;

    TextView mChangeMoreTextView;

    Button mShareButton;

    private String testId;

    private int testScore;

    private FunTestResultService mService = null;

    FunTestService mTestService = null;

    OKHttpRequest okHttpRequest = null;

    List<FunTestResultInfo> mFunTestResultInfoList;

    FunTestInfo funTestInfo;

    private String cId;//分类ID

    private String resultId;//测试结果ID

    private UserInfo userInfo;

    //分享弹出窗口
    private SharePopupWindow shareWindow;

    private UMImage image;

    private UMShareAPI mShareAPI = null;

    private String shareTitle = null;

    FunTestCommentAdapter mFunTestCommentAdapter;

    LinearLayoutManager mLinearLayoutManager;

    LinearLayoutManager mMoreLinearLayoutManager;

    FunMoreTestAdapter mFunMoreTestAdapter;

    private int currentPage = 1;

    MaterialDialog materialDialog;

    private MaterialDialog loginDialog;

    SHARE_MEDIA platform = null;

    String userName;

    String gender;

    String imgUrl;

    UserService mUserService = null;

    int moreTestPage;

    int maxPage;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fun_test_result;
    }

    @Override
    protected void initVars() {
        super.initVars();
    }

    private View getHeaderView() {
        View headView = getLayoutInflater().inflate(R.layout.activity_fun_test_result_head, (ViewGroup) mTestCommentRecyclerView.getParent(), false);
        mTestResultTextView = ButterKnife.findById(headView, R.id.tv_test_result);
        mFunTestTitleTextView = ButterKnife.findById(headView, R.id.tv_test_title);
        mTestCountTextView = ButterKnife.findById(headView, R.id.tv_test_count);
        mShareCountTextView = ButterKnife.findById(headView, R.id.tv_share_count);
        mShareButton = ButterKnife.findById(headView, R.id.btn_share_test);
        mMoreTestRecyclerView = ButterKnife.findById(headView, R.id.more_test_list);
        mMoreTestLayout = ButterKnife.findById(headView, R.id.more_test_layout);
        mChangeMoreTextView = ButterKnife.findById(headView, R.id.tv_change_more_test);
        return headView;
    }

    @Override
    public void initViews() {
        super.initViews();
        moreTestPage = App.currentPage;
        okHttpRequest = new OKHttpRequest();
        mTestService = new FunTestService();
        mService = new FunTestResultService();
        mUserService = new UserService();
        mShareAPI = UMShareAPI.get(this);
        image = new UMImage(FunTestResultActivity.this, R.mipmap.logo_100);

        materialDialog = new MaterialDialog.Builder(FunTestResultActivity.this)
                .content("回复中...")
                .progress(true, 0)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.dialog_content_color).build();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            testId = bundle.getString("test_id");
            testScore = bundle.getInt("test_score");
            funTestInfo = (FunTestInfo) bundle.getSerializable("fun_test_data_info");
        }

        mTitleTextView.setText("测试结果");
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMoreLinearLayoutManager = new LinearLayoutManager(this);
        mFunTestCommentAdapter = new FunTestCommentAdapter(this, null);
        mFunTestCommentAdapter.setHeaderView(getHeaderView());
        mTestCommentRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTestCommentRecyclerView.setAdapter(mFunTestCommentAdapter);
        mFunTestCommentAdapter.setAgreeListener(this);
        if (funTestInfo != null) {
            mTestCountTextView.setText(funTestInfo.shareperson + "人参与测试");
            mShareCountTextView.setText(funTestInfo.sharetotal + "人转发");
            mFunTestTitleTextView.setText(funTestInfo.title);
            shareTitle = funTestInfo.sharetitle;
            if (!StringUtils.isEmpty(shareTitle)) {
                if (shareTitle.length() > 11) {
                    String tShareTitle = shareTitle.substring(0, 11);
                    shareTitle = tShareTitle + "......";
                }
            }
        }

        mFunMoreTestAdapter = new FunMoreTestAdapter(this, null);
        mMoreTestRecyclerView.setLayoutManager(mMoreLinearLayoutManager);
        mMoreTestRecyclerView.setAdapter(mFunMoreTestAdapter);

        final Map<String, String> params = new HashMap<String, String>();
        params.put("testid", testId);
        okHttpRequest.aget(Server.FUN_TEST_RESULT_LIST_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {

                FunTestResultListRet result = mService.getData(response);
                if (result != null && result.data != null && result.data.size() > 0) {
                    mFunTestResultInfoList = result.data;

                    for (int i = 0; i < mFunTestResultInfoList.size(); i++) {
                        int startScore;
                        int endScore;
                        String[] range = mFunTestResultInfoList.get(i).resultcount.split(",");

                        if (range.length == 1) {
                            startScore = Integer.parseInt(range[0]);
                            if (testScore == startScore) {
                                resultId = mFunTestResultInfoList.get(i).sid;
                                mTestResultTextView.setText(mFunTestResultInfoList.get(i).resultdesc);
                                break;
                            }
                        }

                        if (range.length == 2) {
                            startScore = Integer.parseInt(range[0]);
                            endScore = Integer.parseInt(range[1]);
                            if (testScore >= startScore && testScore <= endScore) {
                                resultId = mFunTestResultInfoList.get(i).sid;
                                mTestResultTextView.setText(mFunTestResultInfoList.get(i).resultdesc);
                                break;
                            }
                        }
                    }

                    //跟新参与人数
                    updateJoinCount();
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onBefore() {

            }
        });

        RxView.clicks(mChangeMoreTextView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                loadMoreData();
            }
        });

        RxView.clicks(mBackImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });
        RxView.clicks(mShareButton).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) FunTestResultActivity.this.findViewById(android.R.id.content)).getChildAt(0);

                shareWindow = new SharePopupWindow(FunTestResultActivity.this, itemsOnClick);

                shareWindow.showAtLocation(viewGroup, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                setBackgroundAlpha(FunTestResultActivity.this, 0.5f);
                shareWindow.setOnDismissListener(new FunTestResultActivity.PoponDismissListener());
            }
        });

        RxView.clicks(mSendCommentTextView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                if (StringUtils.isEmpty(mCommentEditView.getText())) {
                    ToastUtils.show(FunTestResultActivity.this, "请输入回复内容");
                    return;
                }

                if (userInfo == null) {
                    loginDialog.show();
                    return;
                }

                final Map<String, String> params = new HashMap<String, String>();
                params.put("testid", testId);
                params.put("uid", userInfo != null ? userInfo.uid : "");
                params.put("content", mCommentEditView.getText().toString());

                okHttpRequest.aget(Server.FUN_TEST_ADD_COMMENT_DATA, params, new OnResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }
                        FunTestAddCommentInfoRet result = mService.addCommentData(response);
                        if (result != null && result.data != null && result.data.size() > 0) {

                            FunTestAddCommentInfo addCommentInfo = result.data.get(0);

                            ToastUtils.show(FunTestResultActivity.this, "回复成功");
                            mCommentEditView.setText("");
                            FunTestCommentInfo tempCommentInfo = new FunTestCommentInfo();
                            tempCommentInfo.nickname = userInfo.nickName;
                            tempCommentInfo.simg = userInfo.userimg;
                            tempCommentInfo.zan = tempCommentInfo.zan + 1;
                            tempCommentInfo.addtime = addCommentInfo.addtime;
                            tempCommentInfo.scontent = addCommentInfo.content;
                            mFunTestCommentAdapter.addData(0, tempCommentInfo);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }
                        ToastUtils.show(FunTestResultActivity.this, "回复失败");
                    }

                    @Override
                    public void onBefore() {
                        materialDialog.show();
                    }
                });

            }
        });

        mFunTestCommentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mTestCommentRecyclerView);

        mFunMoreTestAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(FunTestResultActivity.this, FunTestDetailActivity.class);
                intent.putExtra("fun_test_data_info", mFunMoreTestAdapter.getData().get(position));
                startActivity(intent);
                finish();
            }
        });

        initLoginDialog();

        loadMoreData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfo = (UserInfo) PreferencesUtils.getObject(FunTestResultActivity.this, Constant.USER_INFO, UserInfo.class);
        if(userInfo != null) {
            Uri uri = null;
            if (!StringUtils.isBlank(userInfo.getUserimg())) {
                uri = Uri.parse(userInfo.getUserimg());
            }
            io.rong.imlib.model.UserInfo ryUser = new io.rong.imlib.model.UserInfo(userInfo.getUid(), userInfo.getNickName(), uri);
            RongIM.getInstance().setCurrentUserInfo(ryUser);

            RongIM.getInstance().refreshUserInfoCache(ryUser);
        }
    }

    //弹出窗口监听消失
    public class PoponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            setBackgroundAlpha(FunTestResultActivity.this, 1f);
        }
    }

    public void updateJoinCount() {

        final Map<String, String> params = new HashMap<String, String>();
        params.put("stype", "0");
        params.put("testid", testId);
        params.put("cid", funTestInfo.cid+"");


        if (userInfo != null) {
            params.put("uid", userInfo != null ? userInfo.uid : "");
        }

        if (!StringUtils.isEmpty(resultId)) {
            params.put("resultid", resultId);
        }

        okHttpRequest.aget(Server.FUN_TEST_COUNT_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {

                Result result = mService.getUpdateResult(response);
                if (result != null && result.errCode == 0) {
                    Logger.e("update use count success--->");
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

    @Override
    public void loadData() {
        super.loadData();
        getCommentList();
    }

    public void getCommentList() {
        userInfo = (UserInfo) PreferencesUtils.getObject(FunTestResultActivity.this, Constant.USER_INFO, UserInfo.class);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("uid", userInfo != null ? userInfo.uid : "");
        params.put("testid", testId);
        params.put("act", "sel");
        params.put("p", currentPage + "");
        params.put("num", "20");

        okHttpRequest.aget(Server.FUN_TEST_COMMENT_LIST_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {

                FunTestCommentInfoRet result = mService.getCommentData(response);
                if (result != null && result.data != null && result.data.size() > 0) {

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPxInt(FunTestResultActivity.this, 190));
                    int margin = ScreenUtils.dpToPxInt(FunTestResultActivity.this, 4);
                    layoutParams.setMargins(0, 0, 0, margin * 2);
                    mMoreTestLayout.setLayoutParams(layoutParams);

                    if (currentPage == 1) {
                        mFunTestCommentAdapter.setNewData(result.data);
                    } else {
                        mFunTestCommentAdapter.addData(result.data);
                    }

                    if (result.data.size() == 20) {
                        currentPage++;
                        mFunTestCommentAdapter.loadMoreComplete();
                    } else {
                        mFunTestCommentAdapter.loadMoreEnd();
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
                    new ShareAction(FunTestResultActivity.this)
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
                    new ShareAction(FunTestResultActivity.this)
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
                    new ShareAction(FunTestResultActivity.this)
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
                    new ShareAction(FunTestResultActivity.this)
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
            Toast.makeText(FunTestResultActivity.this, " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(FunTestResultActivity.this, " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(FunTestResultActivity.this, " 分享取消了", Toast.LENGTH_SHORT).show();
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
        params.put("cid", funTestInfo.cid+"");

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

    @Override
    public void agreeComment(final int pos) {

        if (userInfo == null) {
            loginDialog.show();
            return;
        }

        final Map<String, String> params = new HashMap<String, String>();
        params.put("uid", userInfo != null ? userInfo.uid : "");
        params.put("oid", userInfo != null ? userInfo.openid : "");
        params.put("commentid", mFunTestCommentAdapter.getData().get(pos).commentid);

        okHttpRequest.aget(Server.FUN_TEST_AGREE_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                Result result = mService.getAgreeResult(response);
                if (result != null && result.errCode == 0) {
                    Logger.e("agree success--->");
                    mFunTestCommentAdapter.changeView(mLinearLayoutManager.findViewByPosition(pos + 1), pos);
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
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(FunTestResultActivity.this, platform, umAuthListener);
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //Toast.makeText(this, "Authorize succeed", Toast.LENGTH_SHORT).show();

            mShareAPI.getPlatformInfo(FunTestResultActivity.this, platform, umAuthUserInfoListener);

            materialDialog = new MaterialDialog.Builder(FunTestResultActivity.this)
                    .content("获取用户信息，请稍后...")
                    .progress(true, 0)
                    .backgroundColorRes(R.color.white)
                    .contentColorRes(R.color.dialog_content_color).build();
            materialDialog.show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(FunTestResultActivity.this, "登录授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(FunTestResultActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
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

                            UserInfo userInfo = mUserService.login(response);
                            if (userInfo != null) {
                                ToastUtils.show(FunTestResultActivity.this, "登录成功");
                                //根据头像的地址下载用户头像到本地
                                if (!StringUtils.isEmpty(imgUrl)) {
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

                                PreferencesUtils.putObject(FunTestResultActivity.this, Constant.USER_INFO, userInfo);
                                App.isLoginAuth = true;
                                onResume();
                                App.connect(userInfo.getUsertoken());
                                RxBus.get().post(Constant.LOGIN_SUCCESS,"loginSuccess");
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(FunTestResultActivity.this, R.string.get_user_info_fail);
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
            ToastUtils.show(FunTestResultActivity.this, R.string.get_user_info_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show(FunTestResultActivity.this, R.string.get_user_info_cancel);
        }
    };

    public void loadMoreData() {
        moreTestPage++;
        final Map<String, String> params = new HashMap<String, String>();
        params.put("p", String.valueOf(moreTestPage));
        params.put("num", "5");
        params.put("israndom", "1");
        if (App.typeId > 0) {
            params.put("cid", App.typeId + "");
        }
        okHttpRequest.aget(Server.FUN_TEST_LIST_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {

                FunTestInfoData result = mTestService.getData(response);
                if (result != null && result.maxpage > 0) {
                    maxPage = result.maxpage;
                }

                if (result != null && result.data != null && result.getData().size() > 0 && moreTestPage <= maxPage) {
                    mFunMoreTestAdapter.setNewData(result.data);
                } else {
                    ToastUtils.show(FunTestResultActivity.this, "没有更多数据了");
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
