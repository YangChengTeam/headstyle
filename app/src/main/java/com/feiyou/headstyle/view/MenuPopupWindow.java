package com.feiyou.headstyle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.feiyou.headstyle.R;


/**
 * 从顶部弹出或滑出选择菜单或窗口
 *
 * @author admin
 */
public class MenuPopupWindow extends PopupWindow {

    private View mMenuView;

    TextView takePhotoTv;

    TextView chatTv;

    private Animation chat_in, take_photo_in, chat_out, take_photo_out;

    @SuppressLint("InflateParams")
    public MenuPopupWindow(Context context, OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_menu, null);

        takePhotoTv = (TextView) mMenuView.findViewById(R.id.take_photo_tv);
        chatTv = (TextView) mMenuView.findViewById(R.id.chat_tv);

        takePhotoTv.setOnClickListener(itemsOnClick);
        chatTv.setOnClickListener(itemsOnClick);

        chat_in = AnimationUtils.loadAnimation(context, R.anim.qq_friend_in);
        take_photo_in = AnimationUtils.loadAnimation(context, R.anim.take_photo_in);
        chat_out = AnimationUtils.loadAnimation(context, R.anim.qq_friend_out);
        take_photo_out = AnimationUtils.loadAnimation(context, R.anim.take_photo_out);

        takePhotoTv.startAnimation(take_photo_in);
        chatTv.startAnimation(chat_in);

        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.popwin_anim_style);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xA0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
       mMenuView.setOnTouchListener(new OnTouchListener() {

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.menu_item_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height) {
                        //dismiss();
                        closePopwindow();
                    }
                }
                return true;
            }
        });
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    public void closePopwindow() {
        takePhotoTv.startAnimation(take_photo_out);
        chatTv.startAnimation(chat_out);
        handler.postDelayed(runnable, 10);
    }

}