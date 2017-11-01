package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.HeadInfo;

import java.util.List;

public class ImageItemAdapter extends BaseQuickAdapter<HeadInfo, BaseViewHolder> {

    private Context mContext;

    public ImageItemAdapter(Context context, List<HeadInfo> datas) {
        super(R.layout.photo_layout, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final HeadInfo item) {
        Glide.with(mContext).load(item.getHurl()).into((ImageView) helper.getConvertView().findViewById(R.id.photo));
    }
}