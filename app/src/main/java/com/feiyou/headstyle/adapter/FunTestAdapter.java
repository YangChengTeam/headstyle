package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.FunTestInfo;

import java.util.List;

/**
 * Created by admin on 2017/9/14.
 */

public class FunTestAdapter extends BaseQuickAdapter<FunTestInfo, BaseViewHolder> {
    private Context mContext;
    private int mType = 1; //mType:1首页，mType:2分类页
    public interface ShareListener {
        void shareTestTopic(int pos);
    }

    public ShareListener shareListener;

    public void setShareListener(ShareListener shareListener) {
        this.shareListener = shareListener;
    }

    public FunTestAdapter(Context context, List<FunTestInfo> datas,int type) {
        super(R.layout.fun_test_item, datas);
        this.mContext = context;
        this.mType = type;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final FunTestInfo item) {
        helper.setText(R.id.tv_fun_test_title, item.title).setText(R.id.tv_use_count, item.shareperson + "人使用").setText(R.id.tv_share_count, item.sharetotal).setText(R.id.tv_join_count, item.shareperson);
        Glide.with(mContext).load(item.smallimg).into((ImageView) helper.getConvertView().findViewById(R.id.iv_fun_test));

        helper.getConvertView().findViewById(R.id.tv_share_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempIndex = mType == 1 ? helper.getAdapterPosition() - 1 : helper.getAdapterPosition();
                if (tempIndex < 0) {
                    tempIndex = 0;
                }
                shareListener.shareTestTopic(tempIndex);
            }
        });

    }
}
