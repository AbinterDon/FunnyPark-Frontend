package com.example.administrator.funnypark.mall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.funnypark.R;
import com.example.model.util.attention_util;
import com.example.model.util.file_util;

import org.w3c.dom.Text;

public class platform_product_successful_payment extends AppCompatActivity {

    String user_mail;//使用者登入帳號
    TextView pay_info,total_money,bank_name,bank_branch,bank_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_product_successful_payment);

        Dim();//定義宣告

        Intent jump = getIntent();
        String payment_type = jump.getStringExtra("payment_type");//付款方式
        //String temp_pay_info = "";//總金額
        String temp_total_money = "";//總金額
        String temp_bank_name = "";//銀行名稱
        String temp_bank_branch = "";//分行名稱
        String temp_bank_account = "";//匯款帳號

        temp_total_money = jump.getStringExtra("total_money");
        total_money.setText("轉帳金額：" + temp_total_money);//取得打包的銀行id資料

        if(payment_type.equals("ATM轉帳")){
            temp_bank_name = jump.getStringExtra("bank_name");
            temp_bank_branch = jump.getStringExtra("bank_branch");
            temp_bank_account = jump.getStringExtra("bank_account");
            pay_info.setText("以下是您的匯款資訊：");
            bank_name.setText("銀行名稱：" + temp_bank_name);//取得打包的銀行分行資料
            bank_branch.setText("分行名稱：" + temp_bank_branch);//取得打包的銀行分行資料
            bank_account.setText("匯款帳號：" + temp_bank_account);//取得打包的銀行分行資料
            //寫入通知
            attention_util.add_attention("1","以下是您於商場購買商品的匯款資訊" +
                    "\n" + total_money.getText().toString() +
                    "\n銀行名稱：" + bank_name.getText().toString() +
                    "\n分行名稱：" + bank_branch.getText().toString() +
                    "\n匯款名稱：" + bank_account.getText().toString(),user_mail);
        }else if( payment_type.equals("現金")){
            pay_info.setText("以下是您的付款資訊：");
            bank_name.setVisibility(View.GONE);
            bank_branch.setVisibility(View.GONE);
            bank_account.setVisibility(View.GONE);
            //寫入通知
            attention_util.add_attention("1","您於商城購買了商品，總金額為：" + total_money.getText().toString(),user_mail);
        }
        user_mail = file_util.Read_loginInfo_account_Value(platform_product_successful_payment.this);//取得登入會員信箱
    }

    private void Dim(){//宣告定義
        pay_info = (TextView)findViewById(R.id.pay_info);
        total_money = (TextView)findViewById(R.id.total_money);
        bank_name = (TextView)findViewById(R.id.bank_name);
        bank_branch = (TextView)findViewById(R.id.bank_branch);
        bank_account = (TextView)findViewById(R.id.bank_account);
    }

    public void btn_exchange_list(View v){//查看清單
        Intent jump = getIntent();
        jump.setClass(platform_product_successful_payment.this,platform_product_exchange_list.class);
        jump.putExtra("user_mail",user_mail);//傳遞會員帳號
        startActivity(jump);
    }

    public void finish_this_page(View v)//TODO  關閉頁面
    {
        platform_shopping_cart.view_shopping_cart.finish();
        this.finish();
    }
}
