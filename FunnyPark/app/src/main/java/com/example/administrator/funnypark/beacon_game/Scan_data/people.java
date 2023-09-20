package com.example.administrator.funnypark.beacon_game.Scan_data;

public class people {

    //姓名
    private String name;
    //beacon_major
    private  int beacon_Major;
    //beacon_minor
    private int beacon_Minor;
    //beacon_icon
    private int beaconIcon;
    // 0為人類 1為鬼 3為鬼
    private int sex;
    //距離
    private double distance;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //beaconId 判斷玩家的手環 ID
    public int getbeacon_Major() {
        return beacon_Major;
    }

    public void setbeacon_Major(int beacon_Major) {
        this.beacon_Major = beacon_Major;
    }

    //beacon_Minor 判斷玩家的手環 ID
    public int getbeacon_Minor() {
        return beacon_Minor;
    }

    public void setbeacon_Minor(int beacon_Minor) {
        this.beacon_Minor = beacon_Minor;
    }

    //距離
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    //職業(鬼或人或驛站)
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

}
