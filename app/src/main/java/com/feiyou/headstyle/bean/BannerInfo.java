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
public class BannerInfo implements Parcelable {

    @Id
    private Long id;
    private String sid;
    private String sname;
    private String simg;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.sid);
        dest.writeString(this.sname);
        dest.writeString(this.simg);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSname() {
        return this.sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSimg() {
        return this.simg;
    }

    public void setSimg(String simg) {
        this.simg = simg;
    }

    public BannerInfo() {
    }

    protected BannerInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.sid = in.readString();
        this.sname = in.readString();
        this.simg = in.readString();
    }

    @Generated(hash = 2061988102)
    public BannerInfo(Long id, String sid, String sname, String simg) {
        this.id = id;
        this.sid = sid;
        this.sname = sname;
        this.simg = simg;
    }

    public static final Parcelable.Creator<BannerInfo> CREATOR = new Parcelable.Creator<BannerInfo>() {
        @Override
        public BannerInfo createFromParcel(Parcel source) {
            return new BannerInfo(source);
        }

        @Override
        public BannerInfo[] newArray(int size) {
            return new BannerInfo[size];
        }
    };
}
