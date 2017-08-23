package com.feiyou.headstyle.common;

public class Server {
	public final static String BASE_SERVER_IP = "http://tx.qqtn.com/apajax.asp?";

	//获取首页数据
	public final static String HOME_DATA = BASE_SERVER_IP + "action=0&ctype=0&num=60";

	//搜索接口地址
	public final static String SEARCH_DATA = BASE_SERVER_IP + "action=2&num=60";

	//登录接口地址
	public final static String LOGIN_DATA = BASE_SERVER_IP + "action=3";

	//用户添加/取消收藏
	public final static String HEAD_KEEP_DATA = BASE_SERVER_IP + "action=4";

	//用户收藏列表接口地址
	public final static String KEEP_LIST_DATA = BASE_SERVER_IP + "action=5&num=60&selkeep=1";

	//发帖
	public final static String SEND_ARTICLE_DATA = "http://tx.qqtn.com/txapp/sajax.asp?action=upload";

	//发帖(无文件)
	public final static String SEND_ARTICLE_NO_FILE_DATA = "http://tx.qqtn.com/txapp/sajax.asp?action=addshow";

	//版本检测
	public final static String CHECK_VERSION_DATA = "http://tx.qqtn.com/txapp/apk/up.asp?action=up";

	//设置使用次数
	public final static String USE_COUNT_DATA = BASE_SERVER_IP + "action=7";

	//获取头像详情页滚动时提前加载的数据
	public final static String PRE_LOAD_DATA = BASE_SERVER_IP + "action=0&ctype=0&num=10";

	//获取广场页所有数据
	public final static String ARTICLE_ALL_DATA =  "http://tx.qqtn.com/txapp/sajax.asp?action=show";

	//帖子详情
	public final static String ARTICLE_DETAIL_DATA = "http://tx.qqtn.com/txapp/sajax.asp?action=showinfo";

	//评论详情
	public final static String COMMENT_DATA = "http://tx.qqtn.com/txapp/sajax.asp?action=cinfolist&num=10&p=1";

	//点赞
	public final static String UP_ZAN_DATA =  "http://tx.qqtn.com/txapp/sajax.asp?action=upzan";

	//评论
	public final static String ADD_COMMENT_DATA =  "http://tx.qqtn.com/txapp/sajax.asp?action=adcomment";

	//我的发帖
	public final static String MY_ARTICLE_DATA =  "http://tx.qqtn.com/txapp/sajax.asp?action=myshow";
}
