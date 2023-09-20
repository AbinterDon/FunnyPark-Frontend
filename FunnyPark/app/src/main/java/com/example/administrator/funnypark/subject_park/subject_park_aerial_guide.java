package com.example.administrator.funnypark.subject_park;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.administrator.funnypark.R;

public class subject_park_aerial_guide extends AppCompatActivity {

    String park_id;//園區id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_park_aerial_guide);


        Intent jump = getIntent();
        park_id = jump.getStringExtra("park_id");

        VideoView vidView;
        MediaController vidControl;
        String vidAddress = "http://192.192.140.199/~D10516216/video/" + park_id + ".mp4";

        vidView = (VideoView) findViewById(R.id.myVideo);
        vidControl = new MediaController(subject_park_aerial_guide.this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);

        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        vidView.start();
    }

    public void finish_this_page(View v){
        this.finish();
    }
}
