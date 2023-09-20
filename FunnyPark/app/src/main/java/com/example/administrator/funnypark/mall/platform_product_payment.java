package com.example.administrator.funnypark.mall;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.activities.activities_information;
import com.example.administrator.funnypark.activities.activities_ticket_checkout;
import com.example.administrator.funnypark.activities.activities_ticket_payment;
import com.example.administrator.funnypark.activities.activities_ticket_purchase_success;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.util.ArrayList;
import java.util.List;

public class platform_product_payment extends AppCompatActivity {

    String user_mail;//使用者登入帳號
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    final interface_util interface_util  = new interface_util();//介面設定
    final file_util file_util  = new file_util();//檔案參數

    List<String> payment_id = new ArrayList<String>();//票券id

    String choose_payment;//選擇的付款方式
    String choose_get_product;//選擇的取貨方式
    String product_json;
    Button btn_pick_up_on_site,btn_home_delivery;//現場取貨與宅配的按鈕
    LinearLayout on_site_field,delivery_field;
    CheckBox on_site_same_to_member_info,delivery_same_to_member_info;//與會員資料相同
    EditText picker_name,picker_phone,picker_remark,
            recipient_name,recipient_phone,recipient_address,recipient_remark;
    String user_name,user_phone,user_address,user_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_product_payment);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//隱藏跳出的鍵盤

        Dim();//定義

        initialization();//初始化

        Intent jump =this.getIntent();//實體化 抓資料
        product_json = jump.getStringExtra("product_json");//取得購買商品的json

        loading_payment_info();

        //選擇付款方式
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.payment_choose);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                int result1 = radioGroup.indexOfChild(radioButton);
                choose_payment = String.valueOf(result1);
                //Log.e("choose_payment",choose_payment);
            }
        });
                                                 /*
          EditText picker_name,picker_phone,picker_remark,
            recipient_name,recipient_phone,recipient_address,recipient_remark;
         */
        //現場取貨
        btn_pick_up_on_site.setOnClickListener(new View.OnClickListener() {//選擇現場取貨
            @Override
            public void onClick(View v) {
                on_site_field.setVisibility(View.VISIBLE);
                delivery_field.setVisibility(View.GONE);
                clear_get_product_input();
                choose_get_product="0";
            }
        });

        //宅配
        btn_home_delivery.setOnClickListener(new View.OnClickListener() {//選擇宅配
            @Override
            public void onClick(View v) {
                on_site_field.setVisibility(View.GONE);
                delivery_field.setVisibility(View.VISIBLE);
                clear_get_product_input();
                choose_get_product= "1";
            }
        });
    }

    private void Dim(){//定義
        on_site_field = (LinearLayout) findViewById(R.id.on_site_field);//現場取貨方式LinearLayout
        delivery_field = (LinearLayout) findViewById(R.id.delivery_field);//宅配取貨方式LinearLayout
        on_site_same_to_member_info = (CheckBox) findViewById(R.id.on_site_same_to_member_info);//與會員資料相同(現場取貨)
        delivery_same_to_member_info = (CheckBox) findViewById(R.id.delivery_same_to_member_info);//與會員資料相同(宅配)
        btn_pick_up_on_site = (Button)findViewById(R.id.btn_pick_up_on_site);//現場取貨
        btn_home_delivery = (Button)findViewById(R.id.btn_home_delivery);//宅配
        picker_name = (EditText)findViewById(R.id.picker_name);//
        picker_phone = (EditText)findViewById(R.id.picker_phone);//
        picker_remark = (EditText)findViewById(R.id.picker_remark);//
        recipient_name = (EditText)findViewById(R.id.recipient_name);//
        recipient_phone = (EditText)findViewById(R.id.recipient_phone);//
        recipient_address = (EditText)findViewById(R.id.recipient_address);//
        recipient_remark = (EditText)findViewById(R.id.recipient_remark);//
    }

    private void initialization(){//初始化
        user_mail = file_util.Read_loginInfo_account_Value(platform_product_payment.this);//取得登入會員信箱
        on_site_field.setVisibility(View.VISIBLE);//預設現場取貨
        choose_get_product="0";//預設現場取貨
        delivery_field.setVisibility(View.GONE);
        clear_get_product_input();
    }

    private void clear_get_product_input(){//清除取貨方式的欄位
        recipient_name.setText("");
        recipient_phone.setText("");
        recipient_address.setText("");
        recipient_remark.setText("");
        picker_name.setText("");
        picker_phone.setText("");
        picker_remark.setText("");
    }

    private void loading_payment_info()//載入user_mail付款相關資訊
    {
        final String url = temp_url.get_check_purchase_store_detail(user_mail);//取得url 活動詳細資訊
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    //Log.e("tag",url);
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("tag",responseData);
                    platform_product_payment.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        final String[] part = responseData.split("\\|");//分解字串
                                        final String[] pay_method = part[0].split("\\*");//分解字串
                                        final String[] member_info = part[1].split(",");//分解字串
                                        //Log.e("payment.length",String.valueOf(part.length));
                                        for(int i = 0 ; i<pay_method.length;i++){
                                            final String[] payment = pay_method[i].split(",");//分解字串
                                            payment_id.add(payment[1]);//付款id
                                            payment_style(payment[2]);//付款方式
                                        }

                                        //同會員資料(現場取貨)
                                        on_site_same_to_member_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                if(isChecked){
                                                    if(member_info.length == 4){
                                                        picker_name.setText(member_info[1]);
                                                        picker_phone.setText(member_info[2]);
                                                    }
                                                }else{
                                                    picker_name.setText("");
                                                    picker_phone.setText("");
                                                }

                                            }
                                        });

                                        //同會員資料(宅配)
                                        delivery_same_to_member_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                if(isChecked){
                                                    if(member_info.length== 4){
                                                        recipient_name.setText(member_info[1]);
                                                        recipient_phone.setText(member_info[2]);
                                                        recipient_address.setText(member_info[3]);
                                                    }
                                                }else{
                                                    recipient_name.setText("");
                                                    recipient_phone.setText("");
                                                    recipient_address.setText("");
                                                }
                                            }
                                        });

                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(platform_product_payment.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(platform_product_payment.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(platform_product_payment.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                platform_product_payment.this.finish();
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
        layoutParams.setMargins(0, 0, 0, interface_util.transform_dp(18,platform_product_payment.this));//設定margin
        radioButton.setLayoutParams(layoutParams);//套裝layout設定
        radioButton.setText(ticket);//票券名稱與價格
        radioButton.setBackground(getResources().getDrawable(R.drawable.ticket_type_choose));//設定樣式
        radioButton.setTextColor(Color.rgb(0, 39, 56));//文字顏色
        radioButton.setTextSize(14);//文字大小
        radioButton.setButtonDrawable(null);//隱藏radiobutton的圈圈
        radioButton.setPadding(interface_util.transform_dp(80,platform_product_payment.this), 0, 0, 0);//設置padding
        group.addView(radioButton);//將單選按鈕新增到RadioGroup中
    }

    public void pay_money(View v){//確認購買
        final Intent jump = this.getIntent();//實體化 抓資料
        if(choose_get_product.equals("0")){//現場取貨
            user_name = picker_name.getText().toString();
            user_address = "無";
            user_phone = picker_phone.getText().toString();
            user_remark = picker_name.getText().toString();
            if(user_name.equals("") || user_phone.equals("")){//檢查有無缺少資料 ex:姓名/電話
                Toast.makeText(platform_product_payment.this, "請填入姓名與電話。", Toast.LENGTH_LONG).show();//顯示Toast
                return;
            }
        }else if(choose_get_product.equals("1")) {//宅配
            user_name = recipient_name.getText().toString();
            user_address = recipient_address.getText().toString();
            user_phone = recipient_phone.getText().toString();
            user_remark = recipient_remark.getText().toString();
            if(user_name.equals("") || user_phone.equals("") || user_address.equals("")){//檢查有無缺少資料 ex:姓名/電話/地址
                Toast.makeText(platform_product_payment.this, "請填入姓名、電話與地址。", Toast.LENGTH_LONG).show();//顯示Toast
                return;
            }
        }

        final String url = temp_url.check_purchase_store(user_mail,product_json,payment_id.get(Integer.parseInt(choose_payment)),choose_get_product,user_name,user_phone,user_address,user_remark);//取得url 確認購買票券
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(platform_product_payment.this);//loading視窗打開
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒

                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    platform_product_payment.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].equals("y")){//如果購買成功responseData.substring(0,1)
                                        jump.setClass(platform_product_payment.this,platform_product_successful_payment.class);
                                        jump.putExtra("total_money",ans[1]);
                                        if(ans.length >=4){
                                            jump.putExtra("payment_type","ATM轉帳");
                                            jump.putExtra("bank_name",ans[2]);// else jump.putExtra("bank_name","");
                                            jump.putExtra("bank_branch",ans[3]); // else jump.putExtra("bank_branch","");
                                            jump.putExtra("bank_account",ans[4]); // else jump.putExtra("bank_account","");
                                        }else{
                                            jump.putExtra("payment_type","現金");
                                        }
                                        /*
                                        if(ans.length > 2)jump.putExtra("bank_name",ans[2]);// else jump.putExtra("bank_name","");
                                        if(ans.length > 3)jump.putExtra("bank_branch",ans[3]); // else jump.putExtra("bank_branch","");
                                        if(ans.length > 4)jump.putExtra("bank_account",ans[4]); // else jump.putExtra("bank_account","");
                                         */
                                        //jump.putExtra("user_mail",user_mail);//票券id
                                        startActivity(jump);
                                        platform_shopping_cart.view_shopping_cart.finish();//關閉購物車頁面
                                        platform_product_payment.this.finish();
                                        //jump.putExtra("exchange_code",ans[1]);//票券id
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(platform_product_payment.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(platform_product_payment.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(platform_product_payment.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                platform_product_payment.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(platform_product_payment.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
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
