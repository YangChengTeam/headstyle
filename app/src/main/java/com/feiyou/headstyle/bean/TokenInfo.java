package com.feiyou.headstyle.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/12/5.
 */

public class TokenInfo implements Serializable {

    public int code;

    public String token;

    public String userId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
