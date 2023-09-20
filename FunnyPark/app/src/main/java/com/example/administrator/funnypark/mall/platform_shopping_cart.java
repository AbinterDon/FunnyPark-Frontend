package com.example.administrator.funnypark.mall;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.example.administrator.funnypark.R;
import com.example.model.util.connection_util;
import com.example.model.util.file_util;
import com.example.model.util.interface_util;
import com.example.model.util.loading_util;
import com.example.model.util.okhttp_util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class platform_shopping_cart extends AppCompatActivity {
    static AppCompatActivity view_shopping_cart;

    String user_mail;//取得登入會員信箱
    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    LinearLayout total_Linear ;//加總Linear
    TextView money_textview;
    TextView parkcoin_textview;
    TextView total_textview;
    TextView nothing_textview;
    Button btn_buy;
    RecyclerView shopping_cart_list;

    JSONArray json_product;//json傳購物的商品

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_shopping_cart);

        view_shopping_cart=this;

        Dim();//定義

        user_mail = file_util.Read_loginInfo_account_Value(platform_shopping_cart.this);//取得登入會員信箱

        create_shopping_list();//列出在購物車內的商品

        btn_buy.setOnClickListener(new View.OnClickListener() {//確認訂購
            @Override
            public void onClick(View v) {
                confirm_buy();
            }
        });
    }

    //宣告與定義
    private void Dim(){
        total_Linear = (LinearLayout)findViewById(R.id.total_Linear);
        //total_Linear.setVisibility(View.GONE);//隱藏結算介面
        nothing_textview = (TextView)findViewById(R.id.nothing);
        money_textview = (TextView)findViewById(R.id.money);
        parkcoin_textview = (TextView)findViewById(R.id.parkcoin);
        total_textview = (TextView)findViewById(R.id.total);
        btn_buy = (Button)findViewById(R.id.btn_buy);
        //btn_buy.setVisibility(View.GONE);//隱藏購買按鈕
        shopping_cart_list = platform_shopping_cart.this.findViewById(R.id.cart_product_list);//宣告recyler
        Nothing_product();//預設為沒有商品時的介面
    }

    //列出在購物車內的商品
    private void create_shopping_list() {
        json_product = new JSONArray();
        final String url = temp_url.get_store_car_detail(user_mail);//取得url
        final loading_util temp_load = new loading_util();//實體化loading視窗
        temp_load.loading_open(platform_shopping_cart.this);//loading視窗打開
        Log.e("url_tim",url);
        //start
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("res",responseData);
                    temp_load.loading_close();//loading視窗關閉
                    platform_shopping_cart.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!responseData.equals("Network connection failed")){//網路連線有無失敗

                                if(responseData.indexOf("error")<0){//讀取資料有無失敗
                                    total_Linear.setVisibility(View.VISIBLE);//顯示結算
                                    nothing_textview.setVisibility(View.GONE);//隱藏沒東西的標語
                                    btn_buy.setVisibility(View.VISIBLE);//顯示購買按鈕
                                    shopping_cart_list.setVisibility(View.VISIBLE);
                                    final String[] ans = responseData.split("\\*");//分解字串
                                    Log.e("res",responseData);
                                    final ArrayList<String> temp_id = new ArrayList<>();
                                    final ArrayList<String> temp_img = new ArrayList<>();
                                    final ArrayList<String> temp_name = new ArrayList<>();
                                    final ArrayList<String> temp_price= new ArrayList<>();
                                    final ArrayList<String> temp_parkcoin = new ArrayList<>();
                                    final ArrayList<String> temp_purchase_count= new ArrayList<>();
                                    final ArrayList<String> temp_stock= new ArrayList<>();

                                    //Log.e("count",String.valueOf(ans.length));
                                    int total_money=0,total_parkcoin=0;
                                    for (int i = 0; i< ans.length; i++) {//i< ans.length
                                        final String[] temp = ans[i].split(",");//分解字串
                                        //Json
                                        JSONObject temp_json = new JSONObject();
                                        try {
                                            temp_json.put("id",String.valueOf(i));//新增流水號到json
                                            temp_json.put("product_id",temp[0]);//新增商品代號到json
                                            temp_json.put("product_count",temp[6]);//新增時段流水號到json
                                            json_product.put(temp_json);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        //UI
                                        temp_id.add( temp[0]);//商品id
                                        temp_img.add(temp_url.get_url_img() +temp[1]);//商品圖片
                                        temp_name.add(temp[2]);//商品名稱
                                        temp_price.add(temp[3]);//商品價格
                                        temp_parkcoin.add(temp[4]);//商品parkcoin
                                        total_money += Integer.parseInt(temp[3])*Integer.parseInt(temp[6]);
                                        total_parkcoin += Integer.parseInt(temp[4])*Integer.parseInt(temp[6]);
                                        temp_stock.add(temp[5]);//商品庫存
                                        temp_purchase_count.add(temp[6]);//欲購買商品數量
                                    }
                                    money_textview.setText(String.valueOf(total_money));
                                    parkcoin_textview.setText(String.valueOf(total_parkcoin));
                                    total_textview.setText(total_money + " + " + total_parkcoin);//總金額
                                    //開始介面實現
                                    platform_shopping_cart.MyAdapter myAdapter = new platform_shopping_cart.MyAdapter(temp_id, temp_img, temp_name,temp_price,temp_parkcoin,temp_stock,temp_purchase_count);
                                    final LinearLayoutManager layoutManager = new LinearLayoutManager(platform_shopping_cart.this);//getActivity()
                                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//垂直佈局
                                    //ecyclerView mList = platform_shopping_cart.this.findViewById(R.id.cart_product_list);//宣告recyler
                                    shopping_cart_list.setLayoutManager(layoutManager);
                                    shopping_cart_list.setHasFixedSize(true);
                                    shopping_cart_list.setAdapter(myAdapter);
                                    //end
                                }else{//讀取資料失敗 顯示對應資訊
                                    Nothing_product();//預設為沒有商品時的介面
                                    String[] ans = responseData.split(",");
                                    Toast.makeText(platform_shopping_cart.this, ans[1], Toast.LENGTH_LONG).show();//顯示Toast
                                }
                            }else{
                                Toast toast = Toast.makeText(platform_shopping_cart.this, "網路連線失敗", Toast.LENGTH_LONG);
                                //顯示Toast
                                toast.show();
                            }
                        }
                    });
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Toast.makeText(platform_shopping_cart.this, "error100，若持續發生此問題，請聯絡我們", Toast.LENGTH_LONG).show();
                    Log.e(" TAG ", e.getMessage());//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        //end
    }

    //查詢與解析Json的值
    private int IndexOf(String Key,String type) throws JSONException {
        //Log.e("key",Key);
        for (int i = 0; i < json_product.length(); i++) {
            JSONObject jsonObject = json_product.getJSONObject(i);
            String temp_id = jsonObject.getString("product_id");//解析id內容
            if(temp_id.equals(Key)){
                //Log.e("remove",String.valueOf(i));
                json_product.remove(i);
                if(type.equals("modify")){//增加or減少商品數量
                    return Integer.parseInt(jsonObject.getString("id"));//回傳Json商品編號
                }else if(type.equals("Remove")){//減少商品數量
                    return i;//回傳購物車流水號(回傳索引值)
                }
            }
        }
        return -1;//找不到就回傳-1
    }

    //購物車沒有任何商品時
    private void Nothing_product(){
        nothing_textview.setVisibility(View.VISIBLE);//顯示沒東西的標語
        btn_buy.setVisibility(View.GONE);//隱藏購買按鈕
        total_Linear.setVisibility(View.GONE);//隱藏結算介面
        shopping_cart_list.setVisibility(View.GONE);//隱藏Recyer
    }

    private void confirm_buy(){//確認購買
        new AlertDialog.Builder(platform_shopping_cart.this)
                .setTitle("購物車確認")
                .setMessage("請再次確認商品數量是否有誤")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent jump = getIntent();
                        jump.setClass(platform_shopping_cart.this, platform_product_payment.class);
                        jump.putExtra("product_json", String.valueOf(json_product));//把欲購買商品的json帶到下一個頁面
                        startActivity(jump);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //使用者取消刪除此商品
                    }
                })
                .show();
    }


    //    返回上一頁
    public void finish_this_page(View v) {
        this.finish();
    }

    //Recyler
    public class MyAdapter extends RecyclerView.Adapter<platform_shopping_cart.MyAdapter.ViewHolder> {
        private List<String> temp_id;
        private List<String> temp_img;
        private List<String> temp_name;
        private List<String> temp_price;
        private List<String> temp_parkcoin;
        private List<String> temp_product_stock;
        private List<String> temp_purchase_count;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView product_cardview ;
            public ImageView product_img;//temp_main_img.
            public TextView product_name;
            public TextView product_price_parkcoin;
            public TextView product_count;
            public ImageView product_add;
            public ImageView product_reduce;

            public ViewHolder(View v) {
                super(v);
                product_cardview = (CardView)v.findViewById(R.id.product_cardview);
                product_img = (ImageView) v.findViewById(R.id.product_img);
                product_name = (TextView) v.findViewById(R.id.product_name);
                product_price_parkcoin = (TextView) v.findViewById(R.id.product_price);
                product_count = (TextView) v.findViewById(R.id.product_count);//購買數量
                product_add = (ImageView)v.findViewById(R.id.product_count_add);//增加數量
                product_reduce = (ImageView)v.findViewById(R.id.product_count_reduce);//減少數量
            }
        }

        public MyAdapter(List<String> id,
                         List<String> img,
                         List<String> name,
                         List<String> price,
                         List<String> parkcoin,
                         List<String> stock,
                         List<String> purchase_count) {
            temp_id = id;
            temp_img = img;
            temp_name = name;
            temp_price = price;
            temp_parkcoin = parkcoin;
            temp_product_stock = stock;
            temp_purchase_count = purchase_count;
        }

        @Override
        public platform_shopping_cart.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.shopping_cart_style, parent, false);
            platform_shopping_cart.MyAdapter.ViewHolder vh = new platform_shopping_cart.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final platform_shopping_cart.MyAdapter.ViewHolder holder, final int position) {
            holder.product_name.setText(temp_name.get(position));//商品名稱
            holder.product_price_parkcoin.setText("NT." + temp_price.get(position) + " + " + temp_parkcoin.get(position) + "PC");//商品價格
            interface_util.set_img(temp_img.get(position),holder.product_img,platform_shopping_cart.this);//商品圖片
            holder.product_count.setText(temp_purchase_count.get(position));//商品欲購買數量

            holder.product_add.setOnClickListener(new View.OnClickListener() {//增加欲購買數量
                @Override
                public void onClick(View v) {
                    final int temp_count = Integer.parseInt(holder.product_count.getText().toString())+1;
                    //Log.e("tag0",json_product.toString());
                    //Log.e("+",String.valueOf(temp_count));
                    //Log.e("+",String.valueOf(Integer.parseInt(temp_product_stock.get(position))));
                    if(Integer.parseInt(temp_product_stock.get(position)) >= temp_count) {
                        final String url = temp_url.store_car_alter(temp_id.get(position),user_mail,String.valueOf(temp_count));//取得url
                        final loading_util temp_load = new loading_util();//實體化loading視窗
                        temp_load.loading_open(platform_shopping_cart.this);//loading視窗打開
                        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                            @Override
                            public void run() {
                                try {//動到io(網路有關)一定要用try與執行緒
                                    final String responseData = client.urlpost(url);//連線後端 取得資料
                                    //Log.e("alter_url",responseData);
                                    temp_load.loading_close();//關閉loading視窗
                                    if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(responseData.equals("y,")){//若是數量更改成功的話
                                                    holder.product_count.setText(String.valueOf(temp_count));//增加購買數量1
                                                    //改變底下計算總額
                                                    money_textview.setText(String.valueOf(Integer.parseInt(money_textview.getText().toString())
                                                            + Integer.parseInt(temp_price.get(position))));//金額總額加總
                                                    parkcoin_textview.setText(String.valueOf(Integer.parseInt(parkcoin_textview.getText().toString()) + Integer.parseInt(temp_parkcoin.get(position))));//Parkcoin總額加總
                                                    total_textview.setText(money_textview.getText().toString() + " + " + parkcoin_textview.getText().toString());//總金額加總
                                                    //增加購買數量後 修改Json內的資料
                                                    try {
                                                        JSONObject temp_json = new JSONObject();//創建新的JsonObject
                                                        temp_json.put("id",IndexOf(temp_id.get(position),"modify"));//傳送給IndexOf商品id
                                                        temp_json.put("product_id",temp_id.get(position));
                                                        temp_json.put("product_count",String.valueOf(temp_count));
                                                        json_product.put(temp_json);
                                                        Log.e("tag",json_product.toString());
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{//增加失敗的話
                                                    platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast toast = Toast.makeText(platform_shopping_cart.this, "商品增加失敗", Toast.LENGTH_LONG);
                                                            //顯示Toast
                                                            toast.show();
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }else{
                                        platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast toast = Toast.makeText(platform_shopping_cart.this, "網路連線失敗", Toast.LENGTH_LONG);
                                                //顯示Toast
                                                toast.show();
                                            }
                                        });
                                    }
                                    //end 網路連線
                                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                                    platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(platform_shopping_cart.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                }
                            }
                        };
                        okHttpExecuteThread.start();
                        //end
                    }else{
                        platform_shopping_cart.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(platform_shopping_cart.this, "已達庫存上限。", Toast.LENGTH_LONG);
                                //顯示Toast
                                toast.show();
                            }
                        });
                    }
                }
            });

            holder.product_reduce.setOnClickListener(new View.OnClickListener() {//減少欲購買數量
                @Override
                public void onClick(View v) {
                    final int temp_count = Integer.parseInt(holder.product_count.getText().toString())-1;
                    //Log.e("tag0",json_product.toString());
                    if( 1 <= temp_count) {

                        final String url = temp_url.store_car_alter(temp_id.get(position),user_mail,String.valueOf(temp_count));//取得url
                        final loading_util temp_load = new loading_util();//實體化loading視窗
                        temp_load.loading_open(platform_shopping_cart.this);//loading視窗打開
                        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                            @Override
                            public void run() {
                                try {//動到io(網路有關)一定要用try與執行緒
                                    final String responseData = client.urlpost(url);//連線後端 取得資料
                                    //Log.e("alter_url",responseData);
                                    temp_load.loading_close();//關閉loading視窗
                                    if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                                       runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(responseData.equals("y,")){//若是數量更改成功的話
                                                    //改變底下計算總額
                                                    holder.product_count.setText(String.valueOf(temp_count));//減少購買數量1
                                                    money_textview.setText(String.valueOf(Integer.parseInt(money_textview.getText().toString()) - Integer.parseInt(temp_price.get(position))));//金額總額加總
                                                    parkcoin_textview.setText(String.valueOf(Integer.parseInt(parkcoin_textview.getText().toString()) - Integer.parseInt(temp_parkcoin.get(position))));//Parkcoin總額加總
                                                    total_textview.setText(money_textview.getText().toString() + " + " + parkcoin_textview.getText().toString());//總金額加總
                                                    //減少購買數量後 修改Json內的資料
                                                    try {
                                                        JSONObject temp_json = new JSONObject();//創建新的JsonObject
                                                        temp_json.put("id",IndexOf(temp_id.get(position),"modify"));//傳送給IndexOf商品id
                                                        temp_json.put("product_id",temp_id.get(position));
                                                        temp_json.put("product_count",String.valueOf(temp_count));
                                                        json_product.put(temp_json);
                                                        Log.e("tag",json_product.toString());
                                                        //IndexOf(temp_id.get(position),"reduce");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{//減少失敗的話
                                                    platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast toast = Toast.makeText(platform_shopping_cart.this, "商品減少失敗", Toast.LENGTH_LONG);
                                                            //顯示Toast
                                                            toast.show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }else{
                                        platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast toast = Toast.makeText(platform_shopping_cart.this, "網路連線失敗", Toast.LENGTH_LONG);
                                                //顯示Toast
                                                toast.show();
                                            }
                                        });
                                    }
                                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                                    platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(platform_shopping_cart.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
                                            //顯示Toast
                                            toast.show();
                                        }
                                    });
                                }
                            }
                        };
                        okHttpExecuteThread.start();

                    }else if( 0 == temp_count){//若是選購數量==0 代表使用者要刪除此商品
                        new AlertDialog.Builder(platform_shopping_cart.this)
                                .setTitle("購物車選項刪除")
                                .setMessage("確認要刪除『" + temp_name.get(position) + "』嗎?")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        final String url = temp_url.store_car_delete(temp_id.get(position),user_mail);//取得url
                                        final loading_util temp_load = new loading_util();//實體化loading視窗
                                        temp_load.loading_open(platform_shopping_cart.this);//loading視窗打開
                                        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
                                            @Override
                                            public void run() {
                                                try {//動到io(網路有關)一定要用try與執行緒
                                                    final String responseData = client.urlpost(url);//連線後端 取得資料
                                                    //Log.e("detele_url",responseData);
                                                    temp_load.loading_close();//關閉loading視窗
                                                    if(!responseData.equals("Network connection failed")){//網路連線有無失敗
                                                        platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if(responseData.equals("y,")){//若是刪除成功的話
                                                                    create_shopping_list();
                                                                    Toast.makeText(platform_shopping_cart.this, "商品刪除成功", Toast.LENGTH_LONG).show();
                                                                }else{//刪除失敗的話
                                                                    platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Toast.makeText(platform_shopping_cart.this, "商品刪除失敗", Toast.LENGTH_LONG).show();
                                                                            //顯示Toast
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }else{
                                                        platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast toast = Toast.makeText(platform_shopping_cart.this, "網路連線失敗", Toast.LENGTH_LONG);
                                                                //顯示Toast
                                                                toast.show();
                                                            }
                                                        });
                                                    }
                                                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                                                    platform_shopping_cart.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast toast = Toast.makeText(platform_shopping_cart.this, "error100，若持續發生此問題，請聯繫負責人。", Toast.LENGTH_LONG);
                                                            //顯示Toast
                                                            toast.show();
                                                        }
                                                    });
                                                }
                                            }
                                        };
                                        okHttpExecuteThread.start();
                                        //網路連線訊息
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //使用者取消刪除此商品
                                    }
                                })
                                .show();
                    }//end
                }
            });

            /*
             holder.product_cardview.setOnClickListener(new View.OnClickListener() {//點擊後 顯示哪一個商品
                @Override
                public void onClick(View v) {
                    Intent jump=new Intent();
                    jump.setClass(getActivity(),platform_product_information.class);
                    jump.putExtra("activity",temp_id.get(position));
                    startActivity(jump);
                }
            });
             */

        }

        @Override
        public int getItemCount() {
            return temp_id.size();
        }

        //  刪除數據
        public void removeData(int position) {
            //Log.e("position",String.valueOf(position));
            temp_id.remove(position);
            temp_img.remove(position);
            temp_name.remove(position);
            temp_price.remove(position);
            temp_parkcoin.remove(position);
            temp_product_stock.remove(position);
            /*
            for(int i=0;i<temp_id.size();i++){
                Log.e("data",temp_id.get(i) + "-" + temp_img.get(i) + "-" + temp_name.get(i) + "-" + temp_price.get(i) + "-" + temp_parkcoin.get(i) + "-" + temp_product_stock.get(i));
            }
             */
            //删除動畫
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }

    }

    //end
}