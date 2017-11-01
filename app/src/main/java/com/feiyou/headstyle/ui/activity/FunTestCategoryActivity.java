package com.feiyou.headstyle.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.ui.fragment.FunTestCategoryFragment;
import com.jakewharton.rxbinding.view.RxView;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by admin on 2017/9/21.
 */

public class FunTestCategoryActivity extends BaseActivity {

    @BindView(R.id.back_image)
    ImageView mBackImageView;

    @BindView(R.id.title_text)
    TextView mTitleTextView;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.note_indicator)
    FixedIndicatorView mFixedIndicatorView;

    private FragmentAdapter mFragmentAdapter;

    private int classId;//分类ID

    @Override
    public int getLayoutId() {
        return R.layout.activity_funtest_category;
    }

    @Override
    protected void initVars() {
        super.initVars();
    }

    @Override
    public void initViews() {
        super.initViews();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            classId = bundle.getInt("class_id");

            switch (classId){
                case 1:
                    mTitleTextView.setText("爱情测试");
                    break;
                case 2:
                    mTitleTextView.setText("性格测试");
                    break;
                case 3:
                    mTitleTextView.setText("能力测试");
                    break;
                case 4:
                    mTitleTextView.setText("趣味测试");
                    break;
            }

        }

        mFixedIndicatorView.setAdapter(new MyAdapter());
        mFixedIndicatorView.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.colorAccent), 6));

        float unSelectSize = 15;
        float selectSize = 15;
        int selectColor = ContextCompat.getColor(this, R.color.colorAccent);
        int unSelectColor = ContextCompat.getColor(this, R.color.black1);
        mFixedIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));
        mFixedIndicatorView.setOnIndicatorItemClickListener(new Indicator.OnIndicatorItemClickListener() {
            @Override
            public boolean onItemClick(View clickItemView, int position) {
                mViewPager.setCurrentItem(position);
                return false;
            }
        });
        mFixedIndicatorView.setCurrentItem(0, true);

        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mFixedIndicatorView.setCurrentItem(i, true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        RxView.clicks(mBackImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    FunTestCategoryFragment funTestCategoryFragment1;
    FunTestCategoryFragment funTestCategoryFragment2;

    class FragmentAdapter extends FragmentStatePagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            if (position == 0) {
                if (funTestCategoryFragment1 == null) {
                    bundle.putInt("type", 1);
                    bundle.putInt("class_id", classId);
                    funTestCategoryFragment1 = new FunTestCategoryFragment();
                    funTestCategoryFragment1.setArguments(bundle);
                }
                return funTestCategoryFragment1;
            } else if (position == 1) {
                if (funTestCategoryFragment2 == null) {
                    bundle.putInt("type", 2);
                    bundle.putInt("class_id", classId);
                    funTestCategoryFragment2 = new FunTestCategoryFragment();
                    funTestCategoryFragment2.setArguments(bundle);
                }
                return funTestCategoryFragment2;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private class MyAdapter extends Indicator.IndicatorAdapter {
        private final String[] titles = new String[]{"最新测试", "热门测试"};

        public MyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.fun_test_tab, parent, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(titles[position]);
            return convertView;
        }
    }

}
