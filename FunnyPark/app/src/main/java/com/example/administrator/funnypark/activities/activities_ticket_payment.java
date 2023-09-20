package com.example.administrator.funnypark.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.util.ArrayList;
import java.util.List;

public class activities_ticket_payment extends AppCompatActivity {

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    final interface_util interface_util  = new interface_util();//介面設定
    final file_util file_util  = new file_util();//檔案參數

    List<String> payment_id = new ArrayList<String>();//票券id

    String choose_payment;//選擇的付款方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_ticket_payment);

        loading_payment_info();

        //選擇付款方式
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.payment_choose);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                int result1 = radioGroup.indexOfChild(radioButton);
                choose_payment = String.valueOf(result1);
            }
        });
    }

    private void loading_payment_info()//載入付款相關資訊
    {
        final String url = temp_url.get_activity_buy_ticket_detail();//取得url 活動詳細資訊
        //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    //Log.e("tag",url);
                    final String responseData = client.urlget(url);//連線後端 取得資料
                    Log.e("tag",responseData);
                    activities_ticket_payment.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        final String[] part = responseData.split("\\*");//分解字串
                                        Log.e("payment.length",String.valueOf(part.length));
                                        for(int i = 0 ; i<part.length;i++){
                                            final String[] payment = part[i].split(",");//分解字串
                                            payment_id.add(payment[1]);//付款id
                                            payment_style(payment[2]);//付款方式
                                        }
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(activities_ticket_payment.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(activities_ticket_payment.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(activities_ticket_payment.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                activities_ticket_payment.this.finish();
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

    private void payment_style(String ticket)//動態產生 付款的方式
    {
        RadioGroup group = findViewById(R.id.payment_choose);
        RadioButton radioButton = new RadioButton(this);//宣告
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);//設定長寬
        layoutParams.setMargins(0, 0, 0, interface_util.transform_dp(18,activities_ticket_payment.this));//設定margin
        radioButton.setLayoutParams(layoutParams);//套裝layout設定
        radioButton.setText(ticket);//票券名稱與價格
        radioButton.setBackground(getResources().getDrawable(R.drawable.ticket_type_choose));//設定樣式
        radioButton.setTextColor(Color.rgb(0, 39, 56));//文字顏色
        radioButton.setTextSize(14);//文字大小
        radioButton.setButtonDrawable(null);//隱藏radiobutton的圈圈
        radioButton.setPadding(interface_util.transform_dp(80,activities_ticket_payment.this), 0, 0, 0);//設置padding
        group.addView(radioButton);//將單選按鈕新增到RadioGroup中
    }


    public void pay_money(View v){//確認購買
        final Intent jump = this.getIntent();//實體化 抓資料
        final String url = temp_url.check_buy_ticket(
                jump.getStringExtra("username"),
                jump.getStringExtra("activity_id"),
                jump.getStringExtra("activity_time1"),
                jump.getStringExtra("activity_time2"),
                jump.getStringExtra("ticket_id"),
                payment_id.get(Integer.parseInt(choose_payment)));//取得url 確認購買票券
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(activities_ticket_payment.this);//loading視窗打開
        //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    //Log.e("tag",url);
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//loading視窗關閉
                    Log.e("tag",responseData);
                    activities_ticket_payment.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].equals("y")){//如果購買成功responseData.substring(0,1)
                                        jump.setClass(activities_ticket_payment.this,activities_ticket_purchase_success.class);
                                        activities_ticket_checkout.view_activities_ticket_checkout.finish();//關閉選票畫面
                                        activities_information.view_activities_information.finish();//關閉活動畫面
                                        activities_ticket_payment.this.finish();
                                        jump.putExtra("ticket_no",ans[1]);//票券id
                                        startActivity(jump);
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(activities_ticket_payment.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(activities_ticket_payment.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(activities_ticket_payment.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                activities_ticket_payment.this.finish();
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
    }//返回票券購買
}
