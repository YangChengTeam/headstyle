package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.ImageFilterAdapter;
import com.feiyou.headstyle.adapter.StickerAdapter;
import com.feiyou.headstyle.bean.Addon;
import com.feiyou.headstyle.bean.FilterEffect;
import com.feiyou.headstyle.bean.StickerInfoRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.EffectService;
import com.feiyou.headstyle.util.EffectUtil;
import com.feiyou.headstyle.util.GPUImageFilterTools;
import com.feiyou.headstyle.util.ImageUtils;
import com.feiyou.headstyle.util.MyFileUtils;
import com.feiyou.headstyle.util.ScreenUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.MyImageViewDrawableOverlay;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 图片处理界面
 */
public class PhotoEditActivity extends BaseActivity {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindView(R.id.tv_next)
    TextView mNextTextView;

    //滤镜图片
    @BindView(R.id.gpuimage)
    GPUImageView mGPUImageView;

    //绘图区域
    @BindView(R.id.drawing_view_container)
    ViewGroup drawArea;

    @BindView(R.id.toolbar_area)
    ViewGroup toolArea;

    @BindView(R.id.tabs_layout)
    TabLayout tabLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private OKHttpRequest okHttpRequest = null;

    private MyImageViewDrawableOverlay mImageView;

    //当前选择底部按钮
    private TextView currentBtn;
    //当前图片
    private Bitmap currentBitmap;
    //用于预览的小图片
    private Bitmap smallImageBackgroud;

    private int[] tab_images = new int[]{R.mipmap.sticker_icon, R.mipmap.filter_icon};

    private String[] tab_texts = new String[]{"贴纸", "滤镜"};

    private List<Object> mDefaultDatas;

    private List<Integer> filterDatas;

    private List<FilterEffect> effects;

    private StickerAdapter stickerAdapter;

    private ImageFilterAdapter imageFilterAdapter;

    LinearLayoutManager filterManager;

    private String imagePath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_edit;
    }

    @Override
    public void initViews() {
        super.initViews();
        okHttpRequest = new OKHttpRequest();

        MobclickAgent.onEvent(this, "create_image_click", AppUtils.getVersionName(this));

        EffectUtil.clear();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            imagePath = intent.getExtras().getString("image_path");
        }
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        Glide.with(this).asBitmap().apply(options).load(imagePath).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                currentBitmap = resource;
                smallImageBackgroud = resource;
                mGPUImageView.setImage(currentBitmap);
            }
        });

        //画布高度
        int canvasHeight = ScreenUtils.getWidth(this);
        //int canvasHeight = ScreenUtils.getHeight(this) - SizeUtils.dp2px(this, 174) - NavgationBarUtils.getNavigationBarHeight(this);
        //添加贴纸水印的画布
        View overlay = LayoutInflater.from(PhotoEditActivity.this).inflate(
                R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, canvasHeight);
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        drawArea.addView(overlay);

        //初始化滤镜图片
        RelativeLayout.LayoutParams bgParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, canvasHeight);
        mGPUImageView.setLayoutParams(bgParams);
        mGPUImageView.getGPUImage().setScaleType(GPUImage.ScaleType.CENTER_INSIDE);

        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(0)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(1)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    recyclerView.setAdapter(stickerAdapter);
                }
                if (tab.getPosition() == 1) {
                    initFilterToolBar();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mDefaultDatas = new ArrayList<Object>();
        mDefaultDatas.add(R.mipmap.s1);
        mDefaultDatas.add(R.mipmap.s2);
        mDefaultDatas.add(R.mipmap.s3);
        mDefaultDatas.add(R.mipmap.s4);
        mDefaultDatas.add(R.mipmap.s5);
        mDefaultDatas.add(R.mipmap.s6);
        mDefaultDatas.add(R.mipmap.s7);
        mDefaultDatas.add(R.mipmap.s8);
        mDefaultDatas.add(R.mipmap.s9);
        mDefaultDatas.add(R.mipmap.s10);
        mDefaultDatas.add(R.mipmap.s11);
        mDefaultDatas.add(R.mipmap.s12);
        mDefaultDatas.add(R.mipmap.s13);
        filterDatas = new ArrayList<Integer>();
        for (int i = 1; i <= 15; i++) {
            try {
                Field field = R.mipmap.class.getDeclaredField("filter" + i);
                filterDatas.add(Integer.parseInt(field.get(null).toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        stickerAdapter = new StickerAdapter(this, null);
        recyclerView.setAdapter(stickerAdapter);

        effects = EffectService.getInst().getLocalFilters();
        imageFilterAdapter = new ImageFilterAdapter(this, filterDatas, effects);
        filterManager = new LinearLayoutManager(this);
        filterManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        stickerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Object imgUrl = stickerAdapter.getData().get(position);
                Glide.with(PhotoEditActivity.this).asBitmap().load(imgUrl).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        Addon sticker = new Addon(resource);
                        EffectUtil.addStickerImage(mImageView, PhotoEditActivity.this, sticker,
                                new EffectUtil.StickerCallback() {
                                    @Override
                                    public void onRemoveSticker(Addon sticker) {

                                    }
                                });
                    }
                });
            }
        });
        imageFilterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                GPUImageFilter filter = GPUImageFilterTools.createFilterForType(
                        PhotoEditActivity.this, effects.get(position).getType());
                mGPUImageView.setFilter(filter);
                GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter);
                //可调节颜色的滤镜
                if (mFilterAdjuster.canAdjust()) {
                    //mFilterAdjuster.adjust(100); 给可调节的滤镜选一个合适的值
                }

            }
        });

        mNextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });

        getStickers();
    }

    public void getStickers() {
        okHttpRequest.aget("http://sc.wk2.com/api/zbsq/sticker_lists", null, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                if (!StringUtils.isEmpty(response)) {
                    StickerInfoRet stickerInfoRet = Constant.GSON.fromJson(response, StickerInfoRet.class);
                    if (stickerInfoRet != null && stickerInfoRet.code == 1) {
                        List<Object> stickers = null;
                        if (stickerInfoRet.getData() != null && stickerInfoRet.getData().size() > 0) {
                            stickers = new ArrayList<>();
                            for (int i = 0; i < stickerInfoRet.getData().size(); i++) {
                                stickers.add(stickerInfoRet.getData().get(i).src);
                            }
                        }
                        if (stickers != null) {
                            stickerAdapter.setNewData(stickers);
                        }
                    } else {
                        stickerAdapter.setNewData(mDefaultDatas);
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onBefore() {

            }
        });
    }

    //初始化滤镜
    private void initFilterToolBar() {
        recyclerView.setLayoutManager(filterManager);
        recyclerView.setAdapter(imageFilterAdapter);
    }

    /**
     * 自定义Tab
     *
     * @param position
     * @return
     */
    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        TextView tv = view.findViewById(R.id.txt_title);
        tv.setText(tab_texts[position]);
        ImageView img = view.findViewById(R.id.img_title);
        img.setImageResource(tab_images[position]);
        return view;
    }

    //保存图片
    private void savePicture() {
        Logger.e("w" + mImageView.getWidth() + "--h--->" + mImageView.getHeight());
        //加滤镜
        final Bitmap newBitmap = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        RectF dst = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());
        try {
            cv.drawBitmap(mGPUImageView.capture(), null, dst, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            cv.drawBitmap(currentBitmap, null, dst, null);
        }
        //加贴纸水印
        EffectUtil.applyOnSave(cv, mImageView);

        new SavePicToFileTask().execute(newBitmap);
    }

    private class SavePicToFileTask extends AsyncTask<Bitmap, Void, String> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String fileName = null;
            try {
                bitmap = params[0];

                String picName = com.blankj.utilcode.util.TimeUtils.getNowMills() + "";
                fileName = ImageUtils.saveToFile(MyFileUtils.getInst().getPhotoSavedPath() + "/" + picName, false, bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.show(PhotoEditActivity.this, "图片处理错误，请退出相机并重试");
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);

            if (StringUtils.isEmpty(fileName)) {
                return;
            }

            Intent intent = new Intent(PhotoEditActivity.this, HeadCreateShowActivity.class);
            intent.putExtra("isCreateQQImage", true);
            intent.putExtra("imagePath", fileName);

            startActivity(intent);

        }
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }
}
