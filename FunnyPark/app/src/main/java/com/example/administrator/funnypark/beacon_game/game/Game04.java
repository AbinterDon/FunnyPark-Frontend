package com.example.administrator.funnypark.beacon_game.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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


public class Game04 extends AppCompatActivity {

    private TextView tAns;
    private TextView tHour;
    private TextView tMinu;
    private TextView tSec;
    private TextView tAlert;
    private SensorManager mSensorManager;   //體感(Sensor)使用管理
    private Sensor mSensor;                 //體感(Sensor)類別
    private AlertDialog.Builder builder; //create AlertDialog
    private AlertDialog.Builder ebuilder; //create AlertDialog
    private SoundPool sound;
    private int completeMusic;
    private int currentMusic;
    private int correctMusic;

    private MediaPlayer mPlayer;

    private int value;  //時間位置(H:I:S)
    private float x;
    private float y;
    private long mLastUpdateTime;           //觸發時間

    //觸發間隔時間
    private static final int UPTATE_INTERVAL_TIME = 300;


    //定義時間參數(答案)
    private static int TIME_ANS_HOUR_VALUE;
    private static int TIME_ANS_MINU_VALUE;
    private static int TIME_ANS_SEC_VALUE;

    //定義時間參數(玩家)
    private static int TIME_HOUR_VALUE;
    private static int TIME_MINU_VALUE;
    private static int TIME_SEC_VALUE;

    //目前時間位置
    private static int TIME_CURRENT_TIME=0;
    private static boolean is_First_Hour=true;
    private static boolean is_First_Minu=true;
    private static boolean is_First_Sec=true;


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
    public void Intent_Game04_data()
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
                    Intent_Game04_data();
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
        setContentView(R.layout.activity_beacon_small_game04);

        //設置螢幕保持亮起
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //get object
        tAns=(TextView)findViewById(R.id.TimeAns);
        tHour=(TextView)findViewById(R.id.TimeHour);
        tMinu=(TextView)findViewById(R.id.TimeMinu);
        tSec=(TextView)findViewById(R.id.TimeSec);
        tAlert=(TextView)findViewById(R.id.txtAlert);

        sound = new SoundPool(10, AudioManager.STREAM_MUSIC,5);
        completeMusic=sound.load(this,R.raw.complete,1);
        correctMusic=sound.load(this,R.raw.correct,1);
        currentMusic=sound.load(this,R.raw.current,1);


        //GAME BGM
        mPlayer = MediaPlayer.create(this,R.raw.game04);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setLooping(true);
        mPlayer.start();

        initializeSensor();
        initializeTime();

        //設定答案字串
        tAns.setText(setTimeText(TIME_ANS_HOUR_VALUE)+" : "+setTimeText(TIME_ANS_MINU_VALUE)+" : "+setTimeText(TIME_ANS_SEC_VALUE));

        game_check("1",username,"",game_room_id);
        repeat_gema_check();

    }

    //初始化時間
    private void initializeTime()
    {
        TIME_ANS_HOUR_VALUE=(int)Math.round(Math.random()*23); //產生24小時制(Hour)
        TIME_ANS_MINU_VALUE=(int)Math.round(Math.random()*59);
        TIME_ANS_SEC_VALUE=(int)Math.round(Math.random()*59);
        TIME_HOUR_VALUE=0;
        TIME_MINU_VALUE=0;
        TIME_SEC_VALUE=0;
        TIME_CURRENT_TIME=0;
        is_First_Hour=true;
        is_First_Minu=true;
        is_First_Sec=true;
    }

    //初始化Sensor
    private void initializeSensor()
    {
        //取得體感(Sensor)服務使用權限
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        //取得手機Sensor狀態設定
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //註冊體感(Sensor)甩動觸發Listener
        mSensorManager.registerListener(SensorListener, mSensor,SensorManager.SENSOR_DELAY_GAME);

        Log.d("TAG","SensorRegister已註冊...");
    }

    //設定時間字串
    private String setTimeText(int Time)
    {
        if(Time<10)
        {
            return "0"+Time;
        }
        else
        {
            return ""+Time;
        }
    }

    //設定時間顯示樣式
    private void setTimeStyle(TextView txtView,boolean flag)
    {

        if(!flag)
        {
            txtView.setTextColor(Color.WHITE);
            txtView.setBackgroundColor(Color.RED);
        }
        else
        {
            txtView.setBackgroundColor(Color.GREEN);
        }
    }

    private void CompleteDialog()
    {
        //破關成功傳回資料庫
        Intent_Game04_data();
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
    private void exitDialog()
    {
        ebuilder = new AlertDialog.Builder(this);
        ebuilder.setTitle("離開訊息");
        ebuilder.setMessage("即將回主畫面");
        ebuilder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //System.exit(0);
                Game04.this.finish();
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
        else if(music.equals("current"))
        {
            sound.play(this.currentMusic,1,1,0,0,1);
        }
        else if(music.equals("correct"))
        {
            sound.play(this.correctMusic,1,1,0,0,1);
        }

    }

    //取得角度
    private int getY(float y)
    {
        float Min_Value=-9.81f; //負角度
        float Max_Value=9.81f; //正角度

        if(y<(Max_Value/2) && y>1.5) //一般正角度
        {
            return 1;
        }
        else if(y>=(Min_Value/2) && y<-1.5) //一般負角度
        {
            return -1;
        }
        else if(y<=Max_Value && y>(Max_Value/2)) //大正角度
        {
            return 2;
        }
        else if(y>=Min_Value && y<(Min_Value/2)) //大負角度
        {
            return -2;
        }
        else
        {
            //水平
            return 0;
        }
    }

    //註冊感應器監聽器
    private SensorEventListener SensorListener = new SensorEventListener()
    {

        //觸發SensorChanged事件
        public void onSensorChanged(SensorEvent mSensorEvent)
        {

            //當前觸發時間
            long mCurrentUpdateTime = System.currentTimeMillis();

            //觸發間隔時間 = 當前觸發時間 - 上次觸發時間
            long mTimeInterval = mCurrentUpdateTime - mLastUpdateTime;

            //若觸發間隔時間< 10000 則return;
            if (mTimeInterval < UPTATE_INTERVAL_TIME) return;

            mLastUpdateTime = mCurrentUpdateTime;

            //取得xy體感(Sensor)偏移
            x = mSensorEvent.values[0];
            y = mSensorEvent.values[1];

            //時間增加值
            value=getY(y);

            //tSec.setText("x:"+x);

            //水平判斷
            if(x<1.5 && x>-1.5)
            {
                tAlert.setText("");
                //時間位置判斷
                if(TIME_CURRENT_TIME==0) //Hour
                {
                   /* //到目前時間位置時,播放音效
                    if(is_First_Hour)
                    {
                        is_First_Hour=false;
                        playMusic("current");
                    }*/

                    tHour.setText(setTimeText(TIME_HOUR_VALUE));
                    //角度判斷
                    if(value!=0) {
                        //正常範圍值
                        if ((TIME_HOUR_VALUE + value) >= 0 && (TIME_HOUR_VALUE + value) <= 23) {
                            TIME_HOUR_VALUE = TIME_HOUR_VALUE + value;
                            setTimeStyle(tHour,false);
                        }
                        else if((TIME_HOUR_VALUE+value)<0)
                        {
                            TIME_HOUR_VALUE=23;
                        }
                        else if((TIME_HOUR_VALUE+value)>23)
                        {
                            TIME_HOUR_VALUE=0;
                        }
                    }
                    else
                    {
                        //時間判斷
                        if(TIME_HOUR_VALUE==TIME_ANS_HOUR_VALUE)
                        {
                            //時間正確時,播放音效
                            playMusic("correct");
                            TIME_CURRENT_TIME = TIME_CURRENT_TIME + 1;
                            setTimeStyle(tHour,true);
                        }
                    }

                }
                else if(TIME_CURRENT_TIME==1) //Minu
                {
                    /*//到目前時間位置時,播放音效
                    if(is_First_Minu)
                    {
                        is_First_Minu=false;
                        playMusic("current");
                    }*/

                    tMinu.setText(setTimeText(TIME_MINU_VALUE));
                    //角度判斷
                    if(value!=0) {
                        //正常範圍值
                        if ((TIME_MINU_VALUE + value) >= 0 && (TIME_MINU_VALUE + value) <= 59) {
                            TIME_MINU_VALUE = TIME_MINU_VALUE + value;
                            setTimeStyle(tMinu,false);
                        }
                        else if((TIME_MINU_VALUE+value)<0)
                        {
                            TIME_MINU_VALUE=59;
                        }
                        else if((TIME_MINU_VALUE+value)>59)
                        {
                            TIME_MINU_VALUE=0;
                        }
                    }
                    else
                    {
                        //時間判斷
                        if(TIME_MINU_VALUE==TIME_ANS_MINU_VALUE)
                        {
                            //時間正確時,播放音效
                            playMusic("correct");
                            TIME_CURRENT_TIME = TIME_CURRENT_TIME + 1;
                            setTimeStyle(tMinu,true);
                        }
                    }
                }
                else if(TIME_CURRENT_TIME==2) //Sec
                {
                    /*//到目前時間位置時,播放音效
                    if(is_First_Sec)
                    {
                        is_First_Sec=false;
                        playMusic("current");
                    }*/
                    tSec.setText(setTimeText(TIME_SEC_VALUE));
                    //角度判斷
                    if(value!=0) {
                        //正常範圍值
                        if ((TIME_SEC_VALUE + value) >= 0 && (TIME_SEC_VALUE + value) <= 59) {
                            TIME_SEC_VALUE = TIME_SEC_VALUE + value;
                            setTimeStyle(tSec,false);
                        }
                        else if((TIME_SEC_VALUE+value)<0)
                        {
                            TIME_SEC_VALUE=59;
                        }
                        else if((TIME_SEC_VALUE+value)>59)
                        {
                            TIME_SEC_VALUE=0;
                        }
                    }
                    else
                    {
                        //時間判斷
                        if(TIME_SEC_VALUE==TIME_ANS_SEC_VALUE)
                        {
                            //時間正確時,播放音效
                            playMusic("correct");
                            TIME_CURRENT_TIME = TIME_CURRENT_TIME + 1;
                            setTimeStyle(tSec,true);
                        }
                    }
                }
                else if(TIME_CURRENT_TIME==3) //破關成功
                {
                    Log.d("TAG","解除SensorRegister...");
                    //在程式關閉時移除體感(Sensor)觸發
                    mSensorManager.unregisterListener(SensorListener);
                    mPlayer.pause();
                    initializeTime();
                    CompleteDialog();
                    showMessage(builder);
                }
            }
            else
            {
                tAlert.setText("請將手機保持水平狀態");
            }


        }

        public void onAccuracyChanged(Sensor sensor , int accuracy)
        {
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
        initializeTime();
        //在程式關閉時移除體感(Sensor)觸發
        mSensorManager.unregisterListener(SensorListener);
        Game04.this.finish();
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
                        mSensorManager.unregisterListener(SensorListener);
                        Game04.this.finish();
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
