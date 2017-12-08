package com.feiyou.headstyle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.feiyou.headstyle.R;


/**
 * 从底部弹出或滑出选择菜单或窗口
 * @author admin
 *
 */
public class PhotoModePopupWindow extends PopupWindow {

	//private Button takePhotoBtn, pickPhotoBtn, cancelBtn;
	private View mMenuView;

	private LinearLayout mCameraLayout;

	private LinearLayout mLocalLayout;

	private LinearLayout cancelLayout;

	@SuppressLint("InflateParams")
	public PhotoModePopupWindow(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.photo_mode_view, null);

		mCameraLayout = (LinearLayout)mMenuView.findViewById(R.id.layout_camera);
		mLocalLayout = (LinearLayout)mMenuView.findViewById(R.id.layout_local_photo);
		cancelLayout = (LinearLayout)mMenuView.findViewById(R.id.layout_cancel);

		mCameraLayout.setOnClickListener(itemsOnClick);
		mLocalLayout.setOnClickListener(itemsOnClick);
		cancelLayout.setOnClickListener(itemsOnClick);
		
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果

		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x7f040000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(new BitmapDrawable());

		this.setAnimationStyle(R.style.popwin_anim_style);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

}