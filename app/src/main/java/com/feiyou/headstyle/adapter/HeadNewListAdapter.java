package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.util.ScreenUtils;
import com.feiyou.headstyle.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by myflying on 2018/9/28.
 */
public class HeadNewListAdapter extends RecyclerView.Adapter<HeadNewListAdapter.MyViewHolder> {

    private Context mContext;

    private List<HeadInfo> dataList;

    public HeadNewListAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        dataList = new ArrayList<HeadInfo>();
    }

    public HeadNewListAdapter(Context mContext, List<HeadInfo> data) {
        super();
        this.mContext = mContext;
        if (data != null && data.size() > 0) {
            this.dataList = data;
        } else {
            dataList = new ArrayList<HeadInfo>();
        }
    }

    public List<HeadInfo> getDataList() {
        return dataList;
    }

    public void clear() {
        if (dataList != null) {
            dataList.clear();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (dataList != null && dataList.get(position) != null) {
            if (!StringUtils.isEmpty(dataList.get(position).getHurl())) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.mipmap.example_square);
                int width = ScreenUtils.getWidth(mContext) / 3;
                options.override(width, width);
                Glide.with(mContext).load(dataList.get(position).getHurl()).apply(options).into(holder.headImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photo)
        ImageView headImage;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
