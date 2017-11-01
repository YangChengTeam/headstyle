package com.feiyou.headstyle.util;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.feiyou.headstyle.view.GlideCircleTransformation;

/**
 * Created by zhangkai on 2017/8/8.
 */

public class GlideHelper {
    public static void circleBorderImageView(final Context context, ImageView imageView, String url, int
            placehorder, float borderwidth, int bordercolor) {
        RequestOptions options = new RequestOptions();
        if (placehorder != 0) {
            options.placeholder(placehorder);
        }
        options.transform(new GlideCircleTransformation(context, borderwidth,
                bordercolor));

        if (AppUtils.isValidContext(context)) {
            Glide.with(context).load(url).apply(options).into(imageView);
        }
    }

    public static void circleImageView(final Context context, ImageView imageView, String url, int
            placehorder) {
        circleBorderImageView(context, imageView, url, placehorder, 0, Color.WHITE);
    }

    public static void imageView(final Context context, ImageView imageView, String url, int
            placehorder) {
        RequestOptions options = new RequestOptions();
        if (placehorder != 0) {
            options.placeholder(placehorder);
        }
        if (AppUtils.isValidContext(context)) {
            Glide.with(context).load(url).apply(options).into(imageView);
        }
    }

    public static void imageView(final Context context, ImageView imageView, String url, int
            placehorder, int width, int height) {
        RequestOptions options = new RequestOptions();
        if (placehorder != 0) {
            options.placeholder(placehorder);
        }
        options.override(width, height);
        if (AppUtils.isValidContext(context)) {
            Glide.with(context).load(url).apply(options).into(imageView);
        }
    }

}
