package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.MyCreateInfo;
import com.feiyou.headstyle.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyCreateAdapter extends BaseAdapter {

    private static final String TAG = "MyArticleAdapter";

    private Context mContext;

    private List<MyCreateInfo> dataList;

    public MyCreateAdapter(Context mContext, List<MyCreateInfo> data) {
        super();
        this.mContext = mContext;
        if (data != null && data.size() > 0) {
            this.dataList = data;
        } else {
            dataList = new ArrayList<MyCreateInfo>();
        }
    }

    public void clear() {
        dataList.clear();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int pos) {
        return dataList.get(pos);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    public void addNewDatas(List<MyCreateInfo> datas) {
        if (dataList != null) {
            dataList.addAll(datas);
        } else {
            dataList = new ArrayList<MyCreateInfo>();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.photo_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (dataList != null && dataList.get(position) != null) {
            if (!StringUtils.isEmpty(dataList.get(position).getPhoto_id())) {
                Uri uri = Uri.parse("file:///" + dataList.get(position).getPhoto_id());
                //holder.headImage.setImageURI(uri);
                Glide.with(mContext).load(uri).into(holder.headImage);
            }
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.photo)
        ImageView headImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}