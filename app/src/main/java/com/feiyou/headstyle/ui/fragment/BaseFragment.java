package com.feiyou.headstyle.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hwangjr.rxbus.RxBus;

import butterknife.ButterKnife;

/**
 * Created by admin on 2016/8/31.
 */
public abstract class BaseFragment extends Fragment {

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(getLayoutId(), null);
            RxBus.get().register(this);
            initVars();
            initViews();
            loadData();
        }
        return mView;
    }


    public abstract int getLayoutId();


    public void initVars() {
    }

    public void initViews() {
        ButterKnife.bind(this, mView);
    }

    public void loadData() {

    }

    // 暂停图片请求
    public static void imagePause() {
        Fresco.getImagePipeline().pause();
    }

    // 恢复图片请求
    public static void imageResume() {
        Fresco.getImagePipeline().resume();
    }
}
