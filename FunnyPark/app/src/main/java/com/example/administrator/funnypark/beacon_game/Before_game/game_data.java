package com.example.administrator.funnypark.beacon_game.Before_game;

import android.os.Parcel;
import android.os.Parcelable;

public class game_data implements Parcelable {

    private String game_time; //遊戲時間
    private int game_devil; //魔鬼數量
    private int game_person;//人類數量
    private int game_station;//總破譯站數量
    private int game_qua_station;//需破譯數量

    //建構子
    public game_data(String game_time,int game_devil,int game_person,int game_station,int game_qua_station) {
        this.game_time = game_time;
        this.game_devil = game_devil;
        this.game_person = game_person;
        this.game_station = game_station;
        this.game_qua_station = game_qua_station;
    }

    //遊戲時間
    public String get_game_time() {
        return game_time;
    }

    public void set_game_time(String game_time) {
        this.game_time = game_time;
    }

    //魔鬼數量
    public int get_game_devil() {
        return game_devil;
    }

    public void set_game_devil(int game_devil) {
        this.game_devil = game_devil;
    }

    //人類數量
    public int get_game_person() {
        return game_person;
    }

    public void set_game_person(int game_person) {
        this.game_person = game_person;
    }

    //總破譯站數量
    public int get_game_station() {
        return game_station;
    }

    public void set_game_station(int game_station) {
        this.game_station = game_station;
    }

    //需破譯數量
    public int get_game_qua_station() {
        return game_qua_station;
    }

    public void set_game_qua_station(int game_qua_station) {
        this.game_qua_station = game_qua_station;
    }



    protected game_data(Parcel in) {
        game_time = in.readString();
        game_devil = in.readInt();
        game_person = in.readInt();
        game_station = in.readInt();
        game_qua_station = in.readInt();
    }

    public static final Creator<game_data> CREATOR = new Creator<game_data>() {
        @Override
        public game_data createFromParcel(Parcel in) {
            return new game_data(in);
        }

        @Override
        public game_data[] newArray(int size) {
            return new game_data[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(game_time);
        dest.writeInt(game_devil);
        dest.writeInt(game_person);
        dest.writeInt(game_station);
        dest.writeInt(game_qua_station);
    }
}
