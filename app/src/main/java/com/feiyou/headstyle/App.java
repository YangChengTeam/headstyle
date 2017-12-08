package com.feiyou.headstyle;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.DbHelper;
import com.feiyou.headstyle.listener.FrescoPauseOnScrollListener;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.FileUtils;
import com.feiyou.headstyle.view.FrescoImageLoader;
import com.hwangjr.rxbus.RxBus;
import com.umeng.socialize.PlatformConfig;

import org.lasque.tusdk.core.TuSdk;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by admin on 2016/8/31.
 */
public class App extends Application {

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

    @Override
    public void onCreate() {
        super.onCreate();
        RxBus.get().register(this);
        RongIM.init(this);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
        Fresco.initialize(this);
        PlatformConfig.setQQZone("1105592461", "xCJux2hAAjyh1qdx");
        PlatformConfig.setWeixin("wxd1112ca9a216aeda", "0e18de42fc068c41f0aca921403b9932");
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.rgb(251, 83, 96))
                .setTitleBarTextColor(Color.rgb(255, 255, 255))
                .setFabNornalColor(Color.rgb(251, 83, 96))
                .setFabPressedColor(Color.rgb(246, 130, 130))
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
        TuSdk.init(this.getApplicationContext(), "9c6a7d8303d3c7c3-02-muczp1");
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

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
