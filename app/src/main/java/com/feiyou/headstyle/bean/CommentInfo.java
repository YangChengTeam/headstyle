package com.feiyou.headstyle.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/9/5.
 */
public class CommentInfo implements Serializable {
    public String simg;
    public String nickname;
    public String addtime;
    public String scontent;

    public String cid;
    public String uid;
    public int zan;
    public int iszan;
    public String maxpage;
    public String sex;
    public int commentnum;
    public List<CommentReplyInfo> replylist;

    public List<CommentReplyInfo> getReplylist() {
        return replylist;
    }

    public void setReplylist(List<CommentReplyInfo> replylist) {
        this.replylist = replylist;
    }
}
