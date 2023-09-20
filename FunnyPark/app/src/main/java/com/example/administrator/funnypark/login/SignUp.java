package com.example.administrator.funnypark.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//隱藏跳出的鍵盤

        //start
        final EditText user_name =  (EditText)findViewById(R.id.username);//定義名稱
        final EditText mail_id =  (EditText)findViewById(R.id.email);//定義電子郵件
        final EditText password =  (EditText)findViewById(R.id.password);//定義密碼
        final EditText re_password =  (EditText)findViewById(R.id.password_r);//定義確認密碼
        final Button submit =  (Button) findViewById(R.id.sign_up_button);//定義送出

        final Intent jump =this.getIntent();//實體化 抓資料
        String get_phone_id = jump.getStringExtra("mail_id");//取得打包資料的電話
        String get_password = jump.getStringExtra("password");//取得打包資料的密碼
        mail_id.setText(get_phone_id);//設定使用者原先輸入的手機號碼
        password.setText(get_password);//設定使用者原先輸入的密碼


        submit.setOnClickListener(new View.OnClickListener() {//監聽器 使用者送出
            @Override
            public void onClick(View view) {
                //Ok
                final String input_user_name = user_name.getText().toString();//確認暱稱最終輸入的值
                final String input_mail_id = mail_id.getText().toString();//確認手機號碼最終輸入的值
                final String input_password = password.getText().toString();//確認密碼最終輸入的值
                final String input_re_password = re_password.getText().toString();//確認再輸入一次密碼最終輸入的值
                final loading_util temp_load = new loading_util();//實體化loading視窗
                temp_load.loading_open(SignUp.this);//loading視窗打開
                connection_util temp_url = new connection_util();//實體化url物件
                final String url = temp_url.check_register(input_mail_id,input_password,input_re_password,input_user_name);//取得註冊url
                final okhttp_util client = new okhttp_util();//實體化 抓model
                submit.setEnabled(false);//暫時關閉按鈕

                Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                    @Override
                    public void run() {
                        try {//動到io(網路有關)一定要用try與執行緒
                            final String responseData = client.urlpost(url);//連線後端 取得資料
                            Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                            temp_load.loading_close();//loading視窗關閉

                            if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                                final String[] ans = responseData.split(",");//分解字串
                                if(ans[0].equals("y")){ //註冊成功ans[0].equals("-y") 前往驗證介面
                                    //Intent jump = new Intent();//跳畫面
                                    jump.setClass(SignUp.this, mail_verification.class);
                                    jump.putExtra("mail_id", input_mail_id);//帶過去的變數名稱與資料變數名稱
                                    jump.putExtra("verify_type", "0");//帶過去的變數名稱與資料變數名稱
                                    startActivity(jump);
                                }else{//註冊失敗 顯示錯誤資訊
                                    SignUp.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            submit.setEnabled(true);//重新打開按鈕
                                            Toast toast = Toast.makeText(SignUp.this,responseData, Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                }
                                //
                            }else {//網路連線失敗
                                SignUp.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(SignUp.this, "伺服器連接失敗", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                });
                            }
                            //
                        } catch (Exception e) {
                            SignUp.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(SignUp.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
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
        //註冊end
    }
}
