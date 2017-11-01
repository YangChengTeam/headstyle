package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.FunTopicItemOptionAdapter;
import com.feiyou.headstyle.bean.FunTestInfo;
import com.feiyou.headstyle.bean.TestTopicInfo;
import com.feiyou.headstyle.bean.TestTopicInfoListRet;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.FunTopicItemService;
import com.feiyou.headstyle.util.DialogUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by admin on 2017/9/14.
 */

public class FunTestItemActivity extends BaseActivity {

    @BindView(R.id.title_text)
    TextView titleTv;

    @BindView(R.id.tv_test_title)
    TextView mFunTestTitleTextView;

    @BindView(R.id.tv_test_count)
    TextView mTestCountTextView;

    @BindView(R.id.tv_share_count)
    TextView mShareCountTextView;

    @BindView(R.id.tv_test_topic_title)
    TextView mTopicTitle;

    @BindView(R.id.tv_progress)
    TextView mTestProgressTextView;

    @BindView(R.id.all_topic_item_list)
    RecyclerView mTopicItemRecyclerView;

    @BindView(R.id.btn_pre_topic)
    Button mPreButton;

    @BindView(R.id.btn_submit)
    Button mSubmitButton;

    OKHttpRequest okHttpRequest = null;

    FunTopicItemOptionAdapter mFunTopicItemOptionAdapter;

    LinearLayoutManager mLinearLayoutManager;

    @BindView(R.id.btn_next)
    Button mNextButton;

    FunTopicItemService mService;

    List<TestTopicInfo> mTopicList;

    private int currentPosition;

    private FunTestInfo funTestInfo;

    private int[] itemResult;

    private int cType;

    private Map<Integer, Integer> analysisMap;

    private Map<Integer, Integer> topicIndexMap;

    private int aPosition;

    private int hrefPosition;

    private View lastOptionItemView;

    private MaterialDialog testDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test_topic;
    }

    @Override
    protected void initVars() {
        super.initVars();
        okHttpRequest = new OKHttpRequest();
    }

    @Override
    public void initViews() {
        super.initViews();

        Bundle bundle = getIntent().getExtras();
        funTestInfo = (FunTestInfo) bundle.getSerializable("fun_test_data_info");
        if (funTestInfo != null) {
            mTestCountTextView.setText(funTestInfo.shareperson + "人参与测试");
            mShareCountTextView.setText(funTestInfo.sharetotal + "人转发");
            mFunTestTitleTextView.setText(funTestInfo.title);
            cType = funTestInfo.stype;
        }

        analysisMap = new HashMap<Integer, Integer>();
        topicIndexMap = new HashMap<Integer, Integer>();
        if (aPosition == 0) {
            topicIndexMap.put(aPosition, currentPosition);
        }
        mService = new FunTopicItemService();
        titleTv.setText("测试题目详情");
        mLinearLayoutManager = new LinearLayoutManager(this);
        mFunTopicItemOptionAdapter = new FunTopicItemOptionAdapter(this, null);
        mTopicItemRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTopicItemRecyclerView.setAdapter(mFunTopicItemOptionAdapter);

        mFunTopicItemOptionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (lastOptionItemView != null) {
                    lastOptionItemView.findViewById(R.id.layout_topic_option).setBackgroundResource(R.drawable.topic_item_bg);
                }

                view.findViewById(R.id.layout_topic_option).setBackgroundResource(R.drawable.topic_item_selecet_bg);
                lastOptionItemView = view;

                if (cType == 1) {
                    if (currentPosition < itemResult.length) {

                        Logger.e("position score --->" + mFunTopicItemOptionAdapter.getData().get(position).itemcount);

                        itemResult[currentPosition] = position + 1;
                        currentPosition++;
                    }
                } else {
                    hrefPosition = mFunTopicItemOptionAdapter.getData().get(position).itemhref;
                    if (hrefPosition <= mTopicList.size() && aPosition < mTopicList.size()) {
                        if (hrefPosition > 0) {
                            currentPosition = hrefPosition - 1;
                            analysisMap.put(aPosition, position + 1);
                            aPosition++;
                            topicIndexMap.put(aPosition, currentPosition);
                        } else {
                            analysisMap.put(aPosition, position + 1);
                            //topicIndexMap.put(aPosition + 1, currentPosition);
                        }
                    }
                }
                mFunTopicItemOptionAdapter.setSelectItem(-1);

                if (currentPosition >= mTopicList.size() && cType == 1) {
                    currentPosition = mTopicList.size() - 1;
                    //ToastUtils.show(FunTestItemActivity.this, "已经是最后一题了");
                    //return;
                }
                updateItemView();
            }
        });

        RxView.clicks(mPreButton).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (cType == 1) {
                    if (currentPosition > 0) {
                        lastOptionItemView = mLinearLayoutManager.findViewByPosition(currentPosition);
                        currentPosition--;
                        updateItemView();
                    }
                } else {
                    if (aPosition > -1) {
                        aPosition--;
                        if (aPosition < 0) {
                            aPosition = 0;
                        }
                        currentPosition = topicIndexMap.get(aPosition);
                        hrefPosition = mTopicList.get(currentPosition).list.get(0).itemhref;
                        updateItemView();
                    }
                }
            }
        });

        RxView.clicks(mSubmitButton).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                int tempIndex;
                int itemScore = 0;
                if (cType == 1) {
                    if (itemResult != null) {
                        for (int i = 0; i < itemResult.length; i++) {
                            int index = itemResult[i] - 1;
                            if (index < 0) {
                                index = 0;
                            }
                            int tempScore = mTopicList.get(i).list.get(index).itemcount;
                            itemScore += tempScore;
                        }
                    }

                    Logger.e("total score --->" + itemScore);

                } else {
                    currentPosition = topicIndexMap.get(aPosition);
                    tempIndex = analysisMap.get(aPosition) - 1;
                    itemScore = mTopicList.get(currentPosition).list.get(tempIndex).itemcount;
                }
                Intent intent = new Intent(FunTestItemActivity.this, FunTestResultActivity.class);
                intent.putExtra("test_score", itemScore);
                intent.putExtra("test_id", funTestInfo.testid);
                intent.putExtra("fun_test_data_info", funTestInfo);
                startActivity(intent);
                finish();
            }
        });

        initTestDialog();
    }

    /**
     * 初始化提示框
     */
    public void initTestDialog() {
        //弹出QQ登录询问框
        testDialog = DialogUtils.createTestDialog(this, R.string.give_up_text, R.string.cancel_login_text, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                finish();
            }
        }, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void loadData() {
        super.loadData();

        final Map<String, String> params = new HashMap<String, String>();
        params.put("testid", funTestInfo.testid);
        params.put("num", "200");
        params.put("p", "1");
        okHttpRequest.aget(Server.FUN_TOPIC_ITEM_DATA, params, new OnResponseListener() {
            @Override
            public void onSuccess(String response) {
                TestTopicInfoListRet result = mService.getData(response);
                if (result != null && result.data != null && result.data.size() > 0) {
                    mTopicList = result.data;
                    if (cType == 1) {
                        itemResult = new int[mTopicList.size()];
                    }
                    hrefPosition = mTopicList.get(0).list.get(0).itemhref;
                    updateItemView();
                }
            }

            @Override
            public void onError(Exception e) {
            }

            @Override
            public void onBefore() {

            }
        });
    }

    public void updateItemView() {
        if (currentPosition == 0 || mTopicList.size() == 1) {
            mPreButton.setVisibility(View.GONE);
        } else {
            mPreButton.setVisibility(View.VISIBLE);
        }

        if (currentPosition >= mTopicList.size()) {
            currentPosition = mTopicList.size() - 1;
        }

        if (currentPosition == mTopicList.size() - 1 || hrefPosition == 0) {
            mSubmitButton.setBackgroundResource(R.color.colorPrimary);
            mSubmitButton.setClickable(true);
        } else {

            mSubmitButton.setBackgroundResource(R.color.line_color);
            mSubmitButton.setClickable(false);
        }
        if (cType == 1) {
            mTestProgressTextView.setText(Html.fromHtml("<font color='#000000'>[</font><font color='#fb5161'>" + (currentPosition + 1) + "</font><font color='#000000'>/" + mTopicList.size() + "]</font>"));
        } else {
            mTestProgressTextView.setVisibility(View.GONE);
        }
        mTopicTitle.setText((currentPosition + 1) + "." + mTopicList.get(currentPosition).title);

        if (cType == 1) {
            if (itemResult[currentPosition] > 0) {
                mFunTopicItemOptionAdapter.setSelectItem(itemResult[currentPosition] - 1);
            }
            hrefPosition = 1;//cType=1时，设置hrefPosition>0即可
        } else {
            if (analysisMap != null && analysisMap.get(aPosition) != null && analysisMap.get(aPosition) > -1) {
                mFunTopicItemOptionAdapter.setSelectItem(analysisMap.get(aPosition) - 1);
            }
        }

        if (currentPosition < mTopicList.size() && hrefPosition > 0) {
            mFunTopicItemOptionAdapter.setNewData(mTopicList.get(currentPosition).list);
        }
    }

    @OnClick(R.id.back_image)
    public void finishActivity() {
        if (currentPosition > -1 && currentPosition < mTopicList.size()) {
            testDialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentPosition > -1 && currentPosition < mTopicList.size()) {
            testDialog.show();
        } else {
            finish();
        }
    }

}
