package com.feiyou.headstyle.bean;

import java.io.Serializable;


import android.content.Context;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.util.ToastUtils;
import com.umeng.socialize.bean.StatusCode;

public class Result implements Serializable {

    private static final long serialVersionUID = 1L;
    public String message;
    public boolean status;
    public int errCode;

}
