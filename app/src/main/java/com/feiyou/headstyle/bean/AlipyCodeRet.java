package com.feiyou.headstyle.bean;

/**
 * Created by admin on 2017/12/17.
 */

public class AlipyCodeRet {
    public int code;
    public String msg;
    public AlipyCodeInfo data;
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

    public AlipyCodeInfo getData() {
        return data;
    }

    public void setData(AlipyCodeInfo data) {
        this.data = data;
    }
}
