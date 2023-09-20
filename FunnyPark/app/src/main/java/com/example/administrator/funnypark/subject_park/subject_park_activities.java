package com.example.administrator.funnypark.subject_park;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.util.ArrayList;
import java.util.List;

public class subject_park_activities extends AppCompatActivity {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    String park_id;//園區id
    TextView Nothing ;//沒有園區活動的訊息顯示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_park_activities);

        Intent jump = getIntent();
        park_id = jump.getStringExtra("park_id");

        Nothing = (TextView)findViewById(R.id.nothing);//沒有好友的訊息顯示
        Nothing.setVisibility(View.GONE);//隱藏

        create_list();
    }

    private void create_list()//建立園區活動清單
    {
        final String url = temp_url.get_park_activity(park_id);//
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(subject_park_activities.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    subject_park_activities.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        String[] ans = responseData.split("\\*");//分解字串
                                        final ArrayList<String> temp_title = new ArrayList<>();
                                        final ArrayList<String> temp_time = new ArrayList<>();
                                        final ArrayList<String> temp_info = new ArrayList<>();
                                        for (int i = 0; i< ans.length; i++) {//i< ans.length
                                            //Log.e("res5", ans[i]);
                                            final String[] temp = ans[i].split(",");//分解字串
                                            temp_title.add(temp[0]);//活動名稱
                                            temp_time.add(temp[1]);//
                                            temp_info.add(temp[2]);
                                        }
                                        //開始介面實現
                                        subject_park_activities.MyAdapter myAdapter = new subject_park_activities.MyAdapter(temp_title, temp_time, temp_info);
                                        final LinearLayoutManager layoutManager = new LinearLayoutManager(subject_park_activities.this);//getActivity()
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                        RecyclerView mList = subject_park_activities.this.findViewById(R.id.activities_list);
                                        mList.setLayoutManager(layoutManager);
                                        mList.setHasFixedSize(true);
                                        mList.setAdapter(myAdapter);
                                        //end
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(subject_park_activities.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                        Nothing.setVisibility(View.VISIBLE);//顯示沒有園區活動的訊息
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(subject_park_activities.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                    Nothing.setVisibility(View.VISIBLE);//顯示沒有園區活動的訊息
                                }
                            }catch (Exception e){
                                Toast.makeText(subject_park_activities.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                subject_park_activities.this.finish();
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

    //Recyler
    public class MyAdapter extends RecyclerView.Adapter<subject_park_activities.MyAdapter.ViewHolder> {
        private List<String> temp_title;
        private List<String> temp_time;
        private List<String> temp_info;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView activities_title;
            public TextView activities_time;
            public CardView activities_LinearLayout ;
            public TextView activities_info;

            public ViewHolder(View v) {
                super(v);
                activities_title = (TextView) v.findViewById(R.id.list_title);
                activities_time = (TextView) v.findViewById(R.id.list_time);
                activities_info = (TextView) v.findViewById(R.id.list_info);
                activities_LinearLayout = (CardView)v.findViewById(R.id.list_Linear);
            }
        }

        public MyAdapter(List<String> title,
                         List<String> time,
                         List<String> info) {
            temp_title = title;
            temp_time = time;
            temp_info = info;
        }

        @Override
        public subject_park_activities.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.park_list_style, parent, false);
            subject_park_activities.MyAdapter.ViewHolder vh = new subject_park_activities.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(subject_park_activities.MyAdapter.ViewHolder holder, final int position) {
            Log.e("123",temp_title.get(position));
            holder.activities_title.setText(temp_title.get(position));
            holder.activities_time.setText(temp_time.get(position));
            holder.activities_info.setText(temp_info.get(position));
        }

        @Override
        public int getItemCount() {
            return temp_title.size();
        }
    }
}
