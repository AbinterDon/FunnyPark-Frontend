package com.example.administrator.funnypark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.administrator.funnypark.activities.activities_page;
import com.example.administrator.funnypark.login.Login;
import com.example.model.util.file_util;

import java.io.File;

public class index extends AppCompatActivity  implements View.OnClickListener{
    private CardView btn_activities,btn_mall,btn_subject_park,btn_ticket,btn_member,btn_loginout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        btn_activities=(CardView)findViewById(R.id.btn_activities);//定義活動資訊...
        btn_mall=(CardView)findViewById(R.id.btn_mall);
        btn_subject_park=(CardView)findViewById(R.id.btn_subject_park);
        btn_ticket=(CardView)findViewById(R.id.btn_ticket);
        btn_member=(CardView)findViewById(R.id.btn_member);
        btn_loginout=(CardView)findViewById(R.id.btn_loginout);

        //設定監聽器 各個首頁按鈕
        btn_activities.setOnClickListener(this);
        btn_mall.setOnClickListener(this);
        btn_subject_park.setOnClickListener(this);
        btn_ticket.setOnClickListener(this);
        btn_member.setOnClickListener(this);
        btn_loginout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent jump;
        switch (v.getId())
        {
            case R.id.btn_activities://點選活動資訊
                jump = new Intent(this,activities_page.class);
                jump.putExtra("choose_page","1");
                startActivity(jump);
                break;
            case R.id.btn_mall://點選平台商城
                jump = new Intent(this,activities_page.class);
                jump.putExtra("choose_page","2");
                startActivity(jump);
                break;
            case R.id.btn_subject_park://點選主題園區
                jump = new Intent(this,activities_page.class);
                jump.putExtra("choose_page","3");
                startActivity(jump);
                break;
            case R.id.btn_ticket://點選票卷資訊
                jump = new Intent(this,activities_page.class);
                jump.putExtra("choose_page","4");
                startActivity(jump);
                break;
            case R.id.btn_member://點選會員中心
                jump = new Intent(this,activities_page.class);
                jump.putExtra("choose_page","5");
                startActivity(jump);
                break;
            case R.id.btn_loginout: //點選登出
                file_util temp = new file_util();
                temp.Delete_loginInfo(index.this);//登出
                jump = new Intent(index.this,Login.class);
                startActivity(jump);
                index.this.finish();
                break;
            default:break;
        }
    }
}
