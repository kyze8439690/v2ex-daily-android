package com.yugy.v2ex.daily.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-2-23.
 */
public class MemberModel implements Parcelable{
    public int id;
    public String username;
    public String tagline;
    public String avatar;      //73*73

    public MemberModel(){}

    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        username = jsonObject.getString("username");
        tagline = jsonObject.getString("tagline");
        avatar = jsonObject.getString("avatar_large");
        if(avatar.startsWith("//")){
            avatar = "http:" + avatar;
        }
    }

    private MemberModel(Parcel in){
        id = in.readInt();
        String[] strings = new String[3];
        in.readStringArray(strings);
        username = strings[0];
        tagline = strings[1];
        avatar = strings[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeStringArray(new String[]{
                username,
                tagline,
                avatar
        });
    }

    public static final Creator<MemberModel> CREATOR = new Creator<MemberModel>() {
        @Override
        public MemberModel createFromParcel(Parcel source) {
            return new MemberModel(source);
        }

        @Override
        public MemberModel[] newArray(int size) {
            return new MemberModel[size];
        }
    };
}
