package com.example.administrator.funnypark.ticket;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.activities.activities_information;
import com.example.administrator.funnypark.new_attention;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ticket_list extends Fragment {

    public ticket_list() {
        // Required empty public constructor
    }

    private interface_util interface_util = new interface_util();//call interface_util取介面方法
    final private com.example.model.util.file_util file_util  = new file_util();//檔案參數
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    private String choose_ticket_type;//選擇要察看的活動票券　//未結束 已結束
    RecyclerView ticket_list ;//活動清單
    TextView nothing ;//nothing 沒有票券時


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_ticket_list, container, false);

        //列出票劵
        choose_ticket_type="0";
        create_list();

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState)//onActivityCreated onCreate
    {
        //super.onCreate(savedInstanceState);
        super.onActivityCreated(savedInstanceState);

        //活動列表
        ticket_list = (RecyclerView)getView().findViewById(R.id.ticket_list);

        nothing = (TextView)getView().findViewById(R.id.nothing); // 定義 沒有票券時出現的資訊
        nothing.setVisibility(View.GONE);//隱藏

        //設定要察看的票券類型start
        RadioGroup radioGroup=(RadioGroup)getView().findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(listen);//設定RadioGroup監聽器
        //設定要察看的票券類型end

        //Logo點擊
        Button btn_logo;
        btn_logo = (Button)getView().findViewById(R.id.logo);
        btn_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        ImageView btn_search;//介面搜尋
        ImageView btn_notice;//推播提醒
        btn_search = (ImageView)getView().findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("尚未開放");
                builder.setMessage("施工中~敬請期待！");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btn_notice = (ImageView)getView().findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();//跳畫面
                jump.setClass(getActivity(), new_attention.class);
                startActivity(jump);
            }
        });

    }

    private RadioGroup.OnCheckedChangeListener listen=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id=	group.getCheckedRadioButtonId();
            group.getChildAt(0);
            nothing.setVisibility(View.GONE);//隱藏
            switch (group.getCheckedRadioButtonId()) {
                case R.id.radioButton://未使用
                    choose_ticket_type="0";
                    create_list();
                    break;
                case R.id.radioButton2://已使用
                    choose_ticket_type="1";
                    create_list();
                    break;
                default: break;
            }
        }
    };

    private void create_list()//建立票券清單
    {
        final String url = temp_url.get_ticket_info(file_util.Read_loginInfo_account_Value(getActivity()),choose_ticket_type);//取得url 以及登入者帳號 0尚未使用 1已使用
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(getActivity());//loading視窗打開
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e(" TAG ", responseData);//偵錯用( 一行一行)
                    temp_load.loading_close();//loading視窗關閉

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        String[] ans = responseData.split("\\*");//分解字串
                                        ticket_list.setVisibility(View.VISIBLE);
                                        nothing.setVisibility(View.GONE);//

                                        //定義要寫入的容器
                                        final ArrayList<String> temp_activity_aid = new ArrayList<>();
                                        final ArrayList<String> temp_title = new ArrayList<>();
                                        final ArrayList<String> temp_time = new ArrayList<>();
                                        final ArrayList<String> temp_main_img = new ArrayList<>();
                                        final ArrayList<String> temp_user_img = new ArrayList<>();
                                        final ArrayList<String> temp_ticket_no = new ArrayList<>();

                                        for (int i = 0; i < ans.length; i++) {//i< ans.length
                                            String activity_cancel = "";
                                            final String[] temp = ans[i].split(",");//分解字串
                                            if(temp[1].equals("9")){activity_cancel="(活動已取消)";}
                                            temp_ticket_no.add(temp[2]);//票券代號 FARxxx
                                            temp_activity_aid.add(temp[3]);//票券編號
                                            temp_title.add(activity_cancel + temp[5]);//活動名稱
                                            temp_time.add(temp[6] );//活動時間 + " " + temp[7] + "-" + temp[8]
                                            temp_main_img.add(temp_url.get_url_img()  + temp[7]);//活動照片
                                            temp_user_img.add(temp_url.get_url_img() + temp[9]);//發起人頭貼
                                        }
                                        ticket_list.MyAdapter myAdapter = new ticket_list.MyAdapter(temp_activity_aid,temp_title, temp_time, temp_main_img, temp_ticket_no,temp_user_img);
                                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//getActivity()
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                        ticket_list.setLayoutManager(layoutManager);
                                        ticket_list.setAdapter(myAdapter);
                                    }else{//讀取資料失敗 顯示對應資訊
                                        String[] ans = responseData.split(",");//分解字串
                                        ticket_list.setVisibility(View.GONE);
                                        nothing.setVisibility(View.VISIBLE);//顯示 沒有票券的訊息
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
        //end
    }

    //Recyler start
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> temp_activity_aid;//票券id
        private List<String> temp_title;//值容量宣告
        private List<String> temp_time;
        private List<String> temp_main_img;
        private List<String> temp_ticket_no;
        private List<String> temp_user_img;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView activities_cardview ;
            public TextView activities_title;//物件宣告
            public TextView activities_time;
            public ImageView activities_main_img;//temp_main_img
            public TextView activities_ticket_no;
            public ImageView activities_user_img;//temp_main_img.

            public ViewHolder(View v) {//宣告
                super(v);
                // activities_title = (TextView) v.findViewById(R.id.info_text);
                activities_title = (TextView) v.findViewById(R.id.info_text);
                activities_time = (TextView) v.findViewById(R.id.info_time);
                activities_main_img = (ImageView) v.findViewById(R.id.info_cardimage);
                activities_user_img = (ImageView) v.findViewById(R.id.info_userpic);
                activities_ticket_no = (TextView) v.findViewById(R.id.info_hastag_1);
                activities_cardview = (CardView)v.findViewById(R.id.info_cardview);
            }
        }

        public MyAdapter(List<String> activity_aid,List<String> title,List<String> time,List<String> img,List<String> ticket_no ,List<String> user_img) {//存取準備要寫入的值
            temp_activity_aid = activity_aid;
            temp_title = title;
            temp_time = time;
            temp_main_img = img;
            temp_user_img = user_img;
            temp_ticket_no = ticket_no;
        }

        public void clearApplications() {//清除
            int size = temp_title.size();
            temp_activity_aid.clear();
            temp_title.clear();
            temp_time.clear();
            temp_main_img.clear();
            temp_user_img.clear();
            temp_ticket_no.clear();
            notifyItemRangeRemoved(0, size);
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//宣告與定義
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_activities_style, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {//實現要寫入的值
            holder.activities_title.setText(temp_title.get(position));
            holder.activities_time.setText(temp_time.get(position));
            interface_util.set_img(temp_main_img.get(position),holder.activities_main_img,getActivity());//
            interface_util.set_img(temp_user_img.get(position),holder.activities_user_img,getActivity());//發起人頭貼
            holder.activities_ticket_no.setText(temp_ticket_no.get(position));
            holder.activities_cardview.setOnClickListener(new View.OnClickListener() {//點擊後 跳到哪一個活動
                @Override
                public void onClick(View v) {
                    Intent jump=new Intent();
                    jump.setClass(getActivity(),ticket_information.class);
                    jump.putExtra("ticket_no",temp_activity_aid.get(position));
                    startActivity(jump);
                }
            });
        }

        @Override
        public int getItemCount() {
            return temp_title.size();
        }

    }
    //end
}
