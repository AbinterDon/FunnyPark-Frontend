package com.example.model.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.funnypark.R;

public class upload_photo extends AppCompatActivity {

    private LinearLayout LL;
    private ImageView imageView;
    private Button select_photo;
/*
 //初始化
        LL=(LinearLayout)this.findViewById(R.id.LL);
        imageView = (ImageView) findViewById(R.id.imageView);
        Button button = (Button)findViewById(R.id.camerau);
 */

    /*public upload_photo(LinearLayout temp_Linear,ImageView temp_imageview,Button temp_select_photo){
        LL=temp_Linear;
        imageView = temp_imageview;
        select_photo = temp_select_photo;

        temp_select_photo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //讀取圖片
                Intent intent = new Intent();
                //開啟Pictures畫面Type設定為image
                intent.setType("image/*");
                //使用Intent.ACTION_GET_CONTENT這個Action
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //取得照片後返回此畫面
                startActivityForResult(intent, 0);
            }

        });
    }*/

    public void Capture_photo(Context temp) {//擷取照片按鈕監聽器
        //讀取圖片
        Intent intent = new Intent();
        //開啟Pictures畫面Type設定為image
        intent.setType("image/*");
        //使用Intent.ACTION_GET_CONTENT這個Action
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //取得照片後返回此畫面
        startActivityForResult(intent, 0);
        //end
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:  //取得圖片後進行裁剪
                    Uri uri = data.getData();
                    Log.e("uri",String.valueOf(uri));
                    if(uri!=null){
                        doCropPhoto(uri);
                    }
                    break;
                case 1:  //裁剪完的圖片更新到ImageView
                    //先釋放ImageView上的圖片
                    if(imageView.getDrawable()!=null) {
                        imageView.setImageBitmap(null);
                        System.gc();
                    }
                    //更新ImageView
                    Bitmap bitmap = data.getParcelableExtra("data");
                    Log.e("bitmap",String.valueOf(bitmap));
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void doCropPhoto(Uri uri){
        //進行照片裁剪
        Intent intent = getCropImageIntent(uri);
        startActivityForResult(intent, 1);
    }

    //裁剪圖片的Intent設定
    public static Intent getCropImageIntent(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop", "true");// crop=true 有這句才能叫出裁剪頁面.
        intent.putExtra("scale", true); //讓裁剪框支援縮放
        intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 700);//回傳照片比例X
        intent.putExtra("outputY", 700);//回傳照片比例Y
        intent.putExtra("return-data", true);
        return intent;
    }

    @Override
    protected void onDestroy() {        //釋放內存
        try{
            super.onDestroy();
            //釋放整個介面與圖片
            imageView.setImageBitmap(null);
            imageView=null;
            LL.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("text", "New_DISS_Main.onDestroy()崩潰=" + e.toString());
        }
    }
    //end
}
