package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.HeadInfo;

import java.util.List;

public class CommunityImageAdapter extends BaseQuickAdapter<HeadInfo, BaseViewHolder> {

    private Context mContext;

    public CommunityImageAdapter(Context context, List<HeadInfo> datas) {
        super(R.layout.community_image_item, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final HeadInfo headInfo) {
        //GlideHelper.imageView(mContext, (ImageView) helper.getConvertView().findViewById(R.id.iv_community_note), imgUrl, R.mipmap.def_logo);transform(new GlideCircleTransform(mContext))
        Glide.with(mContext).load(headInfo.getHurl()).into((ImageView) helper.getConvertView().findViewById(R.id.iv_community_note));
    }
}