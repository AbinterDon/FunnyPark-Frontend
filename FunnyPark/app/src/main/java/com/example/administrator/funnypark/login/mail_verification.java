package com.example.administrator.funnypark.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;
import com.example.administrator.funnypark.index;

public class mail_verification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_verification);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//隱藏跳出的鍵盤

        //start
        final EditText mail_id =  (EditText)findViewById(R.id.editText5);//定義電子郵件
        final EditText verification =  (EditText)findViewById(R.id.editText9);//定義驗證碼
        final TextView submit_mail =  (TextView)findViewById(R.id.button);//定義發信
        final TextView submit =  (TextView)findViewById(R.id.button4);//定義確認驗證碼

        final Intent jump =this.getIntent();//實體化 抓資料
        final String get_verify_type = jump.getStringExtra("verify_type"); //取得驗證的種類 verify=0 註冊時直接驗證 =1登入時驗證
        final String get_mail_id = jump.getStringExtra("mail_id");//取得打包資料的電話
        mail_id.setText(get_mail_id);//設定使用者原先輸入的手機號碼
        mail_id.setEnabled(false);
        //
        submit_mail.setOnClickListener(new View.OnClickListener() {//監聽器 發送驗證信
            @Override
            public void onClick(View view) {
                //在發送一次驗證信 submit
                send_mail(mail_id,submit_mail);//在發送一次驗證信
            }
        });
        //
        //
        submit.setOnClickListener(new View.OnClickListener() {//送出驗證碼或在發送一次驗證碼
            @Override
            public void onClick(View v) {
                //
                final String input_mail_id = mail_id.getText().toString();//確認手機號碼最終輸入的值
                final String input_code = verification.getText().toString();//確認密碼最終輸入的值
                connection_util temp_url = new connection_util();//實體化url物件
                final String url = temp_url.check_email(input_mail_id,input_code,"0");//取得發送驗證碼url
                final okhttp_util client = new okhttp_util();//實體化 抓model
                submit.setEnabled(false);//暫時關閉按鈕

                Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                    @Override
                    public void run() {
                        try {//動到io(網路有關)一定要用try與執行緒
                            final String responseData = client.urlpost(url);//連線後端 取得資料
                            final String[] ans = responseData.split(",");//分解字串
                            //Log.e("login",responseData);

                            if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                                if(ans[0].equals("y")){ //發送成功
                                    mail_verification.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(mail_verification.this,"驗證成功！", Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                    //Log.e("tag abinter",get_verify_type);
                                    if(get_verify_type.equals("1")){//登入時才驗證信箱 驗證完後必須導回首頁
                                        jump.setClass(mail_verification.this, index.class);
                                    }else{//verify=null 註冊時驗證 必須導回login畫面
                                        jump.setClass(mail_verification.this, Login.class);
                                        jump.putExtra("mail_id", input_mail_id);//帶回去login頁面 的帳號變數名稱與資料變數名稱
                                    }
                                    mail_verification.this.finish();//關閉頁面
                                    startActivity(jump);
                                }else{//發送失敗 顯示錯誤資訊
                                    mail_verification.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            submit.setEnabled(true);//重新打開按鈕
                                            Toast toast = Toast.makeText(mail_verification.this,responseData, Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                }
                                //
                            }else {//網路連線失敗
                                mail_verification.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(mail_verification.this, "伺服器連接失敗", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                });
                            }
                            //
                        } catch (Exception e) {
                            mail_verification.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(mail_verification.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
                                    //顯示Toast
                                    toast.show();
                                }
                            });
                        }
                    }
                };             // Start the child thread.
                okHttpExecuteThread.start();
            }
        });
    }

    private void send_mail(EditText mail_id,final TextView submit_mail){//TODO 驗證信箱 發送驗證信
        final String input_mail_id = mail_id.getText().toString();//確認信箱最終輸入的值

        submit_mail.setEnabled(false);//暫時關閉按鈕
        connection_util temp_url = new connection_util();//實體化url物件
        final String url = temp_url.send_verification(input_mail_id);//取得發送驗證碼url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(mail_verification.this);//loading視窗打開
        final okhttp_util client = new okhttp_util();//實體化 抓model

        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    final String[] ans = responseData.split(",");//分解字串
                    temp_load.loading_close();//關閉Loading讀取視窗
                    //Log.e("login",responseData);

                    if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                        if(ans[0].equals("y")){ //發送成功
                            mail_verification.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(mail_verification.this,"驗證信發送成功", Toast.LENGTH_LONG);
                                    //顯示Toast
                                    toast.show();
                                }
                            });
                        }else{//發送失敗 顯示錯誤資訊
                            mail_verification.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    submit_mail.setEnabled(true);//重新打開按鈕
                                    Toast toast = Toast.makeText(mail_verification.this,responseData, Toast.LENGTH_LONG);
                                    //顯示Toast
                                    toast.show();
                                }
                            });
                        }
                        //
                    }else {//網路連線失敗
                        mail_verification.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(mail_verification.this, "伺服器連接失敗", Toast.LENGTH_LONG);
                                //顯示Toast
                                toast.show();
                            }
                        });
                    }

                } catch (Exception e) {
                    mail_verification.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(mail_verification.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
                            //顯示Toast
                            toast.show();
                        }
                    });
                }
            }
        };             // Start the child thread.
        okHttpExecuteThread.start();
    }
}
