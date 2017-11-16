package com.feiyou.headstyle.common;

public class Server {

    public final static boolean isDebug = false;

    public final static String BASE_SERVER_IP = isDebug ? "http://test.qqtn.com/" : "http://tx.qqtn.com/";

    //获取首页数据
    public final static String HOME_DATA = BASE_SERVER_IP + "apajax.asp?action=0&ctype=0&num=50";

    //获取首页数据
    public final static String NEW_HOME_DATA = BASE_SERVER_IP + "gxtx/0";

    //搜索接口地址
    public final static String SEARCH_DATA = BASE_SERVER_IP + "apajax.asp?action=2&num=50";

    //登录接口地址
    public final static String LOGIN_DATA = BASE_SERVER_IP + "apajax.asp?action=3";

    //用户添加/取消收藏
    public final static String HEAD_KEEP_DATA = BASE_SERVER_IP + "apajax.asp?action=4";

    //用户收藏列表接口地址
    public final static String KEEP_LIST_DATA = BASE_SERVER_IP + "apajax.asp?action=5&num=50&selkeep=1";

    //发帖
    public final static String SEND_ARTICLE_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=upload";

    //发帖(无文件)
    public final static String SEND_ARTICLE_NO_FILE_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=addshow";

    //版本检测
    public final static String CHECK_VERSION_DATA = BASE_SERVER_IP + "txapp/apk/up.asp?action=up";

    //设置使用次数
    public final static String USE_COUNT_DATA = BASE_SERVER_IP + "apajax.asp?action=7";

    //获取头像详情页滚动时提前加载的数据
    public final static String PRE_LOAD_DATA = BASE_SERVER_IP + "apajax.asp?action=0&ctype=0&num=10";

    //获取广场页所有数据
    public final static String ARTICLE_ALL_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=show";

    //帖子详情
    public final static String ARTICLE_DETAIL_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=showinfo";

    //评论详情
    public final static String COMMENT_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=cinfolist&num=10&p=1";

    //点赞
    public final static String UP_ZAN_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=upzan";

    //评论
    public final static String ADD_COMMENT_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=adcomment";

    //我的发帖
    public final static String MY_ARTICLE_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=myshow";

    //专题列表数据
    public final static String SPECIAL_LIST_DATA = BASE_SERVER_IP + "apajax.asp?action=8";

    //测试列表数据
    public final static String FUN_TEST_LIST_DATA = BASE_SERVER_IP + "apajax.asp?action=10";

    //测试问题及答案
    public final static String FUN_TOPIC_ITEM_DATA = BASE_SERVER_IP + "apajax.asp?action=11";

    //测试结果
    public final static String FUN_TEST_RESULT_LIST_DATA = BASE_SERVER_IP + "apajax.asp?action=12";

    //更新分享及测试人数
    public final static String FUN_TEST_COUNT_DATA = BASE_SERVER_IP + "apajax.asp?action=13";

    //查询测试的评论列表
    public final static String FUN_TEST_COMMENT_LIST_DATA = BASE_SERVER_IP + "apajax.asp?action=14";

    //测试添加评论
    public final static String FUN_TEST_ADD_COMMENT_DATA = BASE_SERVER_IP + "apajax.asp?action=14";

    //测试-评论点赞
    public final static String FUN_TEST_AGREE_DATA = BASE_SERVER_IP + "apajax.asp?action=15";

    //帖子-评论点赞
    public final static String COMMENT_AGREE_DATA = BASE_SERVER_IP + "txapp/sajax.asp?action=upcommentzan";
}
