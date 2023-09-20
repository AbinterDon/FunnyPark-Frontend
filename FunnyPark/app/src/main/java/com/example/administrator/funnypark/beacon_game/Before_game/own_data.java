package com.example.administrator.funnypark.beacon_game.Before_game;

import android.os.Parcel;
import android.os.Parcelable;

public class own_data implements Parcelable {

    private String user_name;
    private String major;
    private String minor;
    private String sex;
    private String path;
    private String activity_id;
    private String game_room_id;

    //建構子
    public own_data( String user_name, String major, String minor,String sex,String path,String activity_id,String game_room_id) {
        this.user_name = user_name;
        this.major= major;
        this.minor = minor;
        this.sex =sex;
        this.path = path;
        this.activity_id = activity_id;
        this.game_room_id = game_room_id;
    }

    //玩家帳號(email)
    public String get_user_name() {
        return user_name;
    }

    public void set_user_name(String user_name) {
        this.user_name = user_name;
    }
    //玩家手環ID
    public String get_major() {
        return major;
    }

    public void set_major(String major) {
        this.major = major;
    }
    //玩家手環ID
    public String get_minor() {
        return minor;
    }

    public void set_minor(String minor) {
        this.minor = minor;
    }
    //玩家角色職業
    public String get_sex() {
        return sex;
    }

    public void set_sex(String sex) {
        this.sex = sex;
    }
    //玩家頭貼
    public String get_path() {
        return path;
    }

    public void set_path(String path) {
        this.path = path;
    }
    //活動ID
    public String get_activity_id() {
        return activity_id;
    }

    public void set_activity_id(String activity_id) {
        this.activity_id = activity_id;
    }
    //遊戲房間ID
    public String get_game_room_id() {
        return game_room_id;
    }

    public void set_game_room_id(String game_room_id) {
        this.game_room_id = game_room_id;
    }


    protected own_data(Parcel in) {
        user_name = in.readString();
        major = in.readString();
        minor = in.readString();
        sex = in.readString();
        path = in.readString();
        activity_id = in.readString();
        game_room_id = in.readString();
    }

    public static final Creator<own_data> CREATOR = new Creator<own_data>() {
        @Override
        public own_data createFromParcel(Parcel in) {
            return new own_data(in);
        }

        @Override
        public own_data[] newArray(int size) {
            return new own_data[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);
        dest.writeString(major);
        dest.writeString(minor);
        dest.writeString(sex);
        dest.writeString(path);
        dest.writeString(activity_id);
        dest.writeString(game_room_id);
    }
}
