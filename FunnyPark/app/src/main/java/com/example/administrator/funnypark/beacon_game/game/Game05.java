package com.example.administrator.funnypark.beacon_game.game;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.beacon_game.Scan_data.game_connect_php;
import com.example.model.util.connection_util;
import com.example.model.util.okhttp_util;


public class Game05 extends AppCompatActivity implements RecordThread.ChangeState {

    private TextView tRnd;
    private TextView tAlert;
    private EditText tNum;
    private Button btnSend;
    public static boolean isRunning = true;
    private int light=1;
    public boolean Game05_check = false;

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    private game_connect_php mgame_connect_php = new game_connect_php();

    //遊戲成功後，需傳給後台的數據
    String username;
    String game_level_record_id;
    String game_station_id;
    String game_room_id;
    String game_class_id;

    //從 Intent 接收(SparseArray)玩家基本資料
    public void Intent_Game05_data()
    {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        username = extras.getString("username");
        game_level_record_id = extras.getString("game_level_record_id");
        game_station_id = extras.getString("game_station_id");
        game_room_id = extras.getString("game_room_id");
        game_class_id = extras.getString("game_class_id");
    }

    //重複執行更新，等待每個玩家按下準備，都按下後，會直接進入遊戲內
    //要先執行副程式，waiting_start_handler.postDelayed(waiting_start_runnable, 3500) 才會到這
    private Handler gema_check_handler = new Handler();
    private Runnable gema_check_runnable;
    private void repeat_gema_check()
    {
        gema_check_runnable = new Runnable() {
            public void run() {
                try
                {
                    Intent_Game05_data();
                    game_check("1",username,"",game_room_id);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_small_game05);

        //設置螢幕保持亮起
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //初始手機當前畫面亮度為0
        setScreenBrightness(0);

        //verifyAudioPermissions(this);


        //get object
        tRnd=(TextView)findViewById(R.id.txtRnd);
        tNum=(EditText)findViewById(R.id.inNum);
        tAlert=(TextView)findViewById(R.id.txtAlert);


        btnSend=(Button)findViewById(R.id.btnSend);


        int cRnd =(int)(Math.random()*100000+999999);

        tRnd.setText(""+ cRnd );


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game05_check = true;
                //取得玩家輸入數字
                String aNum = tNum.getText().toString();

                //判斷數字
                if (tRnd.getText().toString().equals(aNum))
                {
                    //破關成功傳回資料庫
                    Intent_Game05_data();
                    mgame_connect_php.check_solve_station(game_level_record_id,game_station_id,game_room_id,game_class_id,"9");
                    tAlert.setText("破關成功！！");
                }
                else
                {
                    tAlert.setText("輸入錯誤囉,再輸入一次");
                }
            }
        });


        // TODO Auto-generated method stub
        //isRunning = !isRunning;

        new RecordThread(this).start();

        game_check("1",username,"",game_room_id);
        repeat_gema_check();

    }

    //設置手機螢幕亮度
    public void setScreenBrightness(int level) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.screenBrightness = level / 255.0f;
        getWindow().setAttributes(attributes);
    }

    @Override
    public void change() {
        // TODO Auto-generated method stub
        if(tAlert != null) {
            String text = tAlert.getText().toString();
            text = text + "\r\n吹粗来了!";
            tAlert.setText(text);
            setScreenBrightness(light);
            light+=10;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("TAG","關閉Game05...");
        Game05.this.finish();
    }

    /* -----------------------------------------判斷玩家是否在遊戲驛站範圍內(人類)---------------------------------------------------------*/
    public void game_check(String type_id, String username, String game_check ,  String game_room_id)
    {
        final String url = temp_url.game_check(type_id,username,game_check,game_room_id);//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("resaaa",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    if(ans[0].equals("y") && ans[1].equals("1")) //0(範圍內) 1(超過範圍)
                    {
                        gema_check_handler.removeCallbacks(gema_check_runnable); //停止Timer
                        Game05.this.finish();
                    }
                    else
                    {
                        gema_check_handler.postDelayed(gema_check_runnable, 4500);
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }
    /* --------------------------------------判斷玩家是否在遊戲驛站範圍內(人類)(結束)---------------------------------------------------*/
}
