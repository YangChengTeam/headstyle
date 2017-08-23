package com.feiyou.headstyle.net.listener;

/**
 * Created by admin on 2016/9/6.
 */
public interface OnResponseListener {
    void onSuccess(String response);
    void onError(Exception e);
    void onBefore();
}
