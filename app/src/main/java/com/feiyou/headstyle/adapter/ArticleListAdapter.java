package com.feiyou.headstyle.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.ui.activity.ArticleDetailActivity;
import com.feiyou.headstyle.ui.activity.MainActivity;
import com.feiyou.headstyle.ui.activity.ShowDetailActivity;
import com.feiyou.headstyle.ui.activity.ShowImageListActivity;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.MyViewHolder> implements View.OnClickListener {

    private List<ArticleInfo> articleData;

    private Context mContext;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    private LoginShowListener showListener;

    OKHttpRequest okHttpRequest = null;

    ArticleService articleService = null;

    public interface LoginShowListener {
        void loginShow(String sid, int iszan);
    }

    public void setShowListener(LoginShowListener showListener) {
        this.showListener = showListener;
    }

    //public List<HeadInfo> data;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data);
    }

    public ArticleListAdapter(Context context, List<ArticleInfo> adata) {
        this.mContext = context;
        this.articleData = adata;
        okHttpRequest = new OKHttpRequest();
        articleService = new ArticleService();
    }

    public void addNewDatas(List<ArticleInfo> datas) {
        if (articleData != null) {
            articleData.addAll(datas);
        } else {
            articleData = new ArrayList<ArticleInfo>();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.article_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Logger.e("position---" + position);

        holder.userName.setText(articleData.get(position).nickname);
        Uri uri = Uri.parse(articleData.get(position).simg);
        holder.userImg.setImageURI(uri);
        holder.itemView.setTag(position);
        holder.articleSendTimeTv.setText(articleData.get(position).addtime);
        holder.articleTitleTv.setText(articleData.get(position).scontent);
        holder.commentCountTv.setText(articleData.get(position).comment+"");
        holder.praiseCountTv.setText(articleData.get(position).zan);

        if (articleData.get(position).sex.equals("1")) {
            holder.userGender.setImageResource(R.mipmap.boy_icon);
        } else {
            holder.userGender.setImageResource(R.mipmap.girl_icon);
        }

        if (articleData.get(position).iszan.equals("0")) {
            Drawable noZanDrawable = ContextCompat.getDrawable(mContext, R.mipmap.no_zan_icon);
            noZanDrawable.setBounds(0, 0, noZanDrawable.getMinimumWidth(), noZanDrawable.getMinimumHeight());
            holder.praiseCountTv.setCompoundDrawables(noZanDrawable, null, null, null);
        } else {
            Drawable isZanDrawable = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
            isZanDrawable.setBounds(0, 0, isZanDrawable.getMinimumWidth(), isZanDrawable.getMinimumHeight());
            holder.praiseCountTv.setCompoundDrawables(isZanDrawable, null, null, null);
        }

        holder.praiseCountTv.setTag(position);

        if (!articleData.get(position).ding.equals("1")) {
            holder.topTv.setVisibility(View.GONE);
        } else {
            holder.topTv.setVisibility(View.VISIBLE);
        }

        holder.topTv.setTag(position);

        final String cimgs = articleData.get(position).cimg;
        HeadWallAdapter gridViewAdapter = new HeadWallAdapter(mContext);

        if (cimgs != null) {
            List<HeadInfo> data = new ArrayList<HeadInfo>();
            String[] imgs = cimgs.split("\\|");
            for (int i = 0; i < imgs.length; i++) {
                HeadInfo tempHeadInfo = new HeadInfo();
                tempHeadInfo.hurl = imgs[i];
                data.add(tempHeadInfo);
            }
            gridViewAdapter.addItemDatas(data);
        }

        holder.articlePhotoGridView.setAdapter(gridViewAdapter);
        holder.articlePhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ToastUtils.show(mContext,"选择图片---"+position);

                Intent intent = new Intent(mContext, ShowImageListActivity.class);

                //String imageList = "http://pic.qqtn.com/up/2016-9/2016090611122547066.jpg,http://pic.qqtn.com/up/2016-9/14749389135118192.jpg,http://pic.qqtn.com/up/2016-6/2016062917512971884.jpg";
                intent.putExtra("imageList", cimgs);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
                //进入图片浏览时的动画
                ((Activity) mContext).overridePendingTransition(R.anim.image_show_in, R.anim.image_show_out);
            }
        });

        final String sid = articleData.get(position).sid;

        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //评论
                Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                intent.putExtra("sid", sid);
                mContext.startActivity(intent);
            }
        });

        final int iszan = StringUtils.isEmpty(articleData.get(position).iszan) == true ? 0 : Integer.parseInt(articleData.get(position).iszan);
        final int zan = StringUtils.isEmpty(articleData.get(position).zan) == true ? 0 : Integer.parseInt(articleData.get(position).zan);
        holder.pariseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtils.show(mContext,"点赞");

                if (AppUtils.isLogin(mContext)) {
                    if (iszan == 0) {
                        final Map<String, String> params = new HashMap<String, String>();
                        UserInfo userInfo = (UserInfo) PreferencesUtils.getObject(mContext, Constant.USER_INFO, UserInfo.class);
                        params.put("sid", sid);
                        if (userInfo != null) {
                            params.put("uid", userInfo.uid);
                            params.put("oid", userInfo.openid);
                        }

                        okHttpRequest.aget(Server.UP_ZAN_DATA, params, new OnResponseListener() {
                            @Override
                            public void onSuccess(String response) {
                                boolean result = articleService.praise(response);
                                if (result) {
                                    Drawable isZanDrawable = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
                                    isZanDrawable.setBounds(0, 0, isZanDrawable.getMinimumWidth(), isZanDrawable.getMinimumHeight());
                                    holder.praiseCountTv.setCompoundDrawables(isZanDrawable, null, null, null);
                                    holder.praiseCountTv.setText((zan + 1) + "");

                                } else {
                                    ToastUtils.show(mContext, "操作失败，请稍后重试");
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }

                            @Override
                            public void onBefore() {
                            }
                        });
                    } else {
                        ToastUtils.show(mContext, "已点过赞了");
                    }

                } else {
                    showListener.loginShow(sid, iszan);
                }
            }
        });
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
        return articleData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView userImg;
        TextView userName;
        TextView topTv;//置顶
        ImageView userGender;
        TextView articleSendTimeTv;
        TextView articleTitleTv;
        GridView articlePhotoGridView;
        TextView commentCountTv;
        TextView praiseCountTv;
        LinearLayout commentLayout;
        LinearLayout pariseLayout;

        public MyViewHolder(View view) {
            super(view);

            userImg = (SimpleDraweeView) view.findViewById(R.id.article_user_img);
            userName = (TextView) view.findViewById(R.id.article_user_name);
            topTv = (TextView) view.findViewById(R.id.top_tv);
            userGender = (ImageView) view.findViewById(R.id.user_gender_icon);
            articleSendTimeTv = (TextView) view.findViewById(R.id.article_send_time);
            articleTitleTv = (TextView) view.findViewById(R.id.article_title);
            articlePhotoGridView = (GridView) view.findViewById(R.id.article_photo_list);
            commentCountTv = (TextView) view.findViewById(R.id.comment_count_tv);
            praiseCountTv = (TextView) view.findViewById(R.id.praise_count_tv);
            commentLayout = (LinearLayout) view.findViewById(R.id.comment_layout);
            pariseLayout = (LinearLayout) view.findViewById(R.id.praise_layout);
        }
    }
}