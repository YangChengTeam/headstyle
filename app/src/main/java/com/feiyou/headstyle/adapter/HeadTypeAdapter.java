package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HeadTypeAdapter extends BaseAdapter {

    private static final String TAG = "HeadTypeAdapter";

    private Context mContext;

    private int[] imageList;

    private int[] typeNameList;

    public HeadTypeAdapter(Context mContext, int[] typeNameList,int[] imageList) {
        super();
        this.mContext = mContext;
        this.typeNameList = typeNameList;
        this.imageList = imageList;
    }

    public void clear() {
        // dataList.clear();
    }

    @Override
    public int getCount() {
        return imageList.length;
    }

    @Override
    public Object getItem(int pos) {
        return imageList[pos];
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.type_item_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.headTypeImage.setImageResource(imageList[position]);
        holder.headTypeTv.setText(typeNameList[position]);
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.head_type_layout)
        LinearLayout headTypeLayout;

        @BindView(R.id.head_type_image)
        ImageView headTypeImage;

        @BindView(R.id.head_type_text)
        TextView headTypeTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}