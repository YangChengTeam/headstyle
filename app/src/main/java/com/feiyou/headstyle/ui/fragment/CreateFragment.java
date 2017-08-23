package com.feiyou.headstyle.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.ui.activity.HeadCreateShowActivity;
import com.feiyou.headstyle.util.ImageUtils;
import com.feiyou.headstyle.util.ToastUtils;

import org.lasque.tusdk.TuSdkGeeV1;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.core.seles.tusdk.FilterManager;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.TuAlbumComponent;
import org.lasque.tusdk.impl.components.TuEditMultipleComponent;
import org.lasque.tusdk.modules.components.TuSdkComponent;
import org.lasque.tusdk.modules.components.TuSdkHelperComponent;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateFragment extends BaseFragment {

    @BindView(R.id.square_head_image)
    SimpleDraweeView squareHeadImage;

    @BindView(R.id.circle_head_image)
    SimpleDraweeView circleHeadImage;

    public TuSdkHelperComponent componentHelper;

    private Bitmap editBitmap;

    // 组件选项配置
    TuEditMultipleComponent component = null;

    private String editImagePath;

    public CreateFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create;
    }

    @Override
    public void initVars() {
        super.initVars();
        this.componentHelper = new TuSdkHelperComponent(getActivity());
        //editBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.logo);
    }

    @Override
    public void initViews() {
        super.initViews();
        // 异步方式初始化滤镜管理器
        // 需要等待滤镜管理器初始化完成，才能使用所有功能
        //TuSdk.messageHub().setStatus(getActivity(), R.string.lsq_initing);
        TuSdk.checkFilterManager(mFilterManagerDelegate);
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    /**
     * 选择图片
     *
     * @param view
     */
    @OnClick(R.id.select_btn)
    public void selectPhoto(View view) {

        TuAlbumComponent comp = TuSdkGeeV1.albumCommponent(getActivity(), new TuSdkComponent.TuSdkComponentDelegate() {
            @Override
            public void onComponentFinished(TuSdkResult result, Error error, TuFragment lastFragment) {

                if (result.imageSqlInfo != null && result.imageSqlInfo.path != null) {

                    String tempPath = ImageUtils.changeFileSizeByLocalPath(result.imageSqlInfo.path);

                    Uri uri = Uri.parse("file:///" + tempPath);

                    editImagePath = tempPath;

                    squareHeadImage.setImageURI(uri);//正方形
                    circleHeadImage.setImageURI(uri);//圆形
                }
            }
        });

        // 在组件执行完成后自动关闭组件
        comp.setAutoDismissWhenCompleted(true)
                // 显示组件
                .showComponent();
    }

    /**
     * 编辑图片
     *
     * @param view
     */
    @OnClick(R.id.edit_btn)
    public void editPhoto(View view) {

        if (editImagePath != null) {

            //editBitmap = ImageUtils.getBitmapFromFile(new File(editImagePath), 500, 500);
            editBitmap = BitmapFactory.decodeFile(editImagePath);
            editBitmap = ImageUtils.compressImage(editBitmap,300);

            component = TuSdkGeeV1.editMultipleCommponent(getActivity(), delegate);

            // 是否保存到相册
            component.componentOption().editMultipleOption().setSaveToAlbum(false);
            // 是否保存到临时文件
            component.componentOption().editMultipleOption().setSaveToTemp(true);

            // 设置图片
            component.setImage(editBitmap)
                    // 在组件执行完成后自动关闭组件
                    .setAutoDismissWhenCompleted(true)
                    // 开启组件
                    .showComponent();
        } else {
            ToastUtils.show(getActivity(), "请选择图片后编辑");
        }
    }

    /**
     * 组件委托
     */
    TuSdkComponent.TuSdkComponentDelegate delegate = new TuSdkComponent.TuSdkComponentDelegate() {
        @Override
        public void onComponentFinished(TuSdkResult result1, Error error, TuFragment lastFragment) {

            if (result1.imageFile.exists() && result1.imageFile.getAbsolutePath() != null) {
                /*Uri uri = Uri.parse("file:///" + result1.imageFile.getAbsolutePath());

                squareHeadImage.setImageURI(uri);

                RoundingParams roundingParams = RoundingParams.fromCornersRadius(2f);
                roundingParams.setBorder(Color.rgb(238, 238, 238), 10f);
                roundingParams.setRoundAsCircle(true);
                circleHeadImage.getHierarchy().setRoundingParams(roundingParams);

                circleHeadImage.setImageURI(uri);*/


                Intent intent = new Intent(getActivity(), HeadCreateShowActivity.class);

                intent.putExtra("isCreateQQImage", true);
                intent.putExtra("imagePath", result1.imageFile.getAbsolutePath());

                startActivity(intent);
            }
        }
    };

    private FilterManager.FilterManagerDelegate mFilterManagerDelegate = new FilterManager.FilterManagerDelegate() {
        @Override
        public void onFilterManagerInited(FilterManager manager) {
            //TuSdk.messageHub().showSuccess(getActivity(), R.string.lsq_inited);
        }
    };
}
