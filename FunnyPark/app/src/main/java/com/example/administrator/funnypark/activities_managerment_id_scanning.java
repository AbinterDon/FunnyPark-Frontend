package com.example.administrator.funnypark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.funnypark.member.member_friends_center;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class activities_managerment_id_scanning extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_managerment_id_scanning);
    }




    public void scan_qrcode(View v)//TODO  掃描qrcode
    {
        IntentIntegrator integrator = new IntentIntegrator(activities_managerment_id_scanning.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("對準QrCode進行掃描");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);//改為直是
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)//TODO Qrcode 掃描結果
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result!= null)
        {
            if (result.getContents()==null)//若是關閉掃描
            {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else//顯示掃瞄到的內容
            {
                //Toast.makeText(this,result.getContents(),Toast.LENGTH_LONG).show();
                //check_friend(result.getContents());//傳遞掃瞄到的好友帳號
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void finish_this_page(View v)//TODO  關閉頁面
    {
        this.finish();
    }
}
