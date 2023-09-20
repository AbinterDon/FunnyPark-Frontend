package com.example.model.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class interface_util extends AppCompatActivity {
    public TextView textview_link_style(TextView temp_textview,String value,int value_start,int value_end){//超連結樣式 建立
        //建立一個 SpannableString物件
        SpannableString sp = new SpannableString(value);
        //設定超連結
        sp.setSpan(new URLSpan(""), value_start, value_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //設定高亮樣式一
        //SpannableString物件設定給TextView
        temp_textview.setText(sp);
        temp_textview.setMovementMethod(LinkMovementMethod.getInstance());
        return temp_textview;
    }

    public ImageView set_img(String url, ImageView img,Context context){//設定圖片
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(url, img);
        return img;
    }

    public int transform_dp (int value,Context context){//轉換成dp
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    public int transform_pix (int value,Context context){//轉換成pixel
       final float scale = context.getResources().getDisplayMetrics().density;
       return (int) (value / scale + 0.5f);
    }


    /*
    public int Msgbox_Check (String Title,String Message,Context context){//詢問彈跳視窗

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setTitle(Title);
        dlgAlert.setMessage(Message);
        dlgAlert.setPositiveButton("確定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog


                    }
                });
    }
     */



}

