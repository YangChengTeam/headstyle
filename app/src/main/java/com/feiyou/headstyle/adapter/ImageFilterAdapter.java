package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.FilterEffect;

import java.util.List;

/**
 * Created by admin on 2018/1/8.
 */

public class ImageFilterAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    private Context mContext;
    private List<FilterEffect> filters;

    public ImageFilterAdapter(Context context, List<Integer> datas, List<FilterEffect> filters) {
        super(R.layout.image_filter_item, datas);
        this.mContext = context;
        this.filters = filters;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Integer item) {
        Glide.with(mContext).load(item).into((ImageView) helper.itemView.findViewById(R.id.iv_sticker));
        helper.setText(R.id.filter_name, filters.get(helper.getAdapterPosition()).getTitle());
    }
}
