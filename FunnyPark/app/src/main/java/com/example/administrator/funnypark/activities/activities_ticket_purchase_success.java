package com.example.administrator.funnypark.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.ticket.ticket_information;

public class activities_ticket_purchase_success extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_ticket_purchase_success);
    }

    public void examine_ticket(View v){
        Intent jump = this.getIntent();//實體化 抓資料
        jump.setClass(this,ticket_information.class);
        jump.putExtra("ticket_no",jump.getStringExtra("ticket_no"));
        startActivity(jump);
    }

    public void finish_this_page(View v){
        this.finish();
    }
}
