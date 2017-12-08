package com.feiyou.headstyle.ui.fragment;


import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.SimpleFragmentPagerAdapter;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.interfaces.CustomWebViewDelegate;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.activity.MainActivity;
import com.feiyou.headstyle.ui.activity.ShowAddActivity;
import com.feiyou.headstyle.ui.fragment.subfragment.AllArticleFragment;
import com.feiyou.headstyle.ui.fragment.subfragment.FriendsFragment;
import com.feiyou.headstyle.ui.fragment.subfragment.PhotosFragment;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.GlideHelper;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.MenuPopupWindow;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class Show1Fragment extends BaseFragment implements CustomWebViewDelegate {

    @BindView(R.id.user_img)
    ImageView userImg;

    @BindView(R.id.top_layout)
    RelativeLayout topLayout;

    @BindView(R.id.show_add_icon)
    ImageView showAddIv;

    private SimpleFragmentPagerAdapter pagerAdapter;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.show_tabs_layout)
    TabLayout tabLayout;

    @BindView(R.id.menu_layout)
    LinearLayout menuLayout;

    @BindView(R.id.menu_item_layout)
    RelativeLayout menuItemLayout;

    @BindView(R.id.game_frends_tv)
    TextView gameFriendsTv;

    @BindView(R.id.qq_friend_tv)
    TextView qqFriendTv;

    @BindView(R.id.ground_view)
    View groundView;

    public static List<String> showImageUrlList;

    public static List<String> showFriendsImageUrlList;

    public static List<String> showgGameImageUrlList;

    private List<BaseFragment> fragments;

    //菜单选择弹出窗口
    private MenuPopupWindow menuPopupWindow;

    private int[] location;

    private float maxSize = 0;

    //子菜单打开，关闭时的动画
    private Animation qq_friend_in, take_photo_in, qq_friend_out, take_photo_out;

    float lastSize = 0;

    public boolean isShow = false;

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    private MaterialDialog loginDialog;

    MaterialDialog materialDialog;

    String userName;

    String gender;

    String imgUrl;

    UserService mService = null;

    OKHttpRequest okHttpRequest = null;

    private boolean isAdd = false;

    private UserInfo userInfo;

    public Show1Fragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_show1;
    }

    @Override
    public void initVars() {
        super.initVars();
        mService = new UserService();
        okHttpRequest = new OKHttpRequest();
    }

    @Override
    public void initViews() {
        super.initViews();

        Bundle hotBundle = new Bundle();
        hotBundle.putString("type","0");

        Bundle qBundle = new Bundle();
        qBundle.putString("type","1");

        Bundle sBundle = new Bundle();
        sBundle.putString("type","2");

        showImageUrlList = new ArrayList<String>();//图片浏览时，全局变量
        showFriendsImageUrlList = new ArrayList<String>();//Q友圈
        showgGameImageUrlList = new ArrayList<String>();//游戏圈
        
        fragments = new ArrayList<BaseFragment>();

        fragments.add(new AllArticleFragment());
        fragments.add(new FriendsFragment());
        fragments.add(new PhotosFragment());

        pagerAdapter = new SimpleFragmentPagerAdapter(((FragmentActivity) getActivity()).getSupportFragmentManager(), getActivity(), fragments);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        location = new int[]{1000, 0};
        maxSize = 2.5f * 15;

        qq_friend_in = AnimationUtils.loadAnimation(getActivity(), R.anim.qq_friend_in);
        take_photo_in = AnimationUtils.loadAnimation(getActivity(), R.anim.take_photo_in);
        qq_friend_out = AnimationUtils.loadAnimation(getActivity(), R.anim.qq_friend_out);
        take_photo_out = AnimationUtils.loadAnimation(getActivity(), R.anim.take_photo_out);

        menuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Logger.e(tab.getText()+"---"+tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        qqFriendTv.startAnimation(qq_friend_out);
        gameFriendsTv.startAnimation(take_photo_out);

        animationClose.setDuration(500);
        animationClose.setInterpolator(new DecelerateInterpolator());
        animationClose.setFillAfter(true);
        groundView.startAnimation(animationClose);
        isShow = false;
    }

    @Override
    public void loadData() {
        super.loadData();
        mShareAPI = UMShareAPI.get(getActivity());
        initLoginDialog();
    }

    /**
     * 初始化登录框
     */
    public void initLoginDialog() {
        //弹出QQ登录询问框
        loginDialog = DialogUtils.createDialog(getActivity(), R.string.to_login_text, R.string.cancel_login_text, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                //直接登录QQ
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(getActivity(), platform, umAuthListener);
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
            //Toast.makeText(getActivity(), "Authorize succeed", Toast.LENGTH_SHORT).show();

            mShareAPI.getPlatformInfo(getActivity(), platform, umAuthUserInfoListener);

            materialDialog = new MaterialDialog.Builder(getActivity())
                    .content("获取用户信息，请稍后...")
                    .progress(true, 0)
                    .backgroundColorRes(R.color.white)
                    .contentColorRes(R.color.dialog_content_color).build();
            materialDialog.show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getActivity(), "登录授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getActivity(), "授权取消", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        isAdd = false;

        if (AppUtils.isLogin(getActivity())) {
            userInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
            if (userInfo != null) {
                GlideHelper.circleImageView(getActivity(), userImg, userInfo.getUserimg(), R.mipmap.user_head_def_icon);
            }
        }
    }

    @OnClick(R.id.show_add_icon)
    void toShowAdd(View view) {
       /* menuPopupWindow = new MenuPopupWindow(getActivity(), itemsOnClick);
        menuPopupWindow.showAsDropDown(topLayout, ScreenUtils.getWidth(getActivity()),0);
        menuPopupWindow.setOutsideTouchable(false);*/

        if (menuLayout.getVisibility() == View.INVISIBLE) {
            menuLayout.setVisibility(View.VISIBLE);
            menuLayout.setFocusable(true);
            menuLayout.setClickable(true);

            animationOpen.setDuration(600);
            animationOpen.setInterpolator(new DecelerateInterpolator());
            animationOpen.setFillAfter(true);
            groundView.startAnimation(animationOpen);

            qqFriendTv.startAnimation(qq_friend_in);
            gameFriendsTv.startAnimation(take_photo_in);

            isShow = true;
            //showAddIv.setClickable(false);
        } else {
            closeMenu();
        }
    }

    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.take_photo_tv:
                    if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
                        menuPopupWindow.closePopwindow();
                        ToastUtils.show(getActivity(), "自拍");
                    }
                    break;
                case R.id.chat_tv:
                    if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
                        menuPopupWindow.closePopwindow();
                        ToastUtils.show(getActivity(), "闲聊");
                    }
                    break;
                default:
                    break;
            }
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
                                Logger.e("我秀-登录成功");

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

                                PreferencesUtils.putObject(getActivity(), Constant.USER_INFO, userInfo);
                                App.isLoginAuth = true;
                                RxBus.get().post(Constant.LOGIN_SUCCESS,"loginSuccess");
                                if (isAdd) {
                                    Intent intent = new Intent(getActivity(), ShowAddActivity.class);
                                    startActivity(intent);
                                } else {
                                    onResume();
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(getActivity(), R.string.get_user_info_fail);
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
            ToastUtils.show(getActivity(), R.string.get_user_info_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show(getActivity(), R.string.get_user_info_cancel);
        }
    };

    @OnClick(R.id.user_img)
    public void userInfo() {
        ((MainActivity) getActivity()).setCurrentTab(4);
    }

    @Override
    public void initWithUrl(String url) {

    }

    @Override
    public void reload() {
    }

    @Override
    public void search(String searchKey) {
    }

    @Override
    public void showDetail(String serverUid, String serverOid, String sid) {
    }


    @Override
    public void showImage(String imageList, String position) {

    }

    @Override
    public void isNotLogin() {
    }

    @Override
    public void addArticle() {

    }

    private Animation animationOpen = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float size = (maxSize - 1) * interpolatedTime + 1;
            lastSize = size;
            Matrix matrix = t.getMatrix();

            Logger.e("平移---" + (location[0] - groundView.getWidth() / 2) + "---" + (location[1] - groundView.getHeight() / 2));

            matrix.postTranslate(location[0] - groundView.getWidth() / 2, location[1] - groundView.getHeight() / 2);
            matrix.postScale(size, size, location[0], location[1]);
            if(interpolatedTime == 1){

            }
        }
    };

    private Animation animationClose = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            Logger.e("interpolatedTime---" + interpolatedTime);
            float size = lastSize - (maxSize - 1) * interpolatedTime + 1;
            Matrix matrix = t.getMatrix();
            matrix.postTranslate(location[0] - groundView.getWidth() / 2, location[1] - groundView.getHeight() / 2);
            matrix.postScale(size, size, location[0], location[1]);
            if (interpolatedTime == 1) {
                menuLayout.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (menuLayout.getVisibility() == View.VISIBLE) {
            menuLayout.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.qq_friend_tv)
    public void qqFriend(){
        addArticle("1");
    }

    @OnClick(R.id.game_frends_tv)
    public void takePhoto(){
        addArticle("3");
    }

    public void addArticle(String stype){
        isAdd = true;
        if (AppUtils.isLogin(getActivity())) {
            Intent intent = new Intent(getActivity(), ShowAddActivity.class);
            intent.putExtra("stype", stype);
            startActivity(intent);
        } else {
            loginDialog.show();
        }
    }
}
