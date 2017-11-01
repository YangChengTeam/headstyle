package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.SpecialInfoData;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.StringUtils;

/**
 * Created by admin on 2016/9/6.
 */
public class SpecialService extends BaseService {
    private SpecialInfoData result;

    @Override
    public SpecialInfoData getData(String response) {
        //if (result != null) return result;
        if (!StringUtils.isEmpty(response)) {
            result = Constant.GSON.fromJson(response, SpecialInfoData.class);
            return result;
        }
        return null;
    }
}
