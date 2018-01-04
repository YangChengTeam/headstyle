package com.feiyou.headstyle.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;


public class WeiXinFollowDialog extends Dialog {

    private Context context;

    private Dialog dialog;

    public interface TimeListener {
        void timeStart();
    }

    public TimeListener timeListener;

    public void setTimeListener(TimeListener timeListener) {
        this.timeListener = timeListener;
    }

    public WeiXinFollowDialog(Context context) {
        super(context, R.style.Dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.follow_weixin_dialog, null);
        Button openButton = (Button) view.findViewById(R.id.btn_open_weixin);
        ImageView closeImageView = (ImageView) view.findViewById(R.id.iv_close);
        setContentView(view);

        openButton.setOnClickListener(new clickListener());
        closeImageView.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_open_weixin:
                    openWeiXin();
                    break;
                case R.id.iv_close:
                    closeChargeDialog();
                    break;
                default:
                    break;
            }
        }
    }

    public void openWeiXin() {

        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(ClipData.newPlainText(null, "头像达人"));
        ToastUtils.show(context, "公众号已复制,可以关注了");

        MobclickAgent.onEvent(context, "open_weixin_click", AppUtils.getVersionName(context));

        AppUtils.launchApp((Activity) context, "com.tencent.mm", 1);

        timeListener.timeStart();
        closeChargeDialog();
    }

    public void showChargeDialog(Dialog dia) {
        this.dialog = dia;
        this.dialog.show();
    }

    public void closeChargeDialog() {
        if (isValidContext(context) && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @SuppressLint("NewApi")
    private boolean isValidContext(Context ctx) {
        Activity activity = (Activity) ctx;

        if (Build.VERSION.SDK_INT > 17) {
            if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                return false;
            } else {
                return true;
            }
        } else {
            if (activity == null || activity.isFinishing()) {
                return false;
            } else {
                return true;
            }
        }
    }
}