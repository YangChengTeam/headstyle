package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.util.ScreenUtils;

import java.util.List;

public class ImageItemAdapter extends BaseQuickAdapter<HeadInfo, BaseViewHolder> {

    private Context mContext;

    public ImageItemAdapter(Context context, List<HeadInfo> datas) {
        super(R.layout.photo_layout, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final HeadInfo item) {

        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.example_square);
        int width = ScreenUtils.getWidth(mContext) / 3;
        options.override(width, width);

        Glide.with(mContext).load(item.getHurl()).apply(options).into((ImageView) helper.getConvertView().findViewById(R.id.photo));
    }
}