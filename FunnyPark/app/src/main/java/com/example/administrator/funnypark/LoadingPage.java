package com.example.administrator.funnypark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.funnypark.login.Login;
import com.example.administrator.funnypark.login.mail_verification;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.okhttp_util;


public class LoadingPage extends AppCompatActivity {

    private com.example.model.util.file_util file_util = new file_util() ;//維持登入檔案參數

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        //Loading...畫面
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1500);  //延遲幾秒
                    String[] login_statement_temp = file_util.Read_loginInfo_Value(LoadingPage.this);//讀取維持登入的file
                    if(file_util.file_null(login_statement_temp)){//檢查登入的檔案內有無紀錄
                        check_login_statement(login_statement_temp);//確認有無登入狀態
                    }else{//無紀錄 代表沒登入過 前往login畫面
                        startActivity(new Intent().setClass(LoadingPage.this,Login.class));
                    }
                    LoadingPage.this.finish();//直接結束Activity的方法
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void check_login_statement(final String[] login_temp_check) {//維持登入
        final connection_util temp_url = new connection_util();//實體化url物件
        final String url = temp_url.check_login(login_temp_check[0], login_temp_check[1]);//取得url
        final okhttp_util client = new okhttp_util();//實體化 抓model
        final Intent jump = new Intent();//實體化 抓資料
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    jump.setClass(LoadingPage.this, Login.class);//預設前往Login頁面
                    if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                        final String[] ans = responseData.split(",");//分解字串
                        if (ans[0].equals("y")) {//登入成功
                            if (ans[3].equals("1")) {//已經驗證過信箱
                                jump.setClass(LoadingPage.this, index.class);//前往首頁
                            } else {//還沒驗證過信箱
                                LoadingPage.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(LoadingPage.this, "登入成功，請完成信箱驗證~", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                });
                                jump.setClass(LoadingPage.this, mail_verification.class);//前往驗證頁面
                                jump.putExtra("verify_type", "1");
                            }
                            jump.putExtra("mail_id", login_temp_check[0]);//帶過去的變數名稱與資料變數名稱
                            jump.putExtra("password", login_temp_check[1]);//帶密碼過去下一個表單
                        }
                    }else{
                        LoadingPage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(LoadingPage.this, "網路連線失敗", Toast.LENGTH_LONG);
                                //顯示Toast
                                toast.show();
                            }
                        });
                    }
                    startActivity(jump);
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    LoadingPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(LoadingPage.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
                            //顯示Toast
                            toast.show();
                        }
                    });
                }
            }
        };
        okHttpExecuteThread.start();
    }
    //end
}
