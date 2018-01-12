package com.feiyou.headstyle.bean;

import java.util.List;

/**
 * Created by admin on 2017/12/17.
 */

public class StickerInfoRet {

    public int code;
    public String msg;
    public List<StickerInfo> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<StickerInfo> getData() {
        return data;
    }

    public void setData(List<StickerInfo> data) {
        this.data = data;
    }
}
