package com.feiyou.headstyle.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/9/5.
 */
public class HeadListRet extends Result implements Serializable {

    public List<HeadInfo> data;

    public List<BannerInfo> topslide;

    public List<SpecialInfo> special;

    public int maxpage;
}
