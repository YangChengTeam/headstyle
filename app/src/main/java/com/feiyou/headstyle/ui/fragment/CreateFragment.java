package com.feiyou.headstyle.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.ui.activity.PhotoEditActivity;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class CreateFragment extends BaseFragment {

    private final int REQUEST_CODE_GALLERY = 1002;

    private final int REQUEST_CODE_CROP = 1003;

    private final int REQUEST_CODE_CAMERA = 1004;

    private static final int OPEN_CAMERA = 1005;

    private String editImagePath;

    private File file;

    public CreateFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create;
    }

    @Override
    public void initVars() {
        super.initVars();
    }

    @Override
    public void initViews() {
        super.initViews();
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
        GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int requestCode, List<PhotoInfo> resultList) {

            if (requestCode == REQUEST_CODE_GALLERY || requestCode == REQUEST_CODE_CAMERA) {
                if (resultList != null && resultList.get(0) != null) {
                    editImagePath = resultList.get(0).getPhotoPath();
                    Logger.e("select path--->" + editImagePath);
                    if (StringUtils.isEmpty(editImagePath)) {
                        ToastUtils.show(getActivity(), "图片加载异常,请重试");
                        return;
                    }

                    FunctionConfig config = new FunctionConfig.Builder()
                            .setEnableEdit(false)
                            .setEnableCrop(false)
                            .setForceCrop(true)
                            .setCropSquare(true)
                            .build();

                    GalleryFinal.openCrop(REQUEST_CODE_CROP, config,editImagePath, mOnHanlderResultCallback);
                }
            }

            if (requestCode == REQUEST_CODE_CROP) {
                if (resultList != null && resultList.get(0) != null) {
                    editImagePath = resultList.get(0).getPhotoPath();
                    Logger.e("crop path--->" + editImagePath);
                    Intent intent = new Intent(getActivity(), PhotoEditActivity.class);
                    intent.putExtra("image_path", editImagePath);
                    getActivity().startActivity(intent);
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            ToastUtils.show(getActivity(), errorMsg);
        }
    };


    /**
     * 使用相机
     */
    private void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();

        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
        Uri uri = FileProvider.getUriForFile(getActivity(), "com.feiyou.headstyle.fileProvider", file);
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Logger.e("crate path--->" + file.getAbsolutePath());

            editImagePath = file.getAbsolutePath();
            Logger.e("select path--->" + editImagePath);
            if (StringUtils.isEmpty(editImagePath)) {
                ToastUtils.show(getActivity(), "图片加载异常,请重试");
                return;
            }

            FunctionConfig config = new FunctionConfig.Builder()
                    .setEnableEdit(false)
                    .setEnableCrop(false)
                    .setForceCrop(true)
                    .setCropSquare(true)
                    .build();

            GalleryFinal.openCrop(REQUEST_CODE_CROP, config,editImagePath, mOnHanlderResultCallback);
        }
    }

    /**
     * 编辑图片
     *
     * @param view
     */
    @OnClick(R.id.edit_btn)
    public void editPhoto(View view) {

        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, OPEN_CAMERA);
                return;
            } else {
                //GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                useCamera();
            }
        } else {
            //GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
            useCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case OPEN_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                } else {
                    ToastUtils.show(getActivity(), "禁止了相机权限,请开启");
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
