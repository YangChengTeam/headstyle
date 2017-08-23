package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.TypeListAdapter;
import com.feiyou.headstyle.util.StringUtils;
import com.feiyou.headstyle.view.DividerItemDecoration;
import com.orhanobut.logger.Logger;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

public class MoreHeadTypeActivity extends BaseActivity {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindString(R.string.more_head_text)
    String titleTextValue;

    @BindView(R.id.more_recyclerview)
    RecyclerView mRecyclerView;

    //头像类别名称
    private int[] headTypeTextArray;

    private int[] headTypeImageArray;

    private int[] headTypeCid;

    private TypeListAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_more_head_type;
    }

    @Override
    public void initViews() {
        super.initViews();
        titleTv.setText(titleTextValue);
    }

    @Override
    public void loadData() {
        headTypeTextArray = new int[]{
                R.string.type_lovers_text, R.string.type_boy_text,
                R.string.type_girl_text, R.string.type_style_text,
                R.string.type_star_text, R.string.type_animation_text,
                R.string.type_game_text, R.string.type_dear_text,
                R.string.type_battle_text, R.string.type_europe_text,
                R.string.type_word_text
        };
        headTypeImageArray = new int[]{
                R.mipmap.type01, R.mipmap.type02, R.mipmap.type03, R.mipmap.type04, R.mipmap.type05, R.mipmap.type06, R.mipmap.type07, R.mipmap.type09, R.mipmap.type10, R.mipmap.type11, R.mipmap.type12
        };

        headTypeCid = new int[]{15,13,14,2,21,18,64,8,17,11,3};

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL, R.drawable.divider_item));
        mAdapter = new TypeListAdapter(this,headTypeTextArray,headTypeImageArray,headTypeCid);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new TypeListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MoreHeadTypeActivity.this, HeadListActivity.class);
                if(position > -1){
                    intent.putExtra("cid", headTypeCid[position]);
                    intent.putExtra("titleName", MoreHeadTypeActivity.this.getResources().getString(headTypeTextArray[position]));
                }
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Logger.e("MoreHeadTypeActivity finish----");
        finish();
    }
}
