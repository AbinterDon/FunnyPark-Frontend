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

public class forget_pwd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//隱藏跳出的鍵盤

        //start
        final EditText mail_id =  (EditText)findViewById(R.id.text_mail);//定義電子郵件
        final EditText verify_id =  (EditText)findViewById(R.id.text_verify);//定義驗證碼
        final TextView submit =  (TextView)findViewById(R.id.btn_submit);//定義送出
        final TextView enter = (TextView)findViewById(R.id.btn_enter);//定義確認


        final Intent jump =this.getIntent();//實體化 抓資料
        String get_mail_id = jump.getStringExtra("mail_id");//取得打包資料的mail

        mail_id.setText(get_mail_id);//設定使用者原先輸入的mail
        //mail_id.setEnabled(false);

        //發送信箱
        submit.setOnClickListener(new View.OnClickListener() {//監聽器 使用者送出
            @Override
            public void onClick(View view) {
                //Ok
                final String input_mail_id = mail_id.getText().toString();//確認mail最終輸入的值
                connection_util temp_url = new connection_util();//實體化url物件
                final String url = temp_url.check_forgot_pwd(input_mail_id);//取得忘記密碼url
                final loading_util temp_load = new loading_util();//實體化loading視窗
                temp_load.loading_open(forget_pwd.this);//loading視窗打開
                final okhttp_util client = new okhttp_util();//實體化 抓model
                submit.setEnabled(false);//暫時關閉按鈕
                mail_id.setEnabled(false); //設定不啟用

                Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                    @Override
                    public void run() {
                        try {//動到io(網路有關)一定要用try與執行緒
                            final String responseData = client.urlpost(url);//連線後端 取得資料
                            temp_load.loading_close();//loading視窗關閉
                            if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                                final String[] ans = responseData.split(",");//分解字串
                                if(ans[0].equals("y")){ //發送成功
                                    forget_pwd.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(forget_pwd.this,"驗證碼發送成功！", Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                }else{//發送失敗 顯示錯誤資訊
                                    forget_pwd.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            submit.setEnabled(true);//重新開啟按鈕
                                            mail_id.setEnabled(true); //重新設定啟用
                                            Toast toast = Toast.makeText(forget_pwd.this,responseData, Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                }
                                //
                            }else {//網路連線失敗
                                forget_pwd.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(forget_pwd.this, "伺服器連接失敗", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            forget_pwd.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(forget_pwd.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
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
        //
        //
        //驗證驗證碼
        enter.setOnClickListener(new View.OnClickListener() {//監聽器 使用者送出
            @Override
            public void onClick(View view) {
                //Ok
                final String input_mail_id = mail_id.getText().toString();//確認mail最終輸入的值
                final String input_verify_id = verify_id.getText().toString();//確認驗證碼最終輸入的值

                connection_util temp_url = new connection_util();//實體化url物件
                final String url = temp_url.check_email(input_mail_id,input_verify_id,"1");//取得密碼驗證url
                //input_verify_flag -> 0:註冊驗證 1:忘記密碼驗證
                final okhttp_util client = new okhttp_util();//實體化 抓model

                Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                    @Override
                    public void run() {
                        try {//動到io(網路有關)一定要用try與執行緒
                            final String responseData = client.urlpost(url);//連線後端 取得資料
                            if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                                final String[] ans = responseData.split(",");//分解字串
                                Intent jump = new Intent();//跳畫面
                                if(ans[0].equals("y")){ //發送成功
                                    forget_pwd.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(forget_pwd.this,"驗證成功！", Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                    jump.setClass(forget_pwd.this, change_pwd.class);
                                    jump.putExtra("mail_id", input_mail_id);//帶過去的變數名稱與資料變數名稱
                                    //jump.putExtra("type", "forget");//帶過去的變數名稱與資料變數名稱
                                    startActivity(jump);
                                }else{//發送失敗 顯示錯誤資訊
                                    forget_pwd.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(forget_pwd.this,responseData, Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                }
                                //
                            }else {//網路連線失敗
                                forget_pwd.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(forget_pwd.this, "伺服器連接失敗", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            forget_pwd.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(forget_pwd.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
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
}
