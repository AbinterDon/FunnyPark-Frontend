package com.example.administrator.funnypark;

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

import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.util.ArrayList;
import java.util.List;

public class new_attention extends AppCompatActivity {

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    String user_mail;//使用者登入帳號
    TextView Nothing ;//沒有最新消息的訊息顯示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_attention);

        user_mail = file_util.Read_loginInfo_account_Value(new_attention.this);//取得登入會員信箱

        Nothing = (TextView)findViewById(R.id.nothing);//沒有最新資訊時的訊息顯示
        Nothing.setVisibility(View.GONE);//隱藏

        create_list();
    }

    private void create_list()//建立折扣清單
    {
        final String url = temp_url.get_ad_recive_list(user_mail);//
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(new_attention.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉
                    new_attention.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        String[] ans = responseData.split("\\*");//分解字串
                                        final ArrayList<String> temp_context = new ArrayList<>();
                                        final ArrayList<String> temp_time = new ArrayList<>();
                                        for (int i = 0; i< ans.length; i++) {//i< ans.length
                                            //Log.e("res5", ans[i]);
                                            final String[] temp = ans[i].split(",");//分解字串
                                            temp_context.add(temp[0]);//消息內容
                                            temp_time.add(temp[1]);//消息日期
                                        }
                                        //開始介面實現
                                        new_attention.MyAdapter myAdapter = new new_attention.MyAdapter(temp_context, temp_time);
                                        final LinearLayoutManager layoutManager = new LinearLayoutManager(new_attention.this);//getActivity()
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                        RecyclerView mList = new_attention.this.findViewById(R.id.notify_list);
                                        mList.setLayoutManager(layoutManager);
                                        mList.setHasFixedSize(true);
                                        mList.setAdapter(myAdapter);
                                        //end
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(new_attention.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                        Nothing.setVisibility(View.VISIBLE);//顯示沒有最新消息的訊息
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(new_attention.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                    Nothing.setVisibility(View.VISIBLE);//顯示沒有最新消息的訊息
                                }
                            }catch (Exception e){
                                Toast.makeText(new_attention.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                                new_attention.this.finish();
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
    public class MyAdapter extends RecyclerView.Adapter<new_attention.MyAdapter.ViewHolder> {
        private List<String> temp_context;
        private List<String> temp_time;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView activities_context;
            public TextView activities_time;
            public CardView activities_LinearLayout ;

            public ViewHolder(View v) {
                super(v);
                activities_context = (TextView) v.findViewById(R.id.notify_context);
                activities_time = (TextView) v.findViewById(R.id.notify_date);
                activities_LinearLayout = (CardView)v.findViewById(R.id.attention_cardview);
            }
        }

        public MyAdapter(List<String> context,
                         List<String> time) {
            temp_context = context;
            temp_time = time;
        }

        @Override
        public new_attention.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.attention_style, parent, false);
            new_attention.MyAdapter.ViewHolder vh = new new_attention.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(new_attention.MyAdapter.ViewHolder holder, final int position) {
            Log.e("123",temp_context.get(position));
            holder.activities_context.setText(temp_context.get(position));
            holder.activities_time.setText(temp_time.get(position));
        }

        @Override
        public int getItemCount() {
            return temp_context.size();
        }
    }
}
