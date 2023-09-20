package com.example.administrator.funnypark.beacon_game.Scan_data;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.funnypark.R;

public class person_dead extends AppCompatActivity {

    String dead , game_room_id;
    TextView person_dead;

    //從 Intent 接收(SparseArray)玩家基本資料
    public void Intent_person_dead()
    {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        dead = extras.getString("person");
        game_room_id = extras.getString("game_room_id");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_game_person_dead);
        dim();

        Intent_person_dead();//傳入誰獲勝
        person_dead.setText(dead);

    }

    private void dim()
    {
        person_dead = (TextView) findViewById(R.id.person_dead); //倒數計時 textView
    }

    public void Settlement(View v)
    {

    }

}
