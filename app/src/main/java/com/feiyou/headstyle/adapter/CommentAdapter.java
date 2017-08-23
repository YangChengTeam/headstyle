package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.net.Uri;
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
    public View getView(int position, View convertView, ViewGroup parent) {

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
        return convertView;
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

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}