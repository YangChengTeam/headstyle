package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.ArticleInfoRet;
import com.feiyou.headstyle.bean.ArticleListRet;
import com.feiyou.headstyle.bean.CommentInfoRet;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.StringUtils;

/**
 * Created by admin on 2016/9/6.
 */
public class CommentService extends BaseService {
    private CommentInfoRet result;

    @Override
    public CommentInfoRet getData(String response) {
        //if (result != null) return result;
        if (!StringUtils.isEmpty(response)) {
            result = Constant.GSON.fromJson(response, CommentInfoRet.class);
            return result;
        }
        return null;
    }

    public boolean addComment(String response) {
        boolean flag = true;
        if (!StringUtils.isEmpty(response)) {
            Result commentResult = Constant.GSON.fromJson(response, Result.class);
            if (commentResult == null || commentResult.errCode == Constant.RESULT_FAIL) {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }
}
