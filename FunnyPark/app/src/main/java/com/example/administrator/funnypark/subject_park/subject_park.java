package com.example.administrator.funnypark.subject_park;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.activities.activities_information;
import com.example.administrator.funnypark.new_attention;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.okhttp_util;
import com.example.model.util.interface_util;

import java.util.ArrayList;
import java.util.List;

public class subject_park extends Fragment  {

    public subject_park() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_subject_park, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)//onActivityCreated onCreate
    {
        super.onActivityCreated(savedInstanceState);

        final LinearLayout park_tour = (LinearLayout) getView().findViewById(R.id.park_tour);//園區導覽
        final LinearLayout park_discount = (LinearLayout) getView().findViewById(R.id.park_discount);//園區折扣
        final LinearLayout park_activities = (LinearLayout) getView().findViewById(R.id.park_activities);//園區活動

        //園區導覽
        park_tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();
                jump.setClass(getActivity(),subject_park_guide.class);
                startActivity(jump);
            }
        });

        //園區折扣
        park_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();
                jump.setClass(getActivity(),subject_park_choose.class);
                jump.putExtra("type","discount");
                startActivity(jump);
            }
        });

        //園區活動
        park_activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();
                jump.setClass(getActivity(),subject_park_choose.class);
                jump.putExtra("type","activities");
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

        //推播提醒
        ImageView btn_notice;
        btn_notice = (ImageView)getView().findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();//跳畫面
                jump.setClass(getActivity(), new_attention.class);
                startActivity(jump);
            }
        });

        //end
    }
}
