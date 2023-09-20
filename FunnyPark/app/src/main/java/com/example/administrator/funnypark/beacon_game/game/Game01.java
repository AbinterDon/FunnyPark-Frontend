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
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.beacon_game.Scan_data.game_connect_php;
import com.example.model.util.connection_util;
import com.example.model.util.okhttp_util;

import me.itangqi.waveloadingview.WaveLoadingView;

public class Game01 extends AppCompatActivity {

    private SensorManager mSensorManager;   //體感(Sensor)使用管理
    private Sensor mSensor;                 //體感(Sensor)類別
    private float mLastX;                    //x軸體感(Sensor)偏移
    private float mLastY;                    //y軸體感(Sensor)偏移
    private float mLastZ;                    //z軸體感(Sensor)偏移
    private double mSpeed;                 //甩動力道數度
    private long mLastUpdateTime;           //觸發時間
    private WaveLoadingView waveView;
    private AlertDialog.Builder builder; //create AlertDialog
    private AlertDialog.Builder ebuilder; //create AlertDialog
    private SoundPool sound;
    private int gameMusic;
    private int waterMusic;
    private int completeMusic;
    private MediaPlayer mPlayer;

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    private game_connect_php mgame_connect_php = new game_connect_php();

    //private AudioManager am;

    //甩動力道數度設定值 (數值越大需甩動越大力，數值越小輕輕甩動即會觸發)
    //private static final int SPEED_SHRESHOLD = 7000;
    private static final int SPEED_SHRESHOLD = 1;

    //觸發間隔時間
    private static final int UPTATE_INTERVAL_TIME = 70;

    //遊戲結束值
    //以亂數設定值
    private static int GAME_END_VALUE = (int)(Math.random()*500000+500000);

    //遊戲搖動值
    private static int GAME_CURRENT_VALUE = 0;

    //遊戲進度
    private static int GAME_PROGRESS = 0;

    //遊戲成功後，需傳給後台的數據
    String username;
    String game_level_record_id;
    String game_station_id;
    String game_room_id;
    String game_class_id;

    //從 Intent 接收(SparseArray)玩家基本資料
    public void Intent_Game01_data()
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
                    Intent_Game01_data();
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
        setContentView(R.layout.activity_beacon_small_game01);

        //設置螢幕保持亮起
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Get waveLoadingView Value
        waveView=(WaveLoadingView)findViewById(R.id.waveLoadingView);
        waveView.setProgressValue(0);

        sound = new SoundPool(10, AudioManager.STREAM_MUSIC,5);
        completeMusic=sound.load(this,R.raw.complete,1);
        waterMusic=sound.load(this,R.raw.water,5);
        //gameMusic=sound.load(this,R.raw.h1,1);

        //GAME BGM
        mPlayer = MediaPlayer.create(this,R.raw.game01);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setLooping(true);
        mPlayer.start();

        //playMusic("complete");

        initializeGame();
        initializeSensor();

        game_check("1",username,"",game_room_id);
        repeat_gema_check();

    }


    private void initializeGame()
    {
        GAME_CURRENT_VALUE=0;
        GAME_PROGRESS=0;
        GAME_END_VALUE = (int)(Math.random()*500000+500000);
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

    //破關訊息
    private void CompleteDialog()
    {
        //破關成功傳回資料庫
        Intent_Game01_data();
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
    public void exitDialog()
    {
        ebuilder = new AlertDialog.Builder(this);
        ebuilder.setTitle("離開訊息");
        ebuilder.setMessage("即將回主畫面");
        ebuilder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //System.exit(0);
                Game01.this.finish();
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
        else if(music.equals("water"))
        {
            sound.play(this.waterMusic,1,1,0,0,1);
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

            //若觸發間隔時間< 70 則return;
            if (mTimeInterval < UPTATE_INTERVAL_TIME) return;

            mLastUpdateTime = mCurrentUpdateTime;

            //取得xyz體感(Sensor)偏移
            float x = mSensorEvent.values[0];
            float y = mSensorEvent.values[1];
            float z = mSensorEvent.values[2];

            //甩動偏移速度 = xyz體感(Sensor)偏移 - 上次xyz體感(Sensor)偏移
            float mDeltaX = x - mLastX;
            float mDeltaY = y - mLastY;
            float mDeltaZ = z - mLastZ;

            mLastX = x;
            mLastY = y;
            mLastZ = z;

            //體感(Sensor)甩動力道速度公式
            mSpeed = Math.sqrt(mDeltaX * mDeltaX + mDeltaY * mDeltaY + mDeltaZ * mDeltaZ)/ mTimeInterval * 10000;

            //若體感(Sensor)甩動速度大於等於甩動設定值則進入 (達到甩動力道及速度)
            if (mSpeed >= SPEED_SHRESHOLD)
            {
                playMusic("water");
                GAME_CURRENT_VALUE=GAME_CURRENT_VALUE+(int)Math.round(mSpeed);

                GAME_PROGRESS=Math.round((GAME_CURRENT_VALUE/(float)GAME_END_VALUE)*100);

                //達到搖一搖甩動後要做的事情
                Log.d("TAG","搖一搖中...");
            }

            //進度判斷
            if(GAME_PROGRESS>=99)
            {
                //完成時為100%
                waveView.setProgressValue(100);
                waveView.setCenterTitle(String.format("%d%%",100));
                Log.d("TAG","解除SensorRegister...");
                //在程式關閉時移除體感(Sensor)觸發
                mSensorManager.unregisterListener(SensorListener);
                mPlayer.pause();
                initializeGame();
                CompleteDialog();
                showMessage(builder);
            }
            else
            {
                waveView.setProgressValue(GAME_PROGRESS);
                waveView.setCenterTitle(String.format("%d%%",GAME_PROGRESS));

            }

//            txtAX.setText("X="+mLastX);
//           txtAY.setText("Y="+mLastY);
//            txtAZ.setText("Z="+mLastZ);
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
        initializeGame();
        //在程式關閉時移除體感(Sensor)觸發
        mSensorManager.unregisterListener(SensorListener);
        Game01.this.finish();
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
                        mSensorManager.unregisterListener(SensorListener);
                        Game01.this.finish();
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
