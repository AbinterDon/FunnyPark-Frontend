package com.example.administrator.funnypark.member;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.activities.activities_initiate;
import com.example.administrator.funnypark.beacon_game.Before_game.connect_watch;
import com.example.administrator.funnypark.new_attention;
import com.example.administrator.funnypark.ticket.ticket_information;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

public class member_center extends Fragment {


    private interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    final file_util file_util  = new file_util();//檔案參數

    String user_mail;
    ImageView user_img;
    TextView parkcoin;
    TextView play_session;
    TextView user_name;
    LinearLayout btn_member_information;
    LinearLayout btn_friend;
    LinearLayout btn_album;
    LinearLayout btn_wristband;
    LinearLayout btn_park_management;
    LinearLayout btn_activities_management;
    LinearLayout btn_about_system;
    ImageView qr_img;

    private void dim(){
        user_img = (ImageView)getActivity().findViewById(R.id.user_img);//使用者圖片
        parkcoin = (TextView)getActivity().findViewById(R.id.parkcoin);//parkcoin數量
        play_session = (TextView)getActivity().findViewById(R.id.play_session);//遊玩場次
        user_name = (TextView)getActivity().findViewById(R.id.user_name);//使用者名稱
        btn_member_information = (LinearLayout)getView().findViewById(R.id.btn_member_info);//會員管理
        btn_friend = (LinearLayout)getActivity().findViewById(R.id.btn_friend);//好友資訊
        //btn_album = (LinearLayout)getActivity().findViewById(R.id.btn_album);//個人相簿
        btn_wristband = (LinearLayout)getActivity().findViewById(R.id.btn_wristband);//手環配對
        //btn_park_management = (LinearLayout)getActivity().findViewById(R.id.btn_park_management);//我的場域
        btn_activities_management = (LinearLayout)getActivity().findViewById(R.id.btn_activities_management);//活動管理
        btn_about_system = (LinearLayout)getActivity().findViewById(R.id.btn_about_system);//關於系統
        qr_img = (ImageView)getActivity().findViewById(R.id.qr_img);//QRcode管理
    }

    public member_center() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.activity_member_center, container, false);
        //return view;
        return inflater.inflate(R.layout.activity_member_center, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)//onActivityCreated onCreate
    {
        //super.onCreate(savedInstanceState);
        super.onActivityCreated(savedInstanceState);

        dim();//定義
        //
        user_mail = file_util.Read_loginInfo_account_Value(getActivity());//取得登入會員信箱
        loading_member();

        //會員中心
        btn_member_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                member_info();
            }
        });

        //好友資訊
        btn_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend_center();
            }
        });


        //手環配對
        btn_wristband.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect_watch_room();
            }
        });

        //活動管理
        btn_activities_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_manager();
            }
        });

        //關於系統
        btn_about_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about_system();
            }
        });

        //Logo點擊
        Button btn_logo;
        btn_logo = (Button)getView().findViewById(R.id.logo);
        btn_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        //推播提醒
        ImageView btn_notice;
        btn_notice = (ImageView)getView().findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();//跳畫面
                jump.setClass(getActivity(), new_attention.class);
                startActivity(jump);
            }
        });

        //end
    }

    private void loading_member(){//載入會員的資料 套到介面上
        final String url = temp_url.get_member_center(user_mail);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(getActivity());//loading視窗打開
        //Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();//loading視窗關閉
                    //Log.e("res",responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        final String[] ans = responseData.split(",");//分解字串
                                        user_name.setText(ans[1]);//使用者名稱
                                        interface_util.set_img(temp_url.get_url_img() +  ans[2], user_img,getActivity());//大頭貼
                                        parkcoin.setText(ans[3]);//parkcoin
                                        play_session.setText(ans[4]);//遊玩場次
                                        interface_util.set_img(ans[5], qr_img,getActivity());//會員qr_code顯示
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(getActivity(), ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(getActivity(), "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(getActivity(), "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                getActivity().finish();
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

    /*public void member_info(){
        Intent jump = new Intent(getActivity(),member_information.class);
        startActivity(jump);
    }*/

    private void member_info(){
        Intent jump = new Intent(getActivity(),member_information.class);
        startActivity(jump);
    }

    private void friend_center(){
        Intent jump = new Intent(getActivity(),member_friends_center.class);
        startActivity(jump);
    }

    private void activity_manager(){
        Intent jump = new Intent(getActivity(),activities_managerment_list.class);
        startActivity(jump);
    }

    private void about_system(){
        Intent jump = new Intent(getActivity(),about_system.class);
        startActivity(jump);
    }

    private void connect_watch_room()
    {
        Intent jump = new Intent(getActivity(), connect_watch.class);
        Bundle bundle = new Bundle();
        bundle.putString("connect_watch_ticket_key","member");//傳遞String
        jump.putExtras(bundle);
        startActivity(jump);
    }

}
