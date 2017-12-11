package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.ScreenUtils;
import com.feiyou.headstyle.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public class HeadShowItemAdapter extends BaseAdapter {

    private static final String TAG = "HeadShowItemAdapter";

    private Context mContext;

    private List<String> heads;

    private int ivWidth;

    private int ivHeight;

    public HeadShowItemAdapter(Context mContext, List<String> heads) {
        super();
        this.mContext = mContext;
        this.heads = heads;
        ivWidth = ScreenUtils.getWidth(mContext) - SizeUtils.dp2px(mContext, 26);
    }

    public List<String> getHeads() {
        return heads;
    }

    public void setHeads(List<String> heads) {
        this.heads = heads;
    }

    public void addItemData(String imageUrl) {
        if (heads == null) {
            heads = new ArrayList<String>();
        }
        heads.add(imageUrl);
    }

    public void addDatas(List<String> images) {
        if (heads == null) {
            heads = new ArrayList<String>();
        } else {
            heads.clear();
        }
        heads.addAll(images);
    }

    public void clear() {
        heads.clear();
    }

    public void remove(int index) {
        if (index > -1 && index < heads.size()) {
            heads.remove(index);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return heads != null ? heads.size() : 0;
    }

    @Override
    public Object getItem(int pos) {
        return heads.get(pos);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_new_item, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.mHeadShowImageView = (ImageView) convertView.findViewById(R.id.iv_show_head);
            holder.mHeadShowImageView.getLayoutParams().width = ivWidth;
            holder.mHeadShowImageView.getLayoutParams().height = ivWidth;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(heads.get(position)).into(holder.mHeadShowImageView);
        return convertView;
    }


    private static class ViewHolder {
        ImageView mHeadShowImageView;
    }
}