package com.feiyou.headstyle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by admin on 2017/9/7.
 */
@Entity
public class SpecialInfo implements Parcelable {
    @Id
    private Long id;
    private String sid;
    private String paixu;
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
        dest.writeString(this.paixu);
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

    public String getPaixu() {
        return this.paixu;
    }

    public void setPaixu(String paixu) {
        this.paixu = paixu;
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

    public SpecialInfo() {
    }

    protected SpecialInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.sid = in.readString();
        this.paixu = in.readString();
        this.sname = in.readString();
        this.simg = in.readString();
    }

    @Generated(hash = 1784842247)
    public SpecialInfo(Long id, String sid, String paixu, String sname,
            String simg) {
        this.id = id;
        this.sid = sid;
        this.paixu = paixu;
        this.sname = sname;
        this.simg = simg;
    }

    public static final Creator<SpecialInfo> CREATOR = new Creator<SpecialInfo>() {
        @Override
        public SpecialInfo createFromParcel(Parcel source) {
            return new SpecialInfo(source);
        }

        @Override
        public SpecialInfo[] newArray(int size) {
            return new SpecialInfo[size];
        }
    };
}
