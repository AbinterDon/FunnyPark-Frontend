package com.example.administrator.funnypark.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.index;
import com.example.administrator.funnypark.login.Login;
import com.example.administrator.funnypark.mall.platform_mall;
import com.example.administrator.funnypark.subject_park.subject_park;
import com.example.administrator.funnypark.ticket.ticket_list;
import com.example.administrator.funnypark.member.member_center;
import com.example.model.util.loading_util;

import java.io.File;

public class activities_page extends AppCompatActivity {
    public static AppCompatActivity view_activities_page;

    private BottomNavigationView mMainNav ;
    private FrameLayout mMainFrame;

    private activities_list fragment_activities;
    private platform_mall fragment_mall;
    private subject_park fragment_subject_park;
    private ticket_list fragment_ticket;
    private member_center fragment_member;

    //public static loading_util index_temp_load = new loading_util();//實體化loading視窗

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_page);

        view_activities_page=this;
        //fragment開始撰寫
        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        fragment_activities = new activities_list();//定義活動資訊
        fragment_mall = new platform_mall();//定義平台商城
        fragment_subject_park = new subject_park();//定義主題園區
        fragment_ticket = new ticket_list();//定義票卷資訊
        fragment_member = new member_center();//定義會員中心

        //讀取使用者從首頁點了哪個項目
        Intent jump = this.getIntent();//跳畫面
        String choose_page = jump.getStringExtra("choose_page");//取得打包資料的信箱
        //開始判別頁面
        switch (choose_page)
        {
            case "1"://活動資訊
                setFragment(fragment_activities);//設定啟始頁面
                mMainNav.getMenu().getItem(0).setChecked(true);//nav選取項目
                break;
            case "2"://平台商城
                setFragment(fragment_mall);//設定啟始頁面
                mMainNav.getMenu().getItem(1).setChecked(true);//nav選取項目
                break;
            case "3"://主題園區
                setFragment(fragment_subject_park);//設定啟始頁面
                mMainNav.getMenu().getItem(2).setChecked(true);//nav選取項目
                break;
            case "4"://票券資訊
                setFragment(fragment_ticket);//設定啟始頁面
                mMainNav.getMenu().getItem(3).setChecked(true);//nav選取項目
                break;
            case "5"://會員中心
                setFragment(fragment_member);//設定啟始頁面
                mMainNav.getMenu().getItem(4).setChecked(true);//nav選取項目
                break;
            default:break;
        }


        //底下選單設定路徑
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Log.e("tag",String.valueOf(menuItem.getItemId()));
                switch (menuItem.getItemId())
                {
                    case R.id.nav_activities: //當點選活動資訊
                        //index_temp_load.loading_open(activities_page.this);//loading視窗打開
                        setFragment (fragment_activities);
                        return true;
                    case R.id.nav_mall: //當點選合作商家
                        setFragment (fragment_mall);
                        return true;
                    case R.id.nav_subject_park: //當點選園區導覽
                        setFragment (fragment_subject_park);
                        return true;
                    case R.id.nav_ticket: //當點選票卷資訊
                        setFragment (fragment_ticket);
                        return true;
                    case R.id.nav_member: //當點選會員資訊
                        setFragment (fragment_member);
                        return true;
                    default:
                        return false;
                }
                //      切換分類
            }
        });
    }
    private void setFragment(Fragment fragment) {//what?
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
    //end
}
