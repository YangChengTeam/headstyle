package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.AgreeInfo;
import com.feiyou.headstyle.util.GlideHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AgreeItemAdapter extends BaseAdapter {

    private Context mContext;

    private List<AgreeInfo> agreeList;

    public AgreeItemAdapter(Context mContext, List<AgreeInfo> agreeList) {
        super();
        this.mContext = mContext;
        if (agreeList == null) {
            this.agreeList = new ArrayList<>();
        } else {
            this.agreeList = agreeList;
        }
    }

    public void clear() {
        if (agreeList != null) {
            agreeList.clear();
        }
    }

    @Override
    public int getCount() {
        return agreeList.size();
    }

    @Override
    public Object getItem(int pos) {
        return agreeList.get(pos);
    }

    public List<AgreeInfo> getAgreeList() {
        return agreeList;
    }

    public void setAgreeList(List<AgreeInfo> agreeList) {
        this.agreeList = agreeList;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.agree_item_view, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AgreeInfo agreeInfo = agreeList.get(position);
        GlideHelper.circleImageView(mContext, holder.userImg, agreeInfo.simg, R.mipmap.user_head_def_icon);
        holder.userName.setText(agreeInfo.nickname);
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.iv_agree_user_head)
        ImageView userImg;
        @BindView(R.id.tv_user_name)
        TextView userName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}