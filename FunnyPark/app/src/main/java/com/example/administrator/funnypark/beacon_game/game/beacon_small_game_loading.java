package com.example.administrator.funnypark.beacon_game.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;

public class beacon_small_game_loading extends AppCompatActivity {

    String game_id;
    String username;
    String game_level_record_id;
    String game_station_id;
    String game_room_id;
    String game_class_id;
    ImageView background;

    interface_util interface_util = new interface_util();
    connection_util connection_util = new connection_util();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_small_game_loading);
        dim();

        Intent_Game_data();

        switch(game_id)
        {
            case "101":
                interface_util.set_img(connection_util.get_url_img()+"images/game01_loading.png",background,beacon_small_game_loading.this);
                Game01_timer.start();
                break;
            case "102":
                interface_util.set_img(connection_util.get_url_img()+"images/game03_loading.png",background,beacon_small_game_loading.this);
                Game03_timer.start();
                break;
            case "103":
                interface_util.set_img(connection_util.get_url_img()+"images/game04_loading.png",background,beacon_small_game_loading.this);
                Game04_timer.start();
                break;
            case "104":
                interface_util.set_img(connection_util.get_url_img()+"images/game05_loading.png",background,beacon_small_game_loading.this);
                Game05_timer.start();
                break;
        }
    }

    public void dim()
    {
        background = (ImageView) findViewById(R.id.imageView);//園區名稱
    }

    //從 Intent 接收(SparseArray)玩家基本資料
    public void Intent_Game_data()
    {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        game_id = extras.getString("game_id");
        username = extras.getString("username");
        game_level_record_id = extras.getString("game_level_record_id");
        game_station_id = extras.getString("game_station_id");
        game_room_id = extras.getString("game_room_id");
        game_class_id = extras.getString("game_class_id");
    }

    public void Game01()
    {
        Intent Game01_jump = new Intent(beacon_small_game_loading.this, Game01.class);
        Bundle bundle01 = new Bundle();
        //bundle01.putString("game_id","101");
        bundle01.putString("username",username);
        bundle01.putString("game_level_record_id",game_level_record_id);
        bundle01.putString("game_station_id",game_station_id);
        bundle01.putString("game_room_id",game_room_id);
        bundle01.putString("game_class_id",game_class_id);
        Game01_jump.putExtras(bundle01);
        beacon_small_game_loading.this.startActivity(Game01_jump);
        this.finish();
    }

    public void Game03()
    {
        Intent Game03_jump = new Intent(beacon_small_game_loading.this, Game03.class);
        Bundle bundle01 = new Bundle();
        //bundle01.putString("game_id","102");
        bundle01.putString("username",username);
        bundle01.putString("game_level_record_id",game_level_record_id);
        bundle01.putString("game_station_id",game_station_id);
        bundle01.putString("game_room_id",game_room_id);
        bundle01.putString("game_class_id",game_class_id);
        Game03_jump.putExtras(bundle01);
        beacon_small_game_loading.this.startActivity(Game03_jump);
        this.finish();
    }

    public void Game04()
    {
        Intent Game04_jump = new Intent(beacon_small_game_loading.this, Game04.class);
        Bundle bundle01 = new Bundle();
        //bundle01.putString("game_id","103");
        bundle01.putString("username",username);
        bundle01.putString("game_level_record_id",game_level_record_id);
        bundle01.putString("game_station_id",game_station_id);
        bundle01.putString("game_room_id",game_room_id);
        bundle01.putString("game_class_id",game_class_id);
        Game04_jump.putExtras(bundle01);
        beacon_small_game_loading.this.startActivity(Game04_jump);
        this.finish();
    }

    public void Game05()
    {
        Intent Game05_jump = new Intent(beacon_small_game_loading.this, Game05.class);
        Bundle bundle01 = new Bundle();
        //bundle01.putString("game_id","104");
        bundle01.putString("username",username);
        bundle01.putString("game_level_record_id",game_level_record_id);
        bundle01.putString("game_station_id",game_station_id);
        bundle01.putString("game_room_id",game_room_id);
        bundle01.putString("game_class_id",game_class_id);
        Game05_jump.putExtras(bundle01);
        beacon_small_game_loading.this.startActivity(Game05_jump);
        this.finish();
    }



    /*---------------------------------Game01計時器--------------------------------------*/

    public CountDownTimer Game01_timer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {
            Game01();
        }
    };
    /*-----------------------------------Game01計時器-------------------------------------*/

    /*---------------------------------Game03計時器--------------------------------------*/

    public CountDownTimer Game03_timer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {
            Game03();
        }
    };
    /*-----------------------------------Game03計時器-------------------------------------*/

    /*---------------------------------Game04計時器--------------------------------------*/

    public CountDownTimer Game04_timer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {
            Game04();
        }
    };
    /*-----------------------------------Game04計時器-------------------------------------*/

    /*---------------------------------Game05計時器--------------------------------------*/

    public CountDownTimer Game05_timer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {
            Game05();
        }
    };
    /*-----------------------------------Game05計時器-------------------------------------*/

}
