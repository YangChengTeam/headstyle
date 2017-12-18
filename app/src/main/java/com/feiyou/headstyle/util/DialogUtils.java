package com.feiyou.headstyle.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.feiyou.headstyle.R;

/**
 * Created by admin on 2016/9/22.
 */
public class DialogUtils {

    public static MaterialDialog createDialog(Context context, int confirmTextRes, int cancelTextRes, MaterialDialog.SingleButtonCallback confirmCallBack, MaterialDialog.SingleButtonCallback cancelCallBack) {
        return new MaterialDialog.Builder(context)
                .titleColorRes(R.color.dialog_title_color)
                .contentColorRes(R.color.dialog_content_color)
                .backgroundColorRes(R.color.white)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.quick_text_color)
                .title(R.string.app_name)
                .content(R.string.not_login_and_use_qq_text)
                .icon(ContextCompat.getDrawable(context, R.drawable.logo))
                .maxIconSize(80)
                .positiveText(confirmTextRes)
                .negativeText(cancelTextRes)
                .onPositive(confirmCallBack)
                .onNegative(cancelCallBack).build();
    }

    public static MaterialDialog createTestDialog(Context context, int confirmTextRes, int cancelTextRes, MaterialDialog.SingleButtonCallback confirmCallBack, MaterialDialog.SingleButtonCallback cancelCallBack) {
        return new MaterialDialog.Builder(context)
                .titleColorRes(R.color.dialog_title_color)
                .contentColorRes(R.color.dialog_content_color)
                .backgroundColorRes(R.color.white)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.quick_text_color)
                .title(R.string.give_up_test_text)
                .content(R.string.continue_test_text)
                .icon(ContextCompat.getDrawable(context, R.drawable.logo))
                .maxIconSize(80)
                .positiveText(confirmTextRes)
                .negativeText(cancelTextRes)
                .onPositive(confirmCallBack)
                .onNegative(cancelCallBack).build();
    }

    public static MaterialDialog logoutDialog(Context context, int confirmTextRes, int cancelTextRes, MaterialDialog.SingleButtonCallback confirmCallBack, MaterialDialog.SingleButtonCallback cancelCallBack) {
        return new MaterialDialog.Builder(context)
                .titleColorRes(R.color.dialog_title_color)
                .contentColorRes(R.color.dialog_content_color)
                .backgroundColorRes(R.color.white)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.quick_text_color)
                .title(R.string.logout_text)
                .content(R.string.logout_current_text)
                .icon(ContextCompat.getDrawable(context, R.drawable.logo))
                .maxIconSize(80)
                .positiveText(confirmTextRes)
                .negativeText(cancelTextRes)
                .onPositive(confirmCallBack)
                .onNegative(cancelCallBack).build();
    }

    public static MaterialDialog createWeiXinDialog(Context context, int confirmTextRes, int cancelTextRes, MaterialDialog.SingleButtonCallback confirmCallBack, MaterialDialog.SingleButtonCallback cancelCallBack) {
        return new MaterialDialog.Builder(context)
                .titleColorRes(R.color.dialog_title_color)
                .contentColorRes(R.color.dialog_content_color)
                .backgroundColorRes(R.color.white)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.quick_text_color)
                .title(R.string.app_name)
                .content(R.string.open_weixin_text)
                .icon(ContextCompat.getDrawable(context, R.drawable.logo))
                .maxIconSize(80)
                .positiveText(confirmTextRes)
                .negativeText(cancelTextRes)
                .onPositive(confirmCallBack)
                .onNegative(cancelCallBack).build();
    }

}
