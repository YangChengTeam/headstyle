package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.feiyou.headstyle.App;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.service.UserService;
import com.feiyou.headstyle.ui.fragment.CreateFragment;
import com.feiyou.headstyle.ui.fragment.FunTestFragment;
import com.feiyou.headstyle.ui.fragment.HomeFragment;
import com.feiyou.headstyle.ui.fragment.MyFragment1;
import com.feiyou.headstyle.ui.fragment.Show1Fragment;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.feiyou.headstyle.util.StringUtils;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMShareAPI;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

public class MainActivity extends BaseActivity {

    private long clickTime = 0;

    //定义FragmentTabHost对象
    @BindView(R.id.tabhost)
    public FragmentTabHost mTabHost;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {HomeFragment.class, Show1Fragment.class, CreateFragment.class, FunTestFragment.class, MyFragment1.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.selector_home, R.drawable.selector_show, R.drawable.selector_create, R.drawable.selector_test, R.drawable.selector_my};

    //Tab选项卡的文字
    private int mTextviewArray[] = {R.string.tab_home_text, R.string.tab_show_text, R.string.tab_create_text, R.string.tab_fun_test_text, R.string.tab_my_text};

    private OKHttpRequest okHttpRequest = null;

    ArticleService articleService = null;

    private UserService mService = null;

    private UserInfo userInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initVars() {
        super.initVars();
        articleService = new ArticleService();
        mService = new UserService();
        okHttpRequest = new OKHttpRequest();
    }

    /**
     * 初始化组件
     */
    public void initViews() {
        super.initViews();
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //初始化TabHost
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        //得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(mTextviewArray[i])).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }

        setCurrentTab(0);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Logger.e("tabId---" + tabId);

                //有消息提示时，当选择了该界面，让提示红点消失
                if (tabId.equals(getResources().getString(mTextviewArray[4]))) {
                    View currentView = mTabHost.getTabWidget().getChildTabViewAt(4);
                    ImageView redImageView = (ImageView) currentView.findViewById(R.id.message_red_point);
                    redImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    public void setCurrentTab(int tabPosition) {
        mTabHost.setCurrentTab(tabPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            clickTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //获取用户信息，主要是获取评论数
        getUserInfo();
    }

    public void getUserInfo() {
        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        if (userInfo != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("uid", userInfo.getUid());
            //查询用户信息
            okHttpRequest.aget(Server.USER_INFO_DATA, params, new OnResponseListener() {
                @Override
                public void onSuccess(String response) {
                    UserInfo tempInfo = mService.login(response);
                    if (tempInfo != null) {
                        userInfo = tempInfo;
                        PreferencesUtils.putObject(MainActivity.this, Constant.USER_INFO, userInfo);

                        //原有的评论数
                        int lastCount = PreferencesUtils.getInt(MainActivity.this, App.AID + userInfo.uid);

                        int currentCount = !StringUtils.isBlank(userInfo.getComment()) ? Integer.parseInt(userInfo.getComment()) : 0;
                        //存储最新的评论数
                        PreferencesUtils.putInt(MainActivity.this, App.AID + userInfo.uid, currentCount);
                        if (currentCount > 0 && currentCount > lastCount) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    View currentView = mTabHost.getTabWidget().getChildTabViewAt(4);
                                    ImageView redImageView = (ImageView) currentView.findViewById(R.id.message_red_point);
                                    redImageView.setVisibility(View.VISIBLE);
                                    App.isMessage = true;
                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                }

                @Override
                public void onBefore() {
                }
            });
            //连接融云服务器
            if (!App.isConnect) {
                connect(userInfo.getUsertoken());
            }

        } else {
            App.isMessage = false;
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.LOGIN_SUCCESS)
            }
    )
    public void loginSuccess(String result) {
        Logger.e("login success--->");
        getUserInfo();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.MESSAGE)
            }
    )
    public void messageHint(String result) {
        View currentView = mTabHost.getTabWidget().getChildTabViewAt(4);
        if (currentView != null) {
            ImageView redImageView = (ImageView) currentView.findViewById(R.id.message_red_point);
            if (App.isLetter) {
                redImageView.setVisibility(View.VISIBLE);
            } else {
                redImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #'init'(Context)} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token      从服务端获取的用户身份令牌（Token）。
     * @param 'callback' 连接回调。
     * @return RongIM  客户端核心类的实例。
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {

                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Logger.e("talk success --->");
                    App.isConnect = true;
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Logger.e("talk error --->");
                }
            });
        }
    }

}
