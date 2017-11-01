package com.feiyou.headstyle.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/9/5.
 */
public class TestTopicInfo implements Serializable {

    public String sid;
    public String qorder;
    public String testid;
    public String title;

    public List<TestTopicItemInfo> list;
}
