package com.feiyou.headstyle.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.feiyou.headstyle.db.greendao.DaoMaster;
import com.feiyou.headstyle.db.greendao.DaoSession;


/**
 * Created by zhangkai on 16/9/12.
 */

/**
 *  DbUtil is a orm for sqlite base on Greendao.
 *
 * */
public class DbUtil {
    /**
     *  Get a Greendao Session, now you can opration models.
     *
     *  @param context  The context of Application or Activity,  and so on.
     *  @return DaoSession The Session of GreenDao Session
     * */
    public static DaoSession getSession(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "headstyle-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}