package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.CommentInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CommentAdapter extends BaseAdapter {

    private static final String TAG = "CommentAdapter";

    private Context mContext;

    private List<CommentInfo> commentList;

   // private int[] typeNameList;

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
        // dataList.clear();
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int pos) {
        return commentList.get(pos);
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
        Uri uri = Uri.parse(commentList.get(position).simg);
        holder.userImg.setImageURI(uri);
        holder.userName.setText(commentList.get(position).nickname);
        holder.articleSendTimeTv.setText(commentList.get(position).addtime);
        holder.commentTv.setText(commentList.get(position).scontent);
        holder.agreeTextView.setText((commentList.get(position).zan) + "");
        if(commentList.get(position).iszan == 0){
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
        }else {
            Drawable isZan = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
            isZan.setBounds(0, 0, isZan.getMinimumWidth(), isZan.getMinimumHeight());
            holder.agreeTextView.setCompoundDrawables(isZan, null, null, null);
            holder.agreeTextView.setClickable(false);
        }

        return convertView;
    }

    public void changeView(View cView, int pos) {
        TextView agreeTextView = (TextView) cView.findViewById(R.id.tv_agree_count);
        //String isPraise = this.getData().get(pos).getAgreed();
        //if (isPraise.equals("1")) {
        Drawable isZan = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
        isZan.setBounds(0, 0, isZan.getMinimumWidth(), isZan.getMinimumHeight());
        agreeTextView.setCompoundDrawables(isZan, null, null, null);
        agreeTextView.setText((commentList.get(pos).zan + 1) + "");
        agreeTextView.setClickable(false);
        /*} else {
            Drawable isZan = ContextCompat.getDrawable(mContext, R.mipmap.no_zan_icon);
            isZan.setBounds(0, 0, isZan.getMinimumWidth(), isZan.getMinimumHeight());
            agreeTextView.setCompoundDrawables(isZan, null, null, null);
        }*/
    }


    class ViewHolder {

        @BindView(R.id.article_user_img)
        SimpleDraweeView userImg;
        @BindView(R.id.article_user_name)
        TextView userName;
        @BindView(R.id.article_send_time)
        TextView articleSendTimeTv;
        @BindView(R.id.comment_content_tv)
        TextView commentTv;
        @BindView(R.id.tv_agree_count)
        TextView agreeTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}