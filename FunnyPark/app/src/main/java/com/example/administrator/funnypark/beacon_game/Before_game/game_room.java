package com.example.administrator.funnypark.beacon_game.Before_game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.beacon_game.Scan_data.bluetooth;
import com.example.administrator.funnypark.beacon_game.Scan_data.scan;
import com.example.administrator.funnypark.ticket.ticket_information;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.util.ArrayList;
import java.util.List;

public class game_room extends AppCompatActivity {

    //取得玩家的大頭貼
    //BeaconID 驗證為1
    //不能返回到連接手環的畫面

    private interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件before_connect_watch
    final okhttp_util client = new okhttp_util();//實體化 抓model
    final loading_util loading = new loading_util();//實體化 抓model

    private SparseArray<own_data> SparseArray_own_data; //玩家基本資料
    ImageView activity_img,activity_icon;
    TextView activity_title;

    Button ready_game , create_game_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_game_room);
        dim();
        game_room_info(game_room.this); //進入遊戲等待室
        repeat_game_room_php();//重複執行更新，等待玩家進入 game_room
        repeat_waiting_start_php();//重複執行更新，等待每個玩家按下準備，都按下後，會直接進入遊戲內 TODO 測試
    }

    private void dim()
    {
        ready_game = (Button) findViewById(R.id.ready_game);
        create_game_btn = (Button) findViewById(R.id.create_game_btn);
        activity_img = (ImageView) findViewById(R.id.activity_img);
        activity_icon = (ImageView)findViewById(R.id.activity_icon);
        activity_title = (TextView) findViewById(R.id.activity_title);
    }


    //從 Intent 接收(SparseArray)玩家基本資料
    private void Intent_own_data()
    {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        SparseArray_own_data = extras.getSparseParcelableArray("SparseArray_own_data");
    }

    //重複執行更新，等待玩家進入 game_room
    private Handler game_room_handler = new Handler();
    private Runnable game_room_runnable;
    private void repeat_game_room_php()
    {
        game_room_runnable = new Runnable() {
            public void run() {
                try
                {
                    game_room_info(game_room.this); //進入遊戲等待室
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    //重複執行更新，等待每個玩家按下準備，都按下後，會直接進入遊戲內
    //要先執行副程式，waiting_start_handler.postDelayed(waiting_start_runnable, 3500) 才會到這
    private Handler waiting_start_handler = new Handler();
    private Runnable waiting_start_runnable;
    private void repeat_waiting_start_php()
    {
        waiting_start_runnable = new Runnable() {
            public void run() {
                try
                {
                    waiting_start(); //等待玩家都按下準備
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    boolean start_game = false;
    public void start_bluetooth(View view)
    {
        if(start_game == false)
        {
            final bluetooth mbluetooth = new bluetooth(); //藍牙權限
            mbluetooth.turnOffBluetooth(); // 強制關閉藍牙
            mbluetooth.requestLocationPermission(game_room.this);// 開啟藍牙(Android 6.0以上)
            mbluetooth.checkPermission(game_room.this);  //開啟定位(Android 6.0以上)
            start_game = true;
            ready_game.setText("準備遊戲");
            create_game_btn.setEnabled(true);
        }
        else
        {
            Ready();
        }
    }

    /* ---------------------------------從資料庫取得玩家的 姓名與頭貼-------------------------------------------*/
    public void game_room_info(final Context context)//連接資料庫，取得玩家的基本資料
    {

        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料
        final own_data own_Data = SparseArray_own_data.get(0); //取得玩家資訊

        final String url = temp_url.game_room_info(own_Data.get_game_room_id());//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("ressssssss",responseData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        final String[] String_ans = responseData.split("\\*");//分解多筆資料
                                        //final ArrayList<String> temp_id = new ArrayList<>();
                                        final ArrayList<String> temp_name = new ArrayList<>();
                                        final ArrayList<String> temp_account = new ArrayList<>();
                                        final ArrayList<String> temp_img = new ArrayList<>();
                                        String[] temp_activity = String_ans[0].split(",");//分解字串
                                        com.example.model.util.interface_util interface_util = new interface_util();
                                        interface_util.set_img(temp_url.get_url_img() + temp_activity[1], activity_img,game_room.this);
                                        interface_util.set_img(temp_url.get_url_img() + temp_activity[2], activity_icon,game_room.this);
                                        activity_title.setText(temp_activity[3]);
                                        for(int j=1 ; j<String_ans.length;j++)
                                        {
                                            String[] ans = String_ans[j].split(",");//分解字串
                                            temp_img.add(temp_url.get_url_img() + ans[2]);
                                            temp_name.add(ans[1]);
                                            temp_account.add("xxxx@gmail.com");
                                        }
                                        //開始介面實現
                                        game_room.MyAdapter myAdapter = new game_room.MyAdapter(temp_name,temp_account, temp_img);
                                        final LinearLayoutManager layoutManager = new LinearLayoutManager(game_room.this);//getActivity()
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                        RecyclerView mList = game_room.this.findViewById(R.id.game_room_list);
                                        mList.setLayoutManager(layoutManager);
                                        mList.setHasFixedSize(true);
                                        mList.setAdapter(myAdapter);
                                        //end

                                        //玩家人數到齊，進入遊戲
                                        if(String_ans[0].equals("3")) //TODO 玩家人數要變動(改)
                                        {
                                            game_room_handler.removeCallbacks(game_room_runnable); //停止Timer
                                        }
                                        else
                                        {
                                            //如果玩家人數還沒到齊，則繼續更新 PHP
                                            game_room_handler.postDelayed(game_room_runnable, 6500);
                                        }
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(game_room.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(game_room.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(game_room.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                game_room.this.finish();
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
    /* ---------------------------------從資料庫取得玩家的 姓名與頭貼(結束)-------------------------------------------*/

    /* ---------------------------------遊戲準備，開啟藍牙---------------------------------------------------------*/
    public void Ready()
    {
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料
        final own_data own_Data = SparseArray_own_data.get(0); //取得玩家資訊
        final String url = temp_url.check_game_attend(own_Data.get_user_name(),own_Data.get_activity_id(),"9");//取得url
        //loading.loading_open(game_room.this);
        // Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    try{
                        if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                            if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                //Log.e("res",responseData);
                                final String[] ans = responseData.split(",");//分解字串

                                //判斷玩家是否已準備，開啟藍牙
                                if(ans[0].equals("9"))
                                {
                                    ready_game.setText("準備成功");
                                    //waiting_start();
                                }
                            }else{//讀取資料失敗 顯示對應資訊
                                String[] ans = responseData.split(",");
                                Toast.makeText(game_room.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                            }
                        }else {//網路連線失敗
                            Toast.makeText(game_room.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                            //顯示Toast
                        }
                    }catch (Exception e){
                        Toast.makeText(game_room.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                        game_room.this.finish();
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    /* ---------------------------------遊戲準備，開啟藍牙(結束)------------------------------------*/



    /* ------------------------等待大家按下準備，如果都準備好了，同時進入遊戲------------------------------------*/

    public void waiting_start(){ //連接資料庫，取得遊戲基本資料
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料
        final own_data own_Data = SparseArray_own_data.get(0); //取得玩家資訊
        final String url = temp_url.waiting_started(own_Data.get_game_room_id(),own_Data.get_activity_id());
        //Log.e("url",url);

        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    //loading.loading_close();
                    Log.e("res",responseData);
                    try{
                        if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                            if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                final String[] ans = responseData.split(",");//分解字串
                                if(ans[0].equals("y"))
                                {
                                    waiting_start_handler.removeCallbacks(waiting_start_runnable); //停止Timer
                                    jump_game_escape();
                                }
                                else if(ans[0].equals("n"))
                                {
                                    //如果有玩家還沒有按下準備，則繼續更新 PHP
                                    waiting_start_handler.postDelayed(waiting_start_runnable, 3500);
                                }
                            }else{//讀取資料失敗 顯示對應資訊
                                String[] ans = responseData.split(",");
                                Toast.makeText(game_room.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                            }
                        }else {//網路連線失敗
                            Toast.makeText(game_room.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                            //顯示Toast
                        }
                    }catch (Exception e){
                        Toast.makeText(game_room.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                        game_room.this.finish();
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", e.getMessage());//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    //建遊戲室
    public void create_game(View view)
    {
        Intent_own_data();//從 Intent 接收(SparseArray)玩家基本資料
        final own_data own_Data = SparseArray_own_data.get(0); //取得玩家資訊
        final String url = temp_url.system_check_game_start(own_Data.get_user_name(),own_Data.get_activity_id(),own_Data.get_game_room_id());
        //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("res",responseData);
                    try{
                        if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                            if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                final String[] ans = responseData.split(",");//分解字串
                                if(ans[0].equals("y"))
                                {
                                    waiting_start();
                                    create_game_btn.setText("進入成功");
                                    create_game_btn.setEnabled(false);
                                }
                                else
                                {
                                    waiting_start();
                                    create_game_btn.setText("等待中...");
                                    create_game_btn.setEnabled(false);
                                }
                            }else{//讀取資料失敗 顯示對應資訊
                                String[] ans = responseData.split(",");
                                Toast.makeText(game_room.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                            }
                        }else {//網路連線失敗
                            Toast.makeText(game_room.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                            //顯示Toast
                        }
                    }catch (Exception e){
                        Toast.makeText(game_room.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                        game_room.this.finish();
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", e.getMessage());//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }

    /* -----------------------------------等待大家按下準備，如果都準備好了，同時進入遊戲(結束)------------------------------------*/

    //跳轉到遊戲
    public void jump_game_escape(){
        Intent jump = new Intent(game_room.this,scan.class);
        //傳遞(SparseArray)玩家基本資料到 game_room
        Bundle bundle = new Bundle();
        bundle.putSparseParcelableArray("SparseArray_own_data",SparseArray_own_data);  //玩家自己的基本資料
        jump.putExtras(bundle);
        startActivity(jump);
    }

    public void before_connect_watch(View view)
    {
       finish();
    }

    //Recyler
    public class MyAdapter extends RecyclerView.Adapter<game_room.MyAdapter.ViewHolder> {
        //private List<String> temp_id;
        private List<String> temp_name;
        private List<String> temp_account;
        private List<String> temp_img;
        //private List<String> temp_exchange_status;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView user_name;
            public TextView user_account;
            public CardView user_cardview ;
            public ImageView user_img;

            public ViewHolder(View v) {
                super(v);
                user_cardview = (CardView) v.findViewById(R.id.game_player_cardview);
                user_name = (TextView) v.findViewById(R.id.user_name);
                user_account = (TextView) v.findViewById(R.id.user_account);
                user_img = (ImageView) v.findViewById(R.id.user_img);
            }
        }

        public MyAdapter(List<String> name,
                         List<String> account,
                         List<String> img) {
            temp_name = name;
            temp_account = account;
            temp_img = img;
        }

        @Override
        public game_room.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_before_game_room_style, parent, false);
            game_room.MyAdapter.ViewHolder vh = new game_room.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(game_room.MyAdapter.ViewHolder holder, final int position) {
            //Log.e("123",temp_title.get(position));
            holder.user_name.setText(temp_name.get(position));//商品名稱
            holder.user_account.setText(temp_account.get(position));//商品兌換日期
            interface_util.set_img(temp_img.get(position),holder.user_img,game_room.this);//商品圖片
        }

        @Override
        public int getItemCount() {
            return temp_name.size();
        }


    }
}
