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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.activities.activities_information;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class activities_managerment_list extends AppCompatActivity {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    private String choose_ticket_type;//選擇要察看的活動票券　//未結束 已結束
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    TextView nothing;//沒有任何活動時的警告
    RecyclerView activities_list ;//活動清單
    String user_mail;//使用者帳號

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_managerment_list);

        Dim();

        //取得登入者身分帳號
        user_mail = file_util.Read_loginInfo_account_Value(activities_managerment_list.this);

        choose_ticket_type = "0";//預設為 未結束
        create_list();
    }

    private void Dim(){//定義
        nothing = (TextView)activities_managerment_list.this.findViewById(R.id.nothing);//現在沒有任何活動的警告

        //活動列表
        activities_list = (RecyclerView)activities_managerment_list.this.findViewById(R.id.activities_list);

        //設定活動類型
        RadioGroup radioGroup=(RadioGroup)activities_managerment_list.this.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(listen);//設定RadioGroup監聽器
    }

    private RadioGroup.OnCheckedChangeListener listen=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id=	group.getCheckedRadioButtonId();
            group.getChildAt(0);
            nothing.setVisibility(View.GONE);//隱藏
            switch (group.getCheckedRadioButtonId()) {
                case R.id.radioButton://未結束
                    choose_ticket_type="0";
                    create_list();
                    break;
                case R.id.radioButton2://已結束
                    choose_ticket_type="1";
                    create_list();
                    break;
                default: break;
            }
        }
    };

    private void create_list()//建立活動清單
    {
        final String url = temp_url.get_activity_admin_info(user_mail,choose_ticket_type);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(activities_managerment_list.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    activities_managerment_list.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    //String[] ans = responseData.split(",");
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        final String[] ans = responseData.split("\\*");//分解字串
                                        Log.e("res",responseData);
                                        final ArrayList<String> temp_title = new ArrayList<>();
                                        final ArrayList<String> temp_time = new ArrayList<>();
                                        final ArrayList<String> temp_activity_id = new ArrayList<>();
                                        final ArrayList<String> temp_ticket = new ArrayList<>();
                                        final ArrayList<String> temp_main_img = new ArrayList<>();
                                        final ArrayList<String> temp_user_img = new ArrayList<>();
                                        final ArrayList<String> temp_hastag_1 = new ArrayList<>();
                                        final ArrayList<String> temp_hastag_2 = new ArrayList<>();
                                        final ArrayList<String> temp_hastag_3 = new ArrayList<>();

                                        activities_list.setVisibility(View.VISIBLE);
                                        nothing.setVisibility(View.GONE);
                                        //Log.e("count",String.valueOf(ans.length));
                                        for (int i = 0; i< ans.length; i++) {//i< ans.length
                                            Log.e("res5", ans[i]);
                                            final String[] temp = ans[i].split(",");//分解字串
                                            temp_activity_id.add(temp[1]);//活動代號
                                            temp_title.add(temp[2]);//活動名稱
                                            temp_time.add(temp[3]);//+ " " + temp[7] + "-" + temp[8] 5剩餘票數
                                            // Log.e("res5","剩餘"+temp[5]+"張");
                                            temp_ticket.add("剩餘"+temp[5]+"張");
                                            temp_main_img.add(temp_url.get_url_img() +  temp[4]);
                                            //Log.e("res6",temp_url.get_url() + "/~D10516216/" + temp[4]);
                                            temp_hastag_1.add(temp[6]);//hastag
                                            // Log.e("res7",temp[7]);
                                            temp_hastag_2.add(temp[7]);//hastag
                                            //Log.e("res8",temp[8]);
                                            temp_hastag_3.add(temp[8]);//hastag
                                            temp_user_img.add(temp_url.get_url_img() + temp[9]);//使用者大頭貼 + "/~D10516216/"
                                        }
                                        //開始介面實現
                                        activities_managerment_list.MyAdapter myAdapter = new activities_managerment_list.MyAdapter(temp_title, temp_time, temp_main_img,temp_ticket, temp_hastag_1,temp_hastag_2,temp_hastag_3,temp_activity_id,temp_user_img);
                                        final LinearLayoutManager layoutManager = new LinearLayoutManager(activities_managerment_list.this);//getActivity()
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                        RecyclerView mList = activities_managerment_list.this.findViewById(R.id.activities_list);
                                        mList.setLayoutManager(layoutManager);
                                        mList.setHasFixedSize(true);
                                        mList.setAdapter(myAdapter);
                                        //end

                                    }else{//讀取資料失敗 顯示對應資訊
                                        activities_list.setVisibility(View.GONE);
                                        nothing.setVisibility(View.VISIBLE);
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(activities_managerment_list.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(activities_managerment_list.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                activities_managerment_list.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(activities_managerment_list.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    public void finish_this_page(View v){
        this.finish();
    }//關閉活動管理

    //Recyler
    public class MyAdapter extends RecyclerView.Adapter<activities_managerment_list.MyAdapter.ViewHolder> {
        private List<String> temp_title;
        private List<String> temp_time;
        private List<String> temp_main_img;
        private List<String> temp_activity_id;
        private List<String> temp_ticket;
        private List<String> temp_hastag_1;
        private List<String> temp_hastag_2;
        private List<String> temp_hastag_3;
        private List<String> temp_user_img;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView activities_title;
            public TextView activities_time;
            public ImageView activities_main_img;//temp_main_img.
            public CardView activities_cardview ;
            public TextView activities_ticket;
            public TextView activities_hastag_1;
            public TextView activities_hastag_2;
            public TextView activities_hastag_3;
            public ImageView activities_user_img;//temp_main_img.

            public ViewHolder(View v) {
                super(v);
                //activities_cardview = new CardView(getActivity());
                // activities_title = (TextView) v.findViewById(R.id.info_text);
                activities_title = (TextView) v.findViewById(R.id.info_text);
                activities_time = (TextView) v.findViewById(R.id.info_time);
                activities_main_img = (ImageView) v.findViewById(R.id.info_cardimage);
                activities_ticket = (TextView) v.findViewById(R.id.info_ticket);
                activities_hastag_1 = (TextView) v.findViewById(R.id.info_hastag_1);
                activities_hastag_2 = (TextView) v.findViewById(R.id.info_hastag_2);
                activities_hastag_3 = (TextView) v.findViewById(R.id.info_hastag_3);
                activities_cardview = (CardView)v.findViewById(R.id.info_cardview);
                activities_user_img = (ImageView)v.findViewById(R.id.info_userpic);
            }
        }

        public MyAdapter(List<String> title,
                         List<String> time,
                         List<String> img,
                         List<String> ticket,
                         List<String> hastag_1,
                         List<String> hastag_2,
                         List<String> hastag_3,
                         List<String> activity_id,
                         List<String> activity_user_img) {
            temp_title = title;
            temp_time = time;
            temp_main_img = img;
            temp_ticket = ticket;
            temp_hastag_1 = hastag_1;
            temp_hastag_2 = hastag_2;
            temp_hastag_3 = hastag_3;
            temp_activity_id = activity_id;
            temp_user_img= activity_user_img;
        }

        @Override
        public activities_managerment_list.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_activities_style, parent, false);
            activities_managerment_list.MyAdapter.ViewHolder vh = new activities_managerment_list.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(activities_managerment_list.MyAdapter.ViewHolder holder, final int position) {
            holder.activities_title.setText(temp_title.get(position));
            holder.activities_time.setText(temp_time.get(position));
            interface_util.set_img(temp_main_img.get(position),holder.activities_main_img,activities_managerment_list.this);
            interface_util.set_img(temp_user_img.get(position),holder.activities_user_img,activities_managerment_list.this);
            holder.activities_ticket.setText(temp_ticket.get(position));
            holder.activities_hastag_1.setText(temp_hastag_1.get(position));
            holder.activities_hastag_2.setText(temp_hastag_2.get(position));
            holder.activities_hastag_3.setText(temp_hastag_3.get(position));
            holder.activities_cardview.setOnClickListener(new View.OnClickListener() {//點及後 跳到哪一個活動
                @Override
                public void onClick(View v) {
                    Intent jump=new Intent();
                    jump.setClass(activities_managerment_list.this,activities_managerment.class);
                    jump.putExtra("activity",temp_activity_id.get(position));
                    startActivity(jump);
                }
            });

            //(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
            /*holder.activities_cardview.setLayoutParams(new CardView.LayoutParams((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 330, getResources().getDisplayMetrics()),
                    CardView.LayoutParams.WRAP_CONTENT)); //interface_util.transform_dp(getActivity(),150))*/
            //holder.activities_cardview.setMinimumHeight(10);
            //holder.activities_cardview

        }

        @Override
        public int getItemCount() {
            return temp_title.size();
        }
    }
    //end
}
