package com.example.administrator.funnypark.mall;

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

public class platform_product_exchange_info extends AppCompatActivity {
    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model

    ImageView product_img;
    TextView exchange_code;
    TextView product_name;
    TextView merchant_name;
    TextView park_name;
    TextView park_address;
    TextView exchange_limit_datetime;
    ImageView exchange_qrcode;
    TextView exchange_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_product_exchange_info);

        Dim();

        Intent jump = platform_product_exchange_info.this.getIntent();
        String exchange_id = jump.getStringExtra("exchange_id");//取得打包資料的兌換id

        create_exchange_info(exchange_id);//建立兌換相關資訊
    }

    private void Dim(){//定義變數
        product_img = (ImageView)findViewById(R.id.product_img);
        exchange_code = (TextView) findViewById(R.id.exchange_code);
        product_name = (TextView) findViewById(R.id.product_name);
        merchant_name = (TextView) findViewById(R.id.merchant_name);
        park_name = (TextView) findViewById(R.id.park_name);
        park_address = (TextView) findViewById(R.id.park_address);
        exchange_limit_datetime = (TextView) findViewById(R.id.exchange_limit_datetime);
        exchange_qrcode = (ImageView)findViewById(R.id.exchange_qrcode);
        exchange_status = (TextView) findViewById(R.id.exchange_status);
    }

    private void create_exchange_info(String exchange_id){//讀取兌換商品的詳細資訊
        final String url = temp_url.get_store_exchange_info_detail(exchange_id);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(platform_product_exchange_info.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    platform_product_exchange_info.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    final String[] ans = responseData.split(",");//分解商品資訊字串
                                    if(ans[0].equals("y")){
                                        Log.e("img",temp_url.get_url_img() + ans[1]);
                                        interface_util.set_img(temp_url.get_url_img() + ans[1],product_img,platform_product_exchange_info.this);//商品圖片
                                        product_name.setText(ans[2]);//商品名稱
                                        merchant_name.setText(ans[9]);//商家名稱
                                        park_name.setText(ans[3]);//園區名稱
                                        park_address.setText(ans[4]);//園區地址
                                        exchange_limit_datetime.setText(ans[5]);//兌換期限
                                        exchange_code.setText(ans[7]);//兌換編號
                                        interface_util.set_img(ans[6],exchange_qrcode,platform_product_exchange_info.this);//兌換qrcode
                                        if(ans[8].equals("0")){
                                            exchange_status.setText("可兌換");
                                        }else{
                                            exchange_status.setText("已兌換");
                                        }
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(platform_product_exchange_info.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(platform_product_exchange_info.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                platform_product_exchange_info.this.finish();
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

    //    返回上一頁
    public void finish_this_page(View v){
        this.finish();
    }
}
