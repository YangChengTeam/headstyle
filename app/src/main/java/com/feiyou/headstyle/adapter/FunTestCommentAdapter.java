package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.FunTestCommentInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.GlideHelper;
import com.feiyou.headstyle.util.PreferencesUtils;

import java.util.List;


public class FunTestCommentAdapter extends BaseQuickAdapter<FunTestCommentInfo, BaseViewHolder> {

    private Context mContext;

    private UserInfo userInfo;

    public interface AgreeListener {
        void agreeComment(int pos);
    }

    public AgreeListener agreeListener;

    public void setAgreeListener(AgreeListener agreeListener) {
        this.agreeListener = agreeListener;
    }

    public FunTestCommentAdapter(Context context, List<FunTestCommentInfo> datas) {
        super(R.layout.fun_test_comment_list_item, datas);
        this.mContext = context;
        userInfo = (UserInfo) PreferencesUtils.getObject(mContext, Constant.USER_INFO, UserInfo.class);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final FunTestCommentInfo item) {
        helper.setText(R.id.tv_comment_user_name, item.nickname)
                .setText(R.id.tv_fun_test_send_time, item.addtime)
                .setText(R.id.tv_comment_content, item.scontent)
                .setText(R.id.tv_agree_count, item.zan + "");
        GlideHelper.circleImageView(mContext, (ImageView) helper.getConvertView().findViewById(R.id.iv_user_img), item.simg, R.mipmap.user_default_icon);

        TextView agreeTextView = (TextView) helper.getConvertView().findViewById(R.id.tv_agree_count);

        agreeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeListener.agreeComment(helper.getAdapterPosition() - 1);
            }
        });

        if(item.iszan == 0){
            Drawable noZan = ContextCompat.getDrawable(mContext, R.mipmap.no_zan_icon);
            noZan.setBounds(0, 0, noZan.getMinimumWidth(), noZan.getMinimumHeight());
            agreeTextView.setCompoundDrawables(noZan, null, null, null);
            agreeTextView.setClickable(true);
        }else {
            Drawable isZan = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
            isZan.setBounds(0, 0, isZan.getMinimumWidth(), isZan.getMinimumHeight());
            agreeTextView.setCompoundDrawables(isZan, null, null, null);
            agreeTextView.setClickable(false);
        }

    }

    public void changeView(View cView, int pos) {
        TextView agreeTextView = (TextView) cView.findViewById(R.id.tv_agree_count);
        //String isPraise = this.getData().get(pos).getAgreed();
        //if (isPraise.equals("1")) {
        Drawable isZan = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
        isZan.setBounds(0, 0, isZan.getMinimumWidth(), isZan.getMinimumHeight());
        agreeTextView.setCompoundDrawables(isZan, null, null, null);
        agreeTextView.setText((this.getData().get(pos).zan + 1) + "");
        agreeTextView.setClickable(false);
        /*} else {
            Drawable isZan = ContextCompat.getDrawable(mContext, R.mipmap.no_zan_icon);
            isZan.setBounds(0, 0, isZan.getMinimumWidth(), isZan.getMinimumHeight());
            agreeTextView.setCompoundDrawables(isZan, null, null, null);
        }*/
    }

}

