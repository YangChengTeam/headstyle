package com.feiyou.headstyle.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.feiyou.headstyle.ui.activity.FriendInfoActivity;
import com.feiyou.headstyle.ui.activity.MyInfoActivity;
import com.feiyou.headstyle.ui.activity.ShowImageListActivity;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.SPUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.orhanobut.logger.Logger;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.feiyou.headstyle.ui.fragment.Show1Fragment.showImageUrlList;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.MyViewHolder> implements View.OnClickListener {

    private static final int TYPE_AD = 1;

    private static final int TYPE_DATA = 0;

    private List<Object> articleData;

    private Context mContext;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    private LoginShowListener showListener;

    OKHttpRequest okHttpRequest = null;

    ArticleService articleService = null;

    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap;

    public interface LoginShowListener {
        void loginShow(String sid, int iszan);
    }

    public void setShowListener(LoginShowListener showListener) {
        this.showListener = showListener;
    }

    public void addADViewToPosition(int position, NativeExpressADView adView) {
        if (articleData != null && position >= 0 && position < articleData.size() && adView != null) {
            articleData.add(position, adView);
            notifyDataSetChanged();
        }
    }

    // 移除NativeExpressADView的时候是一条一条移除的
    public void removeADView(int position, NativeExpressADView adView) {
        articleData.remove(position);
        notifyItemRemoved(position); // position为adView在当前列表中的位置
        notifyItemRangeChanged(0, articleData.size() - 1);
    }

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data);
    }

    public ArticleListAdapter(Context context, List<Object> adata, HashMap<NativeExpressADView, Integer> adViewPositionMap) {

        this.mContext = context;
        this.articleData = adata;
        okHttpRequest = new OKHttpRequest();
        articleService = new ArticleService();
        this.mAdViewPositionMap = adViewPositionMap;
    }

    public void addNewDatas(List<Object> datas) {
        if (articleData == null) {
            articleData = new ArrayList<Object>();
        }

        if (datas != null) {
            articleData.addAll(datas);
            createImageUrlList();
        } else {
            articleData.clear();
        }
        notifyDataSetChanged();
    }

    public List<Object> getArticleData() {
        return articleData;
    }

    public void setArticleData(List<Object> articleData) {
        this.articleData = articleData;
    }

    @Override
    public int getItemViewType(int position) {
        return articleData.get(position) instanceof NativeExpressADView ? TYPE_AD : TYPE_DATA;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = (viewType == TYPE_AD) ? R.layout.item_express_ad : R.layout.article_list_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        view.setOnClickListener(this);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT,ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        params.setMargins(SizeUtils.dp2px(4),SizeUtils.dp2px(4),SizeUtils.dp2px(4),SizeUtils.dp2px(4));
        view.setLayoutParams(params);
        return new MyViewHolder(view);
    }

    public void createImageUrlList() {
        if (articleData != null) {
            for (int i = 0; i < articleData.size(); i++) {
                if((getItemViewType(i) == TYPE_DATA)){
                    final String cimgs = ((ArticleInfo)articleData.get(i)).cimg;
                    if (cimgs != null) {
                        String[] imgs = cimgs.split("\\|");

                        for (int m = imgs.length - 1; m >= 0; m--) {
                            if (!showImageUrlList.contains(imgs[m])) {
                                showImageUrlList.add(imgs[m]);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Logger.e("position---" + position);

        int type = getItemViewType(position);
        if (TYPE_AD == type) {
            Logger.e("ad type--->" + TYPE_AD);

            final NativeExpressADView adView = (NativeExpressADView) articleData.get(position);
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
            SimpleDraweeView userImg = holder.userImg;
            TextView userName = holder.userName;
            TextView topTv = holder.topTv;
            ImageView userGender = holder.userGender;
            TextView articleSendTimeTv = holder.articleSendTimeTv;
            EditText articleTitleTv = holder.articleTitleTv;
            RecyclerView imagesRecyclerView = holder.imagesRecyclerView;
            TextView commentCountTv = holder.commentCountTv;
            final TextView praiseCountTv = holder.praiseCountTv;
            LinearLayout commentLayout = holder.commentLayout;
            final LinearLayout pariseLayout = holder.pariseLayout;

            holder.itemView.setTag(position);
            userName.setText(((ArticleInfo)articleData.get(position)).nickname);
            Uri uri = Uri.parse(((ArticleInfo)articleData.get(position)).simg);
            userImg.setImageURI(uri);

            holder.topTv.setTag(position);

            articleSendTimeTv.setText(((ArticleInfo)articleData.get(position)).addtime);
            articleTitleTv.setText(((ArticleInfo)articleData.get(position)).scontent);
            commentCountTv.setText(((ArticleInfo)articleData.get(position)).comment + "");
            praiseCountTv.setText(((ArticleInfo)articleData.get(position)).zan + "");

            if (((ArticleInfo)articleData.get(position)).sex.equals("1")) {
                userGender.setImageResource(R.mipmap.boy_icon);
            } else {
                userGender.setImageResource(R.mipmap.girl_icon);
            }

            if (((ArticleInfo)articleData.get(position)).iszan == 0) {
                Drawable noZanDrawable = ContextCompat.getDrawable(mContext, R.mipmap.no_zan_icon);
                noZanDrawable.setBounds(0, 0, noZanDrawable.getMinimumWidth(), noZanDrawable.getMinimumHeight());
                praiseCountTv.setCompoundDrawables(noZanDrawable, null, null, null);
            } else {
                Drawable isZanDrawable = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
                isZanDrawable.setBounds(0, 0, isZanDrawable.getMinimumWidth(), isZanDrawable.getMinimumHeight());
                praiseCountTv.setCompoundDrawables(isZanDrawable, null, null, null);
            }

            praiseCountTv.setTag(position);

            if (!((ArticleInfo)articleData.get(position)).ding.equals("1")) {
                topTv.setVisibility(View.GONE);
            } else {
                topTv.setVisibility(View.VISIBLE);
            }

            topTv.setTag(position);

            userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo cUserInfo = (UserInfo) PreferencesUtils.getObject(mContext, Constant.USER_INFO, UserInfo.class);
                    if (cUserInfo != null && cUserInfo.getUid().equals(((ArticleInfo)articleData.get(position)).uid)) {
                        Intent intent = new Intent(mContext, MyInfoActivity.class);
                        mContext.startActivity(intent);
                    } else {
                        if (articleData.get(position) != null) {
                            Intent intent = new Intent(mContext, FriendInfoActivity.class);
                            intent.putExtra("fuid", ((ArticleInfo)articleData.get(position)).uid);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });

            final String cimgs = ((ArticleInfo)articleData.get(position)).cimg;
            //HeadWallAdapter gridViewAdapter = new HeadWallAdapter(mContext);

            CommunityImageAdapter communityImageAdapter = new CommunityImageAdapter(mContext, null);
            imagesRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            imagesRecyclerView.setAdapter(communityImageAdapter);

            final List<HeadInfo> imageDatas = new ArrayList<HeadInfo>();
            if (cimgs != null) {
                String[] imgs = cimgs.split("\\|");
                for (int i = imgs.length - 1; i >= 0; i--) {
                    HeadInfo tempHeadInfo = new HeadInfo();
                    tempHeadInfo.setHurl(imgs[i]);
                    imageDatas.add(tempHeadInfo);
                }
                //gridViewAdapter.addItemDatas(data);
                communityImageAdapter.setNewData(imageDatas);
            }

            communityImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(mContext, ShowImageListActivity.class);

                    intent.putStringArrayListExtra("imageList", (ArrayList<String>) showImageUrlList);
                    intent.putExtra("position", position);
                    intent.putExtra("current_img_url", imageDatas.get(position).getHurl());
                    mContext.startActivity(intent);
                    //进入图片浏览时的动画
                    ((Activity) mContext).overridePendingTransition(R.anim.image_show_in, R.anim.image_show_out);
                }
            });

            final String sid = ((ArticleInfo)articleData.get(position)).sid;
            commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //评论
                    Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                    intent.putExtra("sid", sid);
                    mContext.startActivity(intent);
                }
            });

            final int iszan = ((ArticleInfo)articleData.get(position)).iszan;
            final int zan = ((ArticleInfo)articleData.get(position)).zan;
            pariseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ToastUtils.show(mContext,"点赞");

                    if (AppUtils.isLogin(mContext)) {
                        if (iszan == 0) {
                            pariseLayout.setClickable(false);
                            final Map<String, String> params = new HashMap<String, String>();
                            UserInfo userInfo = (UserInfo) PreferencesUtils.getObject(mContext, Constant.USER_INFO, UserInfo.class);
                            params.put("sid", sid);
                            if (userInfo != null) {
                                params.put("uid", userInfo != null ? userInfo.uid : "");
                                params.put("oid", userInfo != null ? userInfo.openid : "");
                            }

                            okHttpRequest.aget(Server.UP_ZAN_DATA, params, new OnResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    boolean result = articleService.praise(response);
                                    if (result) {
                                        Drawable isZanDrawable = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
                                        isZanDrawable.setBounds(0, 0, isZanDrawable.getMinimumWidth(), isZanDrawable.getMinimumHeight());
                                        praiseCountTv.setCompoundDrawables(isZanDrawable, null, null, null);
                                        praiseCountTv.setText((zan + 1) + "");

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
        public FrameLayout container;
        SimpleDraweeView userImg;
        TextView userName;
        TextView topTv;//置顶
        ImageView userGender;
        TextView articleSendTimeTv;
        EditText articleTitleTv;
        //GridView articlePhotoGridView;
        RecyclerView imagesRecyclerView;
        TextView commentCountTv;
        TextView praiseCountTv;
        LinearLayout commentLayout;
        LinearLayout pariseLayout;

        public MyViewHolder(View view) {
            super(view);
            container = (FrameLayout) view.findViewById(R.id.express_ad_container);
            userImg = (SimpleDraweeView) view.findViewById(R.id.article_user_img);
            userName = (TextView) view.findViewById(R.id.article_user_name);
            topTv = (TextView) view.findViewById(R.id.top_tv);
            userGender = (ImageView) view.findViewById(R.id.user_gender_icon);
            articleSendTimeTv = (TextView) view.findViewById(R.id.article_send_time);
            articleTitleTv = (EditText) view.findViewById(R.id.article_title);
            //articlePhotoGridView = (GridView) view.findViewById(R.id.article_photo_list);
            imagesRecyclerView = (RecyclerView) view.findViewById(R.id.imgs_list);
            commentCountTv = (TextView) view.findViewById(R.id.comment_count_tv);
            praiseCountTv = (TextView) view.findViewById(R.id.praise_count_tv);
            commentLayout = (LinearLayout) view.findViewById(R.id.comment_layout);
            pariseLayout = (LinearLayout) view.findViewById(R.id.praise_layout);
        }
    }
}