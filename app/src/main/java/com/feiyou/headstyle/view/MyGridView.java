package com.feiyou.headstyle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.orhanobut.logger.Logger;

import java.util.Date;

public class MyGridView extends GridView {

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Logger.i("MAX_HEIGHT---" + (Integer.MAX_VALUE >> 2));
        Logger.i("TIME---" + (new Date().getTime()));
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    /*public void setHeight(int num) {
        ListAdapter listAdapter = getAdapter();
        if (listAdapter == null) {
            return;

        }
        int totalHeight = 0;
        int itemHeight = 0;
        int count = listAdapter.getCount();

        if (count > 0) {
            View listItem = listAdapter.getView(0, null, this);
            listItem.measure(0, 0);
            itemHeight = listItem.getMeasuredHeight();
        }
        int rows = count / num + (count % num > 0 ? 1 : 0);

        totalHeight = itemHeight * rows;

        ViewGroup.LayoutParams params = this.getLayoutParams();

        params.height = totalHeight;

        this.setLayoutParams(params);

    }*/
} 