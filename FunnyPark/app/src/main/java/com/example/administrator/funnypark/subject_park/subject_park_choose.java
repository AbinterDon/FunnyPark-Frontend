package com.example.administrator.funnypark.subject_park;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class subject_park_choose extends AppCompatActivity {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    List<String> park_name_id = new ArrayList<String>();//園區名稱id
    List<String> park_name = new ArrayList<String>();//園區名稱
    ListView listView;
    ListAdapter listAdapter;
    String[] park_item;
    Intent jump;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_park_choose);

        jump = getIntent();
        type = jump.getStringExtra("type");

        if(type.equals("discount") || type.equals("activities")){
            park_name.add("全部");
            park_name_id.add("999");
        }

        listView = (ListView) findViewById(R.id.subject_list);//園區清單

        create_subject_park_list();

        //park_item = (String[])park_name.toArray();

        listView.setOnItemClickListener(onClickListView);       //指定事件 Method
    }

    /***
     * 點擊ListView事件Method
     */
    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Toast 快顯功能 第三個參數 Toast.LENGTH_SHORT 2秒  LENGTH_LONG 5秒
            //Toast.makeText(subject_park_choose.this,"點選第 "+(position +1) +" 個 \n內容："+park_item[position], Toast.LENGTH_SHORT).show();
            //Log.e("type",type);
            switch (type){
                case "park_tour":
                    jump.setClass(subject_park_choose.this,subject_park_mobile_guide.class);
                    break;
                case "discount":
                    jump.setClass(subject_park_choose.this,subject_park_discount.class);
                    break;
                case "activities":
                    jump.setClass(subject_park_choose.this,subject_park_activities.class);
                    break;
                case "park_map":
                    jump.setClass(subject_park_choose.this,subject_park_map.class);
                    break;
                case "aerial_guide":
                    jump.setClass(subject_park_choose.this,subject_park_aerial_guide.class);
                    break;
            }
            //jump.setClass(subject_park_choose.this,subject_park_mobile_guide.class);
            jump.putExtra("park_id", park_name_id.get(position));
            startActivity(jump);
        }
    };

    private void create_subject_park_list(){//建立園區清單
        final String url = temp_url.get_park_list("999");//取得url 999全部
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(subject_park_choose.this);//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    subject_park_choose.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                String[] ans = responseData.split("\\*");//分解字串
                                for (int i = 0; i< ans.length; i++) {
                                    String[] temp = ans[i].split(",");
                                    park_name_id.add(temp[1]);
                                    park_name.add(temp[2]);
                                }
                                listAdapter = new ArrayAdapter<String>(subject_park_choose.this, android.R.layout.simple_list_item_1, park_name);
                                listView.setAdapter(listAdapter);
                                park_item = park_name.toArray(new String[park_name.size()]);//將選項轉為陣列
                            }else {//網路連線失敗
                                Toast toast = Toast.makeText(subject_park_choose.this, "網路連線失敗", Toast.LENGTH_LONG);
                                //顯示Toast
                                toast.show();
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


    public void finish_this_page(View v){
        this.finish();
    }
}
