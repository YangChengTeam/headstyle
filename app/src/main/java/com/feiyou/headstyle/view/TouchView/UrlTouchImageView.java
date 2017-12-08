/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.feiyou.headstyle.view.TouchView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.ui.activity.ShowImageListActivity;
import com.feiyou.headstyle.util.ImageUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class UrlTouchImageView extends RelativeLayout {
    //protected ProgressBar mProgressBar;
    protected TouchImageView mImageView;

    protected Context mContext;

    public View loadingView;

    private Bitmap downBitmap;

    private boolean isLongClick = false;

    private Button saveImageBtn;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ToastUtils.show(mContext, "图片已保存");
                    break;
                case 1:
                    ToastUtils.show(mContext, "图片保存失败");
                    break;
                default:
                    break;
            }
        }
    };

    public UrlTouchImageView(Context ctx) {
        super(ctx);
        mContext = ctx;
        init();
    }

    public UrlTouchImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        mContext = ctx;
        init();
    }

    public TouchImageView getImageView() {
        return mImageView;
    }

    @SuppressWarnings("deprecation")
    protected void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.touch_image_view, null);
        mImageView= (TouchImageView)view.findViewById(R.id.touch_image_view);
        saveImageBtn = (Button) view.findViewById(R.id.save_image_btn);
        /*mImageView = new TouchImageView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mImageView.setLayoutParams(params);
        mImageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));*/
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        //view.setLayoutParams(params);
        this.addView(view,params);
        //view.setVisibility(GONE);

        //mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleInverse);

        LayoutParams paramsLoad = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        paramsLoad.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsLoad.setMargins(30, 0, 30, 0);
        /*mProgressBar.setLayoutParams(params);
        mProgressBar.setIndeterminate(false);

        mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(mContext, R.drawable.progress_bar));
        mProgressBar.setMax(100);*/

        //加载进度控件View
        loadingView = LayoutInflater.from(mContext).inflate(R.layout.image_loading, null);

        this.addView(loadingView, paramsLoad);

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!isLongClick){
                    loadingView.setVisibility(GONE);
                    ((ShowImageListActivity) mContext).finish();
                    ((ShowImageListActivity) mContext).overridePendingTransition(R.anim.zoomin,
                            R.anim.zoomout);
                //}
                //isLongClick = false;
            }
        });

        saveImageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downBitmap != null) {
                    try {
                        String fileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) + (int) (Math.random() * (9999 - 1000 + 1)) + 1000 + ".png";
                        boolean flag = ImageUtils.writeImageInSDCard(downBitmap, Constant.BASE_NORMAL_IMAGE_DIR, fileName);
                        if (flag) {
                            File saveFile = new File(Constant.BASE_NORMAL_IMAGE_DIR, fileName);

                            if(saveFile.exists()){
                                //获取图片的路径

                                String filePath = saveFile.getAbsolutePath();
                                MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                                        filePath, filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()), null);

                                MediaScannerConnection.scanFile(mContext, new String[]{filePath}, null, null);
                                // 最后通知图库更新
                                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));

                                Message mes = new Message();
                                mes.what = 0;
                                handler.sendMessage(mes);
                            }

                        } else {
                            Message mes = new Message();
                            mes.what = 1;
                            handler.sendMessage(mes);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Message mes = new Message();
                    mes.what = 1;
                    handler.sendMessage(mes);
                }
            }
        });

    }

    public void setUrl(String imageUrl) {
        new ImageLoadTask().execute(imageUrl);
    }

    public void setScaleType(ScaleType scaleType) {
        mImageView.setScaleType(scaleType);
    }

    //No caching load
    public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                int totalLen = conn.getContentLength();
                InputStreamWrapper bis = new InputStreamWrapper(is, 8192, totalLen);
                bis.setProgressListener(new InputStreamWrapper.InputStreamProgressListener() {
                    @Override
                    public void onProgress(float progressValue, long bytesLoaded,
                                           long bytesTotal) {
                        publishProgress((int) (progressValue * 100));
                    }
                });
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                mImageView.setScaleType(ScaleType.CENTER);
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                mImageView.setImageBitmap(bitmap);
            } else {
                mImageView.setScaleType(ScaleType.MATRIX);
                mImageView.setImageBitmap(bitmap);
            }
            downBitmap = bitmap;
            loadingView.setVisibility(VISIBLE);
            mImageView.setVisibility(VISIBLE);
            loadingView.setVisibility(GONE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //mProgressBar.setProgress(values[0]);
        }
    }

}
