package com.feiyou.headstyle.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * ScreenUtils
 * <ul>
 * <strong>Convert between dp and sp</strong>
 * <li>{@link ScreenUtils#dpToPx(Context, float)}</li>
 * <li>{@link ScreenUtils#pxToDp(Context, float)}</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-14
 */
public class ScreenUtils {

    private ScreenUtils() {
        throw new AssertionError();
    }

    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPxInt(Context context, float dp) {
        return (int)(dpToPx(context, dp) + 0.5f);
    }

    public static int pxToDpCeilInt(Context context, float px) {
        return (int)(pxToDp(context, px) + 0.5f);
    }

    @SuppressLint("NewApi")
    public static int getHeight(Context paramContext) {
        if (Build.VERSION.SDK_INT >= 13) {
            Display localDisplay = ((WindowManager) paramContext.getSystemService(Application.WINDOW_SERVICE)).getDefaultDisplay();
            Point localPoint = new Point();
            localDisplay.getSize(localPoint);
            return localPoint.y;
        }
        return paramContext.getResources().getDisplayMetrics().heightPixels;
    }

    @SuppressLint("NewApi")
    public static int getWidth(Context paramContext) {
        if (Build.VERSION.SDK_INT >= 13) {
            Display localDisplay = ((WindowManager) paramContext.getSystemService(Application.WINDOW_SERVICE)).getDefaultDisplay();
            Point localPoint = new Point();
            localDisplay.getSize(localPoint);
            return localPoint.x;
        }
        return paramContext.getResources().getDisplayMetrics().widthPixels;
    }
}
