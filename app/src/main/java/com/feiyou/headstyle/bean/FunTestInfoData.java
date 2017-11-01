package com.feiyou.headstyle.bean;

import java.util.List;

/**
 * Created by admin on 2017/9/7.
 */

public class FunTestInfoData extends Result {

    public int maxpage;

    public List<FunTestInfo> data;

    public List<FunTestInfo> getData() {
        return data;
    }

    public void setData(List<FunTestInfo> data) {
        this.data = data;
    }
}
