package com.example.administrator.funnypark.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.mall.platform_product_payment;
import com.example.administrator.funnypark.mall.platform_shopping_cart;
import com.example.model.util.DateUtils;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;
import com.example.model.util.file_util;
import com.example.model.util.upload_photo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class activities_initiate extends AppCompatActivity {//TODO 定義
    //static AppCompatActivity view_activities_initiate;

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    interface_util interface_util  = new interface_util();//介面設定
    final file_util file_util  = new file_util();//檔案參數

    String user_mail;//使用者帳號

    Bitmap bitmap ;//上傳圖片的bitmap36
    ImageView activities_img;

    List<String> park_name_id = new ArrayList<String>();//園區名稱id
    List<String> category_id = new ArrayList<String>();//活動種類id

    LinearLayout activity_time_Linear;//
    LinearLayout activity_hastag_Linear;
    LinearLayout activity_brief_Linear;
    LinearLayout activity_ticket_count_Linear;
    LinearLayout activity_ticket_Linear;

    EditText activities_name_edittext;//活動名稱
    Spinner park_name_spinner;// 活動園區名稱
    Spinner category_spinner;// 活動種類
    EditText activities_ticket_count;// 總票券數量
    EditText activities_private_ticket_count;// 開放票券數量
    EditText activities_public_ticket_count;// 開放票券數量
    EditText activities_host_edittext;//主辦單位
    EditText activities_co_organizer_edittext;//協辦單位
    EditText activities_brief_edittext;//活動簡述

    LinearLayout time_Linear;//活動時間新增Linear
    LinearLayout date_time_Linear;//活動日期與時段新增Linear
    EditText activities_date ;//選擇日期
    EditText date_time ;//選擇時段
    EditText start_time ;//開始時段
    EditText end_time ;//結束時段
    ImageView date_time_add;//新增日期與時段的按鈕(遊戲)
    ImageView date_time_add2;//新增日期與時段的按鈕

    EditText activities_hastag_edittext;//hastag
    TextView activities_hastag1;//hastag1
    TextView activities_hastag2;//hastag2
    TextView activities_hastag3;//hastag3
    Button btn_activities_add_hastagt;//hastag add button
    EditText ticket_selling_period;//販售截止日期
    EditText ticket_name;//票券名稱
    EditText ticket_fare;//票券價格
    LinearLayout ticket_list;//活動票券資訊新增的Linear
    TextView activities_initiate_edittext;//發起人
    Button btn_initiate;//發起活動

    List<String> activities_initiate_time_id = new ArrayList<String>();

    private void Dim()//TODO 宣告與定義 控制項
    {
        //
        activity_time_Linear = (LinearLayout)findViewById(R.id.activity_time_Linear);//活動時間的Linear
        activity_hastag_Linear = (LinearLayout)findViewById(R.id.activity_hastag_Linear);//活動hastag的Linear
        activity_brief_Linear = (LinearLayout)findViewById(R.id.activity_brief_Linear);//活動簡介的Linear
        activity_ticket_count_Linear = (LinearLayout)findViewById(R.id.activity_ticket_count_Linear);//活動票券數量的Linear
        activity_ticket_Linear = (LinearLayout)findViewById(R.id.activity_ticket_Linear);//活動票券的Linear

        //發起欄位定義
        activities_img = (ImageView)findViewById(R.id.activities_img);
        activities_name_edittext = (EditText)findViewById(R.id.activities_name);//活動名稱
        park_name_spinner = (Spinner)findViewById(R.id.activities_park_name);// 活動園區名稱
        category_spinner = (Spinner)findViewById(R.id.activities_category);// 活動種類
        activities_ticket_count = (EditText)findViewById(R.id.activities_ticket_count);// 總票券數量
        activities_private_ticket_count = (EditText)findViewById(R.id.activities_private_ticket_count);//保留票券數量
        activities_public_ticket_count = (EditText)findViewById(R.id.activities_public_ticket_count);//開放票券數量
        activities_host_edittext = (EditText)findViewById(R.id.activities_host);//主辦單位
        activities_co_organizer_edittext = (EditText)findViewById(R.id.activities_co_organizer);//協辦單位
        activities_brief_edittext = (EditText)findViewById(R.id.activities_brief);//活動簡述

        //活動日期與時間
        time_Linear = (LinearLayout)findViewById(R.id.activities_time_Linear);//新增活動時間的欄位Linear
        date_time_Linear = (LinearLayout)findViewById(R.id.activities_date_time_Linear);//活動新增日期與時間的Linear
        activities_date = (EditText)findViewById(R.id.activities_date);//活動日期
        date_time = (EditText)findViewById(R.id.activities_time);//活動時段
        start_time = (EditText)findViewById(R.id.start_time);//活動開始時間
        end_time = (EditText)findViewById(R.id.end_time);//活動結束時間
        date_time_add = (ImageView)findViewById(R.id.activities_date_time_add);//新增活動日期與時段(遊戲)
        date_time_add2 = (ImageView)findViewById(R.id.activities_date_time_add2);//新增活動日期與時段
        //date_time_Linear.setEnabled(false);

        //hastag
        activities_hastag_edittext = (EditText)findViewById(R.id.activities_hastag);//hastag
        activities_hastag1 = (TextView) findViewById(R.id.activities_hastag_1);//hastag1
        activities_hastag2 = (TextView) findViewById(R.id.activities_hastag_2);//hastag2
        activities_hastag3 = (TextView) findViewById(R.id.activities_hastag_3);//hastag3
        btn_activities_add_hastagt = (Button) findViewById(R.id.btn_activities_add_hastagt);//新增hastag按鈕

        ticket_selling_period = (EditText)findViewById(R.id.ticket_selling_period);//票券販售期限
        ticket_name = (EditText)findViewById(R.id.ticket_name);//票券名稱
        ticket_fare = (EditText)findViewById(R.id.ticket_fare);//票券價格
        ticket_list = (LinearLayout)findViewById(R.id.ticket_list);//活動時間的Linear

        activities_initiate_edittext = (TextView)findViewById(R.id.activities_initiate);//發起人
        btn_initiate = (Button) findViewById(R.id.btn_initiate);//發起活動按鈕
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)//TODO  create
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_initiate);

        //view_activities_initiate=this;

        Dim();//宣告與定義 控制項

        hastag_delete();//hagtag刪除監聽

        //還沒選擇活動類型前隱藏Linear
        activity_time_Linear.setVisibility(View.GONE);
        date_time_Linear.setVisibility(View.GONE);
        activity_hastag_Linear.setVisibility(View.GONE);
        activity_brief_Linear.setVisibility(View.GONE);
        activity_ticket_count_Linear.setVisibility(View.GONE);
        activity_ticket_Linear.setVisibility(View.GONE);
        ticket_list.setVisibility(View.GONE);

        //取得登入者身分帳號
        user_mail = file_util.Read_loginInfo_account_Value(activities_initiate.this);

        //匯入 地點 種類 資料
        import_location_category();
        activities_initiate_edittext.setText(user_mail);
        activities_initiate_edittext.setEnabled(false);

        //擷取照片按鈕監聽器
        activities_img.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*
                //讀取圖片
                Intent intent = new Intent();
                //開啟Pictures畫面Type設定為image
                intent.setType("image/*");
                //使用Intent.ACTION_GET_CONTENT這個Action
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //取得照片後返回此畫面
                startActivityForResult(intent, 0);

                                        .setPositiveButton("開起相機", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takePhotos();
                            }
                        })
                 */
                new android.support.v7.app.AlertDialog.Builder(activities_initiate.this)
                        .setTitle("選擇活動照片")
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

        //監聽使用者選的活動類別
        category_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                clear_date_time();//初始化
                if(!category_id.get(category_spinner.getSelectedItemPosition()).equals("請選擇")){
                    //選擇活動類型後　顯示欄位Linear
                    activity_time_Linear.setVisibility(View.VISIBLE);
                    activity_hastag_Linear.setVisibility(View.VISIBLE);
                    activity_brief_Linear.setVisibility(View.VISIBLE);
                    activity_ticket_count_Linear.setVisibility(View.VISIBLE);
                    activity_ticket_Linear.setVisibility(View.VISIBLE);
                    date_time_Linear.setVisibility(View.VISIBLE);
                    ticket_list.setVisibility(View.VISIBLE);
                }

                if(category_id.get(category_spinner.getSelectedItemPosition()).equals("102")){//選擇的活動類別是遊戲
                    date_time_add2.setVisibility(View.INVISIBLE);//隱藏其他活動的新增按鈕
                    date_time_add.setVisibility(View.VISIBLE);//顯示遊戲活動的新增按鈕
                    activities_date.setHint("選擇日期");
                    activities_date.setText("");
                    date_time.setHint("選擇時段" );
                    date_time.setText("");
                    time_Linear.setVisibility(View.GONE);//隱藏時段
                    date_time.setOnClickListener(new View.OnClickListener() {//設定選擇時段欄位
                        @Override
                        public void onClick(View v) {
                            activity_game_select_time();
                        }
                    });
                    date_time_add.setOnClickListener(new View.OnClickListener() {//設定新增日期與時段按鈕聽
                        @Override
                        public void onClick(View v) {
                            try {
                                date_time_add();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{//選擇的活動類別不是遊戲
                    date_time_add.setVisibility(View.INVISIBLE);//隱藏遊戲活動的新增按鈕
                    date_time_add2.setVisibility(View.VISIBLE);//顯示其他活動的新增按鈕
                    activities_date.setHint("選擇開始日期");
                    activities_date.setText("");
                    date_time.setHint("選擇結束日期" );
                    date_time.setText("");
                    time_Linear.setVisibility(View.VISIBLE);//隱藏時段
                    date_time.setOnClickListener(new View.OnClickListener() {//設定選擇結束日期欄位
                        @Override
                        public void onClick(View v) {
                            date_time.setText("");
                            Calendar_select(date_time);
                        }
                    });
                    date_time_add2.setOnClickListener(new View.OnClickListener() {//設定新增活動日期按鈕聽
                        @Override
                        public void onClick(View v) {
                            try {
                                date_time_add();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        //遊戲活動開始日期
        activities_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activities_date.setText("");
                Calendar_select(activities_date);
            }
        });

        //遊戲活動開始時間
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_time.setText("");
                time_select(start_time);
            }
        });

        //遊戲活動結束時間
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end_time.setText("");
                time_select(end_time);
            }
        });

        //票券販售截止日期
        ticket_selling_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticket_selling_period.setText("");
                Calendar_select(ticket_selling_period);
            }
        });

        pulbic_ticket_count(activities_private_ticket_count);
        pulbic_ticket_count(activities_ticket_count);
        //end
    }

    public void initiate_activity(View view) throws JSONException//TODO 發起活動
    {
        if(check_data() == false){
            //取得發起活動url
            final String url = temp_url.check_activity_insert(
                    activities_name_edittext.getText().toString(),
                    activities_brief_edittext.getText().toString(),
                    park_name_id.get(park_name_spinner.getSelectedItemPosition()),
                    category_id.get(category_spinner.getSelectedItemPosition()),
                    json_date_time.toString(),
                    activities_host_edittext.getText().toString(),
                    activities_co_organizer_edittext.getText().toString(),
                    bitmapToStringByBase64(bitmap),
                    activities_ticket_count.getText().toString(),
                    activities_private_ticket_count.getText().toString(),
                    ticket_selling_period.getText().toString(),
                    json_ticket.toString(),
                    activities_hastag1.getText().toString(),
                    activities_hastag2.getText().toString(),
                    activities_hastag3.getText().toString(),
                    user_mail);//取得url;

            //Log.e("tag",url);
            //Log.e("activity",json_date_time.toString());
            //Log.e("ticket",json_ticket.toString());

            final loading_util temp_load = new loading_util();//實體化loading視窗
            temp_load.loading_open(activities_initiate.this);//loading視窗打開
            //讀取新增要用的的各式資料
            Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                @Override
                public void run() {
                    try {//動到io(網路有關)一定要用try與執行緒
                        final String responseData = client.urlpost(url);//連線後端 取得資料
                        Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                        temp_load.loading_close();//loading視窗關閉

                        activities_initiate.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                        String[] ans = responseData.split(",");
                                        if(ans[0].equals("y")){//讀取資料有無失敗
                                            Intent jump =  activities_initiate.this.getIntent();//跳轉到發起成功的畫面
                                            jump.setClass(activities_initiate.this, activities_initiate_success.class);
                                            activities_initiate.this.finish();//關閉發起頁面
                                            startActivity(jump);
                                        }else{//讀取資料失敗 顯示對應資訊
                                            Toast.makeText(activities_initiate.this, responseData, Toast.LENGTH_LONG).show();//顯示Toast
                                        }
                                    }else {//網路連線失敗
                                        Toast.makeText(activities_initiate.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                        //顯示Toast
                                    }
                                }catch (Exception e){
                                    activities_initiate.this.finish();
                                }

                            }
                        });

                    } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                        Log.e("TAG", "error100，若持續發生此問題，請聯絡我們");//偵錯用( 一行一行)
                    }
                }
            };
            okHttpExecuteThread.start();
            //end
        }else{
            Toast.makeText(activities_initiate.this, "資料不完整", Toast.LENGTH_LONG).show();
            //顯示Toast
        }

    }

    private boolean check_data(){//確認資料有無完整
        if(activities_name_edittext.getText().toString().equals("")
                || activities_brief_edittext.getText().toString().equals("")
                || park_name_id.get(park_name_spinner.getSelectedItemPosition()).equals("請選擇")
                || category_id.get(category_spinner.getSelectedItemPosition()).equals("請選擇")
                || json_date_time.toString().equals("[]")
                || activities_host_edittext.getText().toString().equals("")
                || activities_co_organizer_edittext.getText().toString().equals("")
                || bitmapToStringByBase64(bitmap).equals("")
                || activities_ticket_count.getText().toString().equals("")
                || activities_private_ticket_count.getText().toString().equals("")
                || ticket_selling_period.getText().toString().equals("")
                || json_ticket.toString().equals("[]")
                || activities_hastag1.getText().toString().equals("")
                || activities_hastag2.getText().toString().equals("")
                || activities_hastag3.getText().toString().equals("")){
            return true;
        }else{
            return false;
        }
    }

    private void pulbic_ticket_count(EditText view)//TODO 計算公開票券
    {
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(!activities_ticket_count.getText().toString().equals("") & !activities_private_ticket_count.getText().toString().equals("")){
                    int temp_public_ticket = Integer.parseInt(activities_ticket_count.getText().toString()) - Integer.parseInt(activities_private_ticket_count.getText().toString());
                    if(temp_public_ticket>=0){
                        activities_public_ticket_count.setText(String.valueOf(temp_public_ticket));
                    }
                }
            }
        });
    }

    public void hastag_add(View view)//TODO 新增hastag
    {
        if(activities_hastag_edittext.getText().toString().trim().isEmpty())return;
        if(activities_hastag1.getText().toString().trim().equals("無")){
            activities_hastag1.setText("" + activities_hastag_edittext.getText());
            activities_hastag_edittext.setText("");
        }else if(activities_hastag2.getText().toString().trim().equals("無")){
            activities_hastag2.setText("" + activities_hastag_edittext.getText());
            activities_hastag_edittext.setText("");
        }else if(activities_hastag3.getText().toString().trim().equals("無")){
            activities_hastag3.setText("" + activities_hastag_edittext.getText());
            activities_hastag_edittext.setText("");
        }else{

        }
        //end
    }

    public void hastag_delete()//TODO 刪除hastag
    {
        activities_hastag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(activities_initiate.this);
                dlgAlert.setTitle("確認視窗");
                dlgAlert.setMessage("是否要刪除此HagTag");
                dlgAlert.setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                                activities_hastag1.setText("無");
                            }
                        });
                dlgAlert.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
            }
        });
        //end
    }

    public void finish_this_page(View v)//TODO  關閉頁面 不想發起了
    {
        this.finish();
    }

    //---------------------------------------------------------------------------------------------------
    //日期與時間
    private void Calendar_select(final TextView output)//TODO 日期選擇
    {
        final Calendar calendar = Calendar.getInstance();
        output.setText(DateUtils.date2String(calendar.getTime(), DateUtils.YMD_FORMAT));//YMD_FORMAT
        DatePickerDialog dialog = new DatePickerDialog(activities_initiate.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //LogUtils.d(TAG, "onDateSet: year: " + year + ", month: " + month + ", dayOfMonth: " + dayOfMonth);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        output.setText(DateUtils.date2String(calendar.getTime(), DateUtils.YMD_FORMAT));//顯示選擇的日期

                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void time_select(final TextView output)//TODO  時間選擇
    {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        new TimePickerDialog(activities_initiate.this,2, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                output.setText(String.format("%02d:%02d", hourOfDay,minute));//定義格式
            }
        }, hour, minute, true).show();
    }
    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //控制項建立
    private LinearLayout create_Linear()//TODO 新增活動時間與票券之輸入框Linear
    {
        //Linear create
        LinearLayout temp_Linear = new LinearLayout(activities_initiate.this);
        temp_Linear.setOrientation(LinearLayout.HORIZONTAL);
        temp_Linear.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams temp_Linear_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        temp_Linear.setLayoutParams(temp_Linear_params);
        return temp_Linear;
    }

    private EditText textbox_style(String temp_text)//TODO 新增活動時間與票券之輸入框style
    {
        //EditText create
        EditText temp_textbox_style = new EditText(activities_initiate.this);
        //temp_textbox_style.setHintTextColor(Color.rgb(0, 39, 56));
        //temp_textbox_style.setHint(temp_hint);//設置hint
        temp_textbox_style.setTextSize(15);
        temp_textbox_style.setGravity(Gravity.CENTER);
        temp_textbox_style.setText(temp_text);
        //Parms
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,2f);//
        lparams.setMargins(
                0,//left
                0,//top
                interface_util.transform_dp(12,activities_initiate.this),//right
                0);//bottom
        temp_textbox_style.setLayoutParams(lparams);

        return temp_textbox_style;
    }

    private ImageView subtract_style(final int key,final int type)//TODO 移除活動時間與票券之按鈕
    {
        ImageView temp_img = new ImageView(activities_initiate.this);
        temp_img.setImageDrawable(this.getResources().getDrawable((R.drawable.dis_btn)));
        temp_img.setVisibility(View.VISIBLE);
        temp_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(type==0){//新增活動時段移除按鈕
                        int temp_id =IndexOf(json_date_time,String.valueOf(key));
                        //Log.e("temp_id",String.valueOf(key));
                        date_time_Linear.removeViewAt(temp_id);
                        json_date_time.remove(temp_id);
                    }else if(type==1){//新增活動票券移除按鈕
                        int temp_id =IndexOf(json_ticket,String.valueOf(key));
                        //Log.e("temp_id",String.valueOf(key));
                        ticket_list.removeViewAt(temp_id);
                        json_ticket.remove(temp_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return temp_img;
    }
    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //新增日期與時段、票券、Json的方法
    int date_time_count;//紀錄新增幾個活動時段
    int ticket_count;//紀錄新增幾種票券
    int select_time_id ;//活動時段代號
    JSONArray json_date_time = new JSONArray();//json傳活動發起
    JSONArray json_ticket= new JSONArray();//json傳送票券種類與價格

    private void date_time_add() throws JSONException//TODO 新增活動日期與時段
    {
        LinearLayout temp_Linear = create_Linear();
        JSONObject temp_json = new JSONObject();
        if(category_id.get(category_spinner.getSelectedItemPosition()).equals("102")){//如果是活動
            temp_Linear.addView(textbox_style(activities_date.getText().toString()));
            temp_Linear.addView(textbox_style(date_time.getText().toString()));
            temp_Linear.addView(subtract_style(date_time_count,0));//寫入時段流水號

            temp_json.put("id",date_time_count++);//新增時段流水號到json
            temp_json.put("activity_start_date",activities_date.getText().toString());//活動日期
            temp_json.put("activity_end_date",activities_date.getText().toString());//活動日期
            temp_json.put("asession_id",activities_initiate_time_id.get(select_time_id));//活動時段
            json_date_time.put(temp_json);
        }else{
            temp_Linear.addView(textbox_style(activities_date.getText().toString()));
            temp_Linear.addView(textbox_style(date_time.getText().toString()));
            temp_Linear.addView(textbox_style(start_time.getText().toString()));
            temp_Linear.addView(textbox_style(end_time.getText().toString()));
            temp_Linear.addView(subtract_style(date_time_count,0));//寫入時段流水號

            temp_json.put("id",date_time_count++);//新增時段流水號到json
            temp_json.put("activity_start_date",activities_date.getText().toString());//活動日期
            temp_json.put("activity_end_date",date_time.getText().toString());//活動時間
            temp_json.put("activity_start_time",start_time.getText().toString());//活動開始時間
            temp_json.put("activity_end_time",end_time.getText().toString());//活動結束時間
            json_date_time.put(temp_json);
            start_time.setText("");//清空開始時間欄位
            end_time.setText("");//清空結束時間欄位
            //Log.e("tag");
        }
        activities_date.setText("");//清空遊戲開始日期欄位
        date_time.setText("");//清空時間or結束日期欄位
        date_time_Linear.addView(temp_Linear);
    }

    public void ticket_add(View view) throws JSONException//TODO 新增票券
    {
        LinearLayout temp_Linear = create_Linear();
        temp_Linear.addView(textbox_style(ticket_name.getText().toString()));
        temp_Linear.addView(textbox_style(ticket_fare.getText().toString()));
        temp_Linear.addView(subtract_style(ticket_count,1));//寫入票券流水號

        JSONObject temp_json = new JSONObject();
        //JsonObject temp_ticket = new JsonObject();
        temp_json.put("id",ticket_count++);//新增票券流水號到json
        temp_json.put("ticket_name",ticket_name.getText().toString());//新增票券名稱
        temp_json.put("ticket_amount", ticket_fare.getText().toString());//新增票券價格
        //activities_ticket_name.add(ticket_name.getText().toString());//新增票券名稱
        //activities_ticket_fare.add(ticket_fare.getText().toString());//新增票券價格
        ticket_name.setText("");
        ticket_fare.setText("");
        json_ticket.put(temp_json);
        ticket_list.addView(temp_Linear);
    }

    private int IndexOf(JSONArray temp_json,String Key) throws JSONException {//查詢與解析Json的值 有找尋到回傳索引值 沒有的話回傳-1
        for (int i = 0; i < temp_json.length(); i++) {
            JSONObject jsonObject = temp_json.getJSONObject(i);
            String temp_id = jsonObject.getString("id");//解析id內容
            if(temp_id.equals(Key)) return i;
        }
        return -1;
    }

    private void clear_date_time(){
        json_date_time = new JSONArray();
        date_time_Linear.removeAllViews();
        date_time_count = 0;
    }
    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //匯入資料
    private void import_location_category()//TODO 匯入 園區地點 活動類別
    {
        final String url = temp_url.activity_insert(user_mail);//取得url 活動詳細資訊
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(activities_initiate.this);//loading視窗打開
        //讀取新增要用的的各式資料
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();
                    Log.e("res",responseData);
                    activities_initiate.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    final String[] part = responseData.split("\\*");//分解字串 "\\."

                                    if(part[0].indexOf("error")<0){//讀取資料有無失敗
                                        final String[] category = part[0].split("y,");//分解字串 有幾筆活動種類
                                        String[] temp_category  = new String[category.length];
                                        temp_category[0] = "請選擇";
                                        category_id.add("請選擇");//存取活動種類id 0活動編號
                                        //活動種類
                                        for(int i =1;i<category.length;i++){//活動種類
                                            final String[] ans = category[i].split(",");//分解字串 活動種類的id與名稱
                                            category_id.add(ans[0]);//存取活動種類id 0活動編號
                                            temp_category[i]=ans[1];//寫入活動類別 1類別名稱
                                        }
                                        category_spinner.setAdapter(new ArrayAdapter(activities_initiate.this, android.R.layout.simple_dropdown_item_1line, temp_category));
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = part[0].split(",");
                                        Toast.makeText(activities_initiate.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }

                                    if(part[1].indexOf("error")<0){//讀取資料有無失敗
                                        final String[] park_name = part[1].split("y,");//分解字串 有幾筆園區地點
                                        String[] temp_park_name  = new String[park_name.length];
                                        temp_park_name[0] = "請選擇";
                                        park_name_id.add("請選擇");
                                        //活動種類
                                        for(int i =1;i<park_name.length;i++){//園區地點
                                            final String[] ans = park_name[i].split(",");//分解字串 活動種類的id與名稱
                                            park_name_id.add(ans[0]);//存取園區地點id 0地點編號
                                            temp_park_name[i]=ans[1];//寫入園區地點 ans 1地點名稱
                                        }
                                        park_name_spinner.setAdapter(new ArrayAdapter(activities_initiate.this, android.R.layout.simple_dropdown_item_1line, temp_park_name));
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = part[1].split(",");
                                        Toast.makeText(activities_initiate.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }


                                }else {//網路連線失敗
                                    Toast.makeText(activities_initiate.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                activities_initiate.this.finish();
                            }
                        }
                    });

                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    public void activity_game_select_time()//TODO 匯入遊戲時段　並選擇時段
    {
        final String  url = temp_url.activity_insert_session(category_id.get(category_spinner.getSelectedItemPosition()),
                activities_date.getText().toString(),
                activities_date.getText().toString());//
        //讀取新增要用的的各式資料
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(activities_initiate.this);//loading視窗打開
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    activities_initiate.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    //String[] ans = responseData.split(",");
                                    if(responseData.substring(0,1).equals("y")){//讀取資料有無失敗
                                        AlertDialog.Builder builder=new AlertDialog.Builder(activities_initiate.this);
                                        builder.setIcon(android.R.drawable.ic_input_get);//ic_dialog_info
                                        builder.setTitle("請選擇時段");
                                        //讀取現有的時段
                                        String[] time = responseData.split("\\*");
                                        final String []items=new String[time.length];//所有時段陣列
                                        for(int i=0;i<time.length;i++){
                                            String[] ans = time[i].split(",");
                                            activities_initiate_time_id.add(ans[1]);//儲存時段id
                                            items[i] = ans[2] + " ~ " + ans[3];//時段
                                        }
                                        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                                            //which指的是用户选择的条目的下标
                                            //dialog:触发这个方法的对话框
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(activities_initiate.this, "您选择的是:"+items[which], Toast.LENGTH_SHORT).show();
                                                //activities_date_start.setText(activities_date_start.getText().toString() + " " + items[which]);
                                                select_time_id = which;
                                                date_time.setText(items[which]);
                                                dialog.dismiss();//当用户选择了一个值后，对话框消失
                                            }
                                        });
                                        builder.show();//这样也是一个show对话框的方式，上面那个也可以
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(activities_initiate.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(activities_initiate.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                activities_initiate.this.finish();
                            }
                        }
                    });

                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }
    //---------------------------------------------------------------------------------------------------

    public static int TAKE_PHOTO_REQUEST_CODE = 1; //拍照
    public static int PHOTO_REQUEST_CUT = 2; //裁切
    public static int PHOTO_REQUEST_GALLERY = 3; //相册
    public Uri imageUri;

    /**
     * 打开相机拍照
     */
    private void takePhotos() {
        imageUri = Uri.fromFile(getImageStoragePath(activities_initiate.this));
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
                activities_img.setImageBitmap(bitmap);
            }
        }else if (requestCode == PHOTO_REQUEST_GALLERY){
            if (data != null) {
               // Log.e("03","03");
                imageUri = data.getData();
                startPhotoZoom(imageUri);
                bitmap = decodeUriBitmap(imageUri);
                activities_img.setImageBitmap(bitmap);
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
        intent.putExtra("aspectX", 360);
        intent.putExtra("aspectY", 180);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 360);
        intent.putExtra("outputY", 180);

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

    /*
    //---------------------------------------------------------------------------------------------------
    //TODO 讀取相簿
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:  //取得圖片後進行裁剪
                    Uri uri = data.getData();
                    if(uri!=null){
                        doCropPhoto(uri);
                    }
                    break;
                case 1:  //裁剪完的圖片更新到ImageView
                    //先釋放ImageView上的圖片
                    if(activities_img.getDrawable()!=null) {
                        activities_img.setImageBitmap(null);
                        System.gc();
                    }
                    //更新ImageView
                    bitmap = data.getParcelableExtra("data");
                    Log.e("bitmap",String.valueOf(bitmap));
                    activities_img.setImageBitmap(bitmap);
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
        intent.putExtra("aspectX", 360);// 这兩項為裁剪框的比例.
        intent.putExtra("aspectY", 180);// x:y=1:1
        intent.putExtra("outputX", 360);//回傳照片比例X
        intent.putExtra("outputY", 180);//回傳照片比例Y

        intent.putExtra("return-data", true);
        //intent.putExtra("return-data", "false");
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }

    @Override
    protected void onDestroy() {        //釋放內存
        try{
            super.onDestroy();
            //釋放整個介面與圖片
            activities_img.setImageBitmap(null);
            activities_img=null;
            //activities_img_Linear.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("text", "New_DISS_Main.onDestroy()崩潰=" + e.toString());
        }
    }

    //圖片轉base64編碼
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
     */
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
}