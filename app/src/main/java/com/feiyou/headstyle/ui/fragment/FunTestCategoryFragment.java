package com.feiyou.headstyle.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.FunTestAdapter;
import com.feiyou.headstyle.adapter.FunTestNewAdapter;
import com.feiyou.headstyle.bean.FunTestInfo;
import com.feiyou.headstyle.bean.FunTestInfoData;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.FunTestResultService;
import com.feiyou.headstyle.service.FunTestService;
import com.feiyou.headstyle.ui.activity.FunTestDetailActivity;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.SharePopupWindow;
import com.orhanobut.logger.Logger;
import com.qq.e.ads.cfg.MultiProcessFlag;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;
import com.qq.e.comm.util.GDTLogger;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by admin on 2017/9/21.
 */

public class FunTestCategoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, FunTestAdapter.ShareListener,NativeExpressAD.NativeExpressADListener{

    @BindView(R.id.layout_fun_test)
    FrameLayout mFunTestLayout;

    @BindView(R.id.loading_layout)
    LinearLayout mLoadingLayout;

    @BindView(R.id.loading_iv)
    ImageView mLoadImageView;

    @BindView(R.id.iv_no_date)
    ImageView mNoDataImageView;

    @BindView(R.id.all_test_list)
    RecyclerView mTestRecyclerView;

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    OKHttpRequest okHttpRequest = null;

    FunTestNewAdapter mFunTestAdapter;

    private int currentPage = 1;

    private int currentNewPage = 1;

    private int currentHotPage = 1;

    FunTestService mService = null;

    private UMShareAPI mShareAPI = null;

    private UMImage image;

    LinearLayoutManager mLinearLayoutManager;

    FunTestResultService mResultService = null;

    //分享弹出窗口
    private SharePopupWindow shareWindow;

    private int type = 1;

    private int isHot = 0;//0最新,1热门

    private int classId;//分类ID

    private String shareTitle = null;

    private FunTestInfo funTestInfo;

    public static final int AD_COUNT = 10;// 加载广告的条数，取值范围为[1, 10]

    public static int FIRST_AD_POSITION = 1; // 第一条广告的位置

    private List<NativeExpressADView> mAdViewList;

    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<>();

    public static final String TAG = "ALL";

    List<Object> funtestDatas;

    public boolean isFirstLoad = false;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_fun_test;
    }

    @Override
    public void initVars() {
        super.initVars();
        okHttpRequest = new OKHttpRequest();
    }

    @Override
    public void initViews() {
        super.initViews();
        funtestDatas = new ArrayList<Object>();
        Glide.with(getActivity()).load(R.drawable.main_loading_gif).into(mLoadImageView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            type = getArguments().getInt("type");
            classId = getArguments().getInt("class_id");
        }

        if (type == 1) {
            isHot = 0;
        } else {
            isHot = 1;
        }

        currentPage = getCurrentPageByType(type);

        mService = new FunTestService();
        mResultService = new FunTestResultService();

        mShareAPI = UMShareAPI.get(getActivity());
        image = new UMImage(getActivity(), R.mipmap.logo_100);

        swipeLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                R.color.refresh_select_color);
        swipeLayout.setOnRefreshListener(this);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mFunTestAdapter = new FunTestNewAdapter(getActivity(), funtestDatas, 2,mAdViewPositionMap);
        mFunTestAdapter.clearDatas();
        mFunTestAdapter.setShareListener(this);
        mTestRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTestRecyclerView.setAdapter(mFunTestAdapter);

        mTestRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(mTestRecyclerView.canScrollVertically(1)){
                    Log.i(TAG, "direction 1: true");
                }else {
                    //Log.i(TAG, "direction 1: false");//滑动到底部
                    currentPage++;
                    swipeLayout.setRefreshing(true);
                    loadData();
                }

            }
        });

        mFunTestAdapter.setOnItemClickListener(new FunTestNewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int data) {
                App.currentPage = currentPage;
                App.typeId = 0;
                Intent intent = new Intent(getActivity(), FunTestDetailActivity.class);
                intent.putExtra("fun_test_data_info", (FunTestInfo)(mFunTestAdapter.getDatas().get(data)));
                startActivity(intent);
            }
        });

    }

    @Override
    public void loadData() {
        super.loadData();

        Logger.d("load data1--->");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("p", currentPage + "");
        params.put("num", "20");
        params.put("ishot", isHot + "");
        params.put("cid", classId + "");

        okHttpRequest.aget(Server.FUN_TEST_LIST_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                swipeLayout.setRefreshing(false);
                //设置首页列表数据

                FunTestInfoData result = mService.getData(response);
                if (result != null && result.data != null && result.getData().size() > 0) {
                    mLoadingLayout.setVisibility(View.GONE);

//                    if (currentPage == 1) {
//                        mFunTestAdapter.setNewData(result.data);
//                    } else {
//                        mFunTestAdapter.addData(result.data);
//                    }
//
//                    if (result.data.size() == 20) {
//                        setCurrentPage();
//                        mFunTestAdapter.loadMoreComplete();
//                    } else {
//                        mFunTestAdapter.loadMoreEnd();
//                    }

                    funtestDatas.addAll(new ArrayList<Object>(result.data));
                    if(mFunTestAdapter != null){
                        mFunTestAdapter.addNewDatas(funtestDatas);
                        mFunTestAdapter.notifyDataSetChanged();
                    }
                    initNativeExpressAD();
                } else {
                    if (currentPage == 1) {
                        mLoadingLayout.setVisibility(View.VISIBLE);
                        mNoDataImageView.setVisibility(View.VISIBLE);
                        mLoadImageView.setVisibility(View.GONE);
                    } else {
                        mLoadingLayout.setVisibility(View.GONE);
                    }
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

    @Override
    public void onRefresh() {
        currentPage = 1;
        currentNewPage = 1;
        currentHotPage = 1;
        funtestDatas.clear();
        loadData();
    }


    public void setCurrentPage() {
        switch (type) {
            case 1:
                currentNewPage++;
                break;
            case 2:
                currentHotPage++;
                break;
        }
    }

    public int getCurrentPageByType(int type) {
        switch (type) {
            case 1:
                currentPage = currentNewPage;
                break;
            case 2:
                currentPage = currentHotPage;
                break;
        }
        return currentPage;
    }


    @Override
    public void shareTestTopic(int pos) {
        funTestInfo = (FunTestInfo) mFunTestAdapter.getDatas().get(pos);

        if (funTestInfo != null) {
            shareTitle = funTestInfo.sharetitle;
            if (!StringUtils.isEmpty(shareTitle)) {
                if (shareTitle.length() > 11) {
                    String tShareTitle = shareTitle.substring(0, 11);
                    shareTitle = tShareTitle + "......";
                }
            }

            shareWindow = new SharePopupWindow(getActivity(), itemsOnClick);
            shareWindow.showAtLocation(mFunTestLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            setBackgroundAlpha(getActivity(), 0.5f);
            shareWindow.setOnDismissListener(new PoponDismissListener());

            Glide.with(this).asBitmap().load(funTestInfo.sharelogo).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    image = new UMImage(getActivity(), bitmap);
                }
            });

        } else {
            ToastUtils.show(getActivity(), "数据异常，请稍后重试");
        }
    }

    //弹出窗口监听消失
    public class PoponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            setBackgroundAlpha(getActivity(), 1f);
        }
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
                    new ShareAction(getActivity())
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
                    new ShareAction(getActivity())
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
                    new ShareAction(getActivity())
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
                    new ShareAction(getActivity())
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
            Toast.makeText(getActivity(), " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(getActivity(), " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(getActivity(), " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                Result result = mResultService.getUpdateResult(response);
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


    private void initNativeExpressAD() {//com.qq.e.ads.nativ.ADSize.FULL_WIDTH
        MultiProcessFlag.setMultiProcess(true);
        com.qq.e.ads.nativ.ADSize adSize = new com.qq.e.ads.nativ.ADSize(com.qq.e.ads.nativ.ADSize.FULL_WIDTH, com.qq.e.ads.nativ.ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT // 消息流中用AUTO_HEIGHT
        NativeExpressAD mADManager = new NativeExpressAD(getActivity(), adSize, Constant.APPID, Constant.AD_LIST_TEST, this);
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
            int adIndex =  (currentPage - 1) % adList.size();
            int position = FIRST_AD_POSITION + (currentPage - 1) * 20;
            view = mAdViewList.get(adIndex);

            mAdViewPositionMap.put(view, position);
            mFunTestAdapter.addADViewToPosition(position, view);
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
        if (mFunTestAdapter != null) {
            int removedPosition = mAdViewPositionMap.get(adView);
            mFunTestAdapter.removeADView(removedPosition, adView);
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
        funtestDatas.clear();
        mFunTestAdapter.clearDatas();
        if(mFunTestAdapter != null){
            mFunTestAdapter = null;
        }
        if (view != null) view.destroy();
    }

}
