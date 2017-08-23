package com.feiyou.headstyle;

import android.app.Application;
import android.graphics.Color;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.feiyou.headstyle.listener.FrescoPauseOnScrollListener;
import com.feiyou.headstyle.util.FileUtils;
import com.feiyou.headstyle.view.FrescoImageLoader;
import com.umeng.socialize.PlatformConfig;

import org.lasque.tusdk.core.TuSdk;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * Created by admin on 2016/8/31.
 */
public class HeadStyleApplication extends Application {

    public static FunctionConfig functionConfig;

    public static String sdPath = "";

    /**
     * 是否登录授权
     */
    public static boolean isLoginAuth = false;

    public static String userImgPath;

    /**
     * 是否有未读消息
     */
    public static boolean isMessage = false;

    @Override
    public void onCreate() {
        super.onCreate();

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

        /*OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .writeTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);*/
    }

}
