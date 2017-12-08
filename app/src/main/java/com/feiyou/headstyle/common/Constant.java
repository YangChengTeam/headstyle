package com.feiyou.headstyle.common;

import android.os.Environment;

import com.feiyou.headstyle.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

/**
 * Created by admin on 2016/9/6.
 */
public class Constant {

    public final static int RESULT_SUCCESS = 0;

    public final static int RESULT_FAIL = -1;

    public final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").disableHtmlEscaping()
            .create();

    public final static String USER_INFO = "user_info";

    public final static String LAST_UPDATE_TIME = "last_update_time";

    public static String SD_DIR = App.sdPath;

    public static final String BASE_SD_DIR = SD_DIR + File.separator + "GXTX";

    public static final String BASE_NORMAL_FILE_DIR = BASE_SD_DIR + File.separator + "files";

    //public static final String BASE_NORMAL_IMAGE_DIR = BASE_SD_DIR + File.separator + "images";

    public static final String BASE_NORMAL_IMAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "gxtx_images";

    public static final String BASE_NORMAL_SAVE_IMAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final int NOTIFICATION_ID_UPDATE = 0x0;

    public static final int NOTIFICATION_LSDD_ID = 0x1;

    //修改用户头像
    public static String USER_IMAGE_UPDATE_ACTION = "com.feiyou.headstyle.userImg";

    public static final String userHeadName = "gxtx_user_head.jpg";

    public static String FIGHT_DOWN_URL = "http://zs.qqtn.com/zbsq/Apk/tnzbsq_QQTN.apk";

    public static String FIGHT_SOURCE_FILE_PATH = BASE_NORMAL_FILE_DIR +File.separator +"fight.apk";

    public static String FIGHT_DOWN_FILE_PATH = BASE_NORMAL_FILE_DIR +File.separator +"fight_down.apk";

    public static String LSDD_DOWN_URL = "http://vip.cr173.com/Apk/sound_gxtx.apk";

    public static String LSDD_SOURCE_FILE_PATH = BASE_NORMAL_FILE_DIR +File.separator +"lsdd_from_gxtx.apk";

    public static String LSDD_DOWN_FILE_PATH = BASE_NORMAL_FILE_DIR +File.separator +"lsdd_from_gxtx_down.apk";

    public final static String IS_SHOW_TIP = "is_show_tip";

    public static final String GAME_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "feiyousdk";// 用隐藏文件，不让看见；

    public static String GAME_INSTALL_URL = "http://apk.6071.com/diguoshidai/diguoshidai_zbsq.apk";

    public static String GAME_DOWN_FILE_PATH = GAME_DIR +File.separator +"game.apk";

    public static final String GAME_PACKAGE_NAME = "com.regin.dgsd.leqi6071";

    public final static String ALL_ARTICLE = "0";

    public final static String TAKE_PHOTO_ARTICLE = "1";

    public final static String CHAT_ARTICLE = "2";

    public final static String COMMENT_COUNT = "comment_count";

    public final static String LOGIN_SUCCESS = "user_login_success";

    public final static String MESSAGE = "message_hint";

    public final static String PRAISE_SUCCESS = "praise_success";
}
