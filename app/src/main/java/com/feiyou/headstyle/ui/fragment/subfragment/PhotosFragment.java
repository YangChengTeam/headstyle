package com.feiyou.headstyle.ui.fragment.subfragment;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.ArticleListAdapter;
import com.feiyou.headstyle.adapter.PhotosListAdapter;
import com.feiyou.headstyle.adapter.PhotosNewListAdapter;
import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.activity.ArticleDetailActivity;
import com.feiyou.headstyle.ui.fragment.BaseFragment;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.orhanobut.logger.Logger;
import com.qq.e.ads.cfg.MultiProcessFlag;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;
import com.qq.e.comm.util.GDTLogger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.finalteam.galleryfinal.adapter.PhotoListAdapter;
import io.rong.imkit.RongIM;

public class PhotosFragment extends BaseFragment implements PhotosListAdapter.LoginShowListener, SwipeRefreshLayout.OnRefreshListener,NativeExpressAD.NativeExpressADListener,ArticleListAdapter.OnRecyclerViewItemClickListener{

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.article_recyclerview_list)
    RecyclerView mRecyclerView;

    private PhotosListAdapter mAdapter;

    private List<Object> articleInfoList;

    private int pageNum = 1;

    OKHttpRequest okHttpRequest = null;

    ArticleService articleService = null;

    private MaterialDialog loginDialog;

    MaterialDialog materialDialog;

    private UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform = null;

    String userName;

    String gender;

    String imgUrl;

    UserService mService = null;

    private UserInfo userInfo;

    public String showType = "3";

    private int maxPage = 0;

    private int lastItemPosition = -1;

    public int pageSize = 10;

    public static final int AD_COUNT = 10;// 加载广告的条数，取值范围为[1, 10]

    public static int FIRST_AD_POSITION = 1; // 第一条广告的位置

    private List<NativeExpressADView> mAdViewList;

    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<>();

    public static final String TAG = "GAME";

    public PhotosFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_all_article;
    }

    @Override
    public void initVars() {
        super.initVars();
        articleInfoList = new ArrayList<Object>();
    }

    @Override
    public void initViews() {
        super.initViews();

        swipeLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);
        swipeLayout.setOnRefreshListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mService = new UserService();
        articleService = new ArticleService();
        okHttpRequest = new OKHttpRequest();

        mAdapter = new PhotosListAdapter(getActivity(), articleInfoList,mAdViewPositionMap);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setShowListener(this);

        mAdapter.setOnItemClickListener(new PhotosListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int data) {
                lastItemPosition = data;
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("show_type", 3);
                intent.putExtra("sid", ((ArticleInfo)mAdapter.getArticleData().get(data)).sid);
                startActivity(intent);
            }
        });

//        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                lastItemPosition = position;
//                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
//                intent.putExtra("show_type", 0);
//                intent.putExtra("sid", ((ArticleInfo)articleInfoList.get(position)).sid);
//                startActivity(intent);
//            }
//        });

//        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                pageNum++;
//                if (pageNum <= maxPage) {
//                    getNextData();
//                }
//            }
//        },mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(mRecyclerView.canScrollVertically(1)){
                    Log.i(TAG, "direction 1: true");
                }else {
                    //Log.i(TAG, "direction 1: false");//滑动到底部
                    pageNum++;
                    if (pageNum <= maxPage) {
                        getNextData();
                    }
                }

            }
        });

        if (AppUtils.isLogin(getActivity())) {
            userInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
        }

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

    public Map<String, String> setParams() {
        Map<String, String> params = new HashMap<String, String>();
        Logger.e("init page---" + pageNum);
        params.put("p", String.valueOf(pageNum));
        params.put("t", showType);
        params.put("ver", AppUtils.getVersionName(getActivity()));//版本号
        params.put("num", "10");//每页条数
        //params.put("state", "1");
        return params;
    }

    @Override
    public void loadData() {
        super.loadData();

        final Map<String, String> params = setParams();

        Logger.e("loadData---showtype---" + showType);

        if (userInfo != null) {
            params.put("uid", userInfo.uid);
            params.put("oid", userInfo.openid);
        }

        okHttpRequest.aget(Server.ARTICLE_ALL_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                swipeLayout.setRefreshing(false);
                //设置首页列表数据
                List<ArticleInfo> temp = articleService.getData(response).data;
                if (temp != null && temp.size() > 0) {
                    if (articleInfoList != null && articleInfoList.size() > 0) {
                        articleInfoList.clear();
                    }
                    maxPage = Integer.parseInt(temp.get(0).maxpage);
                    //articleInfoList.addAll(temp);
                    mAdapter.addNewDatas(new ArrayList<Object>(temp));
                    mAdapter.notifyDataSetChanged();
//                    if(temp.size() <10 ){
//                        mAdapter.loadMoreEnd();
//                    }else{
//                        mAdapter.loadMoreComplete();
//                    }
                    initNativeExpressAD();
                }else{
                    //mAdapter.loadMoreEnd();
                }
            }

            @Override
            public void onError(Exception e) {
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onBefore() {
            }
        });
    }

    public void getNextData() {
        final Map<String, String> params = setParams();
        swipeLayout.setRefreshing(true);
        Logger.e("next---showtype---" + showType);

        if (userInfo != null) {
            params.put("uid", userInfo.uid);
            params.put("oid", userInfo.openid);
        }
        okHttpRequest.aget(Server.ARTICLE_ALL_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                swipeLayout.setRefreshing(false);
                //设置首页列表数据
                List<ArticleInfo> temp = articleService.getData(response).data;
                if (temp != null && temp.size() > 0) {
//                    if(temp.size() == pageSize){
//                        mAdapter.loadMoreComplete();
//                    }
//                    if(temp.size() < pageSize){
//                        mAdapter.loadMoreEnd();
//                    }

                    mAdapter.addNewDatas(new ArrayList<Object>(temp));
                    mAdapter.notifyDataSetChanged();
                    initNativeExpressAD();
                }
            }

            @Override
            public void onError(Exception e) {
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onBefore() {
            }
        });
    }

    /**
     * 判断是否滚动到底部
     *
     * @param recyclerView
     * @return
     */
    public static boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //Toast.makeText(this, "Authorize succeed", Toast.LENGTH_SHORT).show();

            ToastUtils.show(getActivity(), "授权成功");

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

                            UserInfo tempUserInfo = mService.login(response);
                            if (tempUserInfo != null) {

                                ToastUtils.show(getActivity(), "登录成功");

                                PreferencesUtils.putObject(getActivity(), Constant.USER_INFO, tempUserInfo);
                                App.isLoginAuth = true;

                                ToastUtils.show(getActivity(), "登录成功");
                                App.connect(tempUserInfo.getUsertoken());

                                Uri uri = null;
                                if (!StringUtils.isBlank(tempUserInfo.getUserimg())) {
                                    uri = Uri.parse(tempUserInfo.getUserimg());
                                }
                                io.rong.imlib.model.UserInfo ryUser = new io.rong.imlib.model.UserInfo(tempUserInfo.getUid(), tempUserInfo.getNickName(), uri);
                                RongIM.getInstance().setCurrentUserInfo(ryUser);

                                RongIM.getInstance().refreshUserInfoCache(ryUser);

                                RxBus.get().post(Constant.LOGIN_SUCCESS, "loginSuccess");
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            ToastUtils.show(getActivity(), R.string.get_user_info_fail);
                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }
                            ToastUtils.show(getActivity(), "登录失败，请稍后重试");
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

    @Override
    public void loginShow(String sid, int iszan) {
        loginDialog.show();
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        loadData();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.PRAISE_SUCCESS)
            }
    )
    public void praiseSuccess(String type) {
        if (lastItemPosition > -1 && type.equals("0")) {
            if (mAdapter.getArticleData() != null && mAdapter.getArticleData().size() > 0) {
                ((ArticleInfo)mAdapter.getArticleData().get(lastItemPosition)).iszan = 1;
                ((ArticleInfo)mAdapter.getArticleData().get(lastItemPosition)).zan = +1;
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initNativeExpressAD() {//com.qq.e.ads.nativ.ADSize.FULL_WIDTH
        MultiProcessFlag.setMultiProcess(true);
        com.qq.e.ads.nativ.ADSize adSize = new com.qq.e.ads.nativ.ADSize(com.qq.e.ads.nativ.ADSize.FULL_WIDTH, com.qq.e.ads.nativ.ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT // 消息流中用AUTO_HEIGHT
        NativeExpressAD mADManager = new NativeExpressAD(getActivity(), adSize, Constant.APPID, Constant.AD_LIST_FIRENDS, this);
        mADManager.loadAD(AD_COUNT);
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i(TAG,
                String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
                        adError.getErrorMsg()));
    }

    private NativeExpressADView view;

    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
        if (adList != null && adList.size() > 0) {
            mAdViewList = adList;
            int adIndex =  (pageNum - 1) % adList.size();
            int position = FIRST_AD_POSITION + (pageNum - 1) * pageSize;
            view = mAdViewList.get(adIndex);

            mAdViewPositionMap.put(view, position);
            mAdapter.addADViewToPosition(position, view);
        }
    }

    private String getAdInfo(NativeExpressADView nativeExpressADView) {
        AdData adData = nativeExpressADView.getBoundData();
        if (adData != null) {
            StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("title:").append(adData.getTitle()).append(",")
                    .append("desc:").append(adData.getDesc()).append(",")
                    .append("patternType:").append(adData.getAdPatternType());
            return infoBuilder.toString();
        }
        return null;
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADExposure(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADClosed(NativeExpressADView adView) {
        if (mAdapter != null) {
            int removedPosition = mAdViewPositionMap.get(adView);
            mAdapter.removeADView(removedPosition, adView);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (view != null) view.destroy();
    }

    @Override
    public void onItemClick(View view, int data) {
        Logger.d("data--->" + data);
    }
}
