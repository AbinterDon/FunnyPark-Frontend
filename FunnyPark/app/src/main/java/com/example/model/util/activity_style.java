package com.example.model.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.funnypark.R;
import com.example.model.util.interface_util;

public class activity_style extends AppCompatActivity {

    /*
    private interface_util interface_util = new interface_util();//call interface_util取介面方法
    private CardView temp_Cardview = create_Cardview();
    private LinearLayout temp_Linear_out = create_LinearLayout(LinearLayout.VERTICAL,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
    private ImageView temp_main_img = create_main_img();
    private LinearLayout temp_above_Linear = create_LinearLayout(LinearLayout.HORIZONTAL,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    private ImageView temp_user_img = create_user_img();
    private TextView temp_title = create_title();
    private TextView temp_time = create_time();
    private ImageView temp_cardline = create_cardline();
    private LinearLayout temp_hastag_Linear = create_LinearLayout(LinearLayout.HORIZONTAL,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
    private TextView temp_hastag = create_hastag();
    */

    //列出活動清單 暫時封存
    public void activity_style(String temp_title_value,String temp_time_value,String temp_img_value,String temp_hastag_value){//CardView
        //動態產生物件
        /*
        temp_title.setText(temp_title_value);
        temp_time.setText(temp_time_value);
        temp_main_img = interface_util.set_img(temp_img_value,temp_main_img,activity_style.this);
        temp_hastag.setText(temp_hastag_value);

        temp_above_Linear.addView(temp_user_img);//上層合併　Linear 發起人頭貼
        temp_above_Linear.addView(temp_title);//活動名稱
        temp_Linear_out.addView(temp_main_img); //活動照片
        temp_Linear_out.addView(temp_above_Linear);//外層Linear +入上層 **4/5
        temp_Linear_out.addView(temp_time);//時間
        temp_Linear_out.addView(temp_cardline);//格線
        temp_hastag_Linear.addView(temp_hastag);//hastag Linear
        temp_Linear_out.addView(temp_hastag_Linear);//hastag
        temp_Cardview.addView(temp_Linear_out);//加入cardview 外層Linear
        //temp_list.addView(temp_Cardview);//Linear add Cardview
        return temp_Cardview;
        */
    }

    /*public CardView get_activity(){
        return temp_Cardview;
    }*/

    /*
    private CardView create_Cardview (){//CardView 建立
        CardView temp_Cardview = new CardView(activity_style.this); //創建CardView ,CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.MATCH_PARENT
        temp_Cardview.setCardBackgroundColor(Color.rgb(0, 39, 56));//設定顏色
        temp_Cardview.setRadius(3);
        temp_Cardview.setElevation(15);
        temp_Cardview.setPreventCornerOverlap(true);//網路上看的辦法之一
        //Parms
        CardView.LayoutParams temp_Cardview_params = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,interface_util.transform_dp(activity_style.this,285));//285dp
        //設定margin
        temp_Cardview_params.setMargins(
                interface_util.transform_dp(19),//left
                0,//top
                interface_util.transform_dp(19),//right
                interface_util.transform_dp(16));//bottom
        temp_Cardview.setLayoutParams(temp_Cardview_params);
        return temp_Cardview;
    }

    private LinearLayout create_LinearLayout(int Orientation, int width, int height){//Linearout 建立
        LinearLayout temp_Linear = new LinearLayout(activity_style.this);
        temp_Linear.setOrientation(Orientation);
        LinearLayout.LayoutParams temp_Linear_params = new LinearLayout.LayoutParams(width, height);
        temp_Linear.setLayoutParams(temp_Linear_params);
        return temp_Linear;
    }


    private void create_ad(){//建立廣告
        LinearLayout temp_ad_broadcast = (LinearLayout) getView().findViewById(R.id.ad_broadcast);//廣告廣播的Linear
        temp_ad_broadcast.removeAllViews();//清除所有廣告廣播的內容

        ImageView temp_broadcast_img = create_broadcast_img();//create_broadcast_img
        temp_broadcast_img = set_img(temp_url.get_url() +"/~D10516216/images/team.jpg",temp_broadcast_img);//廣告圖片設定
        temp_ad_broadcast.addView(temp_broadcast_img);//套上廣告圖片
    }

    private ImageView create_broadcast_img(){//建立廣告圖片
        ImageView temp_broadcast_img = new ImageView(activity_style.this);
        temp_broadcast_img.setScaleType(ImageView.ScaleType.FIT_XY);
        //Parms

        LinearLayout.LayoutParams temp_broadcast_img_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,//LinearLayout.LayoutParams.MATCH_PARENT  / transform_dp(384)
                interface_util.transform_dp(240));

        temp_broadcast_img.setLayoutParams(temp_broadcast_img_params);
        return temp_broadcast_img;
    }

    private ImageView create_user_img(){//建立使用者大頭貼
        ImageView temp_user_img = new ImageView(activity_style.this);
        temp_user_img.setBackgroundColor(Color.rgb(239, 239, 239));
        //Parms
        LinearLayout.LayoutParams temp_user_img_params = new LinearLayout.LayoutParams(
                interface_util.transform_dp(26),
                interface_util.transform_dp(26));
        temp_user_img_params.setMargins(
                interface_util.transform_dp(13),//left
                interface_util.transform_dp(16),//top
                0,//right
                0);//bottom
        temp_user_img.setLayoutParams(temp_user_img_params);
        return temp_user_img;
    }

    private ImageView create_main_img(){//建立活動圖片 200
        ImageView temp_main_img = new ImageView(activity_style.this);
        temp_main_img.setBackgroundColor(Color.rgb(255, 255, 255));
        LinearLayout.LayoutParams temp_main_img_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                interface_util.transform_dp(200));
        temp_main_img.setScaleType(ImageView.ScaleType.FIT_XY);//android:scaleType="fitXY"
        temp_main_img.setLayoutParams(temp_main_img_params);
        return temp_main_img;
    }

    private ImageView create_cardline(){//建立格線
        ImageView temp_cardline = new ImageView(activity_style.this);
        LinearLayout.LayoutParams temp_cardline_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        temp_cardline_params.setMargins(
                interface_util.transform_dp(6),
                0,
                interface_util.transform_dp(6),
                interface_util.transform_dp(4));
        temp_cardline.setImageDrawable(getResources().getDrawable(R.drawable.cardline));
        temp_cardline.setLayoutParams(temp_cardline_params);
        return temp_cardline;
    }

    private TextView create_title(){//建立活動名稱
        TextView temp_title = new TextView(activity_style.this);
        LinearLayout.LayoutParams temp_title_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        temp_title_params.setMargins(
                interface_util.transform_dp(7),
                interface_util.transform_dp(18),
                0,
                0);
        temp_title.setTextColor(Color.rgb(255, 255, 255));
        temp_title.setTextSize(15);
        temp_title.setLayoutParams(temp_title_params);
        return temp_title;
    }

    private TextView create_time(){//建立活動時間
        TextView temp_time = new TextView(activity_style.this);
        LinearLayout.LayoutParams temp_time_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        temp_time_params.setMargins(
                0,
                interface_util.transform_dp(6),
                interface_util.transform_dp(7),
                interface_util.transform_dp(2));
        temp_time.setTextColor(Color.rgb(255, 255, 255));
        temp_time.setTextSize(7);
        temp_time.setGravity(Gravity.RIGHT);
        temp_time.setLayoutParams(temp_time_params);
        return temp_time;
    }

    private TextView create_hastag(){//建立活動標籤
        TextView temp_hastag = new TextView(activity_style.this);
        LinearLayout.LayoutParams temp_hastag_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        temp_hastag_params.setMargins(
                interface_util.transform_dp(13),
                0,
                0,
                interface_util.transform_dp(8));
        temp_hastag.setTextColor(Color.rgb(255, 255, 255));
        temp_hastag.setTextSize(10);
        temp_hastag.setTypeface(Typeface.DEFAULT_BOLD);
        temp_hastag.setLayoutParams(temp_hastag_params);
        return temp_hastag;
    }

    private TextView create_nothing_activities(){//建立 沒有活動
        TextView temp_nothing_activities = new TextView(activity_style.this);
        temp_nothing_activities.setGravity(Gravity.CENTER);
        temp_nothing_activities.setTextSize(15);
        temp_nothing_activities.setTextColor(Color.rgb(255, 255, 255));
        //Parms
        LinearLayout.LayoutParams temp_nothing_activities_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        temp_nothing_activities_params.setMargins(
                interface_util.transform_dp(7),
                interface_util.transform_dp(18),
                0,
                0);

        temp_nothing_activities.setLayoutParams(temp_nothing_activities_params);
        return temp_nothing_activities;
    }
    */
}
