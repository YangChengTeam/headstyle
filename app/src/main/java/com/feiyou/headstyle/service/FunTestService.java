package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.FunTestInfoData;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.StringUtils;

/**
 * Created by admin on 2016/9/6.
 */
public class FunTestService extends BaseService {
    private FunTestInfoData result;

    @Override
    public FunTestInfoData getData(String response) {
        //if (result != null) return result;
        if (!StringUtils.isEmpty(response)) {
            result = Constant.GSON.fromJson(response, FunTestInfoData.class);
            return result;
        }
        return null;
    }
}
