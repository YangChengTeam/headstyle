package com.feiyou.headstyle.bean;

import java.util.List;

/**
 * Created by admin on 2017/9/7.
 */

public class SpecialInfoData extends Result {

    public List<SpecialListRet> data;

    public List<SpecialListRet> getData() {
        return data;
    }

    public void setData(List<SpecialListRet> data) {
        this.data = data;
    }
}
