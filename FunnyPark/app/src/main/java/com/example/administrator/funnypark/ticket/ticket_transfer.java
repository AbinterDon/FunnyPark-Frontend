package com.example.administrator.funnypark.ticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.funnypark.R;
import com.example.administrator.funnypark.activities_managerment_id_scanning;
import com.example.model.util.connection_util;
import com.example.model.util.interface_util;
import com.example.model.util.okhttp_util;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ticket_transfer extends AppCompatActivity {
    private com.example.model.util.interface_util interface_util = new interface_util();//call interface_util取介面方法
    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    Button btn_confirm;
    EditText code_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_transfer);

        Dim();
    }

    private void Dim(){//宣告定義
        code_text = (EditText)findViewById(R.id.id_code);//輸入框
        btn_confirm = (Button)findViewById(R.id.btn_confirm);//確認按鈕
    }

    public void scan_qrcode(View v)//TODO  掃描qrcode
    {
        IntentIntegrator integrator = new IntentIntegrator(ticket_transfer.this);
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
                code_text.setText(result.getContents());
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
