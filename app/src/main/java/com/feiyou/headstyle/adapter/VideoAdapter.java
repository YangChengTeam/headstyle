package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.VideoInfo;
import com.feiyou.headstyle.util.GlideRoundTransform;
import com.xinqu.videoplayer.XinQuVideoPlayer;
import com.xinqu.videoplayer.XinQuVideoPlayerStandard;

import java.util.List;

/**
 * Created by admin on 2017/9/14.
 */

public class VideoAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> {
    private Context mContext;

    public VideoAdapter(Context context, List<VideoInfo> datas) {
        super(R.layout.video_item, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final VideoInfo item) {
        helper.setText(R.id.tv_video_title, item.getDesp());
        final RequestOptions myOptions = new RequestOptions()
                .transform(new GlideRoundTransform(mContext, 6));

        final XinQuVideoPlayerStandard xinQuVideoPlayerStandard = helper.getConvertView().findViewById(R.id.video_player);
        xinQuVideoPlayerStandard.setUp(item.getPath(), XinQuVideoPlayer.CURRENT_STATE_NORMAL, false, "");
        Glide.with(mContext).load(item.getCover()).apply(myOptions).into(xinQuVideoPlayerStandard.thumbImageView);
        xinQuVideoPlayerStandard.thumbImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xinQuVideoPlayerStandard.startVideo();
                xinQuVideoPlayerStandard.startWindowFullscreen();
            }
        });
    }
}
