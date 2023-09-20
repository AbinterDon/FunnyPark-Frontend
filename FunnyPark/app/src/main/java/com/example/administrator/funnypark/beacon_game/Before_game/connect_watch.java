package com.example.administrator.funnypark.beacon_game.Before_game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.ticket.ticket_information;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.okhttp_util;

public class connect_watch extends AppCompatActivity {

    //從資料庫取得 玩家姓名 票卷種類 Major Minor
    //按下進入等待室後
    //把 Major Minor 寫入玩家遊戲資訊的資料庫
    //並且驗證玩家票卷種類是否吻合，如果是則進入遊戲等待室
    //驗證完成後，程式內部要 get set Major Minor

    //連接資料庫
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model

    String user_mail_default;//使用者登入帳號
    String user_name_txt,major_txt,minor_txt,activity_id;
    Button watch_status;
    EditText user_mail;
    EditText major ;
    EditText minor;


    own_data own_Data = new own_data("","","","","","","") ;//玩家 Beacon Info，(建構子)全部都先清空
    private SparseArray<own_data> SparseArray_own_data = new SparseArray<>(); //(堆疊) 儲存 own_data 資料

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_game_connect_watch);

        user_mail_default = file_util.Read_loginInfo_account_Value(connect_watch.this);//取得登入會員信箱

        Dim();

        user_mail.setText(user_mail_default);

        get_connect_watch_key();
    }

    private void Dim(){
         user_mail = (EditText) findViewById(R.id.user_mail);
         major = (EditText)findViewById(R.id.major);
         minor = (EditText)findViewById(R.id.minor);


    }

    //從資料庫取得 user_name Major Minor，並且顯示出來
    private void get_user_info()
    {
        user_name_txt = user_mail.getText().toString();
        major_txt = major.getText().toString();
        minor_txt = minor.getText().toString();
    }


    String connect_watch_key;
    boolean member_key = false;
    public void get_connect_watch_key()
    {
        Bundle bundle = getIntent().getExtras();
        connect_watch_key = bundle.getString("connect_watch_ticket_key");
        activity_id = bundle.getString("activity_id");

        if(connect_watch_key.equals("member"))
        {
            member_key = true;
        }
        else
        {
            member_key = false;
        }

    }

    // TODO 如果從票卷進入的話 ， member_key 要為false

    public void watch_status(View v)
    {
        watch_status = findViewById(R.id.watch_status);
        beacon_watch_check();
        if(connect_watch_status == true)
        {
            unlink_beacon_watch();
        }
    }

    boolean connect_watch_status = false;
    //連接資料庫 檢查使用者的手環ID 是否輸入正確
    public void beacon_watch_check(){//載入會員的資料 套到介面上
        get_user_info();//取得使用者的 major minor username

        final String url = temp_url.beacon_watch_info(user_name_txt,minor_txt,activity_id);//取得url
       //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                   // Log.e("res",responseData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        final String[] ans = responseData.split(",");//分解字串
                                        //如果配對成功，為y
                                        if(ans[0].equals("y"))
                                        {
                                            if(member_key != true)
                                            {
                                                //如果配對正確，紀錄 user 的手環ID
                                                own_Data.set_user_name(user_name_txt);
                                                own_Data.set_major(major_txt);
                                                own_Data.set_minor(minor_txt);
                                                own_Data.set_activity_id(activity_id); //TODO 活動ID要改(改)
                                                own_Data.set_game_room_id(ans[1]);
                                                SparseArray_own_data.put(0,own_Data); //儲存玩家基本資料到 SparseArray_own_data 堆疊裡
                                                jump_game_room(); //遊戲等待室
                                            }
                                        }
                                        else
                                        {
                                            connect_watch_status = true;
                                            watch_status.setText("解除手環");
                                            Toast.makeText(connect_watch.this,responseData,Toast.LENGTH_SHORT).show();
                                        }
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(connect_watch.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(connect_watch.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(connect_watch.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                connect_watch.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    //解除 Beacon 連結
    public void unlink_beacon_watch()
    {
        get_user_info();//取得使用者的 major minor username
        final String url = temp_url.unlink_beacon_watch(user_name_txt,minor_txt,activity_id);//取得url
        //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒

                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("res",responseData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        final String[] ans = responseData.split(",");//分解字串
                                        //解除成功，為y
                                        if(ans[0].equals("y"))
                                        {
                                            Toast.makeText(connect_watch.this,ans[1],Toast.LENGTH_SHORT).show();
                                            watch_status.setText("配對手環");
                                        }
                                        else
                                        {
                                            connect_watch_status = false;
                                            Toast.makeText(connect_watch.this,responseData,Toast.LENGTH_SHORT).show();
                                        }
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(connect_watch.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(connect_watch.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(connect_watch.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                connect_watch.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    //跳轉到 game_room
    public void jump_game_room(){
        Intent jump = new Intent(connect_watch.this, game_room.class);
        //傳遞(SparseArray)玩家基本資料到 game_room
        Bundle bundle = new Bundle();
        bundle.putSparseParcelableArray("SparseArray_own_data",SparseArray_own_data);
        jump.putExtras(bundle);
        startActivity(jump);
    }

    public void ticket_information(View v)
    {
        finish();
    }

}
