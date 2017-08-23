package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.BannerInfo;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.HeadListRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.StringUtils;

import java.util.List;

/**
 * Created by admin on 2016/9/6.
 */
public class HomeService extends BaseService {
    private HeadListRet result;

    @Override
    public HeadListRet getData(String response) {
        //if (result != null) return result;
        if (!StringUtils.isEmpty(response)) {
            result = Constant.GSON.fromJson(response, HeadListRet.class);
            return result;
        }
        return null;
    }

    public List<HeadInfo> getHeadInfos(String response) {
        HeadListRet result = getData(response);
        if (result == null || result.errCode == Constant.RESULT_FAIL) return null;
        return result.data;
    }

    public List<BannerInfo> getBannerInfos(String response) {
        HeadListRet result = getData(response);
        if (result == null || result.errCode == Constant.RESULT_FAIL) return null;
        return result.topslide;
    }

    public int getMaxPage(String response){
        HeadListRet result = getData(response);
        if (result == null || result.errCode == Constant.RESULT_FAIL) return 0;
        return result.maxpage;
    }

}
