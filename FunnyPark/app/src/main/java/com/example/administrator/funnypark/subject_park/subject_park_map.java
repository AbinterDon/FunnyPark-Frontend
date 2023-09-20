package com.example.administrator.funnypark.subject_park;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

public class subject_park_map extends AppCompatActivity {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    String park_id;//園區id
    TextView park_name,park_context,park_address;
    ImageView park_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_park_map);

        Dim();

        Intent jump = getIntent();
        park_id = jump.getStringExtra("park_id");

        create_map();
    }

    private void Dim(){//定義
        park_name = (TextView)findViewById(R.id.park_name);//園區名稱
        park_map = (ImageView)findViewById(R.id.park_map);//園區地圖
        park_address = (TextView)findViewById(R.id.park_address);//園區敘述
        park_context = (TextView)findViewById(R.id.park_context);//園區敘述
    }

    private void create_map(){
        final String url = temp_url.get_park_map(park_id);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(subject_park_map.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉
                    subject_park_map.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        interface_util.set_img(temp_url.get_url_img() + ans[0],park_map,subject_park_map.this);
                                        park_context.setText(ans[1]);
                                        park_address.setText(ans[2]);
                                        park_name.setText(ans[2]);
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(subject_park_map.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(subject_park_map.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();//顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(subject_park_map.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                subject_park_map.this.finish();
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

    public void finish_this_page(View v){
        this.finish();
    }
}
