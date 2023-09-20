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

public class member_friend_info extends AppCompatActivity {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    String get_usermail;
    String get_friend_usermail;
    ImageView user_img ;
    TextView user_name;
    TextView user_real_name;
    TextView user_birthday;
    TextView user_parkcoin;
    TextView user_attend;
    TextView user_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_friend_info);

        Dim();//定義　

        final Intent jump =this.getIntent();//實體化 抓資料
        get_usermail = jump.getStringExtra("usermail");//取得打包資料的當前使用者身分
        get_friend_usermail = jump.getStringExtra("friend_mail");//取得打包資料的好友身分

        friend_info();//顯示好有詳細資訊
    }

    private void Dim(){
        user_mail = (TextView)findViewById(R.id.user_mail);//帳號
        user_img = (ImageView)findViewById(R.id.user_img);//頭貼
        user_name = (TextView)findViewById(R.id.user_name);//暱稱
        user_real_name = (TextView)findViewById(R.id.user_real_name);//真實名稱
        user_birthday = (TextView)findViewById(R.id.user_birthday);//生日
        user_parkcoin = (TextView)findViewById(R.id.user_parkcoin);//ParkCoin
        user_attend = (TextView)findViewById(R.id.user_attend);//遊玩場次
    }

    private void friend_info()//TODO  顯示好友詳細資訊
    {
        final String url = temp_url.get_member_friend_detail(get_friend_usermail);//好友詳細資訊url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(member_friend_info.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//loading視窗關閉

                    member_friend_info.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");//分解字串\*
                                    if(ans[0].equals("y")){//讀取資料有無失敗
                                        interface_util.set_img(temp_url.get_url_img() + ans[5],user_img,member_friend_info.this);
                                        user_mail.setText(ans[1]);//
                                        user_name.setText( ans[2]);//"暱稱 / " +
                                        user_real_name.setText(ans[3]);//"真實姓名 / " +
                                        user_birthday.setText(ans[4]);//"出生年月日 / " +
                                        user_parkcoin.setText(ans[6]);//"ParkCoin點數 / " +
                                        user_attend.setText( ans[7]);//"活動遊玩場次 / " +
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(member_friend_info.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(member_friend_info.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(member_friend_info.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                member_friend_info.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(member_friend_info.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    member_friend_info.this.finish();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    public void del_friend(View v)//TODO  刪除好友
    {
        final String url = temp_url.check_friend(get_usermail,get_friend_usermail,"9");//好友資訊url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(member_friend_info.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//loading視窗關閉
                    member_friend_info.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");//分解字串\*
                                    if(ans[0].equals("y")){//讀取資料有無失敗
                                        Toast.makeText(member_friend_info.this, "刪除好友成功", Toast.LENGTH_SHORT).show();
                                        member_friend_info.this.finish();
                                        view_member_friends_center.finish();
                                        Intent jump = new Intent(member_friend_info.this,member_friends_center.class);
                                        startActivity(jump);
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(member_friend_info.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(member_friend_info.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(member_friend_info.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                member_friend_info.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(member_friend_info.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    member_friend_info.this.finish();
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    public void finish_this_page(View v){
        this.finish();
    }
}
