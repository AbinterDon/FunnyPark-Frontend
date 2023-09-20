package com.example.administrator.funnypark.beacon_game.Scan_data;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.BeaconManager;
import com.aprilbrother.aprilbrothersdk.Region;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;
import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.beacon_game.Before_game.game_data;
import com.example.administrator.funnypark.beacon_game.Before_game.game_room;
import com.example.administrator.funnypark.beacon_game.Before_game.own_data;
import com.example.model.util.connection_util;
import com.example.model.util.okhttp_util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;
import static java.lang.Math.pow;


public class scan extends AppCompatActivity {

    //連接資料庫
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model

    //iBeacon
    private BeaconManager beaconManager;  //iBeacon 管理
    private static final Region ALL_BEACONS_REGION = new Region("customRegionName", null, null, null); //AprilBeacon

    //儲存 Beacon 相關數據陣列 (String)
    //public static String[] beacon_UUID = new String[20];
    final String[] beacon_major = new String[20];
    final String[] beacon_minor = new String[20];
    final String[] beacon_rssi = new String[20];//距離(string)

    //Beacon(String) 轉換型態為 (*可用的數據)
    public static int[] int_major; //BeaconID(int)
    public static int[] int_minor; //BeaconID(int)
    public static float[] float_rssi ; //距離(float)

    //每位玩家Beacon的相關數據
    private SparseArray<people> mDatas = new SparseArray<>();
    //玩家個人基本資料
    private SparseArray<own_data> SparseArray_own_data;
    //遊戲規則基本資料
    private SparseArray<game_data> SparseArray_game_data = new SparseArray<>(); //(堆疊) 儲存 game_data 資料
    //呼叫 RadarViewLayout class
    private RadarViewLayout mRadarViewLayout;
    //呼叫 RadarView class
    private RadarView mRadarView ;
    //呼叫 mgame_room
    private game_connect_php mgame_connect = new game_connect_php();

    game_data game_Data = new game_data("",0,0,0,0);//遊戲規則基本資料 Info，(建構子)全部都先清空


    TextView Time_countdown , Beacon_data , Assigning_Roles_txt,devil,person,mstation;

    int beacon_size=0; //取得偵測到 Beacon 的數量
    int All_Beacon=0;//全部的玩家 + 破驛站
    int All_People=0;//全部的玩家

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_game_scan);
        dim();

        update_game_info(); //先從資料庫取得數據，並設定基本的遊戲資訊
        get_own_role();
        //assign_role();//變換掃描區中間的圖片

        Assigning_Roles_alertDialog(5); //分配角色倒數計時 AlertDialog(10s)
        get_own_roles_timer.start();//分配角色完，得到自己的角色 AlertDialog(3s)
        before_game_timer.start();//20秒後，遊戲開始
        start_scan_timer.start();//開始掃描

       //game_timer.start(); //遊戲開始倒數計時

    }

    //基本設定宣告
    private void dim()
    {
        Time_countdown =  (TextView)findViewById(R.id.Time_countdown); //倒數計時 textView
        devil = (TextView)findViewById(R.id.devil); //魔鬼人數
        person = (TextView)findViewById(R.id.person); //人類人數
        mstation = (TextView)findViewById(R.id.station); //驛站人數
    }

    //設定基本的遊戲資訊
    private void dim_game_info()
    {
        final game_data game_data = SparseArray_game_data.get(0);
        Time_countdown.setText(game_data.get_game_time());
        //設定玩家狀態列，玩家魔鬼驛站的數量
        devil.setText(String.valueOf(game_data.get_game_devil()));
        person.setText(String.valueOf(game_data.get_game_person()));
        mstation.setText(String.valueOf(game_data.get_game_station()));
        //計算 Beacon 有幾個和玩家人數有幾個，-1扣掉自己的手環
        All_People = (game_data.get_game_devil()+game_data.get_game_person())-1;
        All_Beacon = (All_People + game_data.get_game_station());
    }


    //從 Intent 接收(SparseArray)玩家基本資料
    public void Intent_own_data()
    {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        SparseArray_own_data = extras.getSparseParcelableArray("SparseArray_own_data");
    }


    //分配角色
    private void assign_role()
    {
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料過來
        final own_data own_data = SparseArray_own_data.get(0); //取得玩家基本資料

        //設定掃描區中間的圖片
        mRadarView = (RadarView) findViewById(R.id.radarView); //設定初始值

        //依據玩家的角色，變換不同的圖案
        if(own_sex.equals("101"))//人類
        {
            mRadarView.setCenterIcon(R.drawable.boy);
        }
        else if(own_sex.equals("102"))//魔鬼
        {
            mRadarView.setCenterIcon(R.drawable.devil);//設定掃描區中間的圖片
        }
    }

    //開始掃描 iBeacon
    public void Start_scan() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_BEACONS_REGION);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void Stop_scan() {
        super.onStart();
        try {
            beaconManager.stopRanging(ALL_BEACONS_REGION);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //開始持續掃描 Beacon ，並且接收數據
    private void beacon_scan()
    {
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料過來
        final own_data own_data = SparseArray_own_data.get(0); //取得玩家基本資料
        final game_data game_data = SparseArray_game_data.get(0);

        beaconManager = new BeaconManager(this);

        AprilL.enableDebugLogging(true);
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setRangingExpirationMill(30L);
        /* iBeacon 開始*/
        //掃描2秒，等待0.2秒
        beaconManager.setForegroundScanPeriod(1000, 0);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {

                get_other_role();//更新手環
                get_station();//更新驛站
                update_game_info();//更新遊戲資訊
                dim_game_info();//更新遊戲資訊


                if(game_end_check != true)
                {
                    check_game_result(own_data.get_game_room_id(),own_data.get_activity_id());
                }
                else if(game_data.get_game_station() == 0 || game_end_check == true) //如果遊戲結束，停止掃描
                {
                    Stop_scan();
                }

                if(person_dead != true)
                {
                    check_catched(own_data.get_minor()); //確認自己有沒有被捕捉
                }
                else
                {
                    Stop_scan();
                }

                int c = 0;
                String[] strs = String.valueOf(beacons).split(","); //用逗點分割字串
                Log.e("size", String.valueOf(beacons.size())); //取得搜尋到 Beacon 的數量

                beacon_size = beacons.size();

                for (int i = 0; i < beacons.size(); i++)   //尋找相關數據存入陣列
                {
                    //beacon_UUID[i] = String.valueOf(strs[1]);
                    beacon_major[i] = String.valueOf(strs[2 + c]);
                    beacon_minor[i] = String.valueOf(strs[3 + c]);
                    beacon_rssi[i] = String.valueOf(strs[5 + c]);
                    c += 6; //Beacon數據 6個一循環
                }
                parseType_beacon(beacons.size());//將字串距離(rssi) 取代並改成 float型 ， 並轉換成公尺

                initView(); //玩家基本設定
            }
        });
    }

    /*-----------------------------------parseType_beacon()--------------------------------------*/
    //將 Beacon 讀到的數據，轉換為數值，並存到陣列裡
    //傳入每位玩家的手環ID，篩選不是的手環ID
    //把傳入的手環排序好

    float [][] sort_player ;  //排序過後，搜尋到玩家的陣列
    float [][] sort_station; //排序過後，搜尋到的驛站
    float [][] All_sort_beacon; //排序過後，玩家 + 驛站

    public void parseType_beacon(int beacons_size) {

        //取得玩家自己的資料
        Intent_own_data();
        final own_data own_Data = SparseArray_own_data.get(0);
        //取得遊戲規則資料
        //Intent_game_data();
        final game_data game_data = SparseArray_game_data.get(0);

        get_other_role(); //傳入每位玩家的手環，篩選不是的手環
        get_station(); //傳入驛站
        DecimalFormat df = new DecimalFormat("######0.00");  //小數點前三位 轉換格式

        String [][] filter_beacon = new String [beacons_size][2]; //篩選 Beacon 確認的陣列
        sort_player = new float [All_People][3]; //排序過後，搜尋到玩家的陣列
        sort_station = new float [game_data.get_game_station()][3]; //TODO 要改成game_data.get_game_station()
        All_sort_beacon = new float[All_Beacon][3];//排序過後，玩家 + 驛站


        //陣列初始值為 ""
        for(int j=0 ; j<beacons_size ; j++)
        {
            for(int i=0 ; i<2 ; i++)
            {
                filter_beacon[j][i]="";
            }
        }

        //依照搜尋到 Beacon 數量，給予陣列大小
        int_major = new int[beacons_size];
        int_minor = new int[beacons_size];
        float_rssi = new float[beacons_size];

        /*------------------------把偵測到的 Beacon 處理成可用的數據，存入陣列，並且篩選不是這場遊戲手環的ID-----------------*/
        //第一次篩選 Beacon 的手環ID
        // major(String 轉 int)
        for (int i = 0; i < beacons_size; i++)
        {
            String str_major = beacon_major[i].replaceAll("[a-z]", " ").replace('=', ' ').replace(" ", ""); //取代掉 "major="

            //如果 Beacon Major 不符合玩家手環的 Major 則去除掉
            if(str_major.equals(own_Data.get_major()) || str_major.equals("2019"))  //TODO 如果有驛站編號，要改數值(改1)
            {
                int_major[i] = Integer.parseInt(str_major);   //將字串轉換成 int
            }
            else
            {
                filter_beacon[i][0] = "NO";
            }

        }

        // minor(String 轉 int)
        for (int i = 0; i < beacons_size; i++)
        {
            String str_minor = beacon_minor[i].replaceAll("[a-z]", " ").replace('=', ' ').replace(" ", ""); //取代掉 "minor="
            try{
                //如果 Beacon Minor 不符合玩家手環的 Minor 則去除掉
                for(int j=0 ; j<other_role.length ; j++)
                {
                    if(str_minor.equals(other_role[j][0]))
                    {
                        int_minor[i] = Integer.parseInt(str_minor);   //將字串轉換成 int
                        filter_beacon[i][1] = "";
                    }
                    else if(filter_beacon[i][0].equals("NO"))
                    {
                        filter_beacon[i][1] = "NO";
                    }
                }

                for(int m=0 ; m<station.length ; m++)
                {
                    if(str_minor.equals(station[m][1]))
                    {
                        int_minor[i] = Integer.parseInt(str_minor);   //將字串轉換成 int
                        filter_beacon[i][1] = "";
                    }
                    else if(filter_beacon[i][0].equals("NO"))
                    {
                        filter_beacon[i][1] = "NO";
                    }
                }
            }
            catch(Exception ex) {
                //TODO 改(嚴重)
                showTextToast("other_role.length");
            }

        }

        // rssi(String 轉 float)
        for (int i = 0; i < beacons_size; i++)
        {
            String str_rssi = beacon_rssi[i].replaceAll("[a-z]", " ").replace('=', ' ').replace(" ", "").replace("}","").replaceAll("\\]",""); //取代掉 "rssi="

            if(int_major[i]==0 || int_minor[i]==0) //如果 Major minor 其中一個為 0，則不輸入 rssi
            {
            }
            else
            {
                float_rssi[i] = Integer.parseInt(str_rssi);   //將字串轉換成 int
                //Log.e("rssi", String.valueOf(float_rssi[i]));

                //將 rssi 轉為 m
                float_rssi[i] = (float) ((abs(float_rssi[i]) - 59) / (10 * 2.0));
                float_rssi[i] = (float) pow(10, float_rssi[i]);
                float_rssi[i] = Float.parseFloat(df.format(float_rssi[i]));
            }
        }
        /*------------------------把偵測到的 Beacon 處理成可用的數據，存入陣列，並且篩選不是這場遊戲手環的ID(結束)-----------------*/

        /*--------------把偵測到的 Beacon 做由小到大排序------------------*/
        int c=0,c1=0,c2=0;

        try{
            for(int i=0 ; i<beacons_size ; i++)
            {
                if(int_major[i] != 0 && int_minor[i] != 0 && int_major[i] != 2019)
                {
                    if(own_Data.get_major().equals(String.valueOf(int_major[i])) && own_Data.get_minor().equals(String.valueOf(int_minor[i])))//如果偵測到的Beacon與自已的手環符合的話，則不顯示出小圓點
                    {
                    }
                    else
                    {
                        for(int j=0 ; j<other_role.length ; j++)
                        {
                            if(String.valueOf(int_minor[i]).equals(other_role[j][0]))
                            {
                                sort_player[c][0] = int_major[i];
                                sort_player[c][1] =int_minor[i];
                                sort_player[c][2] =float_rssi[i];
                                c++;
                            }
                        }
                    }
                }
                //篩選驛站
                else if(int_major[i] == 2019 && int_major[i] != 0 && int_minor[i] != 0)
                {
                    for(int m=0 ; m<station.length ; m++)
                    {
                        if(String.valueOf(int_major[i]).equals(station[m][0]) && String.valueOf(int_minor[i]).equals(station[m][1]))
                        {
                            for(int n=0 ; n<sort_station.length ;n++)  // 如果有重複的驛站編號，則跳過不儲存
                            {
                                if(int_minor[i] != sort_station[n][1])
                                {
                                    sort_station[c1][0] = int_major[i];
                                    sort_station[c1][1] =int_minor[i];
                                    sort_station[c1][2] =float_rssi[i];
                                    c1++;
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){

        }


        //針對玩家 Minor 做排序 (小到大)
        for(int j=0 ; j<sort_player.length-1;j++)
        {
            for(int i=0 ; i<sort_player.length-1 ;i++)
            {
                if(sort_player[i+1][1] < sort_player[i][1])
                {
                    float temp1 = sort_player[i][1];//minor
                    float temp0 = sort_player[i][0];//major
                    float temp3 = sort_player[i][2];//rssi
                    sort_player[i][1] = sort_player[i+1][1];
                    sort_player[i][0] = sort_player[i+1][0];
                    sort_player[i][2] = sort_player[i+1][2];
                    sort_player[i+1][1] = temp1;
                    sort_player[i+1][0] = temp0;
                    sort_player[i+1][2] = temp3;
                }
            }
        }

        try{
            //針對驛站 Minor 做排序 (小到大)
            for(int j=0 ; j<sort_station.length-1;j++)
            {
                for(int i=0 ; i<sort_station.length-1 ;i++)
                {
                    if(sort_station[i+1][1] < sort_station[i][1])
                    {
                        float temp1 = sort_station[i][1];//minor
                        float temp0 = sort_station[i][0];//major
                        float temp3 = sort_station[i][2];//rssi
                        sort_station[i][1] = sort_station[i+1][1];
                        sort_station[i][0] = sort_station[i+1][0];
                        sort_station[i][2] = sort_station[i+1][2];
                        sort_station[i+1][1] = temp1;
                        sort_station[i+1][0] = temp0;
                        sort_station[i+1][2] = temp3;
                    }
                }
            }
        }catch(Exception ex){
            showTextToast("驛站排序陣列出錯");
        }

        try{
            //把排序後的玩家和驛站 ， 存到一個陣列
            for(int i=0 ; i<sort_player.length ; i++)
            {
                for (int j=0 ; j<3;j++)
                {
                    All_sort_beacon[c2][j] = sort_player[i][j];
                }
                c2++;
            }

            for(int i=0 ; i<sort_station.length ; i++)
            {
                for (int j=0 ; j<3;j++)
                {
                    All_sort_beacon[c2][j] = sort_station[i][j];
                }
                c2++;
            }
        }catch(Exception ex){
            showTextToast("玩家驛站結合排序陣列出錯");
        }

        /*--------------把偵測到的 Beacon 做由小到大排序(結束)------------------*/
    }

    /* 玩家基本設定 */

    int AlertDialog_Click=100; //取得 AlertDialog 點擊要放大玩家小圓點的位置，預設100不放大任何小圓點
    /*--------------------設定每個玩家基本資料，並設置小圓點---------------------*/
    private void initView() {
        final game_data game_data = SparseArray_game_data.get(0);
        mRadarViewLayout = (RadarViewLayout) findViewById(R.id.radarViewLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
                if(game_data.get_game_station() != 0)
                {
                    mRadarViewLayout.setRole_Info(SparseArray_own_data, SparseArray_game_data, sort_player, sort_station, scan.this, own_sex); //傳玩家相關資料到 mRadarViewLayout，部屬小圓點跟遊戲規則
                }
                mRadarViewLayout.setDatas(mDatas);  //傳玩家相關資料到 mRadarViewLayout，部屬小圓點跟遊戲規則
                if(AlertDialog_Click != 100)
                {
                    mRadarViewLayout.setCurrentShowItem(AlertDialog_Click); //讓小圓點放大
                }

            }
        }, 300);
        mRadarViewLayout.delete_circleview();  //清除小圓點
    }
    /*--------------------設定每個玩家基本資料，並設置小圓點(結束)---------------------*/


    /*--------------------把玩家的 Beacon 資料儲存到 mDatas（堆疊），並設定遊戲角色---------------------*/
    private void initData() {

        //取得玩家自己的資料
        Intent_own_data();
        final own_data own_Data = SparseArray_own_data.get(0);
        get_other_role(); //傳入每位玩家的手環，設定遊戲角色

        mDatas.clear();
        int c=0;

        try{
            //判斷偵測到的 Beacon 與 玩家手環是否吻合，並且分配角色顏色
            //玩家
            for(int j=0 ; j<sort_player.length ; j++)
            {
                people people_data = new people();
                if(sort_player[j][0] != 0 && sort_player[j][1] != 0)
                {
                    people_data.setbeacon_Major((int) sort_player[j][0]);
                    people_data.setbeacon_Minor((int) sort_player[j][1]);
                    people_data.setDistance(sort_player[j][2]);  //距離
                    Log.e("rssi", String.valueOf(sort_player[j][2]));

                    //分配其他玩家的角色顏色
                    for(int i=0 ; i<other_role.length ; i++)
                    {
                        if (other_role[i][0].equals(String.valueOf((int)sort_player[j][1])))
                        {
                            if(other_role[i][1].equals("101")) //人類
                            {
                                people_data.setSex(101);
                            }
                            else if(other_role[i][1].equals("102")) //魔鬼
                            {
                                people_data.setSex(102);
                            }
                        }
                    }
                    mDatas.put(c, people_data); //設定 人與玩家數據
                    c++;
                }
            }
        }catch (Exception ex){
            showTextToast("玩家 Beacon 輸入到堆疊錯誤");
        }

        try{
            //驛站
            for(int j=0 ; j<sort_station.length ; j++)
            {
                //TODO 如果驛站為雙人，顏色要不同
                people people_data = new people();
                if(sort_station[j][0] != 0 && sort_station[j][1] != 0)
                {
                    people_data.setbeacon_Major((int) sort_station[j][0]);
                    people_data.setbeacon_Minor((int) sort_station[j][1]);
                    people_data.setDistance(sort_station[j][2]);  //距離
                    people_data.setSex(2019); //驛站 TODO 如果有驛站編號，要改數值(改4)
                    Log.e("rssi", String.valueOf(sort_station[j][2]));
                    mDatas.put(c, people_data); //設定 人與玩家數據
                    c++;
                }
            }
        }catch (Exception ex){
            showTextToast("驛站 Beacon 輸入到堆疊錯誤");
        }


    }
    /*--------------------把玩家的 Beacon 資料儲存到 mDatas（堆疊），並設定遊戲角色(結束)---------------------*/

    /* -----------------------------------分配角色倒數計時 AlertDialog (開始)------------------------------------*/

    public void Assigning_Roles_alertDialog(final int time)
    {

        View view = View.inflate(this,R.layout.activity_beacon_game_assigning_roles_timer, null);  //使用
        Assigning_Roles_txt = (TextView)view.findViewById(R.id.text1);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .setTitle("正在分配角色中")
                /*
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                */
                .create();
        alertDialog.show();

        //使用 Handler，可以動態顯示倒數計時
        final Handler Assigning_Roles_Timer_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what > 0)
                {
                    Assigning_Roles_txt.setText(String.valueOf(msg.what));
                }
                else
                {
                    //alertDialog 關閉
                    if(alertDialog!=null)
                    {
                        alertDialog.dismiss();
                    }
                }
            }
        };

        //倒數計時
        final Timer Assigning_Roles_Timer = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = time;  //秒數
            public void run() {
                if (countTime > 0) {
                    countTime--;
                }
                Message msg = new Message();
                msg.what = countTime;
                Assigning_Roles_Timer_handler.sendMessage(msg);
            }
        };
        Assigning_Roles_Timer.schedule(tt, 1000, 1000);  //倒數計時的間隔
    }

    /*----------------------------------- 分配角色倒數計時(結束)----------------------------------*/

    /* -----------------------------------得到角色倒數計時 AlertDialog (開始)------------------------------------*/

    public void get_own_roles_alertDialog()
    {
        View view = View.inflate(this,R.layout.activity_beacon_game_assigning_roles_timer, null);  //使用
        Assigning_Roles_txt = (TextView)view.findViewById(R.id.text1);

        //Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料過來
        final own_data own_data = SparseArray_own_data.get(0); //取得玩家基本資料

        if(own_sex.equals("101"))
        {
            Assigning_Roles_txt.setText("人類");
        }
        else if(own_sex.equals("102"))
        {
            Assigning_Roles_txt.setText("魔鬼");
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .setTitle("分配到的角色為")
                /*
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                */
                .create();
        alertDialog.show();

        assign_role();//得到自己的角色完，變換掃描區中間的圖片

        //計數3秒關閉
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                alertDialog.dismiss();
                t.cancel();
            }
        }, 3000);
    }

    /*----------------------------------- 得到角色倒數計時(結束)----------------------------------*/

    /* ------------------------------20s後開始破譯 AlertDialog (開始)------------------------------------*/

    public void before_game_alertDialog(final int time)
    {
        View view = View.inflate(this,R.layout.activity_before_game_timer, null);  //使用
        Assigning_Roles_txt = (TextView)view.findViewById(R.id.text1);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .setTitle("10秒後開始破譯")
                .create();
        alertDialog.show();

        //使用 Handler，可以動態顯示倒數計時
        final Handler Assigning_Roles_Timer_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what > 0)
                {
                    Assigning_Roles_txt.setText(String.valueOf(msg.what));
                }
                else
                {
                    //alertDialog 關閉
                    if(alertDialog!=null)
                    {
                        alertDialog.dismiss();
                    }
                }
            }
        };

        //倒數計時
        final Timer Assigning_Roles_Timer = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = time;  //秒數
            public void run() {
                if (countTime > 0) {
                    countTime--;
                }
                Message msg = new Message();
                msg.what = countTime;
                Assigning_Roles_Timer_handler.sendMessage(msg);
            }
        };
        Assigning_Roles_Timer.schedule(tt, 1000, 1000);  //倒數計時的間隔
    }

    /*----------------------------------- 分配角色倒數計時(結束)----------------------------------*/

    /*--------------------------------------遊戲倒數計時(開始)--------------------------------------*/

    public CountDownTimer game_timer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(millisUntilFinished > 60000)
            {
                Time_countdown.setText(((millisUntilFinished / 1000)/60) + "分" +((millisUntilFinished / 1000)-((millisUntilFinished / 1000)/60)*60) + "秒");
            }
            else if(millisUntilFinished < 60000)
            {
                Time_countdown.setText((millisUntilFinished / 1000) + "秒");
            }
        }

        //倒數時間結束
        @Override
        public void onFinish() {

        }
    };
    /*--------------------------------------遊戲倒數計時(結束)-------------------------------------*/

    /*---------------------------------分配角色完，得到角色倒數計時(開始)--------------------------------------*/

    public CountDownTimer get_own_roles_timer = new CountDownTimer(5500, 1000) { //TODO 分配角色完，得到角色時間要改13s
        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {
            dim_game_info(); //設定基本的遊戲資訊
            get_own_roles_alertDialog();
        }
    };
    /*------------------------------------分配角色完，得到角色(結束)-------------------------------------*/

    /*---------------------------------分配角色完，得到角色倒數計時(開始)--------------------------------------*/

    public CountDownTimer before_game_timer = new CountDownTimer(8500, 1000) { //TODO 分配角色完，得到角色時間要改20s
        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {
            get_other_role(); //設定其他玩家手環
            get_station();//設定驛站
            before_game_alertDialog(10);
        }
    };
    /*------------------------------------分配角色完，得到角色(結束)-------------------------------------*/

    /*---------------------------------開始掃描(遊戲開始)--------------------------------------*/

    public CountDownTimer start_scan_timer = new CountDownTimer(18500, 1000) { //TODO 分配角色完，得到角色時間要改20s
        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {
            beacon_scan(); //開始持續掃描 Beacon ，並且接收數據
            Start_scan();
        }
    };
    /*------------------------------------開始掃描(遊戲結束）-------------------------------------*/


    /*-----------------------AlertDialog Beacon偵測到的數據(major minor Rssi) --------------------*/
    SimpleAdapter adapter;  ListView beacon_msg_list;  HashMap<String, Object> map;  //Beacon AlertDialog(全域)
    public void beacon_data_msg(View v)
    {

        View view = getLayoutInflater().inflate(R.layout.activity_beacon_msg_list, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("Dialog ListView更新")
                .setView(view)
                .setNeutralButton("清除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog_Click=100;
                    }
                })
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface,
                                        int paramAnonymousInt) {
                    }

                }).create();

        beacon_msg_list = view.findViewById(R.id.beacon_msg_list);
        adapter = new SimpleAdapter(scan.this,BeaconData(),R.layout.activity_beacon_msg_list,new String[]{"major" , "minor","rssi" } , new int[]{R.id.text1 , R.id.text2,R.id.text3});
        beacon_msg_list.setAdapter(adapter);
        beacon_msg_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog_Click = position; //傳點擊位置給 mRadarViewLayout
            }
        });
        alertDialog.show();
        beacon_msg_handler.sendEmptyMessage(0);
    }

    // 儲存搜尋到 Beacon 的 major,minor,rssi 到List中
    private List<HashMap<String, Object>> BeaconData() {
        // 新增一個Lits集合 儲存 Beacon 數據
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        try{
            for (int i = 0; i < All_sort_beacon.length; i++) {
                map = new HashMap<>();
                if(All_sort_beacon[i][0] == 0 || All_sort_beacon[i][1] == 0)
                {
                }
                else
                {
                    map.put("major" , "major="+All_sort_beacon[i][0]);
                    map.put("minor" , "minor="+All_sort_beacon[i][1]);
                    map.put("rssi" , All_sort_beacon[i][2]+"m");
                    list.add(map);
                }

            }
        }catch (Exception ex) {
            showTextToast("AlertDialog Beacon偵測 錯誤");
        }
        return list;
    }

    // Handler 讓 Beacon 數據可以動態分析
    private Handler beacon_msg_handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
            adapter = new SimpleAdapter(scan.this,BeaconData(),R.layout.activity_beacon_msg_list,new String[]{"major" , "minor","rssi" } , new int[]{R.id.text1 , R.id.text2,R.id.text3});
            beacon_msg_list.setAdapter(adapter);
            beacon_msg_handler.sendEmptyMessageDelayed(100,1000);
        }
    };

    /*----------------------------AlertDialog Beacon偵測到的數據(major minor Rssi) ----------------------*/

    //禁用返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }


    /* -----------------------------從資料庫抓活動的全部手環(minor)跟玩家角色(更新)------------------------------------*/
    String [][] other_role; //取得玩家分配的角色(全域)
    public void get_other_role(){ //連接資料庫，取得其他玩家的職業
        Intent_own_data();   //取得玩家自己的資料
        final own_data own_Data = SparseArray_own_data.get(0);

        final String url = temp_url.user_role(own_Data.get_activity_id());//取得url
        // Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    //Log.e("res",responseData);

                    //拆解每位玩家分配的角色
                    final String[] String_ans = responseData.split("\\*");//分解多筆資料
                    other_role = new String[String_ans.length][2];
                    for(int j=0 ; j<String_ans.length;j++)
                    {
                        final String[] ans = String_ans[j].split(",");//分解字串
                        for(int i=0 ; i<ans.length ; i++)
                        {
                            other_role[j][i] = ans[i];
                        }
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    /* -----------------------從資料庫抓活動的全部手環(minor)跟玩家角色(更新) (結束)------------------------------------*/

    /* -----------------------------------取得驛站資訊---------------------------------------------------*/
    String [][] station; //取得玩家分配的角色(全域)
    public void get_station(){ //連接資料庫，取得遊戲基本資料
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料
        final own_data own_Data = SparseArray_own_data.get(0); //取得玩家資訊
        final String url = temp_url.get_station(own_Data.get_activity_id());
        // Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    //Log.e("res",responseData);

                    final String[] String_ans = responseData.split("\\*");//分解多筆資料

                    station = new String[String_ans.length][2];
                    for(int j=0 ; j<String_ans.length;j++)
                    {
                        final String[] ans = String_ans[j].split(",");//分解字串
                        for(int i=0 ; i<ans.length ; i++)
                        {
                            station[j][i] = ans[i];
                        }
                    }

                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    /* ---------------------------------------------取得驛站資訊(結束)---------------------------------------------*/

    /* --------------------------------------如果沒有取到遊戲基本資料，在這邊重新取得-------------------------------------------*/
    public void update_game_info()
    {
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料
        final own_data own_Data = SparseArray_own_data.get(0); //取得玩家資訊

        final String url = temp_url.setting_basic_game_info(own_Data.get_game_room_id());//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("resssss",responseData);
                    try{
                        if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                            if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                final String[] ans = responseData.split(",");//分解字串

                                if(ans[0].equals("y"))
                                {
                                    game_Data.set_game_time(ans[1]); //設定遊戲時間
                                    game_Data.set_game_devil(Integer.parseInt(ans[2]));//設定魔鬼數量
                                    game_Data.set_game_person(Integer.parseInt(ans[3]));//設定人類數量
                                    game_Data.set_game_station(Integer.parseInt(ans[4]));//設定總破譯站數量
                                    game_Data.set_game_qua_station(Integer.parseInt(ans[5]));//設定需破譯數量
                                    SparseArray_game_data.put(0,game_Data); //儲存遊戲規則到 SparseArray_game_Data 堆疊裡
                                }
                            }else{//讀取資料失敗 顯示對應資訊
                                String[] ans = responseData.split(",");
                                Toast.makeText(scan.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                            }
                        }else {//網路連線失敗
                            Toast.makeText(scan.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                            //顯示Toast
                        }
                    }catch (Exception e){
                        Toast.makeText(scan.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                        scan.this.finish();
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }
    /* ------------------------------------如果沒有取到遊戲基本資料，在這邊重新取得(結束)------------------------------------*/

    /* ---------------------------------從資料庫抓自己的角色職業------------------------------------*/

    String own_sex; //取得自己的職業
    public void get_own_role(){ //連接資料庫，取得自己角色職業
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料
        final own_data own_Data = SparseArray_own_data.get(0); //取得玩家資訊

        final String url = temp_url.get_own_role(own_Data.get_user_name());//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    //Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    own_sex = ans[0];

                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    /* -----------------------------------從資料庫抓自己的角色職業(結束)------------------------------------*/

    /* -----------------------------------------遊戲結果(一直重複)---------------------------------------------------------*/
    boolean game_end_check=false;
    public void check_game_result(String game_room_id , String activity_id)
    {
        final game_data game_data = SparseArray_game_data.get(0);
        final String url = temp_url.check_game_result(game_room_id,activity_id);//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    //判斷玩家是否已準備，開啟藍牙
                    if(ans[0].equals("y"))
                    {
                        game_end_check = true;
                        Intent game_end = new Intent(scan.this,game_end.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("game_who_win",ans[1]);
                        bundle.putString("survive_person", String.valueOf(game_data.get_game_person()));
                        bundle.putString("game_station", String.valueOf(game_data.get_game_station()));
                        bundle.putString("game_time", String.valueOf(game_data.get_game_time()));
                        game_end.putExtras(bundle);
                        scan.this.startActivity(game_end);
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }
    /* --------------------------------------遊戲結果(結束)---------------------------------------------------*/

    /* -----------------------------------------確認自己有沒有被捕捉(魔鬼)(一直重複)------------------------------*/
    boolean person_dead = false;
    public void check_catched(String own_minor)
    {
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料過來
        final own_data own_data = SparseArray_own_data.get(0); //取得玩家基本資料
        final String url = temp_url.check_catched(own_minor);//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    //判斷玩家是否已準備，開啟藍牙
                    if(ans[0].equals("y"))
                    {
                        person_dead = true;
                        Intent person_dead = new Intent(scan.this,person_dead.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("person","已被捕捉");
                        bundle.putString("game_room_id",own_data.get_game_room_id());
                        person_dead.putExtras(bundle);
                        scan.this.startActivity(person_dead);
                        //Log.e("已捕捉","已捕捉"); //TODO 已捕捉之後的畫面
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    /* -----------toast-----------*/
    private Toast toast = null;
    private void showTextToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(scan.this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
    /* -----------toast(結束)-----------*/

}
