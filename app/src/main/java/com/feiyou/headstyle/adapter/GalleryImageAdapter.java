package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.HeadInfo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class GalleryImageAdapter extends PagerAdapter {
    Context context;
    private List<HeadInfo> images;

    public GalleryImageAdapter(Context context, List<HeadInfo> images) {
        this.context = context;
        this.images = images;
    }

    //添加新的单条数据
    public void addNewData(HeadInfo data) {
        if (images != null) {
            images.add(data);
        } else {
            images = new ArrayList<HeadInfo>();
        }
    }

    //添加新的数据集合
    public void addNewDataList(List<HeadInfo> datas) {
        if (images != null) {
            images.addAll(datas);
        } else {
            images = new ArrayList<HeadInfo>();
        }
    }

    public void clearDatas(){
        if (images != null && images.size() > 0) {
            images.clear();
            images = null;
        }
    }

    @Override
    public int getCount() {
        //Logger.e("getCountsize---"+images.size());
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        /*ImageView imageView = new ImageView(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(GalImages[position]);*/

        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, null);

        SimpleDraweeView squareDraweeView = (SimpleDraweeView) view.findViewById(R.id.square_head_image);
        SimpleDraweeView circleDraweeView = (SimpleDraweeView) view.findViewById(R.id.circle_head_image);

        Uri uri = Uri.parse(images.get(position).hurl);
        squareDraweeView.setImageURI(uri);
        circleDraweeView.setImageURI(uri);

        ((ViewPager) container).addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
