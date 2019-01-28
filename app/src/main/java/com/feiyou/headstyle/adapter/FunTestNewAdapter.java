package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.FunTestInfo;
import com.orhanobut.logger.Logger;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunTestNewAdapter extends RecyclerView.Adapter<FunTestNewAdapter.MyViewHolder> implements View.OnClickListener {

    private static final int TYPE_AD = 1;

    private static final int TYPE_DATA = 0;

    private Context mContext;

    private int mType = 1; //mType:1首页，mType:2分类页

    private List<Object> datas;

    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap;

    private FunTestNewAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface ShareListener {
        void shareTestTopic(int pos);
    }

    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data);
    }

    public FunTestAdapter.ShareListener shareListener;

    public void setShareListener(FunTestAdapter.ShareListener shareListener) {
        this.shareListener = shareListener;
    }

    public FunTestNewAdapter(Context context, List<Object> mDatas, int type,HashMap<NativeExpressADView, Integer> adViewPositionMap) {
        this.mContext = context;
        this.mType = type;
        this.datas = mDatas;
        this.mAdViewPositionMap = adViewPositionMap;
    }

    public void addADViewToPosition(int position, NativeExpressADView adView) {
        if (datas != null && position >= 0 && position < datas.size() && adView != null) {
            datas.add(position, adView);
            notifyDataSetChanged();
        }
    }

    // 移除NativeExpressADView的时候是一条一条移除的
    public void removeADView(int position, NativeExpressADView adView) {
        datas.remove(position);
        notifyItemRemoved(position); // position为adView在当前列表中的位置
        notifyItemRangeChanged(0, datas.size() - 1);
    }

    public void addNewDatas(List<Object> datas) {
        if(datas == null){
            datas = new ArrayList<Object>();
        }else{
            this.datas = datas;
        }
        notifyDataSetChanged();
    }

    public List<Object> getDatas() {
        return datas;
    }

    public void clearDatas(){
        if(datas != null){
            datas.clear();
            datas = null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position) instanceof NativeExpressADView ? TYPE_AD : TYPE_DATA;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = (viewType == TYPE_AD) ? R.layout.item_express_ad : R.layout.fun_test_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        view.setOnClickListener(this);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT,ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        params.setMargins(SizeUtils.dp2px(4),SizeUtils.dp2px(4),SizeUtils.dp2px(4),SizeUtils.dp2px(4));
        view.setLayoutParams(params);
        return new FunTestNewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        int type = getItemViewType(position);
        if (TYPE_AD == type) {
            Logger.e("ad type--->" + TYPE_AD);

            final NativeExpressADView adView = (NativeExpressADView) datas.get(position);
            mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
            if (holder.container.getChildCount() > 0
                    && holder.container.getChildAt(0) == adView) {
                return;
            }

            if (holder.container.getChildCount() > 0) {
                holder.container.removeAllViews();
            }

            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }
            adView.render(); // 调用render方法后sdk才会开始展示广告
            holder.container.addView(adView);
            holder.itemView.setTag(position);
        }else {

            FunTestInfo funTestInfo = (FunTestInfo) datas.get(position);

            holder.testTitleTv.setText(funTestInfo.title);
            holder.useCountTv.setText(funTestInfo.shareperson);
            holder.shareCountTv.setText(funTestInfo.sharetotal);
            holder.joinCountTv.setText(funTestInfo.shareperson);

            Glide.with(mContext).load(funTestInfo.smallimg).into(holder.testImg);
            holder.itemView.setTag(position);
            holder.shareCountTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int tempIndex = mType == 1 ? position - 1 : position;
//                    if (tempIndex < 0) {
//                        tempIndex = 0;
//                    }
                    shareListener.shareTestTopic(position);
                }
            });
        }
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
        return datas != null ? datas.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout container;
        TextView testTitleTv;
        TextView useCountTv;
        TextView shareCountTv;
        TextView joinCountTv;
        ImageView testImg;

        public MyViewHolder(View view) {
            super(view);
            container = (FrameLayout) view.findViewById(R.id.express_ad_container);
            testTitleTv = (TextView) view.findViewById(R.id.tv_fun_test_title);
            useCountTv = (TextView) view.findViewById(R.id.tv_use_count);
            shareCountTv = (TextView) view.findViewById(R.id.tv_share_count);
            joinCountTv = (TextView) view.findViewById(R.id.tv_join_count);
            testImg = (ImageView) view.findViewById(R.id.iv_fun_test);
        }
    }
}