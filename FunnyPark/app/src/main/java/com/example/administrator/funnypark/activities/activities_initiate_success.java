package com.example.administrator.funnypark.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.funnypark.R;

public class activities_initiate_success extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_initiate_success);
    }
    public void finish_this_page(View v){
        this.finish();
    }
}
