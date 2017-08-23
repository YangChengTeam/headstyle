package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.ArticleInfo;
import com.feiyou.headstyle.bean.ArticleInfoRet;
import com.feiyou.headstyle.bean.ArticleListRet;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.StringUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by admin on 2016/9/6.
 */
public class ArticleService extends BaseService {
    private ArticleListRet result;

    @Override
    public ArticleListRet getData(String response) {
        //if (result != null) return result;
        if (!StringUtils.isEmpty(response)) {
            Logger.e("广场数据---"+response);
            result = Constant.GSON.fromJson(response, ArticleListRet.class);
            return result;
        }
        return null;
    }

    public ArticleInfo getArticleInfoBySID(String response) {
        if (!StringUtils.isEmpty(response)) {
            ArticleInfoRet result = Constant.GSON.fromJson(response, ArticleInfoRet.class);
            if (result == null || result.errCode == Constant.RESULT_FAIL) {
                return null;
            }
            return result.data;
        }
        return null;
    }

    public boolean praise(String response) {
        boolean flag = true;
        if (!StringUtils.isEmpty(response)) {
            Result praiseResult = Constant.GSON.fromJson(response, Result.class);
            if (praiseResult == null || praiseResult.errCode == Constant.RESULT_FAIL) {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }
}
