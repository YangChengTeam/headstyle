package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.AgreeItemAdapter;
import com.feiyou.headstyle.adapter.ArticlePagerAdapter;
import com.feiyou.headstyle.adapter.CommentAdapter;
import com.feiyou.headstyle.adapter.CommentReplyItemAdapter;
import com.feiyou.headstyle.adapter.HeadWallAdapter;
import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.CommentInfo;
import com.feiyou.headstyle.bean.CommentInfoRet;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.CommentService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.fragment.Show1Fragment;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.GlideHelper;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.ScreenUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.CommentDialog;
import com.feiyou.headstyle.view.NoScrollViewPager;
import com.hwangjr.rxbus.RxBus;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;


public class ArticleDetailActivity extends BaseActivity implements CommentAdapter.AgreeListener, CommentDialog.SendBackListener {

    ListView listView;

    ListView mAgreeListView;

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

    @BindView(R.id.iv_praise)
    ImageView mPraiseImageView;

    @BindView(R.id.praise_layout)
    LinearLayout praiseLayout;

    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;

    @BindView(R.id.show_tabs_layout)
    TabLayout tabLayout;

    private HeadWallAdapter gridViewAdapter;

    public List<HeadInfo> data;

    private CommentAdapter acAdapter;

    ArticleService articleService = null;

    CommentService commentService = null;

    OKHttpRequest okHttpRequest = null;

    private String sid = "1";

    private String cimgs = null;

    private List<CommentInfo> commentData;

    private int iszan;

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    private MaterialDialog loginDialog;

    MaterialDialog materialDialog;

    String userName;

    String gender;

    String imgUrl;

    UserService mService = null;

    private UserInfo userInfo;

    private int showType;

    BottomSheetDialog commitReplyDialog;

    private View replyView;

    private LinearLayout mReplyLayout;

    private TextView mReplyTitleTextView;

    private TextView mReplyCountTextView;

    private ImageView mArticleUserHead;

    private ImageView mCloseImageView;

    private TextView mArticleUserName;

    private TextView mArticleSendTime;

    private RecyclerView mCommentReplyRecyclerView;

    private CommentReplyItemAdapter replyItemAdapter;

    private TextView mAllCommentTextView;

    private TextView mReplySendTextView;

    CommentDialog commentDialog;

    private String oneLevel;

    private String atUserName = "";

    List<View> views;

    List<String> titles;

    private ArticlePagerAdapter pagerAdapter;

    private View commentView;

    private ArticleInfo articleInfo;

    private AgreeItemAdapter agreeItemAdapter;

    private int currentOnePosition = -1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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
            showType = bundle.getInt("show_type");
        }
        mService = new UserService();
        articleService = new ArticleService();
        commentService = new CommentService();
        okHttpRequest = new OKHttpRequest();
    }

    public void initViews() {
        ButterKnife.bind(this);

        commitReplyDialog = new BottomSheetDialog(this);
        replyView = LayoutInflater.from(ArticleDetailActivity.this).inflate(R.layout.comment_reply_view, null);
        mReplyTitleTextView = ButterKnife.findById(replyView, R.id.tv_reply_title);
        mReplyCountTextView = ButterKnife.findById(replyView, R.id.tv_reply_count);
        mArticleUserHead = ButterKnife.findById(replyView, R.id.iv_article_user_head);
        mArticleUserName = ButterKnife.findById(replyView, R.id.tv_article_user_name);
        mCloseImageView = ButterKnife.findById(replyView, R.id.iv_close);
        mArticleSendTime = ButterKnife.findById(replyView, R.id.tv_article_send_time);
        mCommentReplyRecyclerView = ButterKnife.findById(replyView, R.id.rv_reply_list);
        mAllCommentTextView = ButterKnife.findById(replyView, R.id.tv_all_comment);
        mReplySendTextView = ButterKnife.findById(replyView, R.id.tv_reply_send);
        replyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getHeight(this)));
        commitReplyDialog.setContentView(replyView);

        mCommentReplyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        replyItemAdapter = new CommentReplyItemAdapter(this, null);
        mCommentReplyRecyclerView.setAdapter(replyItemAdapter);

        mReplySendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atUserName = "";
                commentDialog = new CommentDialog(ArticleDetailActivity.this, 2);
                commentDialog.setSendBackListener(ArticleDetailActivity.this);
                commentDialog.show(getFragmentManager(), "replyDialog");
            }
        });

        replyItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                atUserName = "//@<font color='#3385ff' size='+1'>" + replyItemAdapter.getData().get(position).nickname + "</font>";
                commentDialog = new CommentDialog(ArticleDetailActivity.this, 2);
                commentDialog.setSendBackListener(ArticleDetailActivity.this);
                commentDialog.show(getFragmentManager(), "replyDialog");
            }
        });

        views = new ArrayList<>();
        titles = new ArrayList<>();

        titles = new ArrayList<String>();
        titles.add("评论");
        titles.add("点赞");

        commentView = View.inflate(this, R.layout.article_comment_list, null);
        View agreeView = View.inflate(this, R.layout.article_agree_list, null);
        listView = ButterKnife.findById(commentView, R.id.comment_list);
        mAgreeListView = ButterKnife.findById(agreeView, R.id.agree_list);

        acAdapter = new CommentAdapter(this, commentData);
        listView.setAdapter(acAdapter);

        agreeItemAdapter = new AgreeItemAdapter(this, null);
        mAgreeListView.setAdapter(agreeItemAdapter);

        views.add(commentView);
        views.add(agreeView);

        pagerAdapter = new ArticlePagerAdapter(views, titles);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        RxView.clicks(userImg).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (articleInfo != null) {
                    Intent intent = new Intent(ArticleDetailActivity.this, FriendInfoActivity.class);
                    intent.putExtra("fuid", articleInfo.uid);
                    startActivity(intent);
                }
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
        acAdapter.setAgreeListener(this);
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

                        articleInfo = articleService.getArticleInfoBySID(response);
                        if (articleInfo != null) {
                            cimgs = articleInfo.cimg;
                            if (cimgs != null) {
                                data = new ArrayList<HeadInfo>();
                                String[] imgs = cimgs.split("\\|");
                                for (int i = imgs.length - 1; i >= 0; i--) {
                                    HeadInfo tempHeadInfo = new HeadInfo();
                                    tempHeadInfo.setHurl(imgs[i]);
                                    data.add(tempHeadInfo);
                                }
                                gridViewAdapter.addItemDatas(data);
                                articlePhotoGridView.setAdapter(gridViewAdapter);

                                articlePhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        Intent intent = new Intent(ArticleDetailActivity.this, ShowImageListActivity.class);
                                        if (showType == 0) {
                                            intent.putStringArrayListExtra("imageList", (ArrayList<String>) Show1Fragment.showImageUrlList);
                                        } else if (showType == 1) {
                                            intent.putStringArrayListExtra("imageList", (ArrayList<String>) Show1Fragment.showFriendsImageUrlList);
                                        } else {
                                            intent.putStringArrayListExtra("imageList", (ArrayList<String>) Show1Fragment.showgGameImageUrlList);
                                        }

                                        intent.putExtra("position", position);
                                        intent.putExtra("current_img_url", data.get(position).getHurl());

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
                            commentCountTv.setText(articleInfo.comment + "");
                            //praiseCountTv.setText(articleInfo.zan + "");
                            iszan = articleInfo.iszan;

                            if (iszan == 0) {
                                mPraiseImageView.setImageResource(R.mipmap.detail_no_zan_icon);
                            } else {
                                mPraiseImageView.setImageResource(R.mipmap.detail_is_zan_icon);
                            }

                            if (articleInfo.sex.equals("1")) {
                                userGender.setImageResource(R.mipmap.boy_icon);
                            } else {
                                userGender.setImageResource(R.mipmap.girl_icon);
                            }

                            if (articleInfo.getZanlist() != null && articleInfo.getZanlist().size() > 0) {
                                agreeItemAdapter.setAgreeList(articleInfo.getZanlist());
                                agreeItemAdapter.notifyDataSetChanged();

                                setListViewHeightBasedOnChildren(mAgreeListView);
                            }

                            titles = new ArrayList<String>();
                            titles.add("评论 " + articleInfo.comment);
                            titles.add("点赞 " + articleInfo.zan);
                            pagerAdapter.setTitiles(titles);
                            pagerAdapter.notifyDataSetChanged();

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
        }, 500);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (commitReplyDialog != null && !commitReplyDialog.isShowing()) {
                    currentOnePosition = position;
                    commitReplyDialog.show();
                    if (acAdapter.getCommentList() != null && acAdapter.getCommentList().size() > 0) {
                        CommentInfo commentInfo = acAdapter.getCommentList().get(position);
                        mArticleUserName.setText(commentInfo.nickname);
                        GlideHelper.circleImageView(ArticleDetailActivity.this, mArticleUserHead, commentInfo.simg, R.mipmap.user_head_def_icon);
                        try {
                            mReplyTitleTextView.setText(Html.fromHtml(URLDecoder.decode(commentInfo.scontent, "UTF-8")));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (commentInfo.commentnum > 0) {
                            mReplyCountTextView.setText(commentInfo.commentnum + "条回复");
                        } else {
                            mReplyCountTextView.setText("暂无回复");
                        }
                        mArticleSendTime.setText(commentInfo.addtime);

                        if (commentInfo.getReplylist() != null && commentInfo.getReplylist().size() > 0) {
                            mAllCommentTextView.setText("全部回复");
                        } else {
                            mAllCommentTextView.setText("暂无回复");
                        }
                        replyItemAdapter.setNewData(commentInfo.getReplylist());
                        //一级评论的ID
                        oneLevel = commentInfo.cid;
                    }
                }
            }
        });


        mAgreeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (agreeItemAdapter.getAgreeList() != null && agreeItemAdapter.getAgreeList().size() > 0) {
                    Intent intent = new Intent(ArticleDetailActivity.this, FriendInfoActivity.class);
                    intent.putExtra("fuid", agreeItemAdapter.getAgreeList().get(position).uid);
                    startActivity(intent);
                }
            }
        });

        mCloseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commitReplyDialog != null && commitReplyDialog.isShowing()) {
                    commitReplyDialog.dismiss();
                }
            }
        });
    }

    public void initCommentData() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("sid", sid);
        params.put("num", "1000");
        params.put("p", "1");
        if (userInfo != null) {
            params.put("uid", userInfo.uid);
        }
        okHttpRequest.aget(Server.COMMENT_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                CommentInfoRet result = commentService.getData(response);
                if (result != null && result.data != null) {
                    List<CommentInfo> temp = result.data;
                    if (temp != null && temp.size() > 0) {
                        acAdapter.setCommentList(temp);
                        acAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(listView);
                        if (currentOnePosition > -1) {
                            replyItemAdapter.setNewData(acAdapter.getCommentList().get(currentOnePosition).getReplylist());
                        }
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() == 0) {
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
        if (listAdapter.getCount() > 3) {
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 150;
        } else {
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 300;
        }

        listView.setLayoutParams(params);

    }

    /**
     * 点击发表评论框
     *
     * @param v
     */
    @OnClick(R.id.layout_bottom)
    public void showDialog(View v) {
        commentDialog = new CommentDialog(ArticleDetailActivity.this, 1);
        commentDialog.setSendBackListener(this);
        commentDialog.show(getFragmentManager(), "dialog");
    }

    @OnClick(R.id.praise_layout)
    public void praise() {
        if (AppUtils.isLogin(this)) {
            if (iszan == 0) {
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
                            mPraiseImageView.setImageResource(R.mipmap.detail_is_zan_icon);
                            RxBus.get().post(Constant.PRAISE_SUCCESS, showType + "");
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
                            if (!StringUtils.isEmpty(response)) {
                                UserInfo userInfo = mService.login(response);
                                if (userInfo != null) {
                                    PreferencesUtils.putObject(ArticleDetailActivity.this, Constant.USER_INFO, userInfo);
                                    App.isLoginAuth = true;

                                    Message message = new Message();
                                    message.what = 0;
                                    handler.sendMessage(message);
                                    RxBus.get().post(Constant.LOGIN_SUCCESS, "loginSuccess");
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

    @Override
    public void agreeComment(final int pos) {

        if (userInfo == null) {
            loginDialog.show();
            return;
        }

        final Map<String, String> params = new HashMap<String, String>();
        params.put("uid", userInfo != null ? userInfo.uid : "");
        params.put("oid", userInfo != null ? userInfo.openid : "");
        params.put("commentid", acAdapter.getCommentList().get(pos).cid);

        okHttpRequest.aget(Server.COMMENT_AGREE_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                Result result = commentService.getAgreeResult(response);
                if (result != null && result.errCode == 0) {
                    Logger.e("agree success--->");
                    //acAdapter.changeView(mLinearLayoutManager.findViewByPosition(pos + 1), pos);
                    acAdapter.getCommentList().get(pos).iszan = 1;
                    acAdapter.getCommentList().get(pos).zan = acAdapter.getCommentList().get(pos).zan + 1;
                    acAdapter.notifyDataSetChanged();
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
    public void sendContent(String content, final int type) {
        if (AppUtils.isLogin(this)) {

            final Map<String, String> params = new HashMap<String, String>();
            params.put("sid", sid);

            if (userInfo != null) {
                params.put("uid", userInfo.uid);
                params.put("oid", userInfo.openid);
            }

            try {
                if (type == 1) {
                    params.put("content", URLEncoder.encode(content, "UTF-8"));
                } else {
                    if (!StringUtils.isEmpty(atUserName)) {
                        content += atUserName;
                    }
                    content = URLEncoder.encode(content, "UTF-8");
                    params.put("content", content);
                    if (!StringUtils.isEmpty(oneLevel)) {
                        params.put("cid", oneLevel);
                    }
                    params.put("stype", "incom");
                }
            } catch (UnsupportedEncodingException e) {
                Logger.e(e.getMessage());
            }

            okHttpRequest.aget(Server.ADD_COMMENT_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    boolean result = commentService.addComment(response);
                    if (result) {
                        if (commentDialog != null) {
                            commentDialog.hideProgressDialog();
                            commentDialog.dismiss();
                        }
                        ToastUtils.show(ArticleDetailActivity.this, "评论成功");
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
                    if (commentDialog != null) {
                        commentDialog.hideProgressDialog();
                        commentDialog.dismiss();
                    }
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
