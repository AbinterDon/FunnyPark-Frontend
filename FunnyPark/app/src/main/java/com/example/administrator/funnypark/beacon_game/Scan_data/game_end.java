package com.example.administrator.funnypark.beacon_game.Scan_data;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.beacon_game.Before_game.connect_watch;
import com.example.administrator.funnypark.ticket.ticket_information;

public class game_end extends AppCompatActivity {

    String game_who_win,person,station,time;
    TextView game_end_title,survive_person,game_time,game_station;

    //從 Intent 接收(SparseArray)玩家基本資料
    public void Intent_game_end()
    {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        game_who_win = extras.getString("game_who_win");
        person = extras.getString("survive_person");
        station = extras.getString("game_station");
        time = extras.getString("game_time");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_game_end);
        dim();
        Intent_game_end();//傳入誰獲勝
        game_end_title.setText(game_who_win);
        survive_person.setText(person);
        game_station.setText(station);
        game_time.setText(time);

    }

    private void dim()
    {
        game_end_title = (TextView) findViewById(R.id.game_end_title);
        survive_person = (TextView) findViewById(R.id.survive_person);
        game_station = (TextView) findViewById(R.id.game_station);
        game_time = (TextView) findViewById(R.id.game_time);
    }

    public void back_index(View v)
    {
        Intent jump = new Intent(game_end.this, com.example.administrator.funnypark.index.class);
        startActivity(jump);
    }
}
