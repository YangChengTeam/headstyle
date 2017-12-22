package com.feiyou.headstyle.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.GlideHelper;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.GalleryWidget.BasePagerAdapter;
import com.feiyou.headstyle.view.GalleryWidget.GalleryViewPager;
import com.feiyou.headstyle.view.GalleryWidget.UrlPagerAdapter;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

public class ShowImageListActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    GalleryViewPager viewPager;

    @BindView(R.id.iv_down)
    ImageView mDownImageView;

    private int currentPosition;

    private String currentImgUrl;

    private List<String> imgUrlList;

    UrlPagerAdapter pagerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_image_list;
    }

    public void initViews() {
        super.initViews();

        RxView.clicks(mDownImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (imgUrlList != null && imgUrlList.size() > 0) {
                    GlideHelper.downLoadImage(ShowImageListActivity.this, imgUrlList.get(currentPosition));
                }
            }
        });
    }

    @Override
    public void loadData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            currentPosition = bundle.getInt("position");
            currentImgUrl = bundle.getString("current_img_url");
        }
        if (bundle != null && bundle.getStringArrayList("imageList") != null) {

            imgUrlList = bundle.getStringArrayList("imageList");

            if (imgUrlList != null) {
                pagerAdapter = new UrlPagerAdapter(this, imgUrlList);
                pagerAdapter.setOnItemChangeListener(new BasePagerAdapter.OnItemChangeListener() {
                    @Override
                    public void onItemChange(int position) {
                        currentPosition = position;
                    }
                });
                int index = imgUrlList.lastIndexOf(currentImgUrl);
                Logger.e("current index ---> " + index);
                viewPager.setOffscreenPageLimit(3);
                viewPager.setAdapter(pagerAdapter);
                viewPager.setCurrentItem(index);
            }

        } else {
            ToastUtils.show(this, "图片地址有误，请稍后重试");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

}
