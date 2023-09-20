package com.example.model.util;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class   okhttp_util {
    static OkHttpClient client;
    public okhttp_util()
    {
        client= new OkHttpClient();

    }

    /*
     public boolean check_internet(Context context_view,String responses){
        if(responses.equals("error in getting response")||
                responses.equals("error in call get response")||
                responses.equals("error in postting response")||
                responses.equals("error in call post response")||
                responses.equals("error in postting response")||
                responses.equals("error in postting json response")||
                responses.equals("error in call post json response")||
                responses.equals("error in upload file response")||
                responses.equals("error in upload file response")){

        }
    }
     */


    /* 連線用代碼
    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    private void 方法名稱(){
        final String url = temp_url.get_store_info();//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(介面.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    介面.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗

                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                介面.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }
    */


    public static String urlget(String urls)
    {
        Log.d(" TAG ", urls);
        Request request = new Request.Builder()
                .url(urls)
                .build();
        //Log.e(" TAG ", "url ok");
        try {
            Response responses = client.newCall(request).execute();
            if (responses.isSuccessful())
                return responses.body().string();
            else
                return "Network connection failed";//return "error in getting response";
        } catch (IOException e) {
            Log.e(" TAG ", "error in getting response get request okhttp");
            return "Network connection failed";//return "error in call get response";
        }
    }

    public String urlpost(String urls)
    {
        Log.d(" TAG ", urls);
        Uri uri=Uri.parse(urls);
        String path=uri.getPath();
        Log.d(" TAG ", path);
        String q=uri.getQuery();
        Log.d(" TAG ", q);
        Set<String> args = uri.getQueryParameterNames();
        Object[] a=args.toArray();
        String url = "http://"+uri.getAuthority()+path;
        Log.d(" TAG ", url);

        okhttp3.FormBody.Builder formBody = new FormBody.Builder();
        for(int i=0;i<a.length;i++) {
            formBody.add(a[i].toString(),uri.getQueryParameter(a[i].toString()));
            Log.d(" TAG ", a[i].toString()+","+uri.getQueryParameter(a[i].toString()));
        }
        RequestBody formBodys = formBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBodys)
                .build();
        try {
            Response responses = client.newCall(request).execute();
            if (responses.isSuccessful())
                return responses.body().string();
            else
                return "Network connection failed";//return "error in postting response";
        } catch (IOException e) {
            Log.e(" TAG ", "error in getting response get request okhttp");
            return "Network connection failed";//return "error in call post response";
        }
    }

    public String urljson(String url,String jsonStr)
    {
        final MediaType mediaType  = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, jsonStr))
                .build();

        try {
            Response responses = client.newCall(request).execute();
            if (responses.isSuccessful())
                return responses.body().string();
            else
                return "Network connection failed";//return "error in postting json response";
        } catch (IOException e) {
            Log.e(" TAG ", "error in getting response get request okhttp");
            return "Network connection failed";//return "error in call post json response";
        }
    }

    public String urlupload(String url,String filename,String sid,String fpath)
    {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", sid)
                .addFormDataPart("filename", filename, RequestBody.create(MediaType.parse("application/xml"), new File(fpath)))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response responses = client.newCall(request).execute();
            if (responses.isSuccessful())
                return responses.body().string();
            else
                return "Network connection failed";//return "error in upload file response";
        } catch (IOException e) {
            Log.e(" TAG ", "error in uploadfile response get request okhttp");
            return "Network connection failed";//return "error in upload file response";
        }
    }
}
