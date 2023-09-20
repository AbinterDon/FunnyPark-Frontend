package com.example.administrator.funnypark.activities;


import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.GlideImageLoader;
import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.new_attention;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class activities_list extends Fragment implements OnBannerListener {

    public activities_list() {
        // Required empty public constructor
    }

    private interface_util interface_util = new interface_util();//call interface_util取介面方法
    private String choose_activities_type;//選擇要察看的活動清單　//熱門/最新/進行中
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    RecyclerView activities_list ;//活動清單
    TextView nothing;//沒有任何活動時的警告
    LinearLayout ad_layout;
    List<String> ad_type = new ArrayList<>();
    List<String> ad_context = new ArrayList<>();
    private Banner ad_Banner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_activities_list, container, false);

        //預設選擇熱門活動
        choose_activities_type = "0";

        //列出符合條件之活動
        create_list();

        //建立廣告廣播
        create_ad();

        return view;
    }

    private void Dim(){//定義
        ad_layout = (LinearLayout)getActivity().findViewById(R.id.ad_layout);//廣告刊版
        ad_Banner = (Banner)getActivity().findViewById(R.id.ad_img);//廣告容器

        //現在沒有任何活動的警告
        nothing = (TextView)getView().findViewById(R.id.nothing);

        //活動列表
        activities_list = (RecyclerView)getView().findViewById(R.id.activities_list);

        //設定活動類型
        RadioGroup radioGroup=(RadioGroup)getView().findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(listen);//設定RadioGroup監聽器
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)//onActivityCreated onCreate
    {
        //super.onCreate(savedInstanceState);
        super.onActivityCreated(savedInstanceState);

        Dim();//定義

        FloatingActionButton btn_initiate = (FloatingActionButton)getView().findViewById(R.id.btn_initiate);//發起活動
        btn_initiate.setOnClickListener(new View.OnClickListener() {//發起活動監聽器
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();//跳畫面
                jump.setClass(getActivity(), activities_initiate.class);
                startActivity(jump);
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
            switch (group.getCheckedRadioButtonId()) {
                case R.id.radioButton://熱門活動
                    choose_activities_type="0";
                    create_list();
                    break;
                case R.id.radioButton2://最新活動
                    choose_activities_type="1";
                    create_list();
                    break;
                case R.id.radioButton3://進行中
                    choose_activities_type="2";
                    create_list();
                    break;
                default: break;
            }
        }
    };

    private void create_ad(){//建立廣告
        final String url = temp_url.get_ad_info("101");//取得url
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
                                        String[] ad_part = responseData.split("\\*");
                                        List<String> images = new ArrayList<>();
                                        for(int i=0;i<ad_part.length;i++){
                                            String[] ans = ad_part[i].split(",");
                                            Log.e("image", temp_url.get_url_img() + ans[1]);
                                            ad_type.add(ans[0]);//廣告形態 1往app外導向 2自己app內導向
                                            images.add(temp_url.get_url_img() + ans[1]);//廣告圖片
                                            if(ans.length<3) {
                                                ad_context.add("null");//廣告內容 (導向的值 url or 活動代號)
                                            }else{
                                                ad_context.add(ans[2]);//廣告內容 (導向的值 url or 活動代號)
                                            }
                                        }
                                        create_ad_layout(images);//創建廣告容器
                                    }else{//讀取資料失敗 顯示對應資訊
                                        //String[] ans = responseData.split(",");
                                        //Toast.makeText(getActivity(), ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                        ad_layout.setVisibility(View.GONE);//隱藏活動刊版
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
                    //Toast.makeText(getActivity(), "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    private void create_ad_layout( List<String> images ){
        //設定你的Banner想要什麼樣式的，我選的樣式是圖片下面有圓點點，如果要其他的就到他的Git自己去看囉
        ad_Banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //把想要呈現的圖片設定到banner
        ad_Banner.setImages(images)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerListener((OnBannerListener) this)
                //設定每張圖片要呈現的時間
                .setDelayTime(3000)
                .start();
    }

    @Override
    public void OnBannerClick(int position) {//點擊事件
        try{
            if(ad_type.get(position).equals("1")){//導向是App內的活動
                Intent jump = new Intent();
                jump.setClass(getActivity(),activities_information.class);
                jump.putExtra("activity",ad_context.get(position));
                startActivity(jump);
            }else if(ad_type.get(position).equals("2")){//導向至APP外的網站
                Intent jump = new Intent();
                jump.setAction(Intent.ACTION_VIEW);
                jump.addCategory(Intent.CATEGORY_BROWSABLE);
                jump.setData(Uri.parse(ad_context.get(position)));//"https://www.instagram.com/funnypark.official/"
                startActivity(jump);
            }else if(ad_type.get(position).equals("0")){}//展示用
            //Toast.makeText(getActivity(),"點擊了："+position,Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getActivity(),"error100，若持續發生此狀況，請聯絡我們",Toast.LENGTH_SHORT).show();
        }
    }

    private void create_list()//建立活動清單
    {
        final String url = temp_url.get_activity_info(choose_activities_type);//取得url 0 熱門活動 1最新活動 2進行中活動
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
                                        activities_list.MyAdapter myAdapter = new activities_list.MyAdapter(temp_title, temp_time, temp_main_img,temp_ticket, temp_hastag_1,temp_hastag_2,temp_hastag_3,temp_activity_id,temp_user_img);
                                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//getActivity()
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                        RecyclerView mList = getActivity().findViewById(R.id.activities_list);
                                        mList.setLayoutManager(layoutManager);
                                        mList.setHasFixedSize(true);
                                        mList.setAdapter(myAdapter);
                                        //end

                                    }else{//讀取資料失敗 顯示對應資訊
                                        activities_list.setVisibility(View.GONE);
                                        nothing.setVisibility(View.VISIBLE);
                                        String[] ans = responseData.split(",");
                                        Toast.makeText(getActivity(), ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                    }
                                }else {//網路連線失敗
                                    Toast.makeText(getActivity(), "伺服器連接失敗", Toast.LENGTH_LONG).show();
                                    //顯示Toast
                                }
                            }catch (Exception e){
                                getActivity().finish();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(getActivity(), "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    //Recyler
    public class MyAdapter extends RecyclerView.Adapter<activities_list.MyAdapter.ViewHolder> {
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
        public activities_list.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_activities_style, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(activities_list.MyAdapter.ViewHolder holder, final int position) {
            holder.activities_title.setText(temp_title.get(position));
            holder.activities_time.setText(temp_time.get(position));
            interface_util.set_img(temp_main_img.get(position),holder.activities_main_img,getActivity());
            interface_util.set_img(temp_user_img.get(position),holder.activities_user_img,getActivity());
            holder.activities_ticket.setText(temp_ticket.get(position));
            holder.activities_hastag_1.setText(temp_hastag_1.get(position));
            holder.activities_hastag_2.setText(temp_hastag_2.get(position));
            holder.activities_hastag_3.setText(temp_hastag_3.get(position));
            holder.activities_cardview.setOnClickListener(new View.OnClickListener() {//點及後 跳到哪一個活動
                @Override
                public void onClick(View v) {
                    Intent jump=new Intent();
                    jump.setClass(getActivity(),activities_information.class);
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
