package com.example.administrator.funnypark.subject_park;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.BeaconManager;
import com.aprilbrother.aprilbrothersdk.Region;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;
import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.mall.platform_product_exchange;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.text.DecimalFormat;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class subject_park_mobile_guide extends AppCompatActivity {

    ImageView Guide_img;
    TextView Guide_txt;
    String park_id;//園區id

    //儲存 Beacon 相關數據陣列 (String)
    final String[] beacon_major = new String[20];
    final String[] beacon_minor = new String[20];
    final String[] beacon_rssi = new String[20];//距離(string)

    Button btn;

    //Beacon(String) 轉換型態為 (*可用的數據)
    public static int[] int_major; //BeaconID(int)
    public static int[] int_minor; //BeaconID(int)
    public static float[] float_rssi; //距離(float)

    //iBeacon
    private BeaconManager beaconManager;  //iBeacon 管理
    private static final Region ALL_BEACONS_REGION = new Region("customRegionName", null, null, null); //AprilBeacon

    //連線
    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_park_mobile_guide);
        Dim();
        beacon_scan();

        Intent jump = getIntent();
        park_id = jump.getStringExtra("park_id");
        Log.e("park_id",park_id);
        btn.setEnabled(false);

        //開起藍牙定位權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    private void Dim() {//定義
        Guide_img = (ImageView) findViewById(R.id.Guide_img);//園區名稱
        Guide_txt = (TextView) findViewById(R.id.Guide_txt);//園區地圖
        btn = (Button)findViewById(R.id.btn_initiate);//get parkcoin
    }

    //開始掃描 iBeacon
    @Override
    protected void onStart() {
        super.onStart();
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

    //停止掃描 Beacon
    @Override
    protected void onStop() {
        super.onStop();
        try {
            beaconManager.stopRanging(ALL_BEACONS_REGION);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //開始持續掃描 Beacon ，並且接收數據
    int beacon_size = 0; //取得偵測到 Beacon 的數量

    private void beacon_scan() {
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
            }
        });
    }


    String[][] Guide_beacon = new String[5][3];

    public void parseType_beacon(int beacons_size) {

        DecimalFormat df = new DecimalFormat("######0.00");  //小數點前三位 轉換格式

        //依照搜尋到 Beacon 數量，給予陣列大小
        int_major = new int[beacons_size];
        int_minor = new int[beacons_size];
        float_rssi = new float[beacons_size];

        /*------------------------把偵測到的 Beacon 處理成可用的數據，存入陣列-----------------*/
        //第一次篩選 Beacon 的手環ID
        // major(String 轉 int)
        for (int i = 0; i < beacons_size; i++) {
            String str_major = beacon_major[i].replaceAll("[a-z]", " ").replace('=', ' ').replace(" ", ""); //取代掉 "major="
            if (str_major.equals("2"))  //TODO 如果有驛站編號，要改數值(改1)
            {
                int_major[i] = Integer.parseInt(str_major);   //將字串轉換成 int
            } else {
                int_major[i] = 0;   //將字串轉換成 int
            }
        }

        // minor(String 轉 int)
        for (int i = 0; i < beacons_size; i++) {
            String str_minor = beacon_minor[i].replaceAll("[a-z]", " ").replace('=', ' ').replace(" ", ""); //取代掉 "minor="
            if (int_major[i] != 0) {
                int_minor[i] = Integer.parseInt(str_minor);   //將字串轉換成 int
            } else {
                int_minor[i] = 0;   //將字串轉換成 int
            }
        }

        // rssi(String 轉 float)
        for (int i = 0; i < beacons_size; i++) {
            String str_rssi = beacon_rssi[i].replaceAll("[a-z]", " ").replace('=', ' ').replace(" ", "").replace("}", "").replaceAll("\\]", ""); //取代掉 "rssi="

            if (int_major[i] != 0) {
                float_rssi[i] = Integer.parseInt(str_rssi);   //將字串轉換成 int
            }

            //將 rssi 轉為 m
            float_rssi[i] = (float) ((abs(float_rssi[i]) - 59) / (10 * 2.0));
            float_rssi[i] = (float) pow(10, float_rssi[i]);
            float_rssi[i] = Float.parseFloat(df.format(float_rssi[i]));
        }

        int c = 0;
        for (int i = 0; i < beacons_size; i++) {
            if (int_major[i] != 0 && float_rssi[i] < 1) {
                Guide_beacon[c][0] = String.valueOf(int_major[i]);
                Guide_beacon[c][1] = String.valueOf(int_minor[i]);
                Guide_beacon[c][2] = String.valueOf(float_rssi[i]);
                c++;
                create_guide();
            }
        }

    }
    /*------------------------把偵測到的 Beacon 處理成可用的數據，存入陣列(結束)-----------------*/


    private void create_guide() {
        final String url = (String) temp_url.get_park_guide(park_id, Guide_beacon[0][1]);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    subject_park_mobile_guide.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if (ans[0].indexOf("error") < 0) {//讀取資料有無失敗
                                        btn.setEnabled(true);
                                        interface_util.set_img(temp_url.get_url_img() + ans[2], Guide_img, subject_park_mobile_guide.this);
                                        Guide_txt.setText(ans[0] + "\n" + ans[1]);
                                    } else {//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(subject_park_mobile_guide.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                } else {//網路連線失敗
                                    Toast.makeText(subject_park_mobile_guide.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();//顯示Toast
                                }
                                /*
                                if(responseData.equals("此景點已領過獎勵")){
                                    btn.setEnabled(false);
                                }else{

                                }
                                 */
                            } catch (Exception e) {
                                Toast.makeText(subject_park_mobile_guide.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                subject_park_mobile_guide.this.finish();
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


    public void finish_this_page(View v) {
        this.finish();
    }

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    //開起藍牙定位權限
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                }
                break;
        }
    }


     /* 連線用代碼 */
    public void btn_initiate(View view)
    {
        String user_mail;//使用者登入帳號
        user_mail = file_util.Read_loginInfo_account_Value(subject_park_mobile_guide.this);//取得登入會員信箱
        final connection_util temp_url = new connection_util();//實體化url物件
        final okhttp_util client = new okhttp_util();//實體化 抓model
        final String url = temp_url.get_parkcoin(park_id, Guide_beacon[0][1],user_mail);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(subject_park_mobile_guide.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    subject_park_mobile_guide.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].indexOf("error")<0){//讀取資料有無失敗
                                        btn.setEnabled(false);
                                        Toast.makeText(subject_park_mobile_guide.this, "獲得1點ParkCoin成功。", Toast.LENGTH_LONG).show();//顯示Toast
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(subject_park_mobile_guide.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(subject_park_mobile_guide.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(subject_park_mobile_guide.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                subject_park_mobile_guide.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(subject_park_mobile_guide.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end

    }

}

