package com.feiyou.headstyle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.feiyou.headstyle.interfaces.CustomWebViewDelegate;

public class CustomWebView extends WebView {

    public CustomWebViewDelegate delegate;
    private Context context;

    public CustomWebView(Context context) {
        this(context, null);
    }

    public CustomWebView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initWebSettings();
        addJavascriptInterface(new AndroidJSObject(), "AndroidJSObject");
        this.setWebViewClient(new CustomWebViewClient(context));
        this.setWebChromeClient(new WebChromeClient());
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initWebSettings() {
        WebSettings webSettings = this.getSettings();
        webSettings.setLoadsImagesAutomatically(false);
        //手机必须运行 在Android 4.2 或更高才能使用setAllowUniversalAccessFromFileURLs() API 级别 16 + 上才可用。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 默认不使用缓存
        webSettings.setAppCacheMaxSize(8 * 1024 * 1024); // 缓存最多可以有8M
        webSettings.setAllowFileAccess(true); // 可以读取文件缓存(manifest生效)
        String appCaceDir = context.getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCaceDir);
        if ((Build.VERSION.SDK_INT >= 11) && (Build.MANUFACTURER.toLowerCase().contains("lenovo")))
            this.setLayerType(1, null);
    }

    // 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
    private class CustomWebViewClient extends WebViewClient {

        private Context context;

        public CustomWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (delegate != null) {
                delegate.initWithUrl(view.getUrl());
            }
        }
    }

    class AndroidJSObject {

        /**
         * 搜索
         *
         * @param searchKey
         */
        @JavascriptInterface
        public void search(final String searchKey) {
            if (delegate != null) {
                if (searchKey != null) {
                    CustomWebView.this.post(new Runnable() {
                        @Override
                        public void run() {
                            delegate.search(searchKey);
                        }
                    });
                }
            }
        }

        /**
         * @param uid
         * @param oid
         * @param sid
         */
        @JavascriptInterface
        public void showDetail(final String uid, final String oid, final String sid) {
            if (delegate != null) {
                if (uid != null && oid != null && sid != null) {
                    CustomWebView.this.post(new Runnable() {
                        @Override
                        public void run() {
                            delegate.showDetail(uid, oid, sid);
                        }
                    });
                }
            }
        }

        @JavascriptInterface
        public void showImage(final String imageList, final String position) {
            if (delegate != null) {
                if (imageList != null && position != null) {
                    CustomWebView.this.post(new Runnable() {
                        @Override
                        public void run() {
                            delegate.showImage(imageList, position);
                        }
                    });
                }
            }
        }

        @JavascriptInterface
        public void isNotLogin() {
            if (delegate != null) {
                CustomWebView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        delegate.isNotLogin();
                    }
                });
            }
        }

        @JavascriptInterface
        public void addArticle() {
            if (delegate != null) {
                CustomWebView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        delegate.addArticle();
                    }
                });
            }
        }
    }

}
