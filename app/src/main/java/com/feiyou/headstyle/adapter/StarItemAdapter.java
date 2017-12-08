package com.feiyou.headstyle.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.StarInfo;

import java.util.List;

public class StarItemAdapter extends BaseQuickAdapter<StarInfo, BaseViewHolder> {

    private Context mContext;

    public StarItemAdapter(Context context, List<StarInfo> datas) {
        super(R.layout.star_list_item, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final StarInfo item) {
        helper.setText(R.id.tv_star_name, item.getStarName());
        if (item.isChecked()) {
            helper.setBackgroundRes(R.id.ck_star, R.mipmap.checked_icon);
        } else {
            helper.setBackgroundRes(R.id.ck_star, 0);
        }
    }
}