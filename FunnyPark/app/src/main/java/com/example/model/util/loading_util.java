package com.example.model.util;

import android.app.ProgressDialog;
import android.content.Context;

public class loading_util {//loading視窗
    ProgressDialog progress;

    public void loading_open(Context activity){
        progress = ProgressDialog.show(activity,"讀取中", "請等待...",true);
    }

    public void loading_open(Context activity,String title,String msg){
        progress = ProgressDialog.show(activity,title, msg,true);
    }

    public void loading_close(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                progress.dismiss();
            }
        }).start();
    }
}
