package com.example.administrator.funnypark.mall;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.login.SignUp;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

public class platform_product_information extends AppCompatActivity {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    String get_product_id;//目前顯示商品的id
    ImageView product_img;
    ImageView user_img;
    TextView product_name;
    TextView product_park;
    TextView product_merchant;
    TextView product_brief;
    Button product_addtoCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_product_information);

        Dim();

        Intent jump = getIntent();
        get_product_id = jump.getStringExtra("product_id");//取得打包資料的商品id

        display_product_detail();
    }

    private void Dim(){
        product_img = (ImageView) findViewById(R.id.product_img);//商品圖案
        user_img = (ImageView) findViewById(R.id.user_img);//使用者(商家)頭貼
        product_name = (TextView) findViewById(R.id.product_name);//商品名稱
        product_park = (TextView) findViewById(R.id.product_park);//商品園區
        product_merchant = (TextView) findViewById(R.id.product_merchant);//商品商家
        product_brief = (TextView) findViewById(R.id.product_brief);//商品敘述
        product_addtoCart = (Button) findViewById(R.id.addtoCart);//加入購物車
    }

    private void display_product_detail(){//顯示欲查看的商品詳細資訊
        final String url = temp_url.get_store_info_detail(get_product_id);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(platform_product_information.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉
                    platform_product_information.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        final String[] ans = responseData.split(",");//分解商品資訊字串
                                        interface_util.set_img(temp_url.get_url_img() + ans[1],product_img,platform_product_information.this);//商品圖片
                                        product_park.setText(ans[2]);//商品園區
                                        product_merchant.setText(ans[3]);//商家名稱
                                        product_name.setText(ans[4]);//商品名稱
                                        product_brief.setText(ans[8]);//商品簡介
                                        if(ans[7].equals("0")){//若是商品庫存=0
                                            product_addtoCart.setText("已售完");
                                        }else{
                                            product_addtoCart.setText("加入購物車("+ans[5]+ "元/" + ans[6] + "PC)");
                                            product_addtoCart.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    shopping_cart_add(ans[0]);
                                                }
                                            });
                                        }
                                        interface_util.set_img(temp_url.get_url_img()+ans[9],user_img,platform_product_information.this);//商家頭貼
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(platform_product_information.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(platform_product_information.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(platform_product_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                platform_product_information.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(platform_product_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }


    private void shopping_cart_add(final String product_id){//加入購物車 帶入商品id與[加入購物車]按鈕物件
        String user_mail = file_util.Read_loginInfo_account_Value(platform_product_information.this);//取得登入會員信箱
        final String url = temp_url.store_car_add(product_id,user_mail);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(platform_product_information.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉
                    if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                        platform_product_information.this.runOnUiThread(new Runnable() {//
                            @Override
                            public void run() {
                                String ans[] = responseData.split(",");
                                if(ans[0].equals("y")){
                                    Toast.makeText(platform_product_information.this, "加入成功", Toast.LENGTH_LONG).show();
                                    product_addtoCart.setText("已加入購物車");
                                    product_addtoCart.setBackgroundColor(Color.rgb(0,69,100));
                                    product_addtoCart.setEnabled(false);
                                    //004564
                                }else{
                                    Toast.makeText(platform_product_information.this, ans[1], Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else {//網路連線失敗
                        platform_product_information.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(platform_product_information.this, "網路連線失敗", Toast.LENGTH_LONG);
                                //顯示Toast
                                toast.show();
                            }
                        });
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(platform_product_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
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
