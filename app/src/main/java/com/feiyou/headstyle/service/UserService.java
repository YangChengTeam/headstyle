package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.HeadListRet;
import com.feiyou.headstyle.bean.Result;
import com.feiyou.headstyle.bean.UserInfo;
import com.feiyou.headstyle.bean.UserInfoRet;
import com.feiyou.headstyle.bean.UserKeepInfo;
import com.feiyou.headstyle.bean.UserKeepInfoRet;
import com.feiyou.headstyle.bean.VersionRet;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.util.StringUtils;

import java.util.List;

/**
 * Created by admin on 2016/9/6.
 */
public class UserService extends BaseService {

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

    public UserInfo login(String response) {
        if (!StringUtils.isEmpty(response)) {
            UserInfoRet result = Constant.GSON.fromJson(response, UserInfoRet.class);
            if (result == null || result.errCode == Constant.RESULT_FAIL) {
                return null;
            }
            return result.data;
        }
        return null;
    }

    public Result headKeep(String response) {
        if (!StringUtils.isEmpty(response)) {
            Result result = Constant.GSON.fromJson(response, Result.class);
            if (result == null || result.errCode == Constant.RESULT_FAIL) {
                return null;
            }
            return result;
        }
        return null;
    }

    public List<HeadInfo> getKeepHeadInfos(String response) {
        HeadListRet result = getData(response);
        if (result == null || result.errCode == Constant.RESULT_FAIL) return null;
        return result.data;
    }


    public UserKeepInfo isKeepByUser(String response) {
        if (!StringUtils.isEmpty(response)) {
            UserKeepInfoRet result = Constant.GSON.fromJson(response, UserKeepInfoRet.class);
            if (result == null || result.errCode == Constant.RESULT_FAIL) {
                return null;
            }
            return result.data;
        }
        return null;
    }

    public Result sendArticle(String response) {
        if (!StringUtils.isEmpty(response)) {
            Result result = Constant.GSON.fromJson(response, Result.class);
            if (result == null || result.errCode == Constant.RESULT_FAIL) {
                return null;
            }
            return result;
        }
        return null;
    }

    public VersionRet checkVersion(String response) {
        if (!StringUtils.isEmpty(response)) {
            VersionRet versionRet = Constant.GSON.fromJson(response, VersionRet.class);
            if (versionRet == null || versionRet.errCode == Constant.RESULT_FAIL) {
                return null;
            }
            return versionRet;
        }
        return null;
    }

}
