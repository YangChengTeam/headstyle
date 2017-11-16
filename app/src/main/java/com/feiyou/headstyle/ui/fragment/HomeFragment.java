package com.feiyou.headstyle.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.HeadTypeAdapter;
import com.feiyou.headstyle.adapter.HeadWallAdapter;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.SpecialInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.bean.VersionInfo;
import com.feiyou.headstyle.bean.VersionRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.net.service.DownFightService;
import com.feiyou.headstyle.net.service.DownLsddService;
import com.feiyou.headstyle.net.service.UpdateService;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.HomeService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.activity.HeadListActivity;
import com.feiyou.headstyle.ui.activity.HeadShowActivity;
import com.feiyou.headstyle.ui.activity.MainActivity;
import com.feiyou.headstyle.ui.activity.MoreHeadTypeActivity;
import com.feiyou.headstyle.ui.activity.SearchActivity;
import com.feiyou.headstyle.ui.activity.SpecialListActivity;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.NetWorkUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.HeaderGridView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    @BindView(R.id.user_img)
    SimpleDraweeView userImg;

    @BindView(R.id.search_icon)
    ImageView searchBtn;

    ConvenientBanner convenientBanner;

    @BindView(R.id.photo_wall)
    HeaderGridView mPhotoWall;

    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout swipeLayout;

    private LinearLayout loversLayout, boyLayout, girlLayout, styleLayout, starLayout, animationLayout, gameLayout, moreLayout;

    private TextView refreshTv;

    private ImageView refreshIcon;

    private ProgressBar refreshBar;

    private HomeService mService = null;

    private UserService uService = null;

    private OKHttpRequest okHttpRequest = null;

    /**
     * GridView的适配器
     */
    private HeadWallAdapter mAdapter;

    private HeadTypeAdapter headTypeAdapter;

    private int mImageThumbSize;

    private int mImageThumbSpacing;

    private ArrayList<SpecialInfo> bannerImages;

    public List<HeadInfo> data;

    private View view;

    private int pageNum = 1;

    private List<HeadInfo> nextData;

    private int cid;

    private String titleName;

    //是否点击过"换一换"按钮
    private boolean isRefresh;

    private UserInfo userInfo;

    MaterialDialog updateDialog;

    private String downUrl;

    private String currentData;

    ArticleService articleService = null;

    public HomeFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initVars() {
        super.initVars();
        mService = new HomeService();
        uService = new UserService();
        articleService = new ArticleService();
        okHttpRequest = new OKHttpRequest();
        isRefresh = false;
        List<HeadInfo> tHeadInfoList = mService.getHeadInfoListFromDB();
        if (tHeadInfoList != null) {
            data = tHeadInfoList;
        } else {
            data = new ArrayList<HeadInfo>();
        }

        bannerImages = new ArrayList<SpecialInfo>();

        mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_spacing);
    }

    @Override
    public void initViews() {
        super.initViews();

        //头部控件View
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_header, null);

        //轮播控件
        convenientBanner = ButterKnife.findById(headView, R.id.convenientBanner);
        //分类对应控件
        loversLayout = ButterKnife.findById(headView, R.id.lovers_layout);
        boyLayout = ButterKnife.findById(headView, R.id.boy_layout);
        girlLayout = ButterKnife.findById(headView, R.id.girl_layout);
        styleLayout = ButterKnife.findById(headView, R.id.style_layout);
        starLayout = ButterKnife.findById(headView, R.id.star_layout);
        animationLayout = ButterKnife.findById(headView, R.id.animation_layout);
        gameLayout = ButterKnife.findById(headView, R.id.game_layout);
        moreLayout = ButterKnife.findById(headView, R.id.more_layout);

        //换一换
        refreshTv = ButterKnife.findById(headView, R.id.refresh_text);
        refreshIcon = ButterKnife.findById(headView, R.id.refresh_icon);
        refreshBar = ButterKnife.findById(headView, R.id.refresh_bar);

        loversLayout.setOnClickListener(this);
        boyLayout.setOnClickListener(this);
        girlLayout.setOnClickListener(this);
        styleLayout.setOnClickListener(this);
        starLayout.setOnClickListener(this);
        animationLayout.setOnClickListener(this);
        gameLayout.setOnClickListener(this);
        moreLayout.setOnClickListener(this);
        refreshTv.setOnClickListener(this);

        //添加头部控件
        mPhotoWall.addHeaderView(headView);

        nextData = new ArrayList<HeadInfo>();
        mAdapter = new HeadWallAdapter(getActivity(), data);
        mPhotoWall.setAdapter(mAdapter);

        swipeLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);
        swipeLayout.setOnRefreshListener(this);

        mPhotoWall.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        imageResume();

                        // 判断是否滚动到底部
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            //ToastUtils.show(getActivity(),"滚动到了底部");
                            loadDataByParams();//加载下一页数据
                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滚动状态
                        imagePause();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        String hisCheckDate = PreferencesUtils.getString(getActivity(), Constant.LAST_UPDATE_TIME);
        currentData = TimeUtils.getDataTime();

        if (hisCheckDate == null || !currentData.equals(hisCheckDate)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkVersion();
                }
            }).start();
        }
    }

    /**
     * 设置刷新过程进度条的状态
     *
     * @param params
     */
    public void setRefreshState(Map<String, String> params) {
        if (isRefresh) {
            params.put("o", "4");
            if (refreshBar.getVisibility() == View.GONE) {
                refreshBar.setVisibility(View.VISIBLE);
            }
            if (refreshIcon.getVisibility() == View.VISIBLE) {
                refreshIcon.setVisibility(View.GONE);
            }
        } else {
            if (refreshBar.getVisibility() == View.VISIBLE) {
                refreshBar.setVisibility(View.GONE);
            }
            if (refreshIcon.getVisibility() == View.GONE) {
                refreshIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 初始化
     */
    @Override
    public void loadData() {
        List<SpecialInfo> tSpecialInfoList = mService.getSpecialInfoListFromDB();
        if (tSpecialInfoList != null) {
            updataSpecialData(tSpecialInfoList);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadDataByParams();
                }
            }, 1500);
        } else {
            loadDataByParams();
        }
    }

    public void loadDataByParams() {

        if (nextData != null && nextData.size() > 0) {
            data.addAll(nextData);
            mAdapter.addNewDatas(nextData);

            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);

            pageNum++;
            getNextData();
        } else {
            StringBuffer homeUrl = new StringBuffer(Server.NEW_HOME_DATA);
            if (userInfo != null) {
                homeUrl.append("/").append(userInfo.uid);
            }else{
                homeUrl.append("/").append("0");
            }

            if (isRefresh) {
                homeUrl.append("/").append("4");
            }else{
                homeUrl.append("/").append("0");
            }

            homeUrl.append("/").append(String.valueOf(pageNum)).append(".html");

            Logger.e("first url--->" + homeUrl);

            okHttpRequest.aget(homeUrl.toString(), null, new OnResponseListener() {
                @Override
                public void onSuccess(final String response) {

                    swipeLayout.setRefreshing(false);

                    if (isRefresh) {
                        if (refreshBar.getVisibility() == View.VISIBLE) {
                            refreshBar.setVisibility(View.GONE);
                        }
                        if (refreshIcon.getVisibility() == View.GONE) {
                            refreshIcon.setVisibility(View.VISIBLE);
                        }
                    }

                    if (pageNum == 1) {
                        Logger.e("top---");
                        //设置banner数据
                        final List<SpecialInfo> tempSpecialInfos = mService.getSpecialInfos(response);
                        if (tempSpecialInfos != null && tempSpecialInfos.size() > 0) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mService.deleteAllSpecialInfoList();
                                    mService.saveSpecialInfoListToDB(tempSpecialInfos);
                                }
                            }).start();
                            updataSpecialData(tempSpecialInfos);
                        }
                    }

                    //设置首页列表数据
                    final List<HeadInfo> temp = mService.getHeadInfos(response);
                    if (temp != null && temp.size() > 0) {
                        Logger.e("first data ==" + temp.get(0).getHurl());
                        data.addAll(temp);
                        mAdapter.addNewDatas(temp);

                        //缓存第一页数据
                        if (pageNum == 1) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mService.deleteAllHeadInfoList();
                                    mService.saveHeadInfoListToDB(temp);
                                }
                            }).start();
                        }

                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);

                        pageNum++;
                        getNextData();
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
    }

    /**
     * 版本检测更新
     */
    public void checkVersion() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("channel", AppUtils.getMetaDataValue(getActivity(), "UMENG_CHANNEL"));

        okHttpRequest.aget(Server.CHECK_VERSION_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {

                VersionRet versionRet = uService.checkVersion(response);
                if (versionRet != null && versionRet.data != null) {
                    VersionInfo versionInfo = versionRet.data;
                    if (versionInfo != null) {
                        if (versionInfo.version.equals(AppUtils.getVersionName(getActivity()))) {
                            //ToastUtils.show(getActivity(), "当前已是最新版本");
                            Logger.e("当前已是最新版本");
                        } else {
                            if (versionInfo.versionCode > AppUtils.getVersionCode(getActivity())) {
                                if (!AppUtils.checkSdCardExist()) {
                                    ToastUtils.show(getActivity(), "未检测到SD卡，暂时无法更新");
                                } else {
                                    //存储最后一次的时间
                                    PreferencesUtils.putString(getActivity(), Constant.LAST_UPDATE_TIME, currentData);

                                    downUrl = versionInfo.download;

                                    updateDialog = new MaterialDialog.Builder(getActivity())
                                            .titleColorRes(R.color.dialog_title_color)
                                            .contentColorRes(R.color.dialog_content_color)
                                            .backgroundColorRes(R.color.white)
                                            .positiveColorRes(R.color.colorPrimary)
                                            .negativeColorRes(R.color.quick_text_color)
                                            .title(R.string.version_update_text)
                                            .content(R.string.version_update_content_text)
                                            .positiveText(R.string.confirm_update_text)
                                            .negativeText(R.string.cancel_text)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    if (downUrl != null && downUrl.length() > 0) {
                                                        new DownVersionAsyncTask().execute();
                                                    } else {
                                                        ToastUtils.show(getActivity(), "下载地址有误，请稍后重试");
                                                    }
                                                }
                                            })
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .iconRes(R.drawable.logo)
                                            .maxIconSize(80).build();
                                    updateDialog.show();
                                }
                            }
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
    }


    @Override
    public void onResume() {
        super.onResume();
        if (AppUtils.isLogin(getActivity())) {
            userInfo = (UserInfo) PreferencesUtils.getObject(getActivity(), Constant.USER_INFO, UserInfo.class);
            if (userInfo != null) {
                if (HeadStyleApplication.userImgPath != null) {
                    Uri uri = Uri.parse("file:///" + HeadStyleApplication.userImgPath);
                    userImg.setImageURI(uri);
                } else if (userInfo.userimg != null) {
                    Uri uri = Uri.parse(userInfo.userimg);
                    userImg.setImageURI(uri);
                } else {
                    Uri uri = Uri.parse("res://mipmap/" + R.mipmap.user_default_icon);
                    userImg.setImageURI(uri);
                }
            }
        } else {
            Uri uri = Uri.parse("res://mipmap/" + R.mipmap.user_default_icon);
            userImg.setImageURI(uri);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    /**
     * 加载下一页数据
     */
    public void getNextData() {

        StringBuffer homeUrl = new StringBuffer(Server.NEW_HOME_DATA);
        if (userInfo != null) {
            homeUrl.append("/").append(userInfo.uid);
        }else{
            homeUrl.append("/").append("0");
        }

        if (isRefresh) {
            homeUrl.append("/").append("4");
        }else{
            homeUrl.append("/").append("0");
        }

        homeUrl.append("/").append(String.valueOf(pageNum)).append(".html");

        Logger.e("next url --->" + homeUrl.toString());

        okHttpRequest.aget(homeUrl.toString(), null, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                if (isRefresh) {
                    refreshBar.setVisibility(View.GONE);
                    refreshIcon.setVisibility(View.VISIBLE);
                }
                //设置首页列表数据
                if (nextData != null) {
                    nextData.clear();
                }
                nextData = mService.getHeadInfos(response);
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
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        refreshData();
    }

    /**
     * 设置Banner数据
     */
    public void updataSpecialData(final List<SpecialInfo> specialInfos) {
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, specialInfos)
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
        convenientBanner.startTurning(4000);
        //convenientBanner.setScrollDuration(3000);
        convenientBanner.setCanLoop(true);
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if (specialInfos != null && specialInfos.size() > 0) {
                    Intent intent = new Intent(getActivity(), SpecialListActivity.class);
                    SpecialInfo specialInfo = specialInfos.get(position);
                    if (!StringUtils.isEmpty(specialInfo.getSid())) {
                        Logger.e("banner item sid ===" + specialInfo.getSid());
                        intent.putExtra("sid", Integer.parseInt(specialInfo.getSid()));
                    }
                    if (!StringUtils.isEmpty(specialInfo.getSname())) {
                        intent.putExtra("titleName", specialInfo.getSname());
                    }
                    startActivity(intent);
                }
            }
        });
    }

    public class LocalImageHolderView implements Holder<SpecialInfo> {
        private ImageView hImageView;

        @Override
        public View createView(Context context) {
            hImageView = new ImageView(context);
            hImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            hImageView.setLayoutParams(new ViewGroup.LayoutParams(580, 250));
            return hImageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, SpecialInfo data) {
            if (data != null && !StringUtils.isEmpty(data.getSimg())) {
                Glide.with(context).load(data.getSimg()).into(hImageView);
            }
        }
    }

    @OnClick(R.id.user_img)
    public void userInfo() {
        ((MainActivity) getActivity()).setCurrentTab(4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lovers_layout:
                cid = 15;
                titleName = getActivity().getString(R.string.type_lovers_text);
                typeListActivity(cid, titleName);
                break;
            case R.id.boy_layout:
                cid = 13;
                titleName = getActivity().getString(R.string.type_boy_text);
                typeListActivity(cid, titleName);
                break;
            case R.id.girl_layout:
                cid = 14;
                titleName = getActivity().getString(R.string.type_girl_text);
                typeListActivity(cid, titleName);
                break;
            case R.id.star_layout:
                cid = 21;
                titleName = getActivity().getString(R.string.type_star_text);
                typeListActivity(cid, titleName);
                break;
            case R.id.animation_layout:
                cid = 18;
                titleName = getActivity().getString(R.string.type_animation_text);
                typeListActivity(cid, titleName);
                break;
            case R.id.game_layout:
                cid = 64;
                titleName = getActivity().getString(R.string.type_game_text);
                typeListActivity(cid, titleName);
                //downLsdd(cid, titleName);
                break;
            case R.id.more_layout:
                Logger.e("toHeadMoreClick------");
                Intent intent = new Intent(getActivity(), MoreHeadTypeActivity.class);
                startActivity(intent);
                break;
            case R.id.refresh_text:
                refreshData();
                break;
            case R.id.style_layout:
                cid = 2;
                titleName = getActivity().getString(R.string.type_style_text);
                //downFight(cid, titleName);
                typeListActivity(cid, titleName);
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到类别页
     *
     * @param mCid
     * @param mTitleName
     */
    public void typeListActivity(int mCid, String mTitleName) {
        Intent intent = new Intent(getActivity(), HeadListActivity.class);
        if (mCid > 0) {
            intent.putExtra("cid", mCid);
        }
        if (!StringUtils.isEmpty(mTitleName)) {
            intent.putExtra("titleName", mTitleName);
        }
        startActivity(intent);
    }

    /**
     * 版本更新时，启动服务下载
     */
    public class DownVersionAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            return NetWorkUtils.is404NotFound(downUrl);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                Intent intent = new Intent(getActivity(), UpdateService.class);
                intent.putExtra("downUrl", downUrl);
                getActivity().startService(intent);
            } else {
                ToastUtils.show(getActivity(), "下载地址有误，请稍后重试");
            }
        }
    }


    /**
     * 判断是否下载或者打开"装逼神器APP"
     */
    public void downFight(int mCid, String mTitleName) {

        if (AppUtils.appInstalled(getActivity(), "com.fy.tnzbsq")) {
            //Toast.makeText(getActivity(),"应用已安装，请体验",Toast.LENGTH_SHORT).show();

            //如果下载文件存在并且已经安装，直接删除下载文件
            /*File downFile = new File(Constant.FIGHT_DOWN_FILE_PATH);
            if (downFile.exists()) {
                downFile.delete();
            }

            PackageManager packageManager = getActivity().getPackageManager();
            Intent intent = new Intent();
            intent = packageManager.getLaunchIntentForPackage("com.fy.tnzbsq");
            startActivity(intent);*/

            typeListActivity(mCid, mTitleName);

        } else {
            //if (!AppUtils.isServiceWork(getActivity(), "com.feiyou.headstyle.net.service.DownFightService")) {

            //如果下载文件存在，直接启动安装
            File downFile = new File(Constant.FIGHT_DOWN_FILE_PATH);
            if (downFile.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(downFile),
                        "application/vnd.android.package-archive");
                startActivity(intent);
            } else {
                //启动服务重新下载
                //TODO 暂时注释
                //new DownAsyncTask().execute();

                //判断应用宝市场是否存在
                if (AppUtils.appInstalled(getActivity(), "com.tencent.android.qqdownloader")) {
                    AppUtils.shareAppShop(getActivity(), "com.fy.tnzbsq");
                } else {
                    new DownAsyncTask().execute();
                }
            }

            /*} else {
                ToastUtils.show(getActivity(), "斗图神器下载中");
            }*/
        }
    }

    /**
     * 判断是否下载或者打开"装逼神器APP"
     */
    public void downLsdd(int mCid, String mTitleName) {

        if (AppUtils.appInstalled(getActivity(), "com.whfeiyou.sound")) {
            //Toast.makeText(getActivity(),"应用已安装，请体验",Toast.LENGTH_SHORT).show();

            //如果下载文件存在并且已经安装，直接删除下载文件
            /*File downFile = new File(Constant.FIGHT_DOWN_FILE_PATH);
            if (downFile.exists()) {
                downFile.delete();
            }

            PackageManager packageManager = getActivity().getPackageManager();
            Intent intent = new Intent();
            intent = packageManager.getLaunchIntentForPackage("com.fy.tnzbsq");
            startActivity(intent);*/

            typeListActivity(mCid, mTitleName);

        } else {
            //if (!AppUtils.isServiceWork(getActivity(), "com.feiyou.headstyle.net.service.DownFightService")) {

            //如果下载文件存在，直接启动安装
            File downFile = new File(Constant.LSDD_DOWN_FILE_PATH);
            if (downFile.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(downFile),
                        "application/vnd.android.package-archive");
                startActivity(intent);
            } else {
                //启动服务重新下载
                //TODO 暂时注释
                //new DownAsyncTask().execute();

                //判断应用宝市场是否存在
                if (AppUtils.appInstalled(getActivity(), "com.tencent.android.qqdownloader")) {
                    AppUtils.shareAppShop(getActivity(), "com.whfeiyou.sound");
                } else {
                    new DownLsddAsyncTask().execute();
                }
            }

            /*} else {
                ToastUtils.show(getActivity(), "斗图神器下载中");
            }*/
        }
    }

    /**
     * 启动服务下载"装逼神器APP"
     */
    public class DownAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            return NetWorkUtils.is404NotFound(Constant.FIGHT_DOWN_URL);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                Intent intent = new Intent(getActivity(), DownFightService.class);
                intent.putExtra("downUrl", Constant.FIGHT_DOWN_URL);
                getActivity().startService(intent);
            } else {
                ToastUtils.show(getActivity(), "下载地址有误，请稍后重试");
            }
        }
    }

    /**
     * 启动服务下载铃声朵朵
     */
    public class DownLsddAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            return NetWorkUtils.is404NotFound(Constant.LSDD_DOWN_URL);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                Intent intent = new Intent(getActivity(), DownLsddService.class);
                intent.putExtra("downUrl", Constant.LSDD_DOWN_URL);
                getActivity().startService(intent);
            } else {
                ToastUtils.show(getActivity(), "下载地址有误，请稍后重试");
            }
        }
    }

    /**
     * 此处的postion,如果添加过header,则相应的position要减去一行的列数
     *
     * @param position
     */
    @OnItemClick(R.id.photo_wall)
    public void onHeadItemClick(int position) {

        Intent intent = new Intent(getActivity(), HeadShowActivity.class);

        if (data != null && data.size() > 0 && position - 3 >= -1) {
            position = position - 3;
            intent.putExtra("cid", data.get(position).getCid());
            intent.putExtra("imageUrl", data.get(position).getHurl());
            intent.putExtra("gaoqing", data.get(position).getGaoqing());
        }

        startActivity(intent);
    }

    @OnClick(R.id.search_icon)
    public void toSearchClick(View view) {
        Logger.e("toSearchClick------");
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }


    public void refreshData() {
        if (nextData != null) {
            nextData.clear();
        }
        if (data != null) {
            data.clear();
        }
        mAdapter.clear();
        pageNum = 1;
        isRefresh = true;
        loadDataByParams();

        //刷新是否有最新的评论
        ((MainActivity) getActivity()).initCommentCount();
    }

}
