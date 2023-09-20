package com.example.administrator.funnypark.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.login.Login;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class activities_ticket_checkout extends AppCompatActivity {
    static AppCompatActivity view_activities_ticket_checkout;

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    final interface_util interface_util  = new interface_util();//介面設定
    final file_util file_util  = new file_util();//檔案參數

    String user_mail;//使用者帳號

    ImageView activity_user;//發起人圖片
    TextView activity_name;//活動名稱
    TextView activity_host;//主辦單位
    TextView activity_date1;//活動日期1
    TextView activity_date2;//活動日期2
    TextView activity_time1;//活動時間1
    TextView activity_time2;//活動時間2
    TextView activity_park;//活動地點
    TextView activity_hastag1;//hastag1
    TextView activity_hastag2;//hastag2
    TextView activity_hastag3;//hastag3
    TextView ticket_title;//票券title

    List<String> ticket_id = new ArrayList<String>();//票券id

    String activity_id; //活動id
    String choose_ticket ;//選取的票券id
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_ticket_checkout);
        view_activities_ticket_checkout = this;
        dim();//定義

        //取得登入者身分帳號
        user_mail = file_util.Read_loginInfo_account_Value(activities_ticket_checkout.this);

        loading_ticket_info();//讀取票券種類

        //選取票券
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.ticket_choose);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                //String result = radioButton.getText().toString();
                //int radioButtonID = radioGroup.getCheckedRadioButtonId();
                //View temp = radioGroup.findViewById(radioButtonID);
                int result1 = radioGroup.indexOfChild(radioButton);
                choose_ticket = String.valueOf(result1);
                // String result1 = radioGroup.indexOfChild(radioGroup.findViewById(.getCheckedRadioButtonId());
                //Log.e("result",result);
                //Log.e("result1",String.valueOf(result1));
            }
        });


    }

    private void dim()//定義與宣告
    {
        activity_user = (ImageView)findViewById(R.id.activity_user);
        activity_name = (TextView)findViewById(R.id.activity_name);
        activity_host = (TextView)findViewById(R.id.activity_host);
        activity_park = (TextView)findViewById(R.id.activity_park);
        activity_date1 = (TextView)findViewById(R.id.activity_date1);
        activity_date2 = (TextView)findViewById(R.id.activity_date2);
        activity_time1 = (TextView)findViewById(R.id.activity_time1);
        activity_time2 = (TextView)findViewById(R.id.activity_time2);
        activity_hastag1 = (TextView)findViewById(R.id.activity_hastag1);
        activity_hastag2 = (TextView)findViewById(R.id.activity_hastag2);
        activity_hastag3 = (TextView)findViewById(R.id.activity_hastag3);
        ticket_title = (TextView)findViewById(R.id.ticket_title);
    }

    private void loading_ticket_info()//載入票券與活動相關資訊
    {
        Intent jump = this.getIntent();//實體化 抓資料
        activity_id = jump.getStringExtra("activity_id");//取得打包資料的資料
        final String url = temp_url.get_activity_buy_ticket(user_mail,activity_id);//取得url 活動詳細資訊
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(activities_ticket_checkout.this);//loading視窗打開
        //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//loading視窗關閉
                    Log.e("tag",responseData);
                    activities_ticket_checkout.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.substring(0,1).equals("y")){//還沒購買過此票券
                                        final String[] part = responseData.split("\\*");//分解字串
                                        //Log.e("part.length",String.valueOf(part.length));
                                        for(int i = 0 ; i<part.length;i++){
                                            //Log.e("part[i]",part[i]);
                                            if(i == part.length-1){//活動資訊
                                                final String[] activity = part[i].split(",");//分解活動字串
                                                //Log.e("tag",temp_url.get_url_img() + activity[14]);
                                                interface_util.set_img(temp_url.get_url_img() + activity[14],activity_user,activities_ticket_checkout.this);//發起人圖片
                                                activity_name.setText(activity[2]);//活動名稱
                                                activity_host.setText(activity[9]);//活動發起人
                                                activity_park.setText(activity[10]);//活動園區
                                                activity_date1.setText(activity[5]);//活動日期1
                                                activity_date2.setText(activity[6]);//活動日期2
                                                activity_time1.setText(activity[7]);//活動時間1
                                                activity_time2.setText(activity[8]);//活動時間2
                                                activity_hastag1.setText(activity[11]);//hastag1
                                                activity_hastag2.setText(activity[12]);//hastag2
                                                activity_hastag3.setText(activity[13]);//hastag3
                                                //ticket_title.setText("請選擇票券種類(剩餘"+ activity[3] + "/" + activity[4] + "張)");
                                                ticket_title.setText("剩餘"+ activity[3] + "張　共" + activity[4] + "張");
                                            }else{//票券資訊
                                                final String[] ticket = part[i].split(",");//分解票券字串
                                                ticket_id.add(ticket[1]);//票券id
                                                ticket_style(ticket[2] + " NT$" +ticket[3] + "/張");//票券名稱
                                            }
                                        }
                                    }else{//已經購買過此票券
                                        activities_ticket_checkout.this.finish();
                                        Toast toast = Toast.makeText(activities_ticket_checkout.this, responseData, Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(activities_ticket_checkout.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(activities_ticket_checkout.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                activities_ticket_checkout.this.finish();
                            }
                            //end if
                        }
                    });
                    //end onUI
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    private void ticket_style(String ticket){//動態產生 票券的樣式
        RadioGroup group = findViewById(R.id.ticket_choose);
        RadioButton radioButton = new RadioButton(this);//宣告
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);//設定長寬
        layoutParams.setMargins(0, 0, 0, interface_util.transform_dp(18,activities_ticket_checkout.this));//設定margin
        radioButton.setLayoutParams(layoutParams);//套裝layout設定
        radioButton.setText(ticket);//票券名稱與價格
        radioButton.setBackground(getResources().getDrawable(R.drawable.ticket_type_choose));//設定樣式
        radioButton.setTextColor(Color.rgb(0, 39, 56));//文字顏色
        radioButton.setTextSize(14);//文字大小
        radioButton.setButtonDrawable(null);//隱藏radiobutton的圈圈
        radioButton.setPadding(interface_util.transform_dp(80,activities_ticket_checkout.this), 0, 0, 0);//設置padding
        group.addView(radioButton);//將單選按鈕新增到RadioGroup中
    }

    public void finish_this_page(View v){
        this.finish();
    }//關閉購買

    public void check_purchase(View v){//確認購買
        String user_mail = file_util.Read_loginInfo_account_Value(activities_ticket_checkout.this);//使用者帳號
        Intent jump=new Intent();
        jump.setClass(this,activities_ticket_payment.class);
        jump.putExtra("username",user_mail);
        jump.putExtra("activity_id",activity_id);
        jump.putExtra("activity_time1",activity_time1.getText().toString());
        jump.putExtra("activity_time2",activity_time2.getText().toString());
        jump.putExtra("ticket_id",ticket_id.get(Integer.parseInt(choose_ticket)));
        startActivity(jump);
    }
}
