package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.TestTopicInfoListRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.StringUtils;

/**
 * Created by admin on 2016/9/6.
 */
public class FunTopicItemService extends BaseService {
    private TestTopicInfoListRet result;

    @Override
    public TestTopicInfoListRet getData(String response) {
        //if (result != null) return result;
        if (!StringUtils.isEmpty(response)) {
            result = Constant.GSON.fromJson(response, TestTopicInfoListRet.class);
            return result;
        }
        return null;
    }
}
