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
import com.example.model.util.okhttp_util;

public class change_pwd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//隱藏跳出的鍵盤

        //start
        final EditText mail_id =  (EditText)findViewById(R.id.text_mail);//定義電子郵件
        final EditText new_pwd =  (EditText)findViewById(R.id.text_new_pwd);//定義新密碼
        final EditText check_pwd =  (EditText)findViewById(R.id.text_check_pwd);//定義確認密碼
        final Button enter = (Button)findViewById(R.id.btn_enter);//定義確認

        final Intent jump =this.getIntent();//實體化 抓資料
        String get_mail_id = jump.getStringExtra("mail_id");//取得打包資料的mail
        //final String get_type= jump.getStringExtra("type");//取得打包資料的mail

        mail_id.setText(get_mail_id);//設定使用者原先輸入的mail
        mail_id.setEnabled(false); //設定不啟用

        //更改密碼
        enter.setOnClickListener(new View.OnClickListener() {//監聽器 使用者送出
            @Override
            public void onClick(View view) {
                //Ok
                final String input_mail_id = mail_id.getText().toString();//確認mail最終輸入的值
                final String input_new_pwd = new_pwd.getText().toString();//確認new_pwd最終輸入的值
                final String input_check_pwd = check_pwd.getText().toString();//確認check_pwd最終輸入的值
                connection_util temp_url = new connection_util();//實體化url物件
                final String url = temp_url.check_change_pwd(input_mail_id,input_new_pwd,input_check_pwd);//取得更改密碼url
                final okhttp_util client = new okhttp_util();//實體化 抓model
                //mail_id.setEnabled(false); //設定不啟用

                Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                    @Override
                    public void run() {
                        try {//動到io(網路有關)一定要用try與執行緒
                            final String responseData = client.urlpost(url);//連線後端 取得資料
                            final String[] ans = responseData.split(",");//分解字串

                            if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                                if(ans[0].equals("y")){ //發送成功
                                    change_pwd.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(change_pwd.this,"密碼更改成功，請重新登入", Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                    jump.setClass(change_pwd.this, Login.class);
                                    jump.putExtra("mail_id", input_mail_id);//帶回去login頁面 的帳號變數名稱與資料變數名稱
                                    startActivity(jump);
                                    change_pwd.this.finish();
                                }else{//發送失敗 顯示錯誤資訊
                                    change_pwd.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(change_pwd.this,responseData, Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                }
                                //
                            }else {//網路連線失敗
                                change_pwd.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(change_pwd.this, "伺服器連接失敗", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            change_pwd.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(change_pwd.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
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
        //end
    }
}
