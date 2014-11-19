package me.yugy.v2ex.model;

import android.os.Parcel;
import android.os.Parcelable;

import me.yugy.v2ex.fragment.UserInfoFirstFragment;

/**
* Created by yugy on 14/11/18.
*/
public class HeadIconInfo implements Parcelable {
    public int left;
    public int top;
    public int width;
    public int height;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.left);
        dest.writeInt(this.top);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public HeadIconInfo() {
    }

    public HeadIconInfo(Parcel in) {
        this.left = in.readInt();
        this.top = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<HeadIconInfo> CREATOR = new Creator<HeadIconInfo>() {
        public HeadIconInfo createFromParcel(Parcel source) {
            return new HeadIconInfo(source);
        }

        public HeadIconInfo[] newArray(int size) {
            return new HeadIconInfo[size];
        }
    };
}
