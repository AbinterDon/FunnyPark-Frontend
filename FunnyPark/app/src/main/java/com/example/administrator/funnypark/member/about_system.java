package com.example.administrator.funnypark.member;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.funnypark.R;

public class about_system extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_system);
    }

    public void finish_this_page(View v)//TODO  關閉頁面
    {
        this.finish();
    }
}
