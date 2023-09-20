package com.example.administrator.funnypark.beacon_game.Scan_data;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.beacon_game.Before_game.game_data;
import com.example.administrator.funnypark.beacon_game.Before_game.own_data;
import com.example.administrator.funnypark.beacon_game.game.Game01;
import com.example.administrator.funnypark.beacon_game.game.Game03;
import com.example.administrator.funnypark.beacon_game.game.Game04;
import com.example.administrator.funnypark.beacon_game.game.Game05;
import com.example.administrator.funnypark.beacon_game.game.beacon_small_game_loading;
import com.example.model.util.connection_util;
import com.example.model.util.okhttp_util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RadarViewLayout extends ViewGroup implements RadarView.OnScanningListener {
    //寬
    private int  mWidth;
    //高
    private int  mHeight;
    //數據
    private SparseArray<people> mDatas;
    //玩家個人基本資料
    private SparseArray<own_data> SparseArray_own_data;
    //遊戲規則基本資料
    private SparseArray<game_data> SparseArray_game_data;
    //紀錄item所在的掃描位置角度
    private SparseArray<Float> scanAngleList = new SparseArray<>();
    //最小距離的item所在數據源中的位置
    private int minItemPosition;
    //當前展示的item
    private RadarCircleView currentShowChild;
    //最小距離的item
    private RadarCircleView minShowChild;

    private RadarViewLayout mRadarViewLayout;

    private game_connect_php mgame_connect_php = new game_connect_php();

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model

    Context scan_context;

    float [] Angle = {30,60,90,120,150,180,210,240,270,300,330,360}; //圓點放置角度

    public RadarViewLayout(Context context) {
        this(context,null);
    }

    public RadarViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RadarViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidth = mHeight = Math.min(mWidth, mHeight);
        //测量每个children
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getId() == R.id.radarView) {
                //為雷達掃描圖設置需要的屬性
                ((RadarView) child).setOnScanningListener(this);
                //考慮到數據沒有添加前掃描圖在掃描，但是不會開始為CircleView佈局
                if (mDatas != null && mDatas.size() > 0) {
                    ((RadarView) child).setMaxScanItemCount(mDatas.size());
                    ((RadarView) child).startScan();
                }
                continue;
            }
        }
    }

    //測量寬高 預設400
    private int measureSize(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 400;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed,int l, int t, int r, int b) {
        int childCount = getChildCount();
        int random  ;
        int c=0;

        /**
         * 首先放置扫描view的位置
         */
        View view = findViewById(R.id.radarView);
        if (view != null) {
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        //放置雷达图中需要展示的item圆点
        for (int i = 0; i < childCount; i++) {
            final int j = i;
            final View child = getChildAt(i);

            //如果是扫描view直接忽略
            if (child.getId() == R.id.radarView) {
                continue;
            }

            //設置CircleView小圓點的坐標信息
            //坐標 = 旋轉角度 * 半徑 * 根據遠近距離的不同計算得到的應該佔的半徑比例
            if(child instanceof RadarCircleView){
                RadarCircleView radarCircleView=(RadarCircleView) child;

                //設置為0 不讓他溢位
                if(scanAngleList.get(i - 1) != null)
                {
                    c++;

                    if(c <= mDatas.size())
                    {
                        //Log.e("scanAngleList", String.valueOf(scanAngleList.get(i - 1)));
                        // Log.e("XXXXXX", String.valueOf(Math.cos(Math.toRadians(scanAngleList.get(i - 1))) * radarCircleView.getProportion() * mWidth / 2));
                        // Log.e("YYYYYY", String.valueOf(Math.sin(Math.toRadians(scanAngleList.get(i - 1))) * radarCircleView.getProportion() * mWidth / 2));

                        //輸入座標
                        radarCircleView.setDisX((float) (Math.cos(Math.toRadians(scanAngleList.get(i - 1))) * radarCircleView.getProportion() * mWidth / 2));
                        radarCircleView.setDisY((float) (Math.sin(Math.toRadians(scanAngleList.get(i - 1))) * radarCircleView.getProportion() * mWidth / 2));


                        //放置Circle小圆点
                        child.layout((int)radarCircleView.getDisX() + mWidth / 2, (int) radarCircleView.getDisY() + mHeight / 2,
                                (int) radarCircleView.getDisX() + child.getMeasuredWidth() + mWidth / 2,
                                (int)radarCircleView.getDisY() + child.getMeasuredHeight() + mHeight / 2);
                    }

                }
            }
        }
    }

    //設定小圓點的距離，顏色，並且觸發遊戲
    public void setDatas(SparseArray<people> mDatas) {
        this.mDatas = mDatas;
        own_data own_Data = SparseArray_own_data.get(0);//取得玩家基本資料
        game_data game_data = SparseArray_game_data.get(0); //取得遊戲規則資料

        int random  ;
        random = (int)(Math.random()*12);

        //設定 自己 與 其他玩家的距離
        float min = 1.0f; //(m)
        float max = 50.0f;  //(m)

        //找到距離的最大值，最小值對應的 minItemPosition
        for (int j = 0; j <  mDatas.size(); j++) //有幾個人 mDatas.size()
        {
            people item = mDatas.get(j);
            if (item.getDistance() < min)
            {
                min = (float) item.getDistance();
                minItemPosition = j;
            }
            if (item.getDistance() > max)
            {
                max = (float) item.getDistance();
            }

            if(scanAngleList.get(j) == null)  //如果 Beacon 有偵測到，角度卻為0，則給他隨機一個角度
            {
                scanAngleList.put(j , Angle[random]);
                for(int i=0 ; i<scanAngleList.size() ; i++)
                {
                    if(scanAngleList.get(j) == scanAngleList.get(i))
                    {
                        scanAngleList.put(j , Angle[random]);
                    }
                }

            }

        }

        //根據 職業不同 背景不同顏色
        for (int i = 0; i < mDatas.size(); i++) {

            people item = mDatas.get(i);

            RadarCircleView circleView = new RadarCircleView(getContext());
            if (item.getSex()==101) //人類
            {
                circleView.setPaintColor(Color.parseColor("#5893d4"));
            }
            else if (item.getSex()==102) //魔鬼
            {
                circleView.setPaintColor(Color.parseColor("#b7001b"));
            }
            else if (item.getSex()==2019) //驛站
            {
                circleView.setPaintColor(Color.parseColor("#f9fd50"));
            }

            //根據遠近距離的不同計算得到的應該佔的半徑比例 0.312-0.832
            circleView.setProportion((float) ((mDatas.get(i).getDistance() / max + 0.6f) * 0.52f));

            if (minItemPosition == i) {
                minShowChild = circleView;
            }
            addView(circleView);
        }
    }

    /*----------------------------------------------判斷鬼抓人 人破解驛站的過程--------------------------------------------*/

    float [][] other_player ;  //其他玩家的陣列
    float [][] station; //驛站

    String msex;
    String game_room_id;
    String username;
    String activity;

    public void setRole_Info(SparseArray<own_data> mown_data,SparseArray<game_data> mgame_data,float [][] other_player,float [][] station,Context context,String own_sex)
    {
        this.SparseArray_own_data = mown_data; //傳遞玩家基本資料(全域)
        this.SparseArray_game_data = mgame_data;//傳遞遊戲規則資料(全域)

        final own_data own_Data = this.SparseArray_own_data.get(0);//取得玩家基本資料
        final game_data game_data = this.SparseArray_game_data.get(0); //取得遊戲規則資料

        this.other_player =other_player;
        this.station =station;
        this.scan_context = context;
        this.game_room_id = own_Data.get_game_room_id();
        this.username = own_Data.get_user_name();
        this.activity = own_Data.get_activity_id();

        if(own_sex.equals(String.valueOf("101"))) //人類
        {
            msex = own_Data.get_sex();
            person_catch_station();
        }
        else if(own_sex.equals(String.valueOf("102"))) //魔鬼
        {
            msex = own_Data.get_sex();
            devil();
        }
    }

    /*--------------------------判斷鬼抓人---------------------------*/
    float [][] devil_catch;
    boolean check = false;
    boolean fail_person_check=false;
    boolean fail_person_check2 =false;
    private void devil()
    {
        int c=0;
        final own_data own_Data = this.SparseArray_own_data.get(0);//取得玩家基本資料
        devil_catch = new float[other_player.length][3];

        //距離小於 3m 的人類，存到 devil_catch
        try {
            for(int j=0 ; j<other_player.length ; j++)
            {
                if(other_player[j][2] < 1 && other_player[j][2]!=0)  //TODO 鬼抓人的距離 在這改(1)
                {
                    //偵測到且符合條件的，儲存在陣列裡
                    for(int i=0 ; i<3 ; i++)
                    {
                        devil_catch[c][i] = other_player[j][i];
                    }
                    c++;
                }
            }
        }catch (Exception ex){
            Toast.makeText(scan_context,"魔鬼人類錯誤",Toast.LENGTH_SHORT).show();
        }


        if(check == false)
        {
            if(c>1)//偵測到多人
            {
                check =true;
                catch_person_list(c); // TODO 多人看要怎麼判斷
            }
            else if(c==1)  //偵測到1人
            {

                fail_person_check2 = false;
                mgame_connect_php.check_catch_person(own_Data.get_minor(), String.valueOf((int)devil_catch[0][1])); //偵測到人類(魔鬼)，傳值給資料庫

                if(fail_person_check == true) //等有淘汰的玩家數據後，再進去做判斷
                {
                    for(int i=0 ; i<mgame_connect_php.get_fail_person().length ; i++) //如果是淘汰的玩家，不要跳出訊息
                    {
                        if((int)devil_catch[0][1] == mgame_connect_php.get_fail_person()[i] && mgame_connect_php.get_fail_person()[i] !=0) //如果搜尋到的手環為 淘汰者
                        {
                            fail_person_check2 =true;
                        }
                    }

                    if(fail_person_check2 == false) //如果搜尋到的手環為 不是淘汰者
                    {
                        check =true;
                        catch_person_one(); //偵測到1位玩家時，跳出捕捉視窗
                    }
                }
                else
                {
                    check =true;
                    catch_person_one(); //偵測到1位玩家時，跳出捕捉視窗
                }
            }

        }

    }

    //偵測到1位玩家時，跳出捕捉視窗
    int check_person=0; //判斷他是單人還雙人以上(傳到success_catch_person_timer會用到)
    boolean catch_person_one = false;
    public void catch_person_one()
    {
        final own_data own_Data = this.SparseArray_own_data.get(0);//取得玩家基本資料

        if(success_catch_person_check == false)
        {
            final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this.scan_context)
                    .setTitle("偵測到1人在捕捉範圍內")
                    .setMessage("Major="+String.valueOf(devil_catch[0][0])+"\nMinor="+String.valueOf(devil_catch[0][1]))
                    .setPositiveButton("确定捕捉", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if(devil_catch[0][2] == 0) //人類距離太遠
                            {
                                check = false;
                                Toast.makeText(scan_context,"人類跑走囉!!",Toast.LENGTH_SHORT).show();
                            }
                            else if(devil_catch[0][2] < 1 )  //TODO 鬼抓人的距離 在這改(2)
                            {
                                get_position(0,1);
                                catch_person_timer_alertDialog();
                                success_catch_person_timer.start();
                            }
                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(20);
            alertDialog.setCanceledOnTouchOutside(false);

            try {
                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object mAlertController = mAlert.get(alertDialog);
                //通过反射修改title字体大小和颜色
                Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                mTitle.setAccessible(true);
                TextView mTitleView = (TextView) mTitle.get(mAlertController);
                mTitleView.setTextSize(23);
                mTitleView.setTextColor(Color.RED);
                //通过反射修改message字体大小和颜色
                Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                mMessage.setAccessible(true);
                TextView mMessageView = (TextView) mMessage.get(mAlertController);
                mMessageView.setTextSize(20);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }
        else
        {
            check =false;
        }

    }

    /*----------------------------------------------如果同時偵測到多人類，則列出選項捕捉------------------------------------------*/
    SimpleAdapter adapter;  ListView catch_person_list;  HashMap<String, Object> map; int position;  //Beacon AlertDialog(全域)
    public void catch_person_list(int c)
    {
        if(success_catch_person_check == false)
        {
            final own_data own_Data = this.SparseArray_own_data.get(0);//取得玩家基本資料
            View view = View.inflate(this.scan_context,R.layout.activity_beacon_msg_listview, null);  //使用
            final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this.scan_context)
                    .setView(view)
                    .setTitle("偵測到"+c+"人(點擊選項捕捉)")

                    .create();
            alertDialog.show();

            catch_person_list = view.findViewById(R.id.beacon_msg_list);
            adapter = new SimpleAdapter(this.scan_context,catch_person_list_Data(),R.layout.activity_beacon_game_catch_person_list,new String[]{"major" , "minor"} , new int[]{R.id.text1 , R.id.text2});
            catch_person_list.setAdapter(adapter);
            catch_person_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if(devil_catch[position][2] == 0) //人類距離太遠
                    {
                        check = false;
                        alertDialog.dismiss();
                        Toast.makeText(scan_context,"人類跑走囉!!!!",Toast.LENGTH_SHORT).show();
                    }
                    else if(devil_catch[position][2] < 1)  //TODO 鬼抓人的距離 在這改(3)
                    {
                        get_position(position,2);
                        alertDialog.dismiss();
                        catch_person_timer_alertDialog(); //捕捉中 3秒
                        success_catch_person_timer.start();//成功捕捉後，傳回資料庫
                    }

                }
            });
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(false);
        }
        else
        {
            check =false;
        }
    }

    // 儲存在捕捉範圍內的人類，並用List列出來
    private List<HashMap<String, Object>> catch_person_list_Data() {
        // 新增一個Lits集合 儲存 Beacon 數據
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        for (int i = 0; i < devil_catch.length; i++) {
            if(devil_catch[i][0] != 0 && devil_catch[i][1] != 0)
            {
                if(fail_person_check == true) //等有淘汰的玩家數據後，再進去做判斷
                {
                    if((int)devil_catch[position][1] == mgame_connect_php.get_fail_person()[i] && mgame_connect_php.get_fail_person()[i] !=0)//如果是淘汰的玩家，不要跳出訊息
                    {
                    }
                    else
                    {
                        map = new HashMap<>();
                        map.put("major" , "major="+devil_catch[i][0]);
                        map.put("minor" , "minor="+devil_catch[i][1]);
                        list.add(map);
                    }
                }
                else
                {
                    map = new HashMap<>();
                    map.put("major" , "major="+devil_catch[i][0]);
                    map.put("minor" , "minor="+devil_catch[i][1]);
                    list.add(map);
                }

            }

        }
        return list;
    }

    public void get_position(int position,int check_person){
        this.check_person =check_person;
        this.position =position;
    }
    /*----------------------------------------------如果同時偵測到多人類，則列出選項捕捉(結束)------------------------------------------*/

    /* ----------------------------------------------------鬼抓人時間倒數視窗(開始)------------------------------------*/

    public void catch_person_timer_alertDialog()
    {
        View view = View.inflate(scan_context,R.layout.activity_beacon_game_catch_person_timer, null);  //使用
        final TextView catch_person_timer = (TextView)view.findViewById(R.id.text1);

        final AlertDialog alertDialog = new AlertDialog.Builder(scan_context)
                .setCancelable(false)
                .setView(view)
                .setTitle("捕捉中")
                .create();
        alertDialog.show();

        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextSize(23);
            mTitleView.setTextColor(Color.RED);
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        }

        //使用 Handler，可以動態顯示倒數計時
        final Handler Assigning_Roles_Timer_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what > 0)
                {
                    catch_person_timer.setText(String.valueOf(msg.what));
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
            int countTime = 3;  //秒數
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

    /*----------------------------------- 鬼抓人時間倒數視窗(結束)----------------------------------*/

    /*---------------------------------鬼成功抓到人3秒後，傳回資料庫(開始)--------------------------------------*/
    String catch_ans; //鬼抓到人，判斷人類是不是真正淘汰
    public CountDownTimer success_catch_person_timer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {

            if(devil_catch[0][2] == 0)//人類跑走
            {
                check = false;
                Toast.makeText(scan_context,"可惜人類跑走囉!!!!",Toast.LENGTH_SHORT).show();
            }
            else if(check_person == 1)  //單人
            {
                mgame_connect_php.get_context(scan_context);
                final own_data own_Data = SparseArray_own_data.get(0);//取得玩家基本資料
                catch_ans=mgame_connect_php.check_catched_devil(own_Data.get_minor(),String.valueOf((int)devil_catch[0][1]),own_Data.get_game_room_id(),other_player.length);//確認捕捉到人類(魔鬼)，傳值給資料庫
                if(catch_ans.equals(String.valueOf("y")))
                {
                    check = false;
                    fail_person_check =true; //如果有確定有淘汰玩家後，打開判斷通道
                    success_catch_person_check =true;
                    success_catch_person_alertDialog(); //成功捕捉後，跳出視窗
                }
            }
            else if(check_person == 2) //雙人
            {
                mgame_connect_php.get_context(scan_context);

                final own_data own_Data = SparseArray_own_data.get(0);//取得玩家基本資料
                catch_ans=mgame_connect_php.check_catched_devil(own_Data.get_minor(),String.valueOf((int)devil_catch[position][1]),own_Data.get_game_room_id(),other_player.length);//確認捕捉到人類(魔鬼)，傳值給資料庫
                if(catch_ans.equals(String.valueOf("y")))
                {
                    check = false;
                    success_catch_person_check =true;
                    success_catch_person_alertDialog();
                }
            }
        }
    };
    /*------------------------------------鬼成功抓到人3秒後，傳回資料庫(結束)-------------------------------------*/

    /* -------------------------------------  確認捕捉視窗(魔鬼)---------------------------------------------------*/
    boolean success_catch_person_check =false;
    public void success_catch_person_alertDialog() {

        if(success_catch_person_check == true)
        {
            final AlertDialog alertDialog = new AlertDialog.Builder(scan_context)
                    .setCancelable(false)
                    .setTitle("成功捕捉")
                    .setPositiveButton("成功捕捉", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            success_catch_person_check = false;
                        }
                    })
                    .create();
            alertDialog.show();

            try {
                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object mAlertController = mAlert.get(alertDialog);
                //通过反射修改title字体大小和颜色
                Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                mTitle.setAccessible(true);
                TextView mTitleView = (TextView) mTitle.get(mAlertController);
                mTitleView.setTextSize(23);
                mTitleView.setTextColor(Color.RED);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }
    }
    /* --------------------------------------確認捕捉視窗(魔鬼)(結束)---------------------------------------------------*/


    /*----------------------------------------------判斷鬼抓人(結束)--------------------------------------------*/


    /*---------------------------------------------判斷人類破驛站(人類)(開始)----------------------------------------------*/

    float [][] person_station = new float[1][3];
    boolean close_station_check=false; //為了避免一直偵測到，一直重複跳出視窗
    float game_check_minor; //紀錄驛站的 minor

    public void person_catch_station()
    {
        //距離小於 3m 的驛站，存到 person_station
        try {
            for(int j=0 ; j<station.length ; j++)
            {
                if(station[j][2] < 1 && station[j][2]!=0)  //TODO 人破驛站的距離 在這改(1)
                {
                    //偵測到且符合條件的，儲存在陣列裡
                    for(int i=0 ; i<3 ; i++)
                    {
                        person_station[0][i] = station[j][i];
                    }
                }
            }
        }catch (Exception ex){
            Toast.makeText(scan_context,"人類偵測驛站錯誤",Toast.LENGTH_SHORT).show();
        }

        //確認破驛站資訊，取得破驛站相關說明
        if(person_station[0][1] != 0)
        {
            check_station_info();
        }

        //已在破譯站玩遊戲
        if(close_station_check == true)
        {
            //如果跟上一個驛站不同，則開啟遊戲訊息
            if(game_check_minor != person_station[0][1] && game_check_minor != 0)
            {
                close_station_check = false;
            }
        }
    }

    //確認破驛站資訊，取得破驛站相關說明
    boolean close_station_alertDialog_check = false;  //避免偵測到視窗和遊戲說明視窗重疊
    boolean station_description_alertDialog_check = false;//避免偵測到視窗和遊戲說明視窗重疊
    public void check_station_info()
    {
        final own_data own_Data = this.SparseArray_own_data.get(0);//取得玩家基本資料
        if(station[0][2] <= 1 && close_station_check==false && station[0][2] != 0)//TODO 人破驛站的距離 在這改(3)
        {
            close_station_check =true;
            mgame_connect_php.get_context(scan_context);


            if(close_station_alertDialog_check == false && station[0][2] !=0 && station_description_alertDialog_check ==false)
            {
                close_station_alertDialog_check = true;
                close_station_alertDialog(); //接近驛站時，開啟驛站遊戲介紹視窗
            }

        }
        else if(station[0][2] >= 1)//TODO 人破驛站的距離 在這改(4)
        {
            mgame_connect_php.game_check("0",username,"1",game_room_id);//在遊戲過程中，玩家離驛站太遠的話，傳值給後台(n)
            game_check("1",username,"",game_room_id);
            showTextToast("離驛站太遠了哦");
        }
    }



    // 接近驛站時，跳出已達可破譯驛站的距離 視窗
    public void close_station_alertDialog() {
        if(close_station_alertDialog_check == true && station[0][2] != 0)
        {
            final own_data own_Data = this.SparseArray_own_data.get(0);//取得玩家基本資料
            final AlertDialog alertDialog = new AlertDialog.Builder(scan_context)
                    .setCancelable(false)
                    .setTitle("已達可破譯驛站的距離")
                    .setMessage("按下確定，開始進行遊戲說明")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            close_station_alertDialog_check = false;

                            if (station[0][2] <= 1 && station[0][2]!=0)//接近驛站時 //TODO 人破驛站的距離 在這改(5)
                            {
                                mgame_connect_php.get_context(scan_context);
                                close_station_alertDialog_timer.start();//laoding 完後，跳出遊戲說明視窗
                            }
                            else if (station[0][2] >= 1)//如果離驛站太遠 TODO 人破驛站的距離 在這改(6)
                            {
                                close_station_check = false;
                                mgame_connect_php.check_station_info(String.valueOf((int) person_station[0][1]), own_Data.get_activity_id(), "9"); //取消破譯
                                showTextToast("離驛站太遠了哦");
                            }
                            else if(station[0][2] == 0)
                            {
                                showTextToast("驛站已破關或不存在");
                            }
                        }
                    })
                    .create();
            alertDialog.show();

            try {
                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object mAlertController = mAlert.get(alertDialog);
                //通过反射修改title字体大小和颜色
                Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                mTitle.setAccessible(true);
                TextView mTitleView = (TextView) mTitle.get(mAlertController);
                mTitleView.setTextSize(23);
                mTitleView.setTextColor(Color.RED);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }
    }

    //讀取遊戲關卡說明
    public CountDownTimer  close_station_alertDialog_timer = new CountDownTimer(1500, 1000) { //TODO 分配角色完，得到角色時間要改13s
        @Override
        public void onTick(long millisUntilFinished) {
            mgame_connect_php.check_station_info(String.valueOf((int)person_station[0][1]),activity,"1");//資料庫取得驛站遊戲介紹
        }
        //倒數時間結束
        @Override
        public void onFinish() {
            if(station_description_alertDialog_check  == false)
            {
                station_description_alertDialog_check = true;
                station_description_alertDialog();//驛站遊戲說明視窗
            }
        }
    };

    //驛站說明視窗
    public void station_description_alertDialog() {
        if(station_description_alertDialog_check == true)
        {
            final own_data own_Data = this.SparseArray_own_data.get(0);//取得玩家基本資料
            final AlertDialog alertDialog = new AlertDialog.Builder(scan_context)
                    .setCancelable(false)
                    .setTitle("驛站遊戲規則說明")
                    .setMessage("遊戲類別名稱："+mgame_connect_php.get_game_class_name()+"\n關卡名稱："+mgame_connect_php.get_game_level_name()+"\n關卡說明："+mgame_connect_php.get_game_content()+"\n破譯人數："+mgame_connect_php.get_game_count()+"\n*如為雙人合作，要找其他同伴一起破譯哦")
                    .setPositiveButton("開始破譯", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            station_description_alertDialog_check = false;

                            if(station[0][2] <= 1)//接近驛站時 TODO 人破驛站的距離 在這改(7)
                            {
                                close_station_check =true;
                                mgame_connect_php.get_context(scan_context);
                                mgame_connect_php.check_level_info();//從資料庫取得遊戲ID
                                start_game_timer.start();//讀取驛站資料，等待時間，並進入遊戲
                            }
                            else if(station[0][2] >= 1)//如果離驛站太遠 TODO 人破驛站的距離 在這改(8)
                            {
                                close_station_check = false;
                                mgame_connect_php.check_station_info(String.valueOf((int)person_station[0][1]),own_Data.get_activity_id(),"9"); //取消破譯
                                showTextToast("離驛站太遠了哦");
                            }
                            else if(station[0][2] == 0)
                            {
                                showTextToast("驛站已破關或不存在");
                            }
                        }
                    })
                    .create();
            alertDialog.show();

            try {
                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object mAlertController = mAlert.get(alertDialog);
                //通过反射修改title字体大小和颜色
                Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                mTitle.setAccessible(true);
                TextView mTitleView = (TextView) mTitle.get(mAlertController);
                mTitleView.setTextSize(23);
                mTitleView.setTextColor(Color.RED);
                //通过反射修改message字体大小和颜色
                Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                mMessage.setAccessible(true);
                TextView mMessageView = (TextView) mMessage.get(mAlertController);
                mMessageView.setTextSize(20);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }

    }

    public CountDownTimer start_game_timer = new CountDownTimer(2500, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }
        //倒數時間結束
        @Override
        public void onFinish() {

            try {
                //從資料庫取的遊戲ID後，進入遊戲
                switch(mgame_connect_php.get_game_level_record_id())
                {
                    case "101":
                        game_check_minor = person_station[0][1];
                        Intent Game01_jump = new Intent(scan_context, beacon_small_game_loading.class);
                        Bundle bundle01 = new Bundle();
                        bundle01.putString("game_id","101");
                        bundle01.putString("username",username);
                        bundle01.putString("game_level_record_id",mgame_connect_php.get_game_level_record_id());
                        bundle01.putString("game_station_id",String.valueOf((int)person_station[0][1]));
                        bundle01.putString("game_room_id",game_room_id);
                        bundle01.putString("game_class_id",mgame_connect_php.get_game_class_id());
                        Game01_jump.putExtras(bundle01);
                        scan_context.startActivity(Game01_jump);
                        break;
                    case "102":
                        game_check_minor = person_station[0][1];
                        Intent Game03_jump = new Intent(scan_context, beacon_small_game_loading.class);
                        Bundle bundle02 = new Bundle();
                        bundle02.putString("game_id","102");
                        bundle02.putString("username",username);
                        bundle02.putString("game_level_record_id",mgame_connect_php.get_game_level_record_id());
                        bundle02.putString("game_station_id",String.valueOf((int)person_station[0][1]));
                        bundle02.putString("game_room_id",game_room_id);
                        bundle02.putString("game_class_id",mgame_connect_php.get_game_class_id());
                        Game03_jump.putExtras(bundle02);
                        scan_context.startActivity(Game03_jump);
                        break;
                    case "103":
                        game_check_minor = person_station[0][1];
                        Intent Game04_jump = new Intent(scan_context, beacon_small_game_loading.class);
                        Bundle bundle04 = new Bundle();
                        bundle04.putString("game_id","103");
                        bundle04.putString("username",username);
                        bundle04.putString("game_level_record_id",mgame_connect_php.get_game_level_record_id());
                        bundle04.putString("game_station_id",String.valueOf((int)person_station[0][1]));
                        bundle04.putString("game_room_id",game_room_id);
                        bundle04.putString("game_class_id",mgame_connect_php.get_game_class_id());
                        Game04_jump.putExtras(bundle04);
                        scan_context.startActivity(Game04_jump);
                        break;
                    case "104":
                        game_check_minor = person_station[0][1];
                        Intent Game05_jump = new Intent(scan_context,beacon_small_game_loading.class);
                        Bundle bundle05 = new Bundle();
                        bundle05.putString("game_id","104");
                        bundle05.putString("username",username);
                        bundle05.putString("game_level_record_id",mgame_connect_php.get_game_level_record_id());
                        bundle05.putString("game_station_id",String.valueOf((int)person_station[0][1]));
                        bundle05.putString("game_room_id",game_room_id);
                        bundle05.putString("game_class_id",mgame_connect_php.get_game_class_id());
                        Game05_jump.putExtras(bundle05);
                        scan_context.startActivity(Game05_jump);
                        break;
                }
            } catch (Exception ex) {
                //TODO 改(嚴重)
            }


        }
    };

    private Toast toast = null;
    private void showTextToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(scan_context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }



    /*---------------------------------------------判斷人類破驛站(結束)----------------------------------------------*/

    //雷達圖没有掃描完畢時 回傳
    @Override
    public void onScanning(int position , float scanAngle) {

        if (scanAngle == 0) {
            scanAngleList.put(position, scanAngle);
        } else {
            scanAngleList.put(position, scanAngle);
        }

        requestLayout(); //要求重繪圖

    }

    //雷達圖掃描完畢時回傳
    @Override
    public void onScanSuccess() {
        // resetAnim(currentShowChild);
        currentShowChild = minShowChild;
        // startAnim(currentShowChild, minItemPosition);
    }

    //小圓點刪除
    public void delete_circleview()
    {
        int childCount = getChildCount();
        mRadarViewLayout = (RadarViewLayout) findViewById(R.id.radarViewLayout);

        if(childCount > 3)
        {
            for(int i=1 ; i<childCount ; i++)
            {
                mRadarViewLayout.removeViewAt(1);
            }
        }
        requestLayout(); //要求重繪圖
    }


    // 恢復CircleView小圆點 原大小
    private void resetAnim(RadarCircleView object) {
        if (object != null) {
            //object.clearPortaitIcon();
            ObjectAnimator.ofFloat(object, "scaleX", 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(object, "scaleY", 1f).setDuration(300).start();
        }
    }

    //放大CircleView小圆点大小
    private void startAnim(RadarCircleView object, int position) {
        if (object != null) {
            //object.setPortraitIcon(mDatas.get(position).getbeaconIcon());
            ObjectAnimator.ofFloat(object, "scaleX", 2f).setDuration(300).start();
            ObjectAnimator.ofFloat(object, "scaleY", 2f).setDuration(300).start();
        }
    }

    //根據 position，放大指定的CircleView小圆點
    public void setCurrentShowItem(int position) {
        RadarCircleView child = (RadarCircleView) getChildAt(position + 1);
        resetAnim(currentShowChild);
        currentShowChild = child;
        startAnim(currentShowChild, position);
    }


    //雷達圖中點集監聽CircleView小圆點回调接口
    public interface OnRadarClickListener {
        void onRadarItemClick(int position);
    }

    //雷達圖中點集監聽CircleView小圆點回调接口
    private OnRadarClickListener mOnRadarClickListener;
    public void setOnRadarClickListener(OnRadarClickListener mOnRadarClickListener) {
        this.mOnRadarClickListener = mOnRadarClickListener;
    }

    /* -----------------------------------------判斷玩家是否在遊戲驛站範圍內(人類)---------------------------------------------------------*/
    public void game_check(String type_id, final String username, String game_check , final String game_room_id)
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

                    //0(範圍內) 1(超過範圍)
                    if(ans[0].equals("y") && ans[1].equals("1"))
                    {
                        game_check("0",username,"0",game_room_id);
                        close_station_check = false;
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
