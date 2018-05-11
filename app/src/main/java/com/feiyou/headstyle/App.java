package com.feiyou.headstyle;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.blankj.utilcode.util.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.DbHelper;
import com.feiyou.headstyle.common.LocationService;
import com.feiyou.headstyle.listener.FrescoPauseOnScrollListener;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.FileUtils;
import com.feiyou.headstyle.view.FrescoImageLoader;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.PlatformConfig;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

/**
 * Created by admin on 2016/8/31.
 */
public class App extends Application {

    protected static App mInstance;

    public static FunctionConfig functionConfig;

    public static String sdPath = "";

    /**
     * 是否登录授权
     */
    public static boolean isLoginAuth = false;

    public static String userImgPath;

    /**
     * 是否有未读评论
     */
    public static boolean isMessage = false;

    /**
     * 是否有未读私信消息
     */
    public static boolean isLetter = false;

    public static int currentPage;

    public static int typeId;

    public static String AID = "";

    public static boolean isConnect = false;

    public LocationService locationService;

    public static int weixinState;

    public static String weixinUrl = "";

    public App() {
        mInstance = this;
    }

    public static App getApp() {
        if (mInstance != null && mInstance instanceof App) {
            return (App) mInstance;
        } else {
            mInstance = new App();
            mInstance.onCreate();
            return (App) mInstance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RxBus.get().register(this);
        RongIM.init(this);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
        Fresco.initialize(this);
        Utils.init(this);
        PlatformConfig.setQQZone("1105592461", "xCJux2hAAjyh1qdx");
        PlatformConfig.setWeixin("wxd1112ca9a216aeda", "0e18de42fc068c41f0aca921403b9932");
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.rgb(251, 83, 96))
                .setTitleBarTextColor(Color.rgb(255, 255, 255))
                .setFabNornalColor(Color.rgb(251, 83, 96))
                .setFabPressedColor(Color.rgb(246, 130, 130))
                .setCropControlColor(Color.parseColor("#fb5161"))
                .setEditPhotoBgTexture(getResources().getDrawable(R.mipmap.common_cancel_normal))
                .build();

        //配置功能
        functionConfig = new FunctionConfig.Builder()
                .setMutiSelectMaxSize(3)
                .build();

        CoreConfig coreConfig = new CoreConfig.Builder(this, new FrescoImageLoader(getApplicationContext()), theme)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(new FrescoPauseOnScrollListener(false, true))
                .build();
        GalleryFinal.init(coreConfig);

        //获取SD卡文件缓存路径
        if (FileUtils.getDiskCacheDir(getApplicationContext()) != null) {
            sdPath = FileUtils.getDiskCacheDir(getApplicationContext());
        }
        // 开发ID (请前往 http://tusdk.com 获取您的 APP 开发秘钥)
        //TuSdk.init(this.getApplicationContext(), "9c6a7d8303d3c7c3-02-muczp1");
        DbHelper.init(App.this);
        /*OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .writeTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);*/
        AID = AppUtils.getAndroidID(this);


        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                App.isLetter = true;
                RxBus.get().post(Constant.MESSAGE, "message");
                return false;
            }
        });
        locationService = new LocationService(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //获取应用的data/data/....File目录
    public String getFilesDirPath() {
        return getFilesDir().getAbsolutePath();
    }

    //获取应用的data/data/....Cache目录
    public String getCacheDirPath() {
        return getCacheDir().getAbsolutePath();
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
    public static void connect(String token) {

        if (mInstance.getApplicationInfo().packageName.equals(getCurProcessName(mInstance.getApplicationContext()))) {

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
                    Logger.e("talk success --->" + userid);
                    isConnect = true;
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

}
