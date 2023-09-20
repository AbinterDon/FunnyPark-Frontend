package com.example.administrator.funnypark.mall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.funnypark.GlideImageLoader;
import com.example.administrator.funnypark.R;
import com.example.model.util.attention_util;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class platform_mall extends Fragment implements OnBannerListener {

    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    LinearLayout ad_layout;
    List<String> ad_type = new ArrayList<>();
    List<String> ad_context = new ArrayList<>();
    private Banner ad_Banner;


    public platform_mall() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_platform_mall, container, false);

        //列出符合條件之活動
        create_grid();//建立商品清單

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)//onActivityCreated onCreate
    {
        super.onActivityCreated(savedInstanceState);

        //定義宣告
        Dim();

        //建立廣告
        //Banner ad_img = (Banner)getView().findViewById(R.id.mall_ad_img);
        //interface_util.set_img(temp_url.get_url() +"/~D10516216/images/platform_mall_ad.jpg",ad_img,getActivity());
        create_ad();

        //前往購物車
        FloatingActionButton btn_initiate = (FloatingActionButton)getView().findViewById(R.id.btn_shopping_cart);
        btn_initiate.setOnClickListener(new View.OnClickListener() {//發起活動監聽器
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();//跳畫面
                jump.setClass(getActivity(), platform_shopping_cart.class);
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
        btn_search = (ImageView)getView().findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_mail;//使用者登入帳號
                user_mail = file_util.Read_loginInfo_account_Value(getActivity());//取得登入會員信箱
                com.example.model.util.attention_util.add_attention("1","以下是您於商場購買商品的匯款資訊" +
                        "轉帳金額：" + "123" +
                        "銀行名稱：" + "321" +
                        "分行名稱：" + "456" +
                        "匯款名稱：" + "789" ,user_mail);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("尚未開放");
                builder.setMessage("施工中~敬請期待！");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ImageView btn_my_product;//我的商品
        btn_my_product = (ImageView)getView().findViewById(R.id.btn_my_product);
        btn_my_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("尚未開放");
                builder.setMessage("施工中~敬請期待！");
                AlertDialog dialog = builder.create();
                dialog.show();
                 */
                Intent jump = getActivity().getIntent();//實體化 抓資料
                jump.setClass(getActivity(),platform_product_exchange_list.class);//跳往商品兌換頁面
                startActivity(jump);
            }
        });

    }

    private void Dim(){//定義
        ad_layout = (LinearLayout)getActivity().findViewById(R.id.ad_layout);//廣告刊版
        ad_Banner = (Banner)getActivity().findViewById(R.id.mall_ad_img);//廣告容器
    }

    private void create_grid()//建立商品清單
    {
        final String url = temp_url.get_store_info();//取得url 0 熱門活動 1最新活動 2進行中活動
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlget(url);//連線後端 取得資料

                    getActivity().runOnUiThread(new Runnable() {//
                        @Override
                        public void run() {
                            try{
                                if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                    if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                        final String[] ans = responseData.split("\\*");//分解字串
                                        Log.e("res",responseData);
                                        final ArrayList<String> temp_id = new ArrayList<>();
                                        final ArrayList<String> temp_img = new ArrayList<>();
                                        final ArrayList<String> temp_name = new ArrayList<>();
                                        final ArrayList<String> temp_price_parkcoin = new ArrayList<>();
                                        final ArrayList<String> temp_stock = new ArrayList<>();
                                        for (int i = 0; i< ans.length; i++) {//i< ans.length
                                            //Log.e("res5", ans[i]);
                                            final String[] temp = ans[i].split(",");//分解字串
                                            temp_id.add( temp[0]);//商品id
                                            temp_img.add(temp_url.get_url_img() +temp[1]);//商品圖片
                                            temp_name.add(temp[4]);//商品名稱
                                            temp_price_parkcoin.add("NT." + temp[5] + " + " + temp[6] + "PC");//商品價格
                                            temp_stock.add(temp[7]);//商品庫存
                                        }
                                        //開始介面實現
                                        platform_mall.MyAdapter myAdapter = new platform_mall.MyAdapter(temp_id, temp_img, temp_name,temp_price_parkcoin,temp_stock);
                                        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL,false);
                                        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//getActivity()
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                        //layoutManager
                                        RecyclerView mList = getActivity().findViewById(R.id.product_grid);
                                        mList.setLayoutManager(layoutManager);
                                        mList.setHasFixedSize(true);
                                        mList.setAdapter(myAdapter);
                                        //end
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
                    Toast.makeText(getActivity(), "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    private void create_ad(){//建立廣告
        final String url = temp_url.get_ad_info("102");//取得url
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
            if(ad_type.get(position).equals("1")){//導向是App內的平台商品
                Intent jump = new Intent();
                jump.setClass(getActivity(),platform_product_information.class);
                jump.putExtra("product_id",ad_context.get(position));
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

    //Recyler
    public class MyAdapter extends RecyclerView.Adapter<platform_mall.MyAdapter.ViewHolder> {
        private List<String> temp_id;
        private List<String> temp_img;
        private List<String> temp_name;
        private List<String> temp_price_parkcoin;
        private List<String> temp_price_stock;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView  product_cardview ;
            public ImageView product_img;//temp_main_img.
            public TextView product_name;
            public TextView product_price_parkcoin;
            public Button product_add;

            public ViewHolder(View v) {
                super(v);
                product_cardview = (CardView)v.findViewById(R.id.product_cardview);
                product_img = (ImageView) v.findViewById(R.id.product_img);
                product_name = (TextView) v.findViewById(R.id.product_name);
                product_price_parkcoin = (TextView) v.findViewById(R.id.product_price);
                product_add = (Button)v.findViewById(R.id.product_add);//加入購物車
            }
        }

        public MyAdapter(List<String> id,
                         List<String> img,
                         List<String> name,
                         List<String> price,
                         List<String> stock) {
            temp_id = id;
            temp_img = img;
            temp_name = name;
            temp_price_parkcoin = price;
            temp_price_stock = stock;
        }

        @Override
        public platform_mall.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.shopping_product_style, parent, false);
            platform_mall.MyAdapter.ViewHolder vh = new platform_mall.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final platform_mall.MyAdapter.ViewHolder holder, final int position) {
            holder.product_name.setText(temp_name.get(position));//商品名稱
            holder.product_price_parkcoin.setText(temp_price_parkcoin.get(position));//商品價格
            interface_util.set_img(temp_img.get(position),holder.product_img,getActivity());//商品圖片
            holder.product_cardview.setOnClickListener(new View.OnClickListener() {//點擊後 顯示哪一個商品
                @Override
                public void onClick(View v) {
                    Intent jump=new Intent();
                    jump.setClass(getActivity(),platform_product_information.class);
                    jump.putExtra("product_id",temp_id.get(position));
                    startActivity(jump);
                }
            });
            Log.e("stock",temp_price_stock.get(position));
            if(!temp_price_stock.get(position).equals("0")){//若商品庫存不等於0
                holder.product_add.setOnClickListener(new View.OnClickListener() {//點擊後 加入購物車
                    @Override
                    public void onClick(View v) {
                        shopping_cart_add(temp_id.get(position),holder.product_add);
                    }
                });
            }else{
                holder.product_add.setText("已售完");
                holder.product_add.setEnabled(false);//鎖住按鈕
            }


        }

        @Override
        public int getItemCount() {
            return temp_id.size();
        }

        private void shopping_cart_add(final String product_id, final Button btn_add)//加入購物車 帶入商品id與[加入購物車]按鈕物件
        {
            String user_mail = file_util.Read_loginInfo_account_Value(getActivity());//取得登入會員信箱
            final String url = temp_url.store_car_add(product_id,user_mail);//取得url
            //start
            //Log.e("url",url);
            Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                @Override
                public void run() {
                    try {//動到io(網路有關)一定要用try與執行緒
                        final String responseData = client.urlget(url);//連線後端 取得資料
                        Log.e("res",responseData);

                        getActivity().runOnUiThread(new Runnable() {//
                            @Override
                            public void run() {
                                try{
                                    if(!responseData.equals("Network connection failed")) {//網路連線有無失敗
                                        String ans[] = responseData.split(",");
                                        if(ans[0].equals("y")){
                                            Toast.makeText(getActivity(), "加入成功", Toast.LENGTH_LONG).show();
                                            btn_add.setText("已加入購物車");
                                            btn_add.setBackgroundColor(Color.rgb(0,69,100));
                                            btn_add.setEnabled(false);
                                            //004564
                                        }else{
                                            Toast.makeText(getActivity(), ans[1], Toast.LENGTH_LONG).show();
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
    }
    //end
}
