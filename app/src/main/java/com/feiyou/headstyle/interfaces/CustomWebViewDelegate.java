package com.feiyou.headstyle.interfaces;


public interface CustomWebViewDelegate extends BaseCustomeWebViewDelegate {
    void initWithUrl(String url);

    void search(String searchKey);

    /**
     *
     * @param uid 用户ID
     * @param oid 用户openID
     * @param sid 帖子ID
     *
     */
    void showDetail(String uid, String oid, String sid);

    /**
     *
     *
     * @param imageList 图片地址
     * @param position 索引位置
     *
     */
    void showImage(String imageList, String position);

    /**
     * 未登录时，网页调用的方法
     */
    void isNotLogin();

    /**
     * 跳转到"我的发帖"
     */
    void addArticle();
}
