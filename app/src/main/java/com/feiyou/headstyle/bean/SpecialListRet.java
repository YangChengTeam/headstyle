package com.feiyou.headstyle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by admin on 2016/9/5.
 */
public class SpecialListRet implements Parcelable {

    public String sid;
    public String total;
    public String paixu;
    public String ding;
    public String title;
    public String description;
    public String simg;

    public List<HeadInfo> list;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sid);
        dest.writeString(this.total);
        dest.writeString(this.paixu);
        dest.writeString(this.ding);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.simg);
        dest.writeTypedList(this.list);
    }

    public SpecialListRet() {
    }

    protected SpecialListRet(Parcel in) {
        this.sid = in.readString();
        this.total = in.readString();
        this.paixu = in.readString();
        this.ding = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.simg = in.readString();
        this.list = in.createTypedArrayList(HeadInfo.CREATOR);
    }

    public static final Creator<SpecialListRet> CREATOR = new Creator<SpecialListRet>() {
        @Override
        public SpecialListRet createFromParcel(Parcel source) {
            return new SpecialListRet(source);
        }

        @Override
        public SpecialListRet[] newArray(int size) {
            return new SpecialListRet[size];
        }
    };
}
