package com.example.administrator.funnypark.member;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.activities.activities_information;
import com.example.administrator.funnypark.activities.activities_initiate;
import com.example.administrator.funnypark.subject_park.subject_park_discount;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import org.w3c.dom.Text;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class member_friends_center extends AppCompatActivity {
    static AppCompatActivity view_member_friends_center;

    private interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    String user_mail;//使用者帳號
    TextView Nothing ;//沒有好友的訊息顯示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_friends_center);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//隱藏跳出的鍵盤

        view_member_friends_center=this;//保存此介面 以便後面關閉

        user_mail = file_util.Read_loginInfo_account_Value(member_friends_center.this);//讀取會員身分(帳號)

        create_list();//建立好友清單

        Nothing = (TextView)findViewById(R.id.nothing);//沒有好友的訊息顯示
        Nothing.setVisibility(View.GONE);//隱藏
    }

    public void btn_add_friend(View v)//TODO  新增好友按鈕
    {
        TextView friend_id = (TextView)findViewById(R.id.friend_id);
        check_friend(friend_id.getText().toString());//傳遞好友帳號
    }

    private void add_friend(String friend_usermail,String friend_username,String friend_img)//TODO  新增好友
    {
        Intent jump = this.getIntent();
        jump.setClass(member_friends_center.this,member_friend_add.class);
        jump.putExtra("usermail", user_mail);//帶使用者身分　過去下一個表單
        jump.putExtra("friend_usermail", friend_usermail);//帶好友的帳號 過去下一個表單
        jump.putExtra("friend_username", friend_username);//帶好友的暱稱 過去下一個表單
        jump.putExtra("friend_img", friend_img);//帶好友的大頭貼 過去下一個表單
        startActivity(jump);
    }

    private void check_friend(String friend_username)//TODO  確認有無此好友
    {
        final String url = temp_url.get_check_friend_detail(user_mail,friend_username);//好友資訊url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(member_friends_center.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料

                    temp_load.loading_close();//loading視窗關閉

                    member_friends_center.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    String[] ans = responseData.split(",");
                                    if(ans[0].equals("y")){//讀取資料有無失敗
                                        add_friend(ans[1],ans[2],ans[3]);//傳遞好友簡歷 到下一個頁面
                                    }else{//讀取資料失敗 顯示對應資訊
                                        Toast.makeText(member_friends_center.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(member_friends_center.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                Toast.makeText(member_friends_center.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                member_friends_center.this.finish();
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

    private void create_list()//TODO 建立好友清單
    {
        final String url = temp_url.get_member_friends(user_mail);//好友資訊url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(member_friends_center.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    temp_load.loading_close();

                    member_friends_center.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        String[] ans = responseData.split("\\*");//分解字串
                                        Log.e("res",responseData);
                                        ArrayList<String> temp_friend_img = new ArrayList<>();
                                        ArrayList<String> temp_friend_name = new ArrayList<>();
                                        ArrayList<String> temp_friend_mail = new ArrayList<>();
                                        //Log.e("count",String.valueOf(ans.length));
                                        for (int i = 0; i< ans.length; i++) {//i< ans.length
                                            Log.e("res5", ans[i]);
                                            final String[] temp = ans[i].split(",");//分解字串
                                            temp_friend_img.add(temp_url.get_url_img() +  temp[3]);//
                                            temp_friend_name.add(temp[2]);//
                                            temp_friend_mail.add(temp[1]);//
                                        }
                                        //開始介面實現
                                        member_friends_center.MyAdapter myAdapter = new member_friends_center.MyAdapter(temp_friend_img, temp_friend_name, temp_friend_mail);
                                        final LinearLayoutManager layoutManager = new LinearLayoutManager(member_friends_center.this);//getActivity()
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                        RecyclerView mList = member_friends_center.this.findViewById(R.id.friends_list);
                                        mList.setLayoutManager(layoutManager);
                                        mList.setHasFixedSize(true);
                                        mList.setAdapter(myAdapter);
                                        //end
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(member_friends_center.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                        Nothing.setVisibility(View.VISIBLE);//顯示沒有好友的訊息
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(member_friends_center.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                    Nothing.setVisibility(View.VISIBLE);//顯示沒有好友的訊息
                                }
                            }catch (Exception e){
                                Toast.makeText(member_friends_center.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                member_friends_center.this.finish();
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

    public void finish_this_page(View v)//TODO  關閉頁面
    {
        this.finish();
    }

    public void scan_qrcode(View v)//TODO  掃描qrcode
    {
        IntentIntegrator integrator = new IntentIntegrator(member_friends_center.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("對準QrCode進行掃描");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);//改為直是
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)//TODO Qrcode 掃描結果
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result!= null)
        {
            if (result.getContents()==null)//若是關閉掃描
            {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else//顯示掃瞄到的內容
            {
                //Toast.makeText(this,result.getContents(),Toast.LENGTH_LONG).show();
                check_friend(result.getContents());//傳遞掃瞄到的好友帳號
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Recyler
    public class MyAdapter extends RecyclerView.Adapter<member_friends_center.MyAdapter.ViewHolder> {
        private List<String> temp_friend_img;
        private List<String> temp_friend_name;
        private List<String> temp_friend_mail;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView friend_cardview ;
            public ImageView friend_img;//
            public TextView friend_name;
            public TextView friend_info;

            public ViewHolder(View v) {
                super(v);
                friend_cardview = (CardView)v.findViewById(R.id.firend_cardview);
                friend_img = (ImageView) v.findViewById(R.id.user_img);
                friend_name = (TextView) v.findViewById(R.id.user_name);
                friend_info = (TextView) v.findViewById(R.id.user_info);
            }
        }

        public MyAdapter(List<String> img,
                         List<String> name,
                         List<String> mail) {
            temp_friend_img = img;
            temp_friend_name = name;
            temp_friend_mail = mail;
        }

        @Override
        public member_friends_center.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friends_list_style, parent, false);
            member_friends_center.MyAdapter.ViewHolder vh = new member_friends_center.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(member_friends_center.MyAdapter.ViewHolder holder, final int position) {
            interface_util.set_img(temp_friend_img.get(position),holder.friend_img,member_friends_center.this);//好友頭貼
            holder.friend_name.setText(temp_friend_name.get(position));
            //TODO 查詢
            holder.friend_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent jump=new Intent();
                    jump.setClass(member_friends_center.this,member_friend_info.class);
                    jump.putExtra("usermail",user_mail);
                    jump.putExtra("friend_mail",temp_friend_mail.get(position));
                    startActivity(jump);
                }
            });

        }

        @Override
        public int getItemCount() {
            return temp_friend_name.size();
        }
    }
}
