package com.feiyou.headstyle.service;

import com.feiyou.headstyle.bean.BannerInfo;
import com.feiyou.headstyle.bean.HeadInfo;
import com.feiyou.headstyle.bean.HeadListRet;
import com.feiyou.headstyle.bean.SpecialInfo;
import com.feiyou.headstyle.common.Constant;
import com.feiyou.headstyle.common.DbHelper;
import com.feiyou.headstyle.db.greendao.BannerInfoDao;
import com.feiyou.headstyle.db.greendao.HeadInfoDao;
import com.feiyou.headstyle.db.greendao.SpecialInfoDao;
import com.feiyou.headstyle.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/6.
 */
public class HomeService extends BaseService {
    private HeadListRet result;

    private BannerInfoDao bannerInfoDao = DbHelper.getDaoSession().getBannerInfoDao();

    private HeadInfoDao headInfoDao = DbHelper.getDaoSession().getHeadInfoDao();

    private SpecialInfoDao specialInfoDao = DbHelper.getDaoSession().getSpecialInfoDao();

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

    public List<SpecialInfo> getSpecialInfos(String response) {
        HeadListRet result = getData(response);
        if (result == null || result.errCode == Constant.RESULT_FAIL) return null;
        return result.special;
    }

    public int getMaxPage(String response) {
        HeadListRet result = getData(response);
        if (result == null || result.errCode == Constant.RESULT_FAIL) return 0;
        return result.maxpage;
    }

    public List<BannerInfo> getBannerInfoListFromDB() {
        return (ArrayList<BannerInfo>) bannerInfoDao.queryBuilder().list();
    }

    public void saveBannerInfoListToDB(List<BannerInfo> list) {
        if (list != null) {
            for (BannerInfo info : list) {
                bannerInfoDao.insertOrReplace(info);
            }
        }
    }

    public void deleteAllBannerInfoList() {
        bannerInfoDao.deleteAll();
    }

    public List<HeadInfo> getHeadInfoListFromDB() {
        return (ArrayList<HeadInfo>) headInfoDao.queryBuilder().list();
    }

    public void saveHeadInfoListToDB(List<HeadInfo> list) {
        if (list != null) {
            for (HeadInfo info : list) {
                headInfoDao.insertOrReplace(info);
            }
        }
    }

    public void deleteAllHeadInfoList() {
        headInfoDao.deleteAll();
    }


    public List<SpecialInfo> getSpecialInfoListFromDB() {
        return (ArrayList<SpecialInfo>) specialInfoDao.queryBuilder().list();
    }

    public void saveSpecialInfoListToDB(List<SpecialInfo> list) {
        if (list != null) {
            for (SpecialInfo info : list) {
                specialInfoDao.insertOrReplace(info);
            }
        }
    }

    public void deleteAllSpecialInfoList() {
        specialInfoDao.deleteAll();
    }

}
