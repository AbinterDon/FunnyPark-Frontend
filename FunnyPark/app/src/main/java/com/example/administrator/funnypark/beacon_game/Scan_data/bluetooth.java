package com.example.administrator.funnypark.beacon_game.Scan_data;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class bluetooth extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /* 定位藍牙權限開啟關閉 */

    //定位
    public void checkPermission(Context view) {
        if (ContextCompat.checkSelfPermission(view, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) view, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //提示权限已经被禁用 且不在提示
                return;
            }
            ActivityCompat.requestPermissions((Activity)view, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        //檢查手機是否支援BLE
        if(mBluetoothAdapter == null)
        {
            Toast.makeText(view,"手機不支援藍牙功能",Toast.LENGTH_SHORT).show();
        }
        //檢查藍牙是否開啟
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enable_BLE = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            view.startActivity(enable_BLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // 因為這個 method 會帶回任何權限視窗的結果，所以我們要用 requestCode 來判斷這是哪一個權限
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 不等於 PERMISSION_GRANTED 代表被按下拒絕

                // 跳出否些提示 ...（例如：這權限是為了讓你更快找到 xxxx）
            }
        }
    }


    public void requestLocationPermission(Context view) {
        ActivityCompat.requestPermissions((Activity) view, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    //藍牙強制關閉
    public static boolean turnOffBluetooth()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null)
        {
            return bluetoothAdapter.disable();
        }
        return false;
    }

    /* 定位藍牙權限開啟關閉(結束) */

}
