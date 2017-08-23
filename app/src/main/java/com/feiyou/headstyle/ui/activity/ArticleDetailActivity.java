package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.CommentAdapter;
import com.feiyou.headstyle.adapter.HeadWallAdapter;
import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.CommentInfo;
import com.feiyou.headstyle.bean.CommentInfoRet;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.CommentService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ArticleDetailActivity extends SwipeBackActivity {

    @BindView(R.id.comment_list)
    ListView listView;

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

    @BindView(R.id.praise_layout)
    LinearLayout praiseLayout;

    @BindView(R.id.comment_content)
    EditText commentContent;

    @BindView(R.id.send_btn)
    TextView sendTv;

    private HeadWallAdapter gridViewAdapter;

    public List<HeadInfo> data;

    private CommentAdapter articleCommentListAdapter;

    private SwipeBackLayout mSwipeBackLayout;

    ArticleService articleService = null;

    CommentService commentService = null;

    OKHttpRequest okHttpRequest = null;

    private String sid = "1";

    private String cimgs = null;

    private List<CommentInfo> commentData;

    private String iszan = "";

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    private MaterialDialog loginDialog;

    MaterialDialog materialDialog;

    String userName;

    String gender;

    String imgUrl;

    UserService mService = null;

    private UserInfo userInfo;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    ToastUtils.show(ArticleDetailActivity.this, "登录成功");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initVars();
        initViews();
        initData();
        //initCommentData();
        loadData();
    }

    public int getLayoutId() {
        return R.layout.article_detail;
    }

    protected void initVars() {

        data = new ArrayList<HeadInfo>();
        commentData = new ArrayList<CommentInfo>();
        gridViewAdapter = new HeadWallAdapter(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.getString("sid") != null) {
            sid = bundle.getString("sid");
        }
        mService = new UserService();
        articleService = new ArticleService();
        commentService = new CommentService();
        okHttpRequest = new OKHttpRequest();
    }

    public void initViews() {
        ButterKnife.bind(this);
        articleCommentListAdapter = new CommentAdapter(this, commentData);
        listView.setAdapter(articleCommentListAdapter);
        mSwipeBackLayout = getSwipeBackLayout();

        //mSwipeBackLayout.setSwipeMode(SwipeBackLayout.FULL_SCREEN_LEFT);//作用:可实现全屏滑动返回上一页(通过修改源码实现)
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
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
                mShareAPI.doOauthVerify(ArticleDetailActivity.this, platform, umAuthListener);
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    public void initData() {

        mShareAPI = UMShareAPI.get(ArticleDetailActivity.this);
        initLoginDialog();

        if (AppUtils.isLogin(this)) {
            userInfo = (UserInfo) PreferencesUtils.getObject(ArticleDetailActivity.this, Constant.USER_INFO, UserInfo.class);
        }

        materialDialog = new MaterialDialog.Builder(ArticleDetailActivity.this)
                .content("加载中...")
                .progress(true, 0)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.dialog_content_color).build();
        materialDialog.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("sid", sid);
                if (userInfo != null) {
                    params.put("uid", userInfo.uid);
                }
                okHttpRequest.aget(Server.ARTICLE_DETAIL_DATA, params, new OnResponseListener() {
                    @Override
                    public void onSuccess(String response) {

                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }

                        ArticleInfo articleInfo = articleService.getArticleInfoBySID(response);
                        if (articleInfo != null) {
                            cimgs = articleInfo.cimg;
                            if (cimgs != null) {
                                data = new ArrayList<HeadInfo>();
                                String[] imgs = cimgs.split("\\|");
                                for (int i = 0; i < imgs.length; i++) {
                                    HeadInfo tempHeadInfo = new HeadInfo();
                                    tempHeadInfo.hurl = imgs[i];
                                    data.add(tempHeadInfo);
                                }
                                gridViewAdapter.addItemDatas(data);
                                articlePhotoGridView.setAdapter(gridViewAdapter);

                                articlePhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        //ToastUtils.show(mContext,"选择图片---"+position);

                                        Intent intent = new Intent(ArticleDetailActivity.this, ShowImageListActivity.class);

                                        //String imageList = "http://pic.qqtn.com/up/2016-9/2016090611122547066.jpg,http://pic.qqtn.com/up/2016-9/14749389135118192.jpg,http://pic.qqtn.com/up/2016-6/2016062917512971884.jpg";
                                        intent.putExtra("imageList", cimgs);
                                        intent.putExtra("position", position);
                                        ArticleDetailActivity.this.startActivity(intent);
                                        //进入图片浏览时的动画
                                        ArticleDetailActivity.this.overridePendingTransition(R.anim.image_show_in, R.anim.image_show_out);
                                    }
                                });

                            }

                            userNameTv.setText(articleInfo.nickname);
                            Uri uri = Uri.parse(articleInfo.simg);
                            userImg.setImageURI(uri);
                            articleSendTimeTv.setText(articleInfo.addtime);
                            articleTitleTv.setText(articleInfo.scontent);
                            commentCountTv.setText(articleInfo.comment+"");
                            praiseCountTv.setText(articleInfo.zan);
                            iszan = articleInfo.iszan;

                            if (iszan.equals("0")) {
                                Drawable noZanDrawable = ContextCompat.getDrawable(ArticleDetailActivity.this, R.mipmap.no_zan_icon);
                                noZanDrawable.setBounds(0, 0, noZanDrawable.getMinimumWidth(), noZanDrawable.getMinimumHeight());
                                praiseCountTv.setCompoundDrawables(noZanDrawable, null, null, null);
                            } else {
                                Drawable isZanDrawable = ContextCompat.getDrawable(ArticleDetailActivity.this, R.mipmap.is_zan_icon);
                                isZanDrawable.setBounds(0, 0, isZanDrawable.getMinimumWidth(), isZanDrawable.getMinimumHeight());
                                praiseCountTv.setCompoundDrawables(isZanDrawable, null, null, null);
                            }

                            if (articleInfo.sex.equals("1")) {
                                userGender.setImageResource(R.mipmap.boy_icon);
                            } else {
                                userGender.setImageResource(R.mipmap.girl_icon);
                            }
                            initCommentData();
                        }
                    }

                    @Override
                    public void onBefore() {

                    }

                    @Override
                    public void onError(Exception e) {
                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }
                    }
                });
            }
        },500);
    }

    public void initCommentData() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("sid", sid);

        okHttpRequest.aget(Server.COMMENT_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                CommentInfoRet result = commentService.getData(response);
                if (result != null && result.data != null) {
                    List<CommentInfo> temp = result.data;
                    if (temp != null && temp.size() > 0) {
                        if (commentData != null && commentData.size() > 0) {
                            commentData.clear();
                        }
                        commentData.addAll(temp);

                        articleCommentListAdapter.notifyDataSetChanged();
                        listView.setAdapter(articleCommentListAdapter);
                        //loadData();
                        setListViewHeightBasedOnChildren(listView);
                    }
                }
            }

            @Override
            public void onBefore() {

            }

            @Override
            public void onError(Exception e) {
                if (materialDialog != null && materialDialog.isShowing()) {
                    materialDialog.dismiss();
                }
            }
        });
    }

    public void loadData() {
        //设置评论列表高度
        setListViewHeightBasedOnChildren(listView);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 150;
        listView.setLayoutParams(params);
    }

    @OnClick(R.id.praise_layout)
    public void praise() {
        if (AppUtils.isLogin(this)) {
            if (iszan.equals("0")) {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("sid", sid);

                if (userInfo != null) {
                    params.put("uid", userInfo.uid);
                    params.put("oid", userInfo.openid);
                }

                okHttpRequest.aget(Server.UP_ZAN_DATA, params, new OnResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        boolean result = articleService.praise(response);
                        if (result) {
                            Drawable isZanDrawable = ContextCompat.getDrawable(ArticleDetailActivity.this, R.mipmap.is_zan_icon);
                            isZanDrawable.setBounds(0, 0, isZanDrawable.getMinimumWidth(), isZanDrawable.getMinimumHeight());
                            praiseCountTv.setCompoundDrawables(isZanDrawable, null, null, null);

                        } else {
                            ToastUtils.show(ArticleDetailActivity.this, "操作失败，请稍后重试");
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
                ToastUtils.show(ArticleDetailActivity.this, "已点过赞了");
            }
        } else {
            loginDialog.show();
        }
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //Toast.makeText(this, "Authorize succeed", Toast.LENGTH_SHORT).show();

            ToastUtils.show(ArticleDetailActivity.this, "授权成功");

            mShareAPI.getPlatformInfo(ArticleDetailActivity.this, platform, umAuthUserInfoListener);

            materialDialog = new MaterialDialog.Builder(ArticleDetailActivity.this)
                    .content("获取用户信息，请稍后...")
                    .progress(true, 0)
                    .backgroundColorRes(R.color.white)
                    .contentColorRes(R.color.dialog_content_color).build();
            materialDialog.show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(ArticleDetailActivity.this, "登录授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(ArticleDetailActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
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
                            if(!StringUtils.isEmpty(response)){
                                UserInfo userInfo = mService.login(response);
                                if (userInfo != null) {
                                    PreferencesUtils.putObject(ArticleDetailActivity.this, Constant.USER_INFO, userInfo);
                                    HeadStyleApplication.isLoginAuth = true;

                                    Message message = new Message();
                                    message.what = 0;
                                    handler.sendMessage(message);
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(ArticleDetailActivity.this, R.string.get_user_info_fail);
                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }
                            //ToastUtils.show(ArticleDetailActivity.this, "登录失败，请稍后重试");
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
            ToastUtils.show(ArticleDetailActivity.this, R.string.get_user_info_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show(ArticleDetailActivity.this, R.string.get_user_info_cancel);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.send_btn)
    public void sendComment() {
        if (AppUtils.isLogin(this)) {

            final Map<String, String> params = new HashMap<String, String>();
            params.put("sid", sid);

            if (userInfo != null) {
                params.put("uid", userInfo.uid);
                params.put("oid", userInfo.openid);
            }

            if (StringUtils.isEmpty(commentContent.getText())) {
                ToastUtils.show(ArticleDetailActivity.this, R.string.comment_is_not_null_text);
                return;
            }

            params.put("content", commentContent.getText().toString());

            okHttpRequest.aget(Server.ADD_COMMENT_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    boolean result = commentService.addComment(response);
                    if (result) {
                        ToastUtils.show(ArticleDetailActivity.this, "评论成功");
                        commentContent.setText("");
                        //评论数+1
                        if (!StringUtils.isEmpty(commentCountTv.getText())) {
                            int tempCount = Integer.parseInt(commentCountTv.getText().toString()) + 1;
                            commentCountTv.setText(tempCount + "");
                        }
                        initCommentData();
                    } else {
                        ToastUtils.show(ArticleDetailActivity.this, "操作失败，请稍后重试");
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
            loginDialog.show();
        }
    }
}
