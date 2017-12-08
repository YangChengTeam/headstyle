package com.feiyou.headstyle.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.StarItemAdapter;
import com.feiyou.headstyle.bean.StarInfo;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.util.DialogUtils;
import com.feiyou.headstyle.util.GlideHelper;
import com.feiyou.headstyle.util.NavgationBarUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.util.TimeUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.feiyou.headstyle.view.PhotoModePopupWindow;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnMultiCompressListener;
import rx.functions.Action1;

/**
 * Created by admin on 2017/11/21.
 */

public class MyInfoActivity extends BaseActivity {

    private static final int TAKE_BIG_PICTURE = 1000;

    private final int REQUEST_CODE_GALLERY = 1001;

    private final int REQUEST_CODE_EDIT = 1002;

    private static final int CROP_SMALL_PICTURE = 1003;

    @BindView(R.id.layout_user_head)
    RelativeLayout mUserHeadLayout;

    @BindView(R.id.layout_nick_name)
    RelativeLayout mNickNameLayout;

    @BindView(R.id.layout_sign)
    RelativeLayout mSignLayout;

    @BindView(R.id.layout_qq)
    RelativeLayout mQQLayout;

    @BindView(R.id.layout_weixin)
    RelativeLayout mWeiXinLayout;

    @BindView(R.id.layout_bir_date)
    RelativeLayout mBirDateLayout;

    @BindView(R.id.layout_school)
    RelativeLayout mSchoolLayout;

    @BindView(R.id.layout_address)
    RelativeLayout mAddressLayout;

    @BindView(R.id.layout_sex)
    RelativeLayout mSexLayout;

    @BindView(R.id.layout_star)
    RelativeLayout mStarLayout;

    @BindView(R.id.layout_logout)
    RelativeLayout mLogoutLayout;

    @BindView(R.id.iv_user_head)
    ImageView mUserHeadImageView;

    @BindView(R.id.tv_user_name)
    TextView mNickNameTextView;

    @BindView(R.id.tv_auto_id)
    TextView mAutoIdTextView;

    @BindView(R.id.tv_gender)
    TextView mGenderTextView;

    @BindView(R.id.tv_sign)
    TextView mSignTextView;

    @BindView(R.id.tv_bir_date)
    TextView mBirDateTextView;

    @BindView(R.id.tv_star)
    TextView mStarTextView;

    @BindView(R.id.tv_qq)
    TextView mQQTextView;

    @BindView(R.id.tv_weixin)
    TextView mWeiXinTextView;

    @BindView(R.id.tv_school)
    TextView mSchoolTextView;

    @BindView(R.id.tv_address)
    TextView mAddressTextView;

    private Uri imageUri;

    private File outputImage;

    private PhotoModePopupWindow photoModePopupWindow;

    private int updateType = 1;//1修改昵称2修改签名3修改qq4修改微信5修改学习6修改地址

    BottomSheetDialog bottomSheetDialog;

    BottomSheetDialog starDialog;

    View bottomSheetView;

    View starView;

    TextView mSaveTextView;

    TextView mStarSaveTextView;

    TextView mCancelTextView;

    TextView mStarCancelTextView;

    RelativeLayout mBoyLayout;

    RelativeLayout mGirlLayout;

    CheckBox mBoyCheckBox;

    CheckBox mGirlCheckBox;

    RecyclerView starRecyclerView;

    StarItemAdapter starItemAdapter;

    int lastPosition = -1;

    private UserInfo userInfo;

    private MaterialDialog logoutDialog;

    OKHttpRequest okHttpRequest = null;

    private UserService mService = null;

    MaterialDialog materialDialog;

    private int starPosition;

    private int updateGender = 1;

    private int updateStar = 0;

    private String[] stars;

    private boolean isUpdateImage;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_info;
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void initViews() {
        super.initViews();

        mService = new UserService();
        okHttpRequest = new OKHttpRequest();

        materialDialog = new MaterialDialog.Builder(this)
                .content("正在修改，请稍后...")
                .progress(true, 0)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.dialog_content_color).build();

        //性别
        bottomSheetView = LayoutInflater.from(MyInfoActivity.this).inflate(R.layout.sex_select_view, null);
        mSaveTextView = ButterKnife.findById(bottomSheetView, R.id.tv_save);
        mCancelTextView = ButterKnife.findById(bottomSheetView, R.id.tv_cancel);
        mBoyLayout = ButterKnife.findById(bottomSheetView, R.id.layout_boy);
        mGirlLayout = ButterKnife.findById(bottomSheetView, R.id.layout_girl);
        mBoyCheckBox = ButterKnife.findById(bottomSheetView, R.id.ck_boy);
        mGirlCheckBox = ButterKnife.findById(bottomSheetView, R.id.ck_girl);
        bottomSheetDialog = new BottomSheetDialog(MyInfoActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);


        //星座
        starView = LayoutInflater.from(MyInfoActivity.this).inflate(R.layout.star_list_view, null);
        starDialog = new BottomSheetDialog(MyInfoActivity.this);
        starRecyclerView = ButterKnife.findById(starView, R.id.star_list);
        mStarSaveTextView = ButterKnife.findById(starView, R.id.tv_star_save);
        mStarCancelTextView = ButterKnife.findById(starView, R.id.tv_star_cancel);
        starItemAdapter = new StarItemAdapter(MyInfoActivity.this, getStarList());
        starRecyclerView.setLayoutManager(new LinearLayoutManager(MyInfoActivity.this));
        starRecyclerView.setAdapter(starItemAdapter);
        starDialog.setContentView(starView);

        RxView.clicks(mUserHeadLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) MyInfoActivity.this.findViewById(android.R.id.content)).getChildAt(0);
                photoModePopupWindow = new PhotoModePopupWindow(MyInfoActivity.this, itemsOnClick);
                photoModePopupWindow.showAtLocation(viewGroup, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, NavgationBarUtils.getNavigationBarHeight(MyInfoActivity.this));
                setBackgroundAlpha(MyInfoActivity.this, 0.5f);
                photoModePopupWindow.setOnDismissListener(new PoponDismissListener());
            }
        });

        RxView.clicks(mNickNameLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(MyInfoActivity.this, MyInfoUpdateActivity.class);
                intent.putExtra("update_type", 1);
                startActivity(intent);
            }
        });

        RxView.clicks(mSignLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(MyInfoActivity.this, MyInfoUpdateActivity.class);
                intent.putExtra("update_type", 2);
                startActivity(intent);
            }
        });

        RxView.clicks(mQQLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(MyInfoActivity.this, MyInfoUpdateActivity.class);
                intent.putExtra("update_type", 3);
                startActivity(intent);
            }
        });

        RxView.clicks(mWeiXinLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(MyInfoActivity.this, MyInfoUpdateActivity.class);
                intent.putExtra("update_type", 4);
                startActivity(intent);
            }
        });

        RxView.clicks(mSchoolLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(MyInfoActivity.this, MyInfoUpdateActivity.class);
                intent.putExtra("update_type", 5);
                startActivity(intent);
            }
        });

        RxView.clicks(mAddressLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(MyInfoActivity.this, MyInfoUpdateActivity.class);
                intent.putExtra("update_type", 6);
                startActivity(intent);
            }
        });

        RxView.clicks(mSexLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (bottomSheetDialog != null && !bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.show();
                }
            }
        });

        RxView.clicks(mStarLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (starDialog != null && !starDialog.isShowing()) {
                    starDialog.show();
                }
            }
        });

        RxView.clicks(mBirDateLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                chooseDate();
            }
        });

        starItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (lastPosition > -1) {
                    starItemAdapter.getData().get(lastPosition).setChecked(false);
                }
                starItemAdapter.getData().get(position).setChecked(true);

                lastPosition = position;
                starItemAdapter.notifyDataSetChanged();
                updateStar = position;
            }
        });

        //性别确认
        RxView.clicks(mSaveTextView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
                updateInfo(1);
            }
        });
        //性别取消
        RxView.clicks(mCancelTextView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
            }
        });

        //星座确认
        if (mStarSaveTextView != null) {
            RxView.clicks(mStarSaveTextView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    if (starDialog != null && starDialog.isShowing()) {
                        starDialog.dismiss();
                    }
                    updateInfo(2);
                }
            });
        }

        //星座取消
        RxView.clicks(mStarCancelTextView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (starDialog != null && starDialog.isShowing()) {
                    starDialog.dismiss();
                }
            }
        });

        RxView.clicks(mBoyLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mBoyCheckBox.setBackgroundResource(R.mipmap.checked_icon);
                mGirlCheckBox.setBackgroundResource(0);
                updateGender = 1;
            }
        });

        RxView.clicks(mGirlLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mBoyCheckBox.setBackgroundResource(0);
                mGirlCheckBox.setBackgroundResource(R.mipmap.checked_icon);
                updateGender = 2;
            }
        });

        logoutDialog = DialogUtils.logoutDialog(MyInfoActivity.this, R.string.logout_confirm_text, R.string.cancel_login_text, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (userInfo != null) {
                    PreferencesUtils.putObject(MyInfoActivity.this, Constant.USER_INFO, null);
                    App.isLoginAuth = false;
                }
                finish();
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });

        RxView.clicks(mLogoutLayout).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (logoutDialog != null) {
                    logoutDialog.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isUpdateImage) {
            getUserInfo();
        }
    }

    public void getUserInfo() {
        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        if (userInfo != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("uid", userInfo.getUid());
            //查询用户信息
            okHttpRequest.aget(Server.USER_INFO_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    UserInfo tempInfo = mService.login(response);
                    if (tempInfo != null) {
                        userInfo = tempInfo;
                        PreferencesUtils.putObject(MyInfoActivity.this, Constant.USER_INFO, userInfo);

                        GlideHelper.circleImageView(MyInfoActivity.this, mUserHeadImageView, userInfo.getUserimg(), R.mipmap.user_head_def_icon);
                        mNickNameTextView.setText(!StringUtils.isEmpty(userInfo.getNickName()) ? userInfo.getNickName() : "");
                        mAutoIdTextView.setText(userInfo.getUid() + "");
                        mGenderTextView.setText(userInfo.getSex().equals("1") ? "男" : "女");
                        mSignTextView.setText(!StringUtils.isEmpty(userInfo.getSign()) ? userInfo.getSign() : "你还没有个性签名");
                        mBirDateTextView.setText(userInfo.getBirthday());
                        mQQTextView.setText(userInfo.getQq());
                        mWeiXinTextView.setText(userInfo.getWeixin());
                        mSchoolTextView.setText(userInfo.getSchool());
                        mAddressTextView.setText(userInfo.getAddress());

                        if (userInfo.getSex().equals("1")) {
                            mBoyCheckBox.setBackgroundResource(R.mipmap.checked_icon);
                            mGirlCheckBox.setBackgroundResource(0);
                        } else {
                            mBoyCheckBox.setBackgroundResource(0);
                            mGirlCheckBox.setBackgroundResource(R.mipmap.checked_icon);
                        }

                        if (!StringUtils.isBlank(userInfo.getStar()) && isNumeric(userInfo.getStar())) {
                            starPosition = Integer.parseInt(userInfo.getStar());
                            mStarTextView.setText(stars[starPosition]);
                            starItemAdapter.getData().get(starPosition).setChecked(true);
                            lastPosition = starPosition;
                            starItemAdapter.notifyDataSetChanged();
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
        } else {

        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                Logger.e("select image --->" + resultList.get(0).getPhotoPath());
                Uri uri = getImageContentUri(MyInfoActivity.this, new File(resultList.get(0).getPhotoPath()));
                if (uri != null) {
                    outputImage = new File(Constant.SD_DIR, TimeUtils.getCurrentTimeInLong() + ".png");
                    imageUri = Uri.fromFile(outputImage);
                    cropImageUri(uri, 300, 300, CROP_SMALL_PICTURE);
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            ToastUtils.show(MyInfoActivity.this, errorMsg);
        }
    };

    public List<StarInfo> getStarList() {
        stars = new String[]{"白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "人马座", "摩羯座", "水瓶座", "双鱼座"};
        List<StarInfo> list = new ArrayList<>();
        for (int i = 0; i < stars.length; i++) {
            StarInfo starInfo = new StarInfo();
            starInfo.setStarName(stars[i]);
            list.add(starInfo);
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case TAKE_BIG_PICTURE:
                cropImageUri(imageUri, 300, 300, CROP_SMALL_PICTURE);
                break;
            case CROP_SMALL_PICTURE:
                GlideHelper.circleImageView(this, mUserHeadImageView, outputImage.getAbsolutePath(), 0);
                updateImage();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

    //弹出窗口监听消失
    public class PoponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            setBackgroundAlpha(MyInfoActivity.this, 1f);
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (photoModePopupWindow != null && photoModePopupWindow.isShowing()) {
                photoModePopupWindow.dismiss();
            }
            isUpdateImage = true;
            switch (v.getId()) {
                case R.id.layout_camera:
                    outputImage = new File(Constant.SD_DIR, TimeUtils.getCurrentTimeInLong() + ".png");
                    imageUri = Uri.fromFile(outputImage);
                    //启动相机去拍照，直接传入了uri，照完以后，会把照片直接存储到这个uri
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, TAKE_BIG_PICTURE);
                    break;
                case R.id.layout_local_photo:
                    GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                    break;
                case R.id.layout_cancel:
                    Logger.e("cancel--->");
                    break;
                default:
                    break;
            }
        }
    };

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //是否裁剪
        intent.putExtra("crop", "true");
        //设置xy的裁剪比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置输出的宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        //是否缩放
        intent.putExtra("scale", false);
        //输入图片的Uri，指定以后，可以在这个uri获得图片
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //是否返回图片数据，可以不用，直接用uri就可以了
        intent.putExtra("return-data", false);
        //设置输入图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        //是否关闭面部识别
        intent.putExtra("noFaceDetection", true); // no face detection
        //启动
        startActivityForResult(intent, requestCode);
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     * 绝对路径转uri
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    private void chooseDate() {
        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = calendar.get(Calendar.MONTH);//当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日
        //new一个日期选择对话框的对象,并设置默认显示时间为当前的年月日时间.
        DatePickerDialog dialog = new DatePickerDialog(this, mdateListener, year, month, day);
        dialog.show();
    }

    /**
     * 日期选择的回调监听
     */
    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
            mBirDateTextView.setText(years + "-" + monthOfYear + "-" + dayOfMonth);
        }
    };

    public void updateImage() {
        if (userInfo != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("openid", userInfo.getOpenid());
            params.put("stype", "edit");
            params.put("hasimg", "1");
            params.put("uid", userInfo.getUid());

            final StringBuffer updateImageUrl = new StringBuffer(Server.USER_INFO_DATA);
            updateImageUrl.append("&openid=").append(userInfo.getOpenid());
            updateImageUrl.append("&stype=edit&hasimg=1");
            updateImageUrl.append("&uid=").append(userInfo.getUid());

            List<File> files = new ArrayList<File>();
            if (outputImage.exists()) {
                files.add(outputImage);
            }

            Luban.compress(this, files).putGear(Luban.THIRD_GEAR).launch(new OnMultiCompressListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(List<File> fileList) {
                    isUpdateImage = false;
                    if (fileList != null && fileList.size() > 0) {
                        okHttpRequest.aget(updateImageUrl.toString(), null, fileList, new OnResponseListener() {
                            @Override
                            public void onSuccess(String response) {
                                if (materialDialog != null && materialDialog.isShowing()) {
                                    materialDialog.dismiss();
                                }
                                UserInfo tempInfo = mService.login(response);
                                if (tempInfo != null) {
                                    ToastUtils.show(MyInfoActivity.this, "修改成功");
                                    Logger.e("修改成功");
                                } else {
                                    ToastUtils.show(MyInfoActivity.this, "修改失败,请稍后重试");
                                    Logger.e("修改失败");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                if (materialDialog != null && materialDialog.isShowing()) {
                                    materialDialog.dismiss();
                                }
                                ToastUtils.show(MyInfoActivity.this, "修改失败,请稍后重试");
                                Logger.e("修改失败");
                            }

                            @Override
                            public void onBefore() {

                            }
                        });
                    } else {
                        if (materialDialog != null && materialDialog.isShowing()) {
                            materialDialog.dismiss();
                        }
                        ToastUtils.show(MyInfoActivity.this, "图片上传有误，请稍后重试");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtils.show(MyInfoActivity.this, "图片上传有误，请稍后重试");
                }
            });

        }
    }

    public void updateInfo(final int type) {
        if (userInfo != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("openid", userInfo.getOpenid());
            params.put("stype", "edit");
            params.put("uid", userInfo.getUid());

            if (type == 1) {
                params.put("sex", updateGender + "");
            }
            if (type == 2) {
                params.put("star", updateStar + "");
            }

            okHttpRequest.aget(Server.USER_INFO_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    if (materialDialog != null && materialDialog.isShowing()) {
                        materialDialog.dismiss();
                    }
                    UserInfo tempInfo = mService.login(response);
                    if (tempInfo != null) {
                        ToastUtils.show(MyInfoActivity.this, "修改成功");
                        Logger.e("修改成功");
                        if (type == 1) {
                            mGenderTextView.setText(updateGender == 1 ? "男" : "女");
                        }

                        if (type == 2) {
                            mStarTextView.setText(stars[updateStar]);
                        }
                    } else {
                        ToastUtils.show(MyInfoActivity.this, "修改失败,请稍后重试");
                        Logger.e("修改失败");
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (materialDialog != null && materialDialog.isShowing()) {
                        materialDialog.dismiss();
                    }
                    ToastUtils.show(MyInfoActivity.this, "修改失败,请稍后重试");
                    Logger.e("修改失败");
                }

                @Override
                public void onBefore() {

                }
            });

        }
    }

}
