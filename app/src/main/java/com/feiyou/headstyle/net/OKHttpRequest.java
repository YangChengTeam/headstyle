package com.feiyou.headstyle.net;

import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.util.ImgUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by admin on 2016/9/6.
 */
public class OKHttpRequest {

    public void aget(String url, Map<String, String> params, final OnResponseListener onResponseListener) {

        OkHttpUtils.get().params(params).url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Logger.e("--- onError data aget---");
                onResponseListener.onError(e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                //Logger.e("--- data success---" + response);
                if (onResponseListener != null) {
                    onResponseListener.onSuccess(response);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                onResponseListener.onBefore();
            }
        });
    }

    public void aget(String url, Map<String, String> params, List<File> files, final OnResponseListener onResponseListener) {

        PostFormBuilder builder = OkHttpUtils.post().params(params).url(url);
        if (files != null) {
            //修改上传图片的尺寸
            files = ImgUtils.changeFileSize(files);

            for (int i = 0; i < files.size(); i++) {
                String fileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) +(int)(Math.random()*(9999-1000+1))+1000 + ".jpg";
                Logger.e("fileName---"+fileName);
                builder.addFile("file" + (i + 1), fileName, files.get(i));
            }
        }else{
            builder.addFile("file1", null, null);
        }

        builder.build().execute(new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                onResponseListener.onBefore();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                Logger.e("---data error---");
                onResponseListener.onError(e);
            }

            @Override
            public void onResponse(String response, int id) {
                //Logger.e("--- data success---" + response);
                if (onResponseListener != null) {
                    onResponseListener.onSuccess(response);
                }
            }
        });
    }

}

