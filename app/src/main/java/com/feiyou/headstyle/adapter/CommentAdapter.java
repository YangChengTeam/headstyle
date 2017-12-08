package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.CommentInfo;
import com.feiyou.headstyle.ui.activity.FriendInfoActivity;
import com.feiyou.headstyle.util.GlideHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CommentAdapter extends BaseAdapter {

    private Context mContext;

    private List<CommentInfo> commentList;

    public interface AgreeListener {
        void agreeComment(int pos);
    }

    public AgreeListener agreeListener;

    public void setAgreeListener(AgreeListener agreeListener) {
        this.agreeListener = agreeListener;
    }

    public CommentAdapter(Context mContext, List<CommentInfo> commentList) {
        super();
        this.mContext = mContext;
        this.commentList = commentList;
    }

    public void clear() {
        if (commentList != null) {
            commentList.clear();
        }
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int pos) {
        return commentList.get(pos);
    }

    public List<CommentInfo> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentInfo> commentList) {
        this.commentList = commentList;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CommentInfo commentInfo = commentList.get(position);
        GlideHelper.circleImageView(mContext, holder.userImg, commentInfo.simg, R.mipmap.user_head_def_icon);
        holder.userName.setText(commentInfo.nickname);
        holder.articleSendTimeTv.setText(commentInfo.addtime);
        try {
            holder.commentTv.setText(Html.fromHtml(URLDecoder.decode(commentInfo.scontent,"UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.agreeTextView.setText(commentInfo.zan + "");
        if (commentList.get(position).iszan == 0) {
            Drawable noZan = ContextCompat.getDrawable(mContext, R.mipmap.no_zan_icon);
            noZan.setBounds(0, 0, noZan.getMinimumWidth(), noZan.getMinimumHeight());
            holder.agreeTextView.setCompoundDrawables(noZan, null, null, null);
            holder.agreeTextView.setClickable(true);

            holder.agreeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agreeListener.agreeComment(position);
                }
            });
        } else {
            Drawable isZan = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
            isZan.setBounds(0, 0, isZan.getMinimumWidth(), isZan.getMinimumHeight());
            holder.agreeTextView.setCompoundDrawables(isZan, null, null, null);
            holder.agreeTextView.setClickable(false);
        }
        if (commentInfo.commentnum > 0) {
            holder.replyCountTv.setText(commentInfo.commentnum+" 回复");
        }else{
            holder.replyCountTv.setText("回复");
        }

        holder.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FriendInfoActivity.class);
                intent.putExtra("fuid", commentInfo.uid);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.article_user_img)
        ImageView userImg;
        @BindView(R.id.article_user_name)
        TextView userName;
        @BindView(R.id.article_send_time)
        TextView articleSendTimeTv;
        @BindView(R.id.comment_content_tv)
        TextView commentTv;
        @BindView(R.id.tv_agree_count)
        TextView agreeTextView;
        @BindView(R.id.tv_reply_count)
        TextView replyCountTv;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}