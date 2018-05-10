package com.feiyou.headstyle.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
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
import com.feiyou.headstyle.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.feiyou.headstyle.ui.fragment.Show1Fragment.showgGameImageUrlList;

public class PhotosNewListAdapter extends BaseQuickAdapter<ArticleInfo,BaseViewHolder> {

    private Context mContext;

    private List<ArticleInfo> articleData;

    OKHttpRequest okHttpRequest = null;

    ArticleService articleService = null;

    private LoginShowListener showListener;

    public interface LoginShowListener {
        void loginShow(String sid, int iszan);
    }

    public void setShowListener(LoginShowListener showListener) {
        this.showListener = showListener;
    }

    public PhotosNewListAdapter(Context context, List<ArticleInfo> datas) {
        super(R.layout.article_list_item, datas);
        this.mContext = context;
        this.articleData = datas;
        okHttpRequest = new OKHttpRequest();
        articleService = new ArticleService();
    }

    public void addNewDatas(List<ArticleInfo> datas) {
        if (articleData == null) {
            articleData = new ArrayList<ArticleInfo>();
        }

        if (datas != null) {
            articleData.addAll(datas);
            createImageUrlList();
        } else {
            articleData.clear();
        }
    }

    public List<ArticleInfo> getArticleData() {
        return articleData;
    }

    public void setArticleData(List<ArticleInfo> articleData) {
        this.articleData = articleData;
    }

    public void createImageUrlList() {
        if (articleData != null) {
            for (int i = 0; i < articleData.size(); i++) {
                final String cimgs = articleData.get(i).cimg;
                if (cimgs != null) {
                    String[] imgs = cimgs.split("\\|");
                    for (int m = imgs.length - 1; m >= 0; m--) {
                        if (!showgGameImageUrlList.contains(imgs[m])) {
                            showgGameImageUrlList.add(imgs[m]);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, final ArticleInfo item) {

        final int position = helper.getAdapterPosition();

        SimpleDraweeView userImg = helper.getConvertView().findViewById(R.id.article_user_img);
        TextView userName = helper.getConvertView().findViewById(R.id.article_user_name);
        TextView topTv = helper.getConvertView().findViewById(R.id.top_tv);
        ImageView userGender = helper.getConvertView().findViewById(R.id.user_gender_icon);
        TextView articleSendTimeTv = helper.getConvertView().findViewById(R.id.article_send_time);
        EditText articleTitleTv = helper.getConvertView().findViewById(R.id.article_title);
        RecyclerView imagesRecyclerView = helper.getConvertView().findViewById(R.id.imgs_list);
        TextView commentCountTv = helper.getConvertView().findViewById(R.id.comment_count_tv);
        final TextView praiseCountTv = helper.getConvertView().findViewById(R.id.praise_count_tv);
        LinearLayout commentLayout = helper.getConvertView().findViewById(R.id.comment_layout);
        LinearLayout pariseLayout = helper.getConvertView().findViewById(R.id.praise_layout);

        userName.setText(articleData.get(position).nickname);
        Uri uri = Uri.parse(articleData.get(position).simg);
        userImg.setImageURI(uri);
        helper.getConvertView().setTag(position);

        articleSendTimeTv.setText(articleData.get(position).addtime);
        articleTitleTv.setText(articleData.get(position).scontent);
        commentCountTv.setText(articleData.get(position).comment + "");
        praiseCountTv.setText(articleData.get(position).zan + "");

        if (articleData.get(position).sex.equals("1")) {
            userGender.setImageResource(R.mipmap.boy_icon);
        } else {
            userGender.setImageResource(R.mipmap.girl_icon);
        }

        if (articleData.get(position).iszan == 0) {
            Drawable noZanDrawable = ContextCompat.getDrawable(mContext, R.mipmap.no_zan_icon);
            noZanDrawable.setBounds(0, 0, noZanDrawable.getMinimumWidth(), noZanDrawable.getMinimumHeight());
            praiseCountTv.setCompoundDrawables(noZanDrawable, null, null, null);
        } else {
            Drawable isZanDrawable = ContextCompat.getDrawable(mContext, R.mipmap.is_zan_icon);
            isZanDrawable.setBounds(0, 0, isZanDrawable.getMinimumWidth(), isZanDrawable.getMinimumHeight());
            praiseCountTv.setCompoundDrawables(isZanDrawable, null, null, null);
        }

        praiseCountTv.setTag(position);

        if (!articleData.get(position).ding.equals("1")) {
            topTv.setVisibility(View.GONE);
        } else {
            topTv.setVisibility(View.VISIBLE);
        }

        topTv.setTag(position);

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo cUserInfo = (UserInfo) PreferencesUtils.getObject(mContext, Constant.USER_INFO, UserInfo.class);
                if (cUserInfo != null && cUserInfo.getUid().equals(articleData.get(position).uid)) {
                    Intent intent = new Intent(mContext, MyInfoActivity.class);
                    mContext.startActivity(intent);
                } else {
                    if (articleData.get(position) != null) {
                        Intent intent = new Intent(mContext, FriendInfoActivity.class);
                        intent.putExtra("fuid", articleData.get(position).uid);
                        mContext.startActivity(intent);
                    }
                }
            }
        });

        final String cimgs = articleData.get(position).cimg;
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

        communityImageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, ShowImageListActivity.class);

                intent.putStringArrayListExtra("imageList", (ArrayList<String>) showgGameImageUrlList);
                intent.putExtra("position", position);
                intent.putExtra("current_img_url", imageDatas.get(position).getHurl());
                mContext.startActivity(intent);
                //进入图片浏览时的动画
                ((Activity) mContext).overridePendingTransition(R.anim.image_show_in, R.anim.image_show_out);
            }
        });

        final String sid = articleData.get(position).sid;
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //评论
                Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                intent.putExtra("sid", sid);
                mContext.startActivity(intent);
            }
        });

        final int iszan = articleData.get(position).iszan;
        final int zan = articleData.get(position).zan;
        pariseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtils.show(mContext,"点赞");

                if (AppUtils.isLogin(mContext)) {
                    if (iszan == 0) {
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