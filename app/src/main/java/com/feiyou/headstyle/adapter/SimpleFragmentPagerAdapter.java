package com.feiyou.headstyle.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.ui.fragment.BaseFragment;

import java.util.List;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    private int tabTitles[] = new int[]{R.string.hot_text, R.string.qq_friend_text, R.string.game_friends_text,R.string.video_text};
    private Context context;

    List<BaseFragment> fragments;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context, List<BaseFragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(tabTitles[position]);
    }
}