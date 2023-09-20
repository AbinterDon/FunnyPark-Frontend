package com.example.administrator.funnypark.member;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import static com.example.administrator.funnypark.member.member_friends_center.view_member_friends_center;

public class member_friend_add extends AppCompatActivity {

    private interface_util interface_util = new interface_util();//call interface_util取介面方法
    final okhttp_util client = new okhttp_util();//實體化 抓model
    final connection_util temp_url = new connection_util();//實體化url物件

    String get_usermail;
    String get_friend_usermail;
    ImageView user_img ;
    TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_friend_add);

        Dim();//定義

        final Intent jump =this.getIntent();//實體化 抓資料
        get_usermail = jump.getStringExtra("usermail");//取得打包資料的當前使用者身分
        get_friend_usermail = jump.getStringExtra("friend_usermail");//取得打包資料的好友身分
        String get_friend_username = jump.getStringExtra("friend_username");//取得打包資料的好友暱稱
        String get_friend_img = jump.getStringExtra("friend_img");//取得打包資料的好友圖片

        interface_util.set_img(temp_url.get_url_img() + get_friend_img,user_img,member_friend_add.this);//好友大頭貼
        user_name.setText(get_friend_username);//暱稱

    }

    private void Dim(){
        user_img = (ImageView)findViewById(R.id.user_img);//頭貼
        user_name = (TextView)findViewById(R.id.user_name);//暱稱
    }

    public void add_friend(View v)//TODO  新增好友
    {
        final String url = temp_url.check_friend(get_usermail,get_friend_usermail,"0");//好友資訊url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(member_friend_add.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//loading視窗關閉
                    member_friend_add.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    final String[] ans = responseData.split(",");//分解字串\*
                                    Log.e("res",responseData);
                                    if(ans[0].equals("y")){//讀取資料有無失敗
                                        Toast.makeText(member_friend_add.this, "新增好友成功", Toast.LENGTH_SHORT).show();
                                        member_friend_add.this.finish();
                                        view_member_friends_center.finish();
                                        Intent jump = new Intent(member_friend_add.this,member_friends_center.class);
                                        startActivity(jump);
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(member_friend_add.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(member_friend_add.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(member_friend_add.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                member_friend_add.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    public void finish_this_page(View v)//TODO  關閉頁面
    {
        this.finish();
    }
}
