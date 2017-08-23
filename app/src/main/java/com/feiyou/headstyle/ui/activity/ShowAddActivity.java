package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.PhotoUploadAdapter;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.net.service.DownFightService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.ImageUtils;
import com.feiyou.headstyle.util.NetWorkUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.HorizontalListView;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnMultiCompressListener;

public class ShowAddActivity extends BaseActivity {

    private final int REQUEST_CODE_GALLERY = 1001;

    @BindView(R.id.show_top_layout)
    RelativeLayout showTopLayout;

    @BindView(R.id.show_content_layout)
    FrameLayout showContentLayout;

    /*@BindView(R.id.show_type_tv)
    TextView showTypeTv;*/

    /*@BindString(R.string.take_photo_self_text)
    String takePhotoText;

    @BindString(R.string.chat_text)
    String chatText;*/

    /*@BindView(R.id.show_type_select_layout)
    LinearLayout showTypeSelectLayout;*/

    /*@BindView(R.id.take_photo_self_tv)
    TextView takePhotoTv;

    @BindView(R.id.chat_tv)
    TextView chatTv;*/

    @BindView(R.id.lv_photo)
    HorizontalListView mLvPhoto;

    @BindView(R.id.show_content_et)
    EditText showContentEt;

    private PhotoUploadAdapter mAdapter;

    public List<Uri> dataUriList;

    private List<PhotoInfo> mPhotoList;

    UserService mService = null;

    OKHttpRequest okHttpRequest = null;

    private String stype = "1";//1.处Q友，2.晒自拍

    private int selectPosition;

    private UserInfo userInfo;

    MaterialDialog materialDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_add;
    }

    @Override
    protected void initVars() {
        super.initVars();
        dataUriList = new ArrayList<Uri>();
        dataUriList.add(null);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.getString("stype") != null) {
            stype = bundle.getString("stype");
        }
    }

    @Override
    public void initViews() {
        super.initViews();

        mService = new UserService();
        okHttpRequest = new OKHttpRequest();

        mPhotoList = new ArrayList<>();
        mPhotoList.add(new PhotoInfo());
        mAdapter = new PhotoUploadAdapter(this, mPhotoList);
        //photoGridView.setAdapter(mAdapter);
        mLvPhoto.setAdapter(mAdapter);
        mLvPhoto.setDividerWidth(20);

        materialDialog = new MaterialDialog.Builder(this)
                .content("正在发布，请稍后...")
                .progress(true, 0)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.dialog_content_color).build();
    }

    @Override
    public void loadData() {
        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                if (mPhotoList != null) {
                    //移除空白图
                    for (int i = 0; i < mPhotoList.size(); i++) {
                        if (mPhotoList.get(i).getPhotoPath() == null) {
                            mPhotoList.remove(i);
                        }
                    }
                }

                mPhotoList.addAll(resultList);

                if (mPhotoList.size() < 3) {
                    mPhotoList.add(new PhotoInfo());//添加一张空白"+"图
                }

                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            ToastUtils.show(ShowAddActivity.this, errorMsg);
        }
    };

    @OnClick(R.id.send_btn)
    void sendArticle(View view) {

        if (StringUtils.isEmpty(showContentEt.getText())) {
            ToastUtils.show(ShowAddActivity.this, R.string.show_content_is_not_null_text);
            return;
        }

        final Map<String, String> params = new HashMap<String, String>();
        params.put("scontent", showContentEt.getText().toString());
        params.put("stype", stype);

        if (userInfo != null) {

            params.put("openid", userInfo.openid);
            params.put("uid", userInfo.uid);

            List<File> files = new ArrayList<File>();

            if (mPhotoList != null && mPhotoList.size() > 0) {
                for (int i = 0; i < mPhotoList.size(); i++) {
                    if (mPhotoList.get(i).getPhotoPath() != null) {
                        files.add(new File(mPhotoList.get(i).getPhotoPath()));
                    }
                }
            }

            //判断是否选择了图片
            boolean isSelected = true;

            if(mPhotoList.size() == 1 && mPhotoList.get(0).getPhotoPath() == null){
                //isSelected = false;
                ToastUtils.show(this,"请选择素材后上传");
                return;
            }

            materialDialog.show();

            if(isSelected){
                //new UpdateFileAsyncTask(params,files).execute();

                Luban.compress(this,files).putGear(Luban.THIRD_GEAR).launch(new OnMultiCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(List<File> fileList) {
                        if (fileList != null && fileList.size() > 0) {
                            okHttpRequest.aget(Server.SEND_ARTICLE_DATA, params, fileList, new OnResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    if (materialDialog != null && materialDialog.isShowing()) {
                                        materialDialog.dismiss();
                                    }
                                    Result result = mService.sendArticle(response);
                                    if (result != null) {
                                        ToastUtils.show(ShowAddActivity.this, "发帖成功");
                                        Logger.e("发帖成功");
                                        finish();
                                    } else {
                                        ToastUtils.show(ShowAddActivity.this, "发帖失败,请稍后重试");
                                        Logger.e("发帖失败");
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    if (materialDialog != null && materialDialog.isShowing()) {
                                        materialDialog.dismiss();
                                    }
                                    ToastUtils.show(ShowAddActivity.this, "发帖失败,请稍后重试");
                                    Logger.e("发帖失败");
                                }

                                @Override
                                public void onBefore() {

                                }
                            });
                        } else {
                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }
                            ToastUtils.show(ShowAddActivity.this, "图片上传有误，请稍后重试");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.show(ShowAddActivity.this, "图片上传有误，请稍后重试");
                    }
                });



            }else{
                okHttpRequest.aget(Server.SEND_ARTICLE_NO_FILE_DATA, params, new OnResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }
                        Result result = mService.sendArticle(response);
                        if (result != null) {
                            ToastUtils.show(ShowAddActivity.this, "发帖成功");
                            Logger.e("发帖成功");
                            finish();
                        } else {
                            ToastUtils.show(ShowAddActivity.this, "发帖失败,请稍后重试");
                            Logger.e("发帖失败");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }
                        ToastUtils.show(ShowAddActivity.this, "发帖失败,请稍后重试");
                        Logger.e("发帖失败");
                    }

                    @Override
                    public void onBefore() {

                    }
                });
            }

        } else {
            ToastUtils.show(ShowAddActivity.this, "用户未登录,请登录后重试");
        }
    }

    public class UpdateFileAsyncTask extends AsyncTask<Integer, Integer, List<File>> {

        List<File> tempFiles;
        Map<String, String> mparams;
        public UpdateFileAsyncTask(Map<String, String> uparams,List<File> files){
            tempFiles = files;
            mparams = uparams;
        }

        @Override
        protected List<File> doInBackground(Integer... params) {
           // return NetWorkUtils.is404NotFound(Constant.FIGHT_DOWN_URL);
            return ImageUtils.changeFileSize(tempFiles);
        }

        @Override
        protected void onPostExecute(List<File> resultList) {
            super.onPostExecute(resultList);
            if (resultList != null && resultList.size() > 0) {
                okHttpRequest.aget(Server.SEND_ARTICLE_DATA, mparams, resultList, new OnResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }
                        Result result = mService.sendArticle(response);
                        if (result != null) {
                            ToastUtils.show(ShowAddActivity.this, "发帖成功");
                            Logger.e("发帖成功");
                            finish();
                        } else {
                            ToastUtils.show(ShowAddActivity.this, "发帖失败,请稍后重试");
                            Logger.e("发帖失败");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }
                        ToastUtils.show(ShowAddActivity.this, "发帖失败,请稍后重试");
                        Logger.e("发帖失败");
                    }

                    @Override
                    public void onBefore() {

                    }
                });
            } else {
                if (materialDialog != null && materialDialog.isShowing()) {
                    materialDialog.dismiss();
                }
                ToastUtils.show(ShowAddActivity.this, "图片上传有误，请稍后重试");
            }
        }
    }

    @OnItemClick(R.id.lv_photo)
    public void selectPhoto(int position) {
        selectPosition = position;

        int dataSelectNum = 3;

        if (mPhotoList != null && mPhotoList.get(selectPosition).getPhotoPath() == null) {

            for (int i = 0; i < mPhotoList.size(); i++) {
                if (mPhotoList.get(i).getPhotoPath() != null) {
                    dataSelectNum--;
                }
            }

            if (dataSelectNum == 0) {
                dataSelectNum = 1;
            }

            FunctionConfig config = new FunctionConfig.Builder()
                    .setMutiSelectMaxSize(dataSelectNum)
                    .build();

            //带配置
            GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, config, mOnHanlderResultCallback);
        }
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }
}
