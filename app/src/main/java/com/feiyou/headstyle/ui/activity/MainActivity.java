package com.feiyou.headstyle.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTabHost;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.fragment.CreateFragment;
import com.feiyou.headstyle.ui.fragment.FunTestFragment;
import com.feiyou.headstyle.ui.fragment.HomeFragment;
import com.feiyou.headstyle.ui.fragment.MyFragment1;
import com.feiyou.headstyle.ui.fragment.Show1Fragment;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.SPUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.WeiXinUtil;
import com.feiyou.headstyle.view.WebPopupWindow;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMShareAPI;
import com.xinqu.videoplayer.XinQuVideoPlayer;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

@RuntimePermissions
public class MainActivity extends BaseActivity {

    private long clickTime = 0;

    //定义FragmentTabHost对象
    @BindView(R.id.tabhost)
    public FragmentTabHost mTabHost;

    @BindView(R.id.tv_create_tips)
    public TextView tipsTextView;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {HomeFragment.class, Show1Fragment.class, CreateFragment.class, FunTestFragment.class, MyFragment1.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.selector_home, R.drawable.selector_show, R.drawable.selector_create, R.drawable.selector_test, R.drawable.selector_my};

    //Tab选项卡的文字
    private int mTextviewArray[] = {R.string.tab_home_text, R.string.tab_show_text, R.string.tab_create_text, R.string.tab_fun_test_text, R.string.tab_my_text};

    private OKHttpRequest okHttpRequest = null;

    ArticleService articleService = null;

    private UserService mService = null;

    private UserInfo userInfo;

    private int is_close_gzh;

    //private LocationService locationService;

    public static MainActivity mainActivity;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void requestLocation() {
        //startLocation();
    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForLocation(final PermissionRequest request) {

    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForLocation() {

    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForLocation() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void initVars() {
        super.initVars();
        //QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        articleService = new ArticleService();
        mService = new UserService();
        okHttpRequest = new OKHttpRequest();
        MainActivityPermissionsDispatcher.requestLocationWithPermissionCheck(this);
        mainActivity = this;
    }

    /**
     * 初始化组件
     */
    public void initViews() {
        super.initViews();
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //初始化TabHost
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        //得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(mTextviewArray[i])).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }

        setCurrentTab(0);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Logger.e("tabId---" + tabId);

                if (tabId.equals(getResources().getString(mTextviewArray[2]))) {
                    SPUtils.getInstance(MainActivity.this, "TIPS_SHOW").put("tips", false);
                    tipsTextView.setVisibility(View.GONE);
                }

                //有消息提示时，当选择了该界面，让提示红点消失
                if (tabId.equals(getResources().getString(mTextviewArray[4]))) {
                    View currentView = mTabHost.getTabWidget().getChildTabViewAt(4);
                    ImageView redImageView = (ImageView) currentView.findViewById(R.id.message_red_point);
                    redImageView.setVisibility(View.INVISIBLE);
                }
            }
        });

        tipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.getInstance(MainActivity.this, "TIPS_SHOW").put("tips", false);
                tipsTextView.setVisibility(View.GONE);
            }
        });

        boolean isShow = SPUtils.getInstance(MainActivity.this, "TIPS_SHOW").getBoolean("tips", true);
        if (!isShow) {
            tipsTextView.setVisibility(View.GONE);
        } else {
            tipsTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    public void setCurrentTab(int tabPosition) {
        mTabHost.setCurrentTab(tabPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (XinQuVideoPlayer.backPress()) {
            return;
        }

        exit();
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            clickTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取用户信息，主要是获取评论数
        getUserInfo();
    }

    public void getUserInfo() {
        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        if (userInfo != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("uid", userInfo.getUid());
            //查询用户信息
            okHttpRequest.aget(Server.USER_INFO_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    UserInfo tempInfo = mService.login(response);
                    if (tempInfo != null) {
                        userInfo = tempInfo;
                        PreferencesUtils.putObject(MainActivity.this, Constant.USER_INFO, userInfo);

                        //原有的评论数
                        int lastCount = PreferencesUtils.getInt(MainActivity.this, App.AID + userInfo.uid);

                        int currentCount = !StringUtils.isBlank(userInfo.getComment()) ? Integer.parseInt(userInfo.getComment()) : 0;
                        //存储最新的评论数
                        PreferencesUtils.putInt(MainActivity.this, App.AID + userInfo.uid, currentCount);
                        if (currentCount > 0 && currentCount > lastCount) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    View currentView = mTabHost.getTabWidget().getChildTabViewAt(4);
                                    ImageView redImageView = (ImageView) currentView.findViewById(R.id.message_red_point);
                                    redImageView.setVisibility(View.VISIBLE);
                                    App.isMessage = true;
                                }
                            });
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
            //连接融云服务器
            if (!App.isConnect) {
                connect(userInfo.getUsertoken());
            }

        } else {
            App.isMessage = false;
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.LOGIN_SUCCESS)
            }
    )
    public void loginSuccess(String result) {
        Logger.e("login success--->");
        getUserInfo();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.MESSAGE)
            }
    )
    public void messageHint(String result) {
        View currentView = mTabHost.getTabWidget().getChildTabViewAt(4);
        if (currentView != null) {
            ImageView redImageView = (ImageView) currentView.findViewById(R.id.message_red_point);
            if (App.isLetter) {
                redImageView.setVisibility(View.VISIBLE);
            } else {
                redImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #'init'(Context)} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token      从服务端获取的用户身份令牌（Token）。
     * @param 'callback' 连接回调。
     * @return RongIM  客户端核心类的实例。
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {

                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Logger.e("talk success --->");
                    App.isConnect = true;
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Logger.e("talk error --->");
                }
            });
        }
    }

    /*protected void startLocation() {
        super.onStart();
        // -----------location config ------------
        locationService = ((App) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听

        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();//定位SDK
    }

    *//***
     * Stop location service
     *//*
    @Override
    protected void onStop() {
        super.onStop();
        if(locationService != null) {
            locationService.unregisterListener(mListener); //注销掉监听
            locationService.stop(); //停止定位服务
            mListener = null;
        }
    }*/

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\nProvince : ");//省份
                sb.append(location.getProvince());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                Logger.e("定位信息--->" + sb.toString());
            }
        }
    };

    public void fixOpenwx(int state,String url) {
        if (state == 1) {
            WebPopupWindow webPopupWindow = new WebPopupWindow(MainActivity.getMainActivity(), url);
            webPopupWindow.setTimeListener(null);
            webPopupWindow.show(MainActivity.getMainActivity().getWindow().getDecorView().getRootView());

            return;
        }else {
            //AppUtil.copy(MainActivity.this, Config.WEIXIN);
            String html = "关注【头像达人】微信公众号，做时尚达人吧!";
            new MaterialDialog.Builder(MainActivity.this)
                    .title("关注微信公众号")
                    .content(Html.fromHtml(html))
                    .positiveText("确定")
                    .backgroundColor(Color.WHITE)
                    .contentColor(Color.GRAY)
                    .canceledOnTouchOutside(false)
                    .titleColor(Color.BLACK)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            WeiXinUtil.gotoWeiXin(MainActivity.this, "正在前往微信...");
                        }
                    })
                    .build().show();
        }
    }
}
