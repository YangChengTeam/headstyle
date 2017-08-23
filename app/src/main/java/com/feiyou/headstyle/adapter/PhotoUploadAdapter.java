package com.feiyou.headstyle.adapter;

import android.content.ContentResolver;
import android.content.Context;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.model.PhotoInfo;


public class PhotoUploadAdapter extends BaseAdapter {

    private static final String TAG = "PhotoUploadAdapter";

    private Context mContext;

    private List<PhotoInfo> dataList;

    public PhotoUploadAdapter(Context mContext, List<PhotoInfo> data) {
        super();
        this.mContext = mContext;
        if (data != null) {
            this.dataList = data;
        } else {
            dataList = new ArrayList<PhotoInfo>();
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


    public void addNewData(PhotoInfo data) {
        if (dataList != null) {
            dataList.add(data);
        } else {
            dataList = new ArrayList<PhotoInfo>();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.show_photo_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (dataList != null && dataList.get(position) != null) {
            if (dataList.get(position).getPhotoPath() != null) {
                Uri uri = Uri.parse("file:///" + dataList.get(position).getPhotoPath());
                Logger.e("uri===" + dataList.get(position).getPhotoPath());
                holder.headImage.setImageURI(uri);
                holder.deleteIv.setVisibility(View.VISIBLE);
                holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ToastUtils.show(mContext,"删除---"+position);
                        if (dataList != null && dataList.size() > 0) {
                            dataList.remove(position);
                        }

                        //判断图片列表中是否有空白图
                        boolean isHaveEmpty = false;
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).getPhotoPath() == null) {
                                isHaveEmpty = true;
                            }
                        }

                        //没有空白图片时,添加一张空白"+"图
                        if (!isHaveEmpty) {
                            dataList.add(new PhotoInfo());
                        }

                        PhotoUploadAdapter.this.notifyDataSetChanged();
                    }
                });
            } else {
                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(mContext.getResources());
                GenericDraweeHierarchy hierarchy = builder.setPlaceholderImage(R.mipmap.show_add_default_icon)
                        .build();
                holder.headImage.setHierarchy(hierarchy);
                //直接设置BackgroundResource图片会变的模糊
                //holder.headImage.setBackgroundResource(R.mipmap.show_add_default_icon);
            }
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.photo)
        SimpleDraweeView headImage;

        @BindView(R.id.show_photo_delete_iv)
        ImageView deleteIv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}