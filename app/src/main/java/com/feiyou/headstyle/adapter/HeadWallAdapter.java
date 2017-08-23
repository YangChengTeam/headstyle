package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class HeadWallAdapter extends BaseAdapter {

    private static final String TAG = "HeadWallAdapter";

    private Context mContext;

    private List<HeadInfo> dataList;

    public HeadWallAdapter(Context mContext){
        super();
        this.mContext = mContext;
        dataList = new ArrayList<HeadInfo>();
    }

    public HeadWallAdapter(Context mContext, List<HeadInfo> data) {
        super();
        this.mContext = mContext;
        if(data != null && data.size() >0){
            this.dataList = data;
        }else{
            dataList = new ArrayList<HeadInfo>();
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

    public void addNewDatas(List<HeadInfo> datas){
        if(dataList != null){
            dataList.addAll(datas);
        }else{
            dataList = new ArrayList<HeadInfo>();
        }
    }

    public void addItemDatas(List<HeadInfo> datas){
        if(dataList != null){
            dataList.clear();
            dataList.addAll(datas);
        }else{
            if(datas != null){
                dataList = datas;
            }else{
                dataList = new ArrayList<HeadInfo>();
            }
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

        if(dataList != null && dataList.get(position) != null){
            if(!StringUtils.isEmpty(dataList.get(position).hurl)){
                Uri uri = Uri.parse(dataList.get(position).hurl);
                holder.headImage.setImageURI(uri);
            }
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.photo)
        SimpleDraweeView headImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}