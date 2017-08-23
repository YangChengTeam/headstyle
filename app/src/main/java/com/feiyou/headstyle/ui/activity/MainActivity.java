package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.feiyou.headstyle.HeadStyleApplication;
import com.feiyou.headstyle.R;
import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.ArticleListRet;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.Server;
import com.feiyou.headstyle.net.OKHttpRequest;
import com.feiyou.headstyle.net.listener.OnResponseListener;
import com.feiyou.headstyle.service.ArticleService;
import com.feiyou.headstyle.ui.fragment.CreateFragment;
import com.feiyou.headstyle.ui.fragment.HomeFragment;
import com.feiyou.headstyle.ui.fragment.MyFragment;
import com.feiyou.headstyle.ui.fragment.Show1Fragment;
import com.feiyou.headstyle.util.AppUtils;
import com.feiyou.headstyle.util.PreferencesUtils;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMShareAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    private long clickTime = 0;
    //定义FragmentTabHost对象
    @BindView(R.id.tabhost)
    public FragmentTabHost mTabHost;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {HomeFragment.class, Show1Fragment.class, CreateFragment.class, MyFragment.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.selector_home, R.drawable.selector_show, R.drawable.selector_create,
            R.drawable.selector_my};

    //Tab选项卡的文字
    private int mTextviewArray[] = {R.string.tab_home_text, R.string.tab_show_text, R.string.tab_create_text, R.string.tab_my_text};

    private OKHttpRequest okHttpRequest = null;

    ArticleService articleService = null;

    private UserInfo userInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    View currentView = mTabHost.getTabWidget().getChildTabViewAt(3);
                    ImageView redImageView = (ImageView) currentView.findViewById(R.id.message_red_point);
                    redImageView.setVisibility(View.VISIBLE);
                    HeadStyleApplication.isMessage = true;
                    break;
                case 1:
                    //ToastUtils.show(MainActivity.this,msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initVars() {
        super.initVars();
        articleService = new ArticleService();
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

        //wifi下，下载游戏,可免安装试玩
        //if(NetUtil.isWIFIConnected(context)){
        new DownGameAsyncTask().execute();
        //}

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Logger.e("tabId---" + tabId);

                if (tabId.equals(getResources().getString(mTextviewArray[3]))) {
                    View currentView = mTabHost.getTabWidget().getChildTabViewAt(3);
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
        if (isMenuOpen()) {
            String tag = mTabHost.getCurrentTabTag();
            Show1Fragment currnetFragment = (Show1Fragment) getSupportFragmentManager().findFragmentByTag(tag);
            //currnetFragment.closeMenu();
        } else {
            exit();
        }
    }

    public boolean isMenuOpen() {
        boolean flag = false;
        String tag = mTabHost.getCurrentTabTag();

        if (tag.equals("我秀")) {
            Show1Fragment currnetFragment = (Show1Fragment) getSupportFragmentManager().findFragmentByTag(tag);
            /*if(currnetFragment.isShow){
                flag = true;
            }*/
        }
        return flag;
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
        if (AppUtils.isLogin(this)) {
            initCommentCount();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class DownGameAsyncTask extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            return downLoadGame();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {

               /* try {
                    System.loadLibrary("cocos2dlua");
                } catch (Throwable e) {

                }*/
                // Log.e("install---","install---"+res);
            }
        }
    }

    public static boolean downLoadGame() {
        boolean flag = true;

        /*HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        try {

            File fileDir = new File(Constant.GAME_DIR);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            final File file = new File(Constant.GAME_DOWN_FILE_PATH);
            URL url = new URL(Constant.GAME_INSTALL_URL);
            //LogUtil.msg("插件正在下载...");
            conn = (HttpURLConnection) url
                    .openConnection();
            if (file.exists() && file.length() == conn.getContentLength()) {
                flag = true;
                conn.disconnect();
            }
            is = conn.getInputStream();
            os = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            int len;
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }*/

        /*if(flag){
            FreeInstallUtil.installApk(Constant.GAME_PACKAGE_NAME);
        }*/

        return flag;
    }

    /**
     * 获取我的发帖的评论数
     */
    public void initCommentCount() {
        userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
        if (userInfo != null) {
            Map<String, String> params = new HashMap<String, String>();
            userInfo = (UserInfo) PreferencesUtils.getObject(this, Constant.USER_INFO, UserInfo.class);
            if (userInfo != null) {
                params.put("uid", userInfo.uid);
                params.put("num", "10000");//设置一个分页每页的条数，目的是查出所有记录
                okHttpRequest.aget(Server.MY_ARTICLE_DATA, params, new OnResponseListener() {
                    @Override
                    public void onSuccess(String response) {

                        ArticleListRet articleListRet = articleService.getData(response);
                        int commentCount = 0;
                        if (articleListRet != null) {
                            List<ArticleInfo> temp = articleListRet.data;
                            if (temp != null) {
                                for (int i = 0; i < temp.size(); i++) {
                                    commentCount += temp.get(i).comment;
                                }
                            }
                        }

                        int lastComment = PreferencesUtils.getInt(MainActivity.this, userInfo.uid);

                        //ToastUtils.show(MainActivity.this,"原有评论数--->"+lastComment);

                        //ToastUtils.show(MainActivity.this,"最新评论数--->"+commentCount);

                        PreferencesUtils.putInt(MainActivity.this, userInfo.uid, commentCount);

                        if (commentCount > 0 && commentCount > lastComment) {
                            Message mes = new Message();
                            mes.what = 0;
                            handler.sendMessage(mes);
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
        }
    }


}
