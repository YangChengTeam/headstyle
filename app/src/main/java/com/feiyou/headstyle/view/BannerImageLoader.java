package com.feiyou.headstyle.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by zhangkai on 2017/7/26.
 */

public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        try {
            imageView.setBackgroundColor(Color.parseColor("#e0eaf4"));
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            if (path != null) {
                if (path.toString().endsWith(".gif")) {
                    //TODO
                    Glide.with(context).load(path).apply(options).into(imageView);
                } else {
                    Glide.with(context).load(path).apply(options).into(imageView);
                }
            }
        } catch (Exception e) {
            Log.e("BannerImageLoader", e.getMessage());
        }
    }
}
