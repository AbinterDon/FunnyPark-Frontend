package com.example.administrator.funnypark.mall;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.subject_park.subject_park_discount;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.util.ArrayList;
import java.util.List;

public class platform_product_exchange_list extends AppCompatActivity {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    TextView nothing_textview;//查無兌換商品的警示標語


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_product_exchange_list);

        Dim();//定義宣告

        ImageView btn_exchange;
        btn_exchange = (ImageView)findViewById(R.id.btn_exchange);//兌換按鈕
        btn_exchange.setOnClickListener(new View.OnClickListener() {//前往兌換頁面
            @Override
            public void onClick(View v) {
                Intent jump = getIntent();
                jump.setClass(platform_product_exchange_list.this,platform_product_exchange.class);
                startActivity(jump);
            }
        });

        create_list();//建立兌換清單
    }

    private void Dim(){//宣告定義
        nothing_textview = (TextView)findViewById(R.id.nothing);
        nothing_textview.setVisibility(View.GONE);//隱藏無商品的警示標語
        //RecyclerView product_exchange_list = (RecyclerView)findViewById(R.id.product_exchange_list);//兌換清單
    }

    private void create_list(){//建立兌換清單
        String user_mail = file_util.Read_loginInfo_account_Value(platform_product_exchange_list.this);//取得登入會員信箱
        final String url = temp_url.get_store_exchange_info(user_mail);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(platform_product_exchange_list.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    platform_product_exchange_list.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                if(responseData.indexOf("error")<0){
                                    final String[] ans = responseData.split("\\*");//分解字串
                                    //Log.e("res",responseData);
                                    final ArrayList<String> temp_id = new ArrayList<>();
                                    final ArrayList<String> temp_name = new ArrayList<>();
                                    final ArrayList<String> temp_period = new ArrayList<>();
                                    final ArrayList<String> temp_img = new ArrayList<>();
                                    //final ArrayList<String> temp_exchange_status = new ArrayList<>();
                                    for (int i = 0; i< ans.length; i++) {//i< ans.length
                                        //Log.e("res5", ans[i]);
                                        final String[] temp = ans[i].split(",");//分解字串
                                        temp_id.add(temp[1]);//商品兌換id
                                        temp_img.add(temp_url.get_url_img() + temp[2]);//商品圖片
                                        temp_name.add(temp[3]);//商品名稱
                                        temp_period.add(temp[4]);//商品兌換期限
                                    }
                                    //開始介面實現
                                    platform_product_exchange_list.MyAdapter myAdapter = new platform_product_exchange_list.MyAdapter(temp_id,temp_name, temp_period, temp_img);
                                    final LinearLayoutManager layoutManager = new LinearLayoutManager(platform_product_exchange_list.this);//getActivity()
                                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                    RecyclerView mList = platform_product_exchange_list.this.findViewById(R.id.product_exchange_list);
                                    mList.setLayoutManager(layoutManager);
                                    mList.setHasFixedSize(true);
                                    mList.setAdapter(myAdapter);
                                    //end
                                }else{//無可換商品
                                    nothing_textview.setVisibility(View.VISIBLE);
                                }
                            }else {//網路連線失敗
                                platform_product_exchange_list.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(platform_product_exchange_list.this, "網路連線失敗", Toast.LENGTH_LONG);
                                        //顯示Toast
                                        toast.show();
                                    }
                                });
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

    //    返回上一頁
    public void finish_this_page(View v){
        this.finish();
    }

    //Recyler
    public class MyAdapter extends RecyclerView.Adapter<platform_product_exchange_list.MyAdapter.ViewHolder> {
        private  List<String> temp_id;
        private List<String> temp_name;
        private List<String> temp_period;
        private List<String> temp_img;
        //private List<String> temp_exchange_status;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView product_name;
            public TextView product_period;
            public CardView product_cardview ;
            public ImageView product_img;

            public ViewHolder(View v) {
                super(v);
                product_cardview = (CardView) v.findViewById(R.id.product_cardview);
                product_name = (TextView) v.findViewById(R.id.product_name);
                product_period = (TextView) v.findViewById(R.id.product_period);
                product_img = (ImageView) v.findViewById(R.id.product_img);
            }
        }

        public MyAdapter(List<String> id,
                         List<String> name,
                         List<String> period,
                         List<String> img) {
            temp_id = id;
            temp_name = name;
            temp_period = period;
            temp_img = img;
        }

        @Override
        public platform_product_exchange_list.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_exchange_style, parent, false);
            platform_product_exchange_list.MyAdapter.ViewHolder vh = new platform_product_exchange_list.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(platform_product_exchange_list.MyAdapter.ViewHolder holder, final int position) {
            //Log.e("123",temp_title.get(position));
            holder.product_name.setText(temp_name.get(position));//商品名稱
            holder.product_period.setText(temp_period.get(position));//商品兌換日期
            interface_util.set_img(temp_img.get(position),holder.product_img,platform_product_exchange_list.this);//商品圖片
            holder.product_cardview.setOnClickListener(new View.OnClickListener() {//查看兌換資訊
                @Override
                public void onClick(View v) {
                    Intent jump=new Intent();
                    jump.setClass(platform_product_exchange_list.this,platform_product_exchange_info.class);
                    jump.putExtra("exchange_id",temp_id.get(position));
                    startActivity(jump);
                }
            });
        }

        @Override
        public int getItemCount() {
            return temp_name.size();
        }
    }
}
