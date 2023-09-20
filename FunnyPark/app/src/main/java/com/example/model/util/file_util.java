package com.example.model.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class file_util extends AppCompatActivity {
    //public SharedPreferences setting;
    //public static File file;
    private static final String APP_SETTINGS = "LoginInfo";//寫入檔案名稱

    public  file_util (){}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }
    //.edit().clear().commit();
    public  void Write_loginInfo(Context context,String mail_id,String password) {//寫入登入狀態
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("user",mail_id)
                .putString("pwd",password)
                .commit();
    }

    public String[] Read_loginInfo_Value(Context context) {//讀取登入狀態
        String[] temp = new String[2];
        temp[0] = getSharedPreferences(context).getString("user" , "");
        temp[1] = getSharedPreferences(context).getString("pwd" , "");
        return temp;
    }

    public static String Read_loginInfo_account_Value(Context context) {//讀取使用者身分
        return getSharedPreferences(context).getString("user" , "");
    }

    public void Delete_loginInfo(Context context) {//清除登入狀態
        getSharedPreferences(context).edit().clear().commit();
    }

    public boolean file_null(String[] temp){//是否是空
        if(temp[0].equals("") || temp[1].equals("")){
            return false;
        }else return true;
    }
}
