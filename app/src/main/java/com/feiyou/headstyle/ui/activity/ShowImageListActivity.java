package com.feiyou.headstyle.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.GalleryWidget.BasePagerAdapter;
import com.feiyou.headstyle.view.GalleryWidget.GalleryViewPager;
import com.feiyou.headstyle.view.GalleryWidget.UrlPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class ShowImageListActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    GalleryViewPager viewPager;

    private int currentPosition;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_image_list;
    }

    public void initViews() {
        super.initViews();
    }


    @Override
    public void loadData() {

        Bundle bundle = this.getIntent().getExtras();

        if(bundle != null){
            currentPosition = bundle.getInt("position");
        }

        if (bundle != null && bundle.getString("imageList") != null) {
            String imageList = bundle.getString("imageList");
            if(!StringUtils.isEmpty(imageList)){
                /*String[] urls = {
                        "http://img3.imgtn.bdimg.com/it/u=4201345522,2791155945&fm=11&gp=0.jpg",
                        "http://uploads.xuexila.com/allimg/1608/704-160Q5103023.jpg",
                        "http://img2.imgtn.bdimg.com/it/u=1719883226,3198015880&fm=11&gp=0.jpg",
                        "http://img1.imgtn.bdimg.com/it/u=2857028710,1354526997&fm=11&gp=0.jpg",
                        "http://imgsrc.baidu.com/forum/pic/item/622762d0f703918fbd53195e513d269758eec4a7.jpg",
                        "http://img0.imgtn.bdimg.com/it/u=1012743149,55982983&fm=11&gp=0.jpg"
                };*/

                String[] urls = imageList.split("\\|");

                List<String> items = new ArrayList<String>();
                Collections.addAll(items, urls);

                UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);
                pagerAdapter.setOnItemChangeListener(new BasePagerAdapter.OnItemChangeListener() {
                    @Override
                    public void onItemChange(int currentPosition) {
                        //ToastUtils.show(ShowImageListActivity.this, "Current item is " + currentPosition);
                    }
                });

                viewPager.setOffscreenPageLimit(3);
                viewPager.setAdapter(pagerAdapter);
                viewPager.setCurrentItem(currentPosition);
            }

        }else{
            ToastUtils.show(this,"图片地址有误，请稍后重试");
        }
    }

    public void cropSelect(View view) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.zoomin,
                R.anim.zoomout);
    }

}
