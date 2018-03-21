package com.feiyou.headstyle.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.view.GlideCircleTransformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public static void circleBorderImageView(final Context context, ImageView imageView, int res, int
            placehorder, float borderwidth, int bordercolor) {
        RequestOptions options = new RequestOptions();
        if (placehorder != 0) {
            options.placeholder(placehorder);
        }
        options.transform(new GlideCircleTransformation(context, borderwidth,
                bordercolor));

        if (AppUtils.isValidContext(context)) {
            Glide.with(context).load(res).apply(options).into(imageView);
        }
    }

    public static void circleImageView(final Context context, ImageView imageView, String url, int
            placehorder) {
        circleBorderImageView(context, imageView, url, placehorder, 0, 0);
    }

    public static void circleImageView(final Context context, ImageView imageView, int res, int
            placehorder) {
        circleBorderImageView(context, imageView, res, placehorder, 0, Color.WHITE);
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

    public static void downLoadImage(final Context context, String url) {
        RequestOptions options = new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //指定图片大小(原图)
        Glide.with(context).asBitmap().load(url).apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        Bitmap tempBitmip = resource;
                        //图片名称
                        String mFileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) + (int) (Math.random() * (9999 - 1000 + 1)) + 1000 + ".png";
                        //图片路径
                        File tempPath = new File(Constant.BASE_NORMAL_IMAGE_DIR);
                        //保存图片
                        File newFile = saveBitmap(tempPath, mFileName, tempBitmip);
                        String backgroundPath = newFile.getPath();
                        if (backgroundPath != null) {

                            // 其次把文件插入到系统图库
                            try {
                                MediaStore.Images.Media.insertImage(context.getContentResolver(), newFile.getAbsolutePath(), backgroundPath, null);

                                MediaScannerConnection.scanFile(context, new String[]{backgroundPath}, null, null);
                                // 最后通知图库更新
                                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + backgroundPath)));

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            // 最后通知图库更新
                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(new File(newFile.getPath()))));

                            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /**
     * 保存图片方法
     */

    public static File saveBitmap(File path, String imgName, Bitmap bitmap) {

        File f = new File(path, imgName);
        //创建文件夹
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return f;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}