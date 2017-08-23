package com.feiyou.headstyle.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
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

}
