package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feiyou.headstyle.R;

public class TypeListAdapter extends RecyclerView.Adapter<TypeListAdapter.MyViewHolder> implements View.OnClickListener {

    private int[] headTypeTextArray;

    private int[] headTypeImageArray;

    private Context mContext;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data);
    }

    public TypeListAdapter(Context context, int[] textData, int[] imgData,int[] cidData) {
        this.mContext = context;
        this.headTypeTextArray = textData;
        this.headTypeImageArray = imgData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.more_head_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv.setText(mContext.getResources().getString(headTypeTextArray[position]));
        holder.headIv.setImageResource(headTypeImageArray[position]);
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return headTypeTextArray.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView headIv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.more_head_type_text);
            headIv = (ImageView) view.findViewById(R.id.more_head_type_image);
        }
    }
}