package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.crop.ClipImageLayout;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

public class ImageCropActivity extends BaseActivity {

    @BindView(R.id.qq_nick_name)
    TextView qqNickName;

    @BindView(R.id.id_clipImageLayout)
    ClipImageLayout mClipImageLayout;

    @BindView(R.id.crop_select_btn)
    Button cropSelectBtn;

    @BindView(R.id.crop_cancel_btn)
    Button cropCancelBtn;

    private String imageUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_crop;
    }

    public void initViews() {
        super.initViews();
    }

    @Override
    public void loadData() {

        mClipImageLayout.setCropImagePadding(150);

        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && bundle.getString("screen_name") != null) {
            qqNickName.setText(bundle.getString("screen_name"));
        }

        if (bundle != null && bundle.getString("imageUrl") != null) {
            imageUrl = bundle.getString("imageUrl");
            OkHttpUtils
                    .get()
                    .url(imageUrl)
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onResponse(Bitmap response, int id) {
                            if(response != null){
                                mClipImageLayout.setCropImage(new BitmapDrawable(getResources(),response));
                            }else{
                                ToastUtils.show(ImageCropActivity.this,R.string.image_load_fail);
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtils.show(ImageCropActivity.this,R.string.image_load_fail);
                            Logger.e("图片加载失败");
                        }
                    });
        }

    }

    @OnClick(R.id.crop_select_btn)
    public void cropSelect(View view) {
        Bitmap bitmap = mClipImageLayout.clip();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();

        /*Intent intent = new Intent(this, ShowImageActivity.class);
        intent.putExtra("bitmap", datas);
        startActivity(intent);*/
    }

    @OnClick(R.id.crop_cancel_btn)
    public void cropCancel(View view) {
        finish();
    }
}
