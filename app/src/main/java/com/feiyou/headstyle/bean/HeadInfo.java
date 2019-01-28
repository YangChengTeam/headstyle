package com.feiyou.headstyle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by admin on 2016/9/5.
 */

@Entity
public class HeadInfo implements Parcelable {
    @Id
    private Long id;
    private String cid;
    private String hurl;
    private String clicknum;
    private String keepnum;
    private String gaoqing;
    private Long is_keep;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getHurl() {
        return this.hurl;
    }

    public void setHurl(String hurl) {
        this.hurl = hurl;
    }

    public String getClicknum() {
        return this.clicknum;
    }

    public void setClicknum(String clicknum) {
        this.clicknum = clicknum;
    }

    public String getKeepnum() {
        return this.keepnum;
    }

    public void setKeepnum(String keepnum) {
        this.keepnum = keepnum;
    }

    public String getGaoqing() {
        return this.gaoqing;
    }

    public void setGaoqing(String gaoqing) {
        this.gaoqing = gaoqing;
    }

    public Long getIs_keep() {
        return is_keep;
    }

    public void setIs_keep(Long is_keep) {
        this.is_keep = is_keep;
    }

    public HeadInfo() {
    }

 

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.cid);
        dest.writeString(this.hurl);
        dest.writeString(this.clicknum);
        dest.writeString(this.keepnum);
        dest.writeString(this.gaoqing);
        dest.writeValue(this.is_keep);
    }

    protected HeadInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.cid = in.readString();
        this.hurl = in.readString();
        this.clicknum = in.readString();
        this.keepnum = in.readString();
        this.gaoqing = in.readString();
        this.is_keep = (Long) in.readValue(Long.class.getClassLoader());
    }

    @Generated(hash = 926401397)
    public HeadInfo(Long id, String cid, String hurl, String clicknum,
            String keepnum, String gaoqing, Long is_keep) {
        this.id = id;
        this.cid = cid;
        this.hurl = hurl;
        this.clicknum = clicknum;
        this.keepnum = keepnum;
        this.gaoqing = gaoqing;
        this.is_keep = is_keep;
    }


    public static final Creator<HeadInfo> CREATOR = new Creator<HeadInfo>() {
        @Override
        public HeadInfo createFromParcel(Parcel source) {
            return new HeadInfo(source);
        }

        @Override
        public HeadInfo[] newArray(int size) {
            return new HeadInfo[size];
        }
    };


}
