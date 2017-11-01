package com.feiyou.headstyle.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.FunTestInfo;

import java.util.List;

/**
 * Created by admin on 2017/9/14.
 */

public class FunMoreTestAdapter extends BaseQuickAdapter<FunTestInfo, BaseViewHolder> {
    private Context mContext;

    public FunMoreTestAdapter(Context context, List<FunTestInfo> datas) {
        super(R.layout.fun_more_test_item, datas);
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final FunTestInfo item) {
        helper.setText(R.id.tv_fun_test_title, item.title);
    }

}
