package com.feiyou.headstyle.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/9/5.
 */
public class ArticleInfo implements Serializable {

    public String sid;
    public String uid;
    public String ding;
    public String simg;
    public String scontent;
    public String cimg;
    public int comment;
    public int zan;
    public int iszan;
    public String addtime;
    public String sex;
    public String maxpage;
    public String pagenow;
    public String type;
    public String nickname;
    public List<AgreeInfo> zanlist;

    public List<AgreeInfo> getZanlist() {
        return zanlist;
    }

    public void setZanlist(List<AgreeInfo> zanlist) {
        this.zanlist = zanlist;
    }
}
