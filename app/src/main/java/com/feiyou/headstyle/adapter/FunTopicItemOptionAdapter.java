package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.TestTopicItemInfo;
import com.feiyou.headstyle.util.StringUtils;

import java.util.List;

/**
 * Created by admin on 2017/9/14.
 */

public class FunTopicItemOptionAdapter extends BaseQuickAdapter<TestTopicItemInfo, BaseViewHolder> {
    private Context mContext;

    private int sPosition = -1;

    public FunTopicItemOptionAdapter(Context context, List<TestTopicItemInfo> datas) {
        super(R.layout.fun_test_topic_item_option, datas);
        this.mContext = context;
    }

    public void setSelectItem(int pos) {
        sPosition = pos;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final TestTopicItemInfo item) {
        helper.setText(R.id.tv_topic_option, StringUtils.numToLetter(helper.getAdapterPosition()) + item.content);
        if (sPosition > -1 && helper.getAdapterPosition() == sPosition) {
            helper.setTextColor(R.id.tv_topic_option, ContextCompat.getColor(mContext,R.color.white));
            helper.setBackgroundRes(R.id.layout_topic_option, R.drawable.topic_item_selecet_bg);
        } else {
            helper.setTextColor(R.id.tv_topic_option, ContextCompat.getColor(mContext,R.color.dialog_content_color));
            helper.setBackgroundRes(R.id.layout_topic_option, R.drawable.topic_item_bg);
        }
    }
}
