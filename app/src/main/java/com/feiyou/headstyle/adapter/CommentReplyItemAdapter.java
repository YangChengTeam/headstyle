package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.text.Html;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.CommentReplyInfo;
import com.feiyou.headstyle.util.GlideHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class CommentReplyItemAdapter extends BaseQuickAdapter<CommentReplyInfo, BaseViewHolder> {

    private Context mContext;

    public CommentReplyItemAdapter(Context context, List<CommentReplyInfo> datas) {
        super(R.layout.comment_reply_item, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final CommentReplyInfo item) {
        GlideHelper.circleImageView(mContext, (ImageView) helper.getConvertView().findViewById(R.id.iv_reply_user_img), item.simg, R.mipmap.user_head_def_icon);
        try {
            helper.setText(R.id.tv_reply_user_name, item.nickname)
                    .setText(R.id.tv_reply_content, Html.fromHtml(URLDecoder.decode(item.scontent, "UTF-8")))
                    .setText(R.id.tv_reply_send_time, item.addtime);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}