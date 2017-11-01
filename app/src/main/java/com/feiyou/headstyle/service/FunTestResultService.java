package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.FunTestAddCommentInfoRet;
import com.feiyou.headstyle.bean.FunTestCommentInfoRet;
import com.feiyou.headstyle.bean.FunTestResultListRet;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.StringUtils;

/**
 * Created by admin on 2016/9/6.
 */
public class FunTestResultService extends BaseService {
    private FunTestResultListRet result;

    private FunTestCommentInfoRet commentInfoRet;

    private FunTestAddCommentInfoRet addCommentInfoRet;

    @Override
    public FunTestResultListRet getData(String response) {
        if (!StringUtils.isEmpty(response)) {
            result = Constant.GSON.fromJson(response, FunTestResultListRet.class);
            return result;
        }
        return null;
    }

    public Result getUpdateResult(String response) {
        if (!StringUtils.isEmpty(response)) {
            Result uResult = Constant.GSON.fromJson(response, Result.class);
            return uResult;
        }
        return null;
    }

    public FunTestCommentInfoRet getCommentData(String response) {
        if (!StringUtils.isEmpty(response)) {
            commentInfoRet = Constant.GSON.fromJson(response, FunTestCommentInfoRet.class);
            return commentInfoRet;
        }
        return null;
    }

    public FunTestAddCommentInfoRet addCommentData(String response) {
        if (!StringUtils.isEmpty(response)) {
            addCommentInfoRet = Constant.GSON.fromJson(response, FunTestAddCommentInfoRet.class);
            return addCommentInfoRet;
        }
        return null;
    }

    public Result getAgreeResult(String response) {
        if (!StringUtils.isEmpty(response)) {
            Result uResult = Constant.GSON.fromJson(response, Result.class);
            return uResult;
        }
        return null;
    }

}
