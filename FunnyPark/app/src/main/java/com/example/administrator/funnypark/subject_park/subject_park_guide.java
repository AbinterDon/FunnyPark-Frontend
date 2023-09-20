package com.example.administrator.funnypark.subject_park;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.administrator.funnypark.R;

public class subject_park_guide extends AppCompatActivity {

    LinearLayout action_guide;
    LinearLayout subject_map;
    LinearLayout aerial_guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_park_guide);

        Dim();

        action_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = new Intent();
                jump.setClass(subject_park_guide.this,subject_park_choose.class);
                jump.putExtra("type","park_tour");
                startActivity(jump);
                /*
                Intent jump = getIntent();
                jump.setClass(subject_park_guide.this,subject_park_mobile_guide.class);
                startActivity(jump);
                 */

                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(subject_park_guide.this);
                builder.setTitle("尚未開放");
                builder.setMessage("施工中~敬請期待！");
                AlertDialog dialog = builder.create();
                dialog.show();
                 */
            }
        });

        subject_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(subject_park_guide.this);
                builder.setTitle("尚未開放");
                builder.setMessage("施工中~敬請期待！");
                AlertDialog dialog = builder.create();
                dialog.show();
                 */
                Intent jump = getIntent();
                jump.setClass(subject_park_guide.this,subject_park_choose.class);
                jump.putExtra("type","park_map");
                startActivity(jump);
            }
        });

        aerial_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jump = getIntent();
                jump.setClass(subject_park_guide.this,subject_park_choose.class);
                jump.putExtra("type","aerial_guide");
                startActivity(jump);
            }
        });

    }

    private void Dim(){
        action_guide = (LinearLayout)findViewById(R.id.action_guide);
        subject_map = (LinearLayout)findViewById(R.id.subject_map);
        aerial_guide = (LinearLayout)findViewById(R.id.aerial_guide);
    }

    public void finish_this_page(View v)//TODO  關閉頁面
    {
        this.finish();
    }
}
