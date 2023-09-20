package com.example.model.util;

import android.content.Context;
import android.util.Log;


public class attention_util {
    final static connection_util temp_url = new connection_util();//實體化url物件
    final static okhttp_util client = new okhttp_util();//實體化 抓model

    public static void add_attention(String attention_type,String context,String username){//attention_type 0=全部人皆可看 1=個別帳號觀看
        final String url = temp_url.check_ad_recive(attention_type,context,username);//取得url
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    //Toast.makeText(.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

}
