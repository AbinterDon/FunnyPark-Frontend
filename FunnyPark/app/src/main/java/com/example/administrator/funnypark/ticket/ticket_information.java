package com.example.administrator.funnypark.ticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.beacon_game.Before_game.connect_watch;
import com.example.administrator.funnypark.login.Login;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

public class ticket_information extends AppCompatActivity {

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    final interface_util interface_util  = new interface_util();//介面設定
    final file_util file_util  = new file_util();//檔案參數

    ImageView activity_img;
    ImageView user_img;
    TextView activity_name;
    TextView exchange_id;
    TextView ticket_no;
    TextView ticket_category;
    TextView activity_category;
    TextView activity_park;
    TextView activity_host;
    TextView activity_date1;
    TextView activity_date2;
    TextView activity_time1;
    TextView activity_time2;
    TextView activity_address;
    TextView activity_hastag1;
    TextView activity_hastag2;
    TextView activity_hastag3;
    TextView ticket_statement;
    ImageView activity_qrcode;
    String activity_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_information);

        dim();

        loading_ticket();

    }

    private void dim()
    {
        activity_img = findViewById(R.id.activity_image);
        user_img = findViewById(R.id.user_img);
        activity_name = findViewById(R.id.activity_name);
        exchange_id = findViewById(R.id.exchange_id);
        ticket_no = findViewById(R.id.ticket_no);
        ticket_category = findViewById(R.id.ticket_category);
        activity_category = findViewById(R.id.activity_category);
        activity_park = findViewById(R.id.activity_park);
        activity_host = findViewById(R.id.activity_host);
        activity_date1 = findViewById(R.id.activity_date1);
        activity_date2 = findViewById(R.id.activity_date2);
        activity_time1 = findViewById(R.id.activity_time1);
        activity_time2 = findViewById(R.id.activity_time2);
        activity_address = findViewById(R.id.activity_address);
        activity_hastag1 = findViewById(R.id.activity_hastag1);
        activity_hastag2 = findViewById(R.id.activity_hastag2);
        activity_hastag3 = findViewById(R.id.activity_hastag3);
        activity_qrcode = findViewById(R.id.activity_qrcode);
        ticket_statement = findViewById(R.id.ticket_statement);
    }

    private void loading_ticket(){
        final Intent jump = this.getIntent();//實體化 抓資料
        final String url = temp_url.get_ticket_info_detail(jump.getStringExtra("ticket_no"));//取得url 確認購買票券
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(ticket_information.this);//loading視窗打開

        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    //Log.e("tag",url);
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉
                    ticket_information.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        for(int i = 0 ; i<ans.length;i++){
                                            interface_util.set_img(temp_url.get_url_img() + ans[1],activity_img,ticket_information.this);//活動圖片
                                            interface_util.set_img(temp_url.get_url_img() + ans[2],user_img,ticket_information.this);//活動發起人
                                            activity_name.setText(ans[3]);//活動名稱
                                            exchange_id.setText(ans[17]);//兌換編號
                                            activity_category.setText(ans[4]);
                                            ticket_no.setText(ans[5]);//票券編號
                                            ticket_category.setText(ans[6]);//票券種類
                                            activity_park.setText(ans[7]);//活動園區
                                            activity_host.setText(ans[8]);//活動主辦
                                            activity_date1.setText(ans[9]);//活動日期1
                                            activity_date2.setText(ans[10]);//活動日期2
                                            String[] temp_time = ans[11].split("-");//分割時間
                                            activity_time1.setText(temp_time[0]);//活動時間1
                                            activity_time2.setText(temp_time[1]);//活動時間2
                                            activity_address.setText(ans[12]);//活動地址
                                            activity_hastag1.setText(ans[13]);//hastag1
                                            activity_hastag2.setText(ans[14]);//hastag2
                                            activity_hastag3.setText(ans[15]);//hastag3
                                            Log.e("URl", String.valueOf(activity_qrcode));
                                            interface_util.set_img(ans[16],activity_qrcode,ticket_information.this);//qrcode
                                            activity_id = ans[18];
                                            activity_qrcode.setOnClickListener(new View.OnClickListener() {//點擊QRcode
                                                @Override
                                                public void onClick(View v) {
                                                    check_activity_premit(exchange_id.getText().toString());
                                                }
                                            });
                                        }
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(ticket_information.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(ticket_information.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(ticket_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                ticket_information.this.finish();
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

    private void check_activity_premit(String attend_verify){//check_activity_attend
        final String url = temp_url.check_activity_attend(attend_verify);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(ticket_information.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    ticket_information.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        Intent jump = new Intent(ticket_information.this, connect_watch.class);//準備跳轉至手環連接室
                                        Bundle bundle = new Bundle();
                                        bundle.putString("connect_watch_ticket_key","ticket");//傳遞String
                                        bundle.putString("activity_id",activity_id);//傳遞String
                                        jump.putExtras(bundle);
                                        startActivity(jump);
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(ticket_information.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(ticket_information.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(ticket_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                ticket_information.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(ticket_information.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    public void finish_this_page(View v){
        this.finish();
    }

/*
    public void connect_watch_room(View v)
    {
        Log.e("hi",activity_id);
        Intent jump = new Intent(ticket_information.this, connect_watch.class);
        Bundle bundle = new Bundle();
        bundle.putString("connect_watch_ticket_key","ticket");//傳遞String
        bundle.putString("activity_id",activity_id);//傳遞String
        jump.putExtras(bundle);
        startActivity(jump);
    }

 */
}
