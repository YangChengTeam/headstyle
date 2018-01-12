package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;

import java.util.List;

/**
 * Created by admin on 2018/1/8.
 */

public class StickerAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

    private Context mContext;

    public StickerAdapter(Context context, List<Object> datas) {
        super(R.layout.sticker_item, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Object item) {
        Glide.with(mContext).load(item).into((ImageView) helper.itemView.findViewById(R.id.iv_sticker));
    }
}
