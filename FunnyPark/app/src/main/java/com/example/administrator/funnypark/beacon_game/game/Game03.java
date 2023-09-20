package com.example.administrator.funnypark.beacon_game.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.beacon_game.Scan_data.game_connect_php;
import com.example.model.util.connection_util;
import com.example.model.util.okhttp_util;

public class Game03 extends AppCompatActivity {

    private CircleProgress mCircleProgress;
    private SensorManager mySensorManager;
    private Sensor mySensor;//體感(Sensor)類別
    private TextView tlight;
    private TextView maxlight;
    private TextView minlight;
    private TextView clight;
    private SoundPool sound;
    private int completeMusic;
    private int lightMusic;
    private MediaPlayer mPlayer;
    private double mlight;
    private float light;
    private AlertDialog.Builder builder; //create AlertDialog
    private AlertDialog.Builder ebuilder; //create AlertDialog

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    private game_connect_php mgame_connect_php = new game_connect_php();

    //亮度感測最大值
    private static float LIGHT_MAX_VALUE = 0;

    //亮度感測最小值
    private static float LIGHT_MIN_VALUE = 0;

    //目前亮度感測值
    private static int LIGHT_CURRENT_VALUE = 0;

    //亮度進度
    private static int LIGHT_PROGRESS = 0;

    private static boolean is_First = true;

    private static boolean check_Light = false;

    private static float FIRST_LIGHT_VALUE = 0;

    //遊戲成功後，需傳給後台的數據
    String username;
    String game_level_record_id;
    String game_station_id;
    String game_room_id;
    String game_class_id;

    //從 Intent 接收(SparseArray)玩家基本資料
    public void Intent_Game03_data()
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
                    Intent_Game03_data();
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
        setContentView(R.layout.activity_beacon_small_game03);

        //設置螢幕保持亮起
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //初始手機當前畫面亮度為0
        //setScreenBrightness(0);
        setScreenBrightness(100);

        //get object
        mCircleProgress = (CircleProgress) findViewById(R.id.circle_progress);

        maxlight = (TextView)findViewById(R.id.maxLight);
        minlight = (TextView)findViewById(R.id.minLight);
        clight = (TextView)findViewById(R.id.currLight);

        sound = new SoundPool(10, AudioManager.STREAM_MUSIC,5);
        completeMusic=sound.load(this,R.raw.complete,1);
        lightMusic=sound.load(this,R.raw.light,1);

        //GAME BGM
        mPlayer = MediaPlayer.create(this,R.raw.game03);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setLooping(true);
        mPlayer.start();

        initializeLight();
        initializeSensor();

        game_check("1",username,"",game_room_id);
        repeat_gema_check();

    }

    //設置手機螢幕亮度
    public void setScreenBrightness(int level) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.screenBrightness = level / 255.0f;
        getWindow().setAttributes(attributes);
    }

    //初始化Sensor
    private void initializeSensor()
    {
        //取得體感(Sensor)服務使用權限
        mySensorManager =(SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        //取得手機Sensor狀態設定
        mySensor=mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //註冊亮度(Sensor)變化觸發Listener
        mySensorManager.registerListener(mySensorListener,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initializeLight()
    {
        LIGHT_CURRENT_VALUE=0;
        LIGHT_PROGRESS=0;
        is_First=true;
        check_Light=false;
    }

    //破關訊息
    private void CompleteDialog()
    {
        //破關成功傳回資料庫
        Intent_Game03_data();
        mgame_connect_php.check_solve_station(game_level_record_id,game_station_id,game_room_id,game_class_id,"9");

        playMusic("complete");
        builder = new AlertDialog.Builder(this);
        builder.setTitle("破關訊息");
        builder.setMessage("恭喜你!!破關成功");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exitDialog();
                showMessage(ebuilder);
            }
        });
    }

    //離開訊息
    public boolean Game03_check = false;
    private void exitDialog()
    {
        ebuilder = new AlertDialog.Builder(this);
        ebuilder.setTitle("離開訊息");
        ebuilder.setMessage("即將回主畫面");
        ebuilder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //System.exit(0);
                Game03_check = true;
                Game03.this.finish();
            }
        });
    }

    //顯示訊息
    private void showMessage(AlertDialog.Builder builder)
    {
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); //禁止點選Dialog以外的範圍
        dialog.setCancelable(false); //禁止點選back button
        dialog.show(); //顯示破關訊息
    }

    private void playMusic(String music)
    {
        if(music.equals("complete"))
        {
            sound.play(this.completeMusic,1,1,0,0,1);
        }
        else if(music.equals("light"))
        {
            sound.play(this.lightMusic,1,1,0,0,1);
        }

    }


    private SensorEventListener mySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(is_First)
            {
                is_First=false;
                FIRST_LIGHT_VALUE=event.values[0];

                //光線充足
                if(FIRST_LIGHT_VALUE>150)
                {
                    do {
                        LIGHT_MAX_VALUE=(int)(Math.random()*FIRST_LIGHT_VALUE+(FIRST_LIGHT_VALUE*2));
                        LIGHT_MIN_VALUE=(int)(Math.random()*FIRST_LIGHT_VALUE+FIRST_LIGHT_VALUE);
                    }while((LIGHT_MAX_VALUE-LIGHT_MIN_VALUE)>LIGHT_MIN_VALUE);

                }
                else
                {
                    FIRST_LIGHT_VALUE=150;
                    do {
                        LIGHT_MAX_VALUE=(int)(Math.random()*(FIRST_LIGHT_VALUE*2)+(FIRST_LIGHT_VALUE*3));
                        LIGHT_MIN_VALUE=(int)(Math.random()* FIRST_LIGHT_VALUE+(FIRST_LIGHT_VALUE*2));
                    }while((LIGHT_MAX_VALUE-LIGHT_MIN_VALUE)>LIGHT_MIN_VALUE);

                }
                maxlight.setText("最大亮度 "+LIGHT_MAX_VALUE);
                minlight.setText("最小亮度 "+LIGHT_MIN_VALUE);
            }

            //取得目前亮度
            light = event.values[0];

            mlight= Math.round((light+LIGHT_MAX_VALUE)/LIGHT_MAX_VALUE);

            //判斷亮度變化
            if(light <= LIGHT_MAX_VALUE && light >= LIGHT_MIN_VALUE)
            {
                playMusic("light");
                check_Light=true;
                clight.setText("目前亮度 "+light);
                //當螢幕亮度介於範圍之間,螢幕會持續增亮
                LIGHT_CURRENT_VALUE = LIGHT_CURRENT_VALUE + (int)mlight;
                setScreenBrightness(LIGHT_CURRENT_VALUE);
            }
            else if(light > LIGHT_MAX_VALUE)
            {
                check_Light=false;
                clight.setText("太亮了,我快瞎了！！");
            }
            else if(light < LIGHT_MIN_VALUE)
            {
                check_Light=false;
                clight.setText("太暗了,亮一點好嗎？？");
            }

            //判斷亮度是否在範圍內
            if(!check_Light)
            {
                if((LIGHT_CURRENT_VALUE-2)>0)
                {
                    LIGHT_CURRENT_VALUE = LIGHT_CURRENT_VALUE - 2 ;
                    setScreenBrightness(LIGHT_CURRENT_VALUE);
                }
            }


            //計算進度
            LIGHT_PROGRESS=Math.round((LIGHT_CURRENT_VALUE/255.0f)*100);

            //進度判斷
            if(LIGHT_PROGRESS>=98)
            {
                mCircleProgress.setValue(100);
                mySensorManager.unregisterListener(mySensorListener);
                mPlayer.pause();
                initializeLight();
                CompleteDialog();
                showMessage(builder);
            }
            else
            {
                mCircleProgress.setValue(LIGHT_PROGRESS);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPlayer.release();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("TAG","解除SensorRegister...");
        mPlayer.pause();
        //在程式關閉時移除體感(Sensor)觸發
        mySensorManager.unregisterListener(mySensorListener);
        initializeLight();
        Game03.this.finish();
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
                    Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    if(ans[0].equals("y") && ans[1].equals("1")) //0(範圍內) 1(超過範圍)
                    {
                        gema_check_handler.removeCallbacks(gema_check_runnable); //停止Timer
                        mySensorManager.unregisterListener(mySensorListener);
                        Game03.this.finish();
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
