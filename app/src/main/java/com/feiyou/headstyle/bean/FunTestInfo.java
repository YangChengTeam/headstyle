package com.feiyou.headstyle.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by admin on 2016/9/5.
 */
public class FunTestInfo implements Serializable,MultiItemEntity {

    public String testid;
    public int cid;
    public String sharetotal;
    public String shareperson;
    public String title;
    public String content;
    public String sharetitle;
    public String sharedesc;
    public String sharelogo;
    public String smallimg;
    public String bigimg;
    public String paixu;
    public int stype;

    public int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
