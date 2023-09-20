package com.example.administrator.funnypark.mall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.ticket.ticket_transfer;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class platform_product_exchange extends AppCompatActivity {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    String user_mail;//使用者登入帳號
    Button btn_confirm;
    EditText code_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_product_exchange);

        Dim();//定義變數

        user_mail = file_util.Read_loginInfo_account_Value(platform_product_exchange.this);//取得登入會員信箱

        btn_confirm.setOnClickListener(new View.OnClickListener() {//兌換
            @Override
            public void onClick(View v) {
                exchange(code_text.getText().toString());
            }
        });

    }

    private void Dim(){//定義變數
        code_text = (EditText)findViewById(R.id.id_code);//輸入框
        btn_confirm = (Button)findViewById(R.id.btn_confirm);//確認按鈕
    }

    private void exchange(String exchange_code){//商家兌換商品
        final String url = temp_url.check_exchange_store(user_mail,exchange_code);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(platform_product_exchange.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    platform_product_exchange.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        if(ans[0].equals("y")){
                                            Toast.makeText(platform_product_exchange.this, "兌換成功", Toast.LENGTH_LONG).show();//顯示Toast
                                        }else{
                                            Toast.makeText(platform_product_exchange.this, "兌換失敗", Toast.LENGTH_LONG).show();//顯示Toast
                                        }
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(platform_product_exchange.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(platform_product_exchange.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(platform_product_exchange.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                platform_product_exchange.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(platform_product_exchange.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }


    public void scan_qrcode(View v)//TODO  掃描qrcode
    {
        IntentIntegrator integrator = new IntentIntegrator(platform_product_exchange.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("對準QrCode進行掃描");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);//改為直是
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)//TODO Qrcode 掃描結果
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result!= null)
        {
            if (result.getContents()==null)//若是關閉掃描
            {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else//顯示掃瞄到的內容
            {
                //Toast.makeText(this,result.getContents(),Toast.LENGTH_LONG).show();
                //check_friend(result.getContents());//傳遞掃瞄到的好友帳號
                //code_text.setText(result.getContents());
                exchange(result.getContents());
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void finish_this_page(View v)//TODO  關閉頁面
    {
        this.finish();
    }
}
