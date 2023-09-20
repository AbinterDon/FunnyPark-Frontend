package com.example.administrator.funnypark.member;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.activities.activities_initiate;
import com.example.administrator.funnypark.activities.activities_page;
import com.example.administrator.funnypark.login.Login;
import com.example.administrator.funnypark.login.change_pwd;
import com.example.model.util.DatePickerUtil;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class member_information extends AppCompatActivity {

    private interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    final file_util file_util  = new file_util();//檔案參數

    Bitmap bitmap ;//上傳圖片的bitmap

    String user_mail;
    ImageView user_image;
    EditText user_original_name,user_name,user_email,user_pwd,user_birthday,user_address,user_phone;

    private void dim()//定義變數
    {
        user_image = (ImageView)this.findViewById(R.id.user_image);//使用者圖片
        user_original_name = (EditText)this.findViewById(R.id.user_original_name);//使用者本名
        user_name = (EditText)this.findViewById(R.id.user_name);//使用者名稱
        user_address = (EditText)this.findViewById(R.id.user_address);//使用者mail
        user_phone = (EditText)this.findViewById(R.id.user_phone);//使用者mail
        user_email = (EditText)this.findViewById(R.id.user_email);//使用者mail
        user_pwd = (EditText)this.findViewById(R.id.user_pwd);//使用者密碼
        user_birthday = (EditText)this.findViewById(R.id.user_birthday);//使用者生日
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_information);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//隱藏跳出的鍵盤

        dim();//定義變數

        user_mail = file_util.Read_loginInfo_account_Value(member_information.this);//取得登入會員信箱

        loading_member_info();//讀取會員資料

        //擷取照片按鈕監聽器
        user_image.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //讀取圖片
                new android.support.v7.app.AlertDialog.Builder(member_information.this)
                        .setTitle("選擇頭貼照片")
                        .setMessage("請選擇照片來源")
                        .setNegativeButton("從相簿", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                choicePicFromAlbum();
                            }
                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }

        });

        //選擇生日
        user_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  //DatePickerUtil.pickDate(member_information.this, user_birthday, DatePickerUtil.THEME_DEVICE_DEFAULT_DARK);
                  //DatePickerUtil.pickDate(member_information.this, user_birthday, DatePickerUtil.THEME_DEVICE_DEFAULT_LIGHT);
                  DatePickerUtil.pickDate(member_information.this, user_birthday, DatePickerUtil.THEME_HOLO_DARK);
                  //DatePickerUtil.pickDate(member_information.this, user_birthday, DatePickerUtil.THEME_HOLO_LIGHT);
                  //DatePickerUtil.pickDate(member_information.this, user_birthday, DatePickerUtil.THEME_TRADITIONAL);

                // 不设置日期选择框的主题，用手机系统默认的，看看效果
                //DatePickerUtil.pickDate(member_information.this, user_birthday);

            }
        });

        //end
    }

    public void loading_member_info()//讀取會員資料
    {
        final String url = temp_url.get_member_admin(user_mail);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(member_information.this);//loading視窗打開
        //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//關閉Loading視窗
                    Log.e("res",responseData);
                    member_information.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        interface_util.set_img(temp_url.get_url_img() +  ans[5], user_image,member_information.this);//大頭貼
                                        user_original_name.setText(ans[2]);
                                        user_name.setText(ans[1]);//使用者名稱
                                        user_email.setText(ans[3]);//使用者帳號
                                        //user_pwd.setText(ans[3]);//parkcoin
                                        user_birthday.setText(ans[4]);//生日
                                        if(ans.length>=7)user_phone.setText(ans[6]);//手機
                                        if(ans.length>=8)user_address.setText(ans[7]);//地址
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(member_information.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(member_information.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(member_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                member_information.this.finish();
                            }
                        }
                    });

                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    public void update_member_info(View v){//會員修改資料
        String check_image_statement = "";//確認是否有更改圖片
        if(bitmap!=null){//有無更改圖片?
            check_image_statement = temp_url.member_modify(
                    user_mail,
                    user_name.getText().toString(),
                    user_original_name.getText().toString(),
                    user_birthday.getText().toString(),
                    bitmapToStringByBase64(bitmap),
                    user_phone.getText().toString(),
                    user_address.getText().toString());
        }else{//無更改圖片
            check_image_statement = temp_url.member_modify(
                    user_mail,
                    user_name.getText().toString(),
                    user_original_name.getText().toString(),
                    user_birthday.getText().toString(),
                    user_phone.getText().toString(),
                    user_address.getText().toString());
        }
        final String url = check_image_statement;//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(member_information.this);//loading視窗打開
        Log.e("url",check_image_statement);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//關閉Loading視窗
                    //Log.e("res",responseData);

                    member_information.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        Toast toast = Toast.makeText(member_information.this,"會員資料修改完成", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                        member_information.this.finish();//關閉此修改頁面
                                        activities_page.view_activities_page.finish();//關閉原本的畫面
                                        Intent jump = new Intent(member_information.this,activities_page.class);//開啟新的畫面
                                        jump.putExtra("choose_page","5");//導向到會員中心
                                        startActivity(jump);
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(member_information.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(member_information.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(member_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                member_information.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(member_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    member_information.this.finish();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    public void finish_this_page(View v){
        this.finish();
    }

    //---------------------------------------------------------------------------------------------------
    //修改密碼
    public void check_old_password(View v)//驗證使用者輸入的舊密碼
    {
        final String url = temp_url.check_member_pwd(user_mail,user_pwd.getText().toString());//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(member_information.this);//loading視窗打開
        //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//loading視窗關閉
                    Log.e("res",responseData);

                    member_information.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        Intent jump = new Intent();//實體化 抓資料
                                        jump.setClass(member_information.this,change_pwd.class);
                                        jump.putExtra("mail_id", user_mail);//帶去忘記密碼頁面 的帳號變數名稱與資料變數名稱
                                        //jump.putExtra("type", "member_center");//帶去忘記密碼頁面 的帳號變數名稱與資料變數名稱
                                        startActivity(jump);
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(member_information.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(member_information.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(member_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                member_information.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }
    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //讀取相簿
    public static int TAKE_PHOTO_REQUEST_CODE = 1; //拍照
    public static int PHOTO_REQUEST_CUT = 2; //裁切
    public static int PHOTO_REQUEST_GALLERY = 3; //相册
    public Uri imageUri;

    /**
     * 打开相机拍照
     */
    private void takePhotos() {
        imageUri = Uri.fromFile(getImageStoragePath(member_information.this));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //指定照片存储路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,TAKE_PHOTO_REQUEST_CODE);
    }

    /**
     * 打开相册选择图片
     */
    private void choicePicFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 设置图片保存路径
     * @return
     */
    private File getImageStoragePath(Context context){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"temp.jpg");
            return file;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST_CODE){
            if (imageUri != null){
                //Log.e("01","01");
                startPhotoZoom(imageUri);
            }
        }else if (requestCode == PHOTO_REQUEST_CUT){
            if (imageUri != null) {
                // Log.e("02","02");
                bitmap = decodeUriBitmap(imageUri);
                user_image.setImageBitmap(bitmap);
            }
        }else if (requestCode == PHOTO_REQUEST_GALLERY){
            if (data != null) {
                // Log.e("03","03");
                imageUri = data.getData();
                startPhotoZoom(imageUri);
                bitmap = decodeUriBitmap(imageUri);
                user_image.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap decodeUriBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 调用系统裁剪
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 220);
        intent.putExtra("aspectY", 220);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 220);
        intent.putExtra("outputY", 220);

        //设置了true的话直接返回bitmap，可能会很占内存
        intent.putExtra("return-data", false);
        //设置输出的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //设置输出的地址
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //不启用人脸识别
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private String bitmapToStringByBase64(Bitmap bitmap) { //將圖片轉成 base64
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            //Log.d("d", "压缩后的大小=" + bytes.length);
            return Base64.encodeToString(bytes, Base64.NO_WRAP).replace("+","%2B");
        }catch (Exception e){
            Log.e("error","png to base64 is failed. ");
            return "";
        }
    }
    //end//----------------------------------------------------------------------------------------------
}