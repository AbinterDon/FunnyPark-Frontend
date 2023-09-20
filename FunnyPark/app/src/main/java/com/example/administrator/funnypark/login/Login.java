package com.example.administrator.funnypark.login;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.index;
import com.example.administrator.funnypark.subject_park.subject_park_guide;
import com.example.model.util.file_util;
import com.example.model.util.loading_util;
import com.example.model.util.connection_util;
import com.example.model.util.okhttp_util;
import com.example.model.util.interface_util;

import java.io.File;


public class Login extends AppCompatActivity {

    private file_util file_util = new file_util() ;//維持登入檔案參數
    Button submit;
    EditText mail_id;
    EditText password;
    TextView forget;
    TextView Sign_Up;
    ImageView btn_fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//隱藏跳出的鍵盤

        Dim();//定義

        //Start
        interface_util temp_style = new interface_util();
        Sign_Up = temp_style.textview_link_style(Sign_Up,"註冊會員",0,4);//設定超連結樣式
        forget = temp_style.textview_link_style(forget,"忘記密碼?",0,5);//設定超連結樣式

        final Intent jump = this.getIntent();//實體化 抓資料
        String get_mail_id = jump.getStringExtra("mail_id");//取得打包資料的信箱
        mail_id.setText(get_mail_id);//設定使用者原先輸入的手機號碼


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//按下登入按紐時 觸發事件
               user_login(jump);///登入
            }
        });
        //ok on click end

        //reguser 文字監聽
        Sign_Up.setOnClickListener(new View.OnClickListener() { //註冊
            @Override
            public void onClick(View v) {
                final String input_phone_id = mail_id.getText().toString();//確認最終輸入的值
                final String input_password = password.getText().toString();//確認最終輸入的值
                Intent jump = new Intent();//跳畫面
                jump.setClass(Login.this, SignUp.class);
                jump.putExtra("mail_id", input_phone_id);//帶過去的變數名稱與資料變數名稱
                jump.putExtra("password", input_password);//帶密碼過去下一個表單
                startActivity(jump);
            }
        });

        //
        forget.setOnClickListener(new View.OnClickListener() { //忘記密碼
            @Override
            public void onClick(View v) {//忘記密碼
                final String input_mail_id = mail_id.getText().toString();//確認最終輸入的值
                Intent jump = new Intent();//跳畫面
                jump.setClass(Login.this, forget_pwd.class);
                jump.putExtra("mail_id", input_mail_id);//帶過去的變數名稱與資料變數名稱
                startActivity(jump);
            }
        });

        btn_fb.setOnClickListener(new View.OnClickListener() { //fb登入
            @Override
            public void onClick(View v) {//fb登入
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("尚未開放");
                builder.setMessage("施工中~敬請期待！");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //end
    }

    private void Dim(){//定義
        submit = (Button) findViewById(R.id.login);//送出
        mail_id = (EditText) findViewById(R.id.email);//電子郵件
        password = (EditText) findViewById(R.id.password);//密碼
        forget = (TextView) findViewById(R.id.forget_password);//忘記密碼
        Sign_Up = (TextView) findViewById(R.id.signup_text);//加入會員
        btn_fb = (ImageView)findViewById(R.id.btn_fb);//fb 登入
    }

    private void user_login(final Intent jump){
        //login 登入
        final String input_mail_id = mail_id.getText().toString();//確認最終輸入的值
        final String input_password = password.getText().toString();//確認最終輸入的值
        final connection_util temp_url = new connection_util();//實體化url物件
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(Login.this);//loading視窗打開
        final String url = temp_url.check_login(input_mail_id, input_password);//取得url
        final okhttp_util client = new okhttp_util();//實體化 抓model
        submit.setEnabled(false);//暫時關閉按鈕
        //執行緒
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("login",responseData);
                    temp_load.loading_close();//loading視窗關閉
                    if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                        final String[] ans = responseData.split(",");//分解字串
                        if (ans[0].equals("y")) { //ans[1].equalsIgnoreCase("y") ans.length>1 登入成功
                            file_util.Write_loginInfo(Login.this,input_mail_id,input_password); //寫入登入狀態
                            if (ans[3].equals("1")) {//已經驗證過信箱
                                jump.setClass(Login.this, index.class);
                            } else {//還沒驗證過信箱
                                Login.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(Login.this, "登入成功，請完成信箱驗證~", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                });
                                jump.setClass(Login.this, mail_verification.class);
                                jump.putExtra("verify_type", "1");
                            }
                            jump.putExtra("mail_id", input_mail_id);//帶過去的變數名稱與資料變數名稱
                            jump.putExtra("password", input_password);//帶密碼過去下一個表單
                            startActivity(jump);
                            Login.this.finish();//關閉頁面
                        }else {//登入失敗 顯示錯誤資訊
                            Login.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    submit.setEnabled(true);//重新打開按鈕
                                    Toast toast = Toast.makeText(Login.this, responseData, Toast.LENGTH_LONG);
                                    //顯示Toast
                                    toast.show();
                                }
                            });
                        }
                    }else{
                        Login.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(Login.this, "伺服器連接失敗", Toast.LENGTH_LONG);
                                //顯示Toast
                                toast.show();
                            }
                        });
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Login.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(Login.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
                            //顯示Toast
                            toast.show();
                        }
                    });
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }
    //end
}