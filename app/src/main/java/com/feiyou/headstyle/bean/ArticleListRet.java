package com.feiyou.headstyle.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/9/5.
 */
public class ArticleListRet extends Result implements Serializable {

    public List<ArticleInfo> data;

    public int maxpage;
}
