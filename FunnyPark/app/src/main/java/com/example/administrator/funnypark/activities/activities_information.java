package com.example.administrator.funnypark.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;
import com.example.model.util.interface_util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class activities_information extends AppCompatActivity {
    static AppCompatActivity view_activities_information;

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model

    TextView activities_title,activities_host,activities_Co_organizer,
            activities_category,activities_date_start,activities_date_end,
            activities_park_name,activities_address,activities_hastag_1,activities_hastag_2,activities_hastag_3,
            activities_brief,activities_initiate,activities_initiate_time;
    ImageView activities_img,activities_user_img;
    Button btn_buy_ticket ;//購票
    String activity_id; //票券id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_information);

        view_activities_information=this;//儲存當前頁面 之後關閉用

        final Intent jump = this.getIntent();//實體化 抓資料
        activity_id = jump.getStringExtra("activity");//取得打包資料的資料

        Dim();//定義

        loading_activities_info();//讀取活動詳細葉面
    }

    private void Dim(){//定義宣告
        activities_img = (ImageView)findViewById(R.id.activities_img);//活動照片
        activities_user_img = (ImageView)findViewById(R.id.activities_user_img);//發起者頭貼
        activities_title = (TextView)findViewById(R.id.activities_title);//活動名稱
        activities_host = (TextView)findViewById(R.id.activities_host);//主辦單位
        activities_Co_organizer = (TextView)findViewById(R.id.activities_Co_organizer);//協辦單位
        activities_category = (TextView)findViewById(R.id.activities_category);//活動種類
        activities_date_start = (TextView)findViewById(R.id.activities_date_start);//活動開始日期
        activities_date_end = (TextView)findViewById(R.id.activities_date_end);//活動結束日期
        activities_park_name = (TextView)findViewById(R.id.activities_park_name);//活動地點
        activities_address = (TextView)findViewById(R.id.activities_address);//活動地址
        activities_hastag_1 = (TextView)findViewById(R.id.activities_hastag_1);//標籤1
        activities_hastag_2 = (TextView)findViewById(R.id.activities_hastag_2);//標籤2
        activities_hastag_3 = (TextView)findViewById(R.id.activities_hastag_3);//標籤3
        activities_brief = (TextView)findViewById(R.id.activities_brief);//活動簡述
        activities_initiate = (TextView)findViewById(R.id.activities_initiate);//活動發起人
        activities_initiate_time = (TextView)findViewById(R.id.activities_initiate_time);//活動發起時間
        btn_buy_ticket = (Button)findViewById(R.id.btn_initiate);//購票
    }

    private void loading_activities_info(){//讀取活動詳細資料
        final String url = temp_url.get_activity_info_detail(activity_id);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(activities_information.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉
                    activities_information.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                String[] ans = responseData.split(",");
                                if(ans[0].equals("y")){//讀取資料有無失敗
                                    interface_util.set_img(temp_url.get_url_img() +  ans[2],activities_img,activities_information.this);//活動圖片
                                    interface_util.set_img(temp_url.get_url_img() +  ans[16],activities_user_img,activities_information.this);//活動發起人頭貼
                                    activities_title.setText(ans[3]);//活動名稱
                                    activities_host.setText(ans[8]); //主辦單位
                                    activities_Co_organizer.setText(ans[9]);//協辦單位
                                    activities_category.setText(ans[6]);//活動種類
                                    activities_date_start.setText(ans[10] + " " + ans[12]);//活動開始時間
                                    activities_date_end.setText(ans[11] + " " + ans[13]);//活動結束時間
                                    activities_park_name.setText(ans[4]);//活動地點
                                    activities_address.setText(ans[5]);//活動地址
                                    if(ans.length >= 19 )activities_hastag_1.setText(ans[18]);//hastag 1
                                    if(ans.length >= 20 )activities_hastag_2.setText(ans[19]);//hastag 2
                                    if(ans.length >= 21 )activities_hastag_3.setText(ans[20]);//hastag 3
                                    activities_brief.setText(ans[17]);//活動簡述
                                    activities_initiate.setText(ans[15]);//活動發起人
                                    activities_initiate_time.setText(ans[14]);//活動發起時間
                                    if(ans[21].equals("T"))selling_statement();//偵測販賣狀態
                                }else{//讀取資料失敗 顯示對應資訊
                                    Toast.makeText(activities_information.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                }
                            }else {//網路連線失敗
                                Toast.makeText(activities_information.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                //顯示Toast
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

    public void ticket_purchase(View v){
        //Log.e("tag","taggg");
        Intent jump = new Intent();//跳轉到購買票券畫面
        jump.setClass(activities_information.this, activities_ticket_checkout.class);
        jump.putExtra("activity_id",activity_id);
        startActivity(jump);
    }

    //    返回上一頁
    public void finish_this_page(View v){
        this.finish();
    }

    private void selling_statement(){//偵測販賣狀態
        btn_buy_ticket.setText("開放購票");
        btn_buy_ticket.setTextColor(Color.rgb(255, 255, 255));
        btn_buy_ticket.setBackgroundColor(Color.rgb(0,69,100));
    }
}
