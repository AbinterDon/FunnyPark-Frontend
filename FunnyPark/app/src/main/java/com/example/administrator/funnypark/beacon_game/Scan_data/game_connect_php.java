package com.example.administrator.funnypark.beacon_game.Scan_data;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.model.util.connection_util;
import com.example.model.util.okhttp_util;

public class game_connect_php extends AppCompatActivity {

    final connection_util temp_url = new connection_util();//實體化url物件
    final okhttp_util client = new okhttp_util();//實體化 抓model
    Context scan_context;

    /* -----------------------------------------偵測到人類(魔鬼)---------------------------------------------------------*/
    public void check_catch_person(String own_minor ,String other_minor)
    {
        final String url = temp_url.check_catch_person(own_minor,other_minor);//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    //Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    if(ans[0].equals("y"))
                    {

                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }
    /* --------------------------------------偵測到人類(魔鬼)(結束)---------------------------------------------------*/

    /* -----------------------------------------確認捕捉人類(魔鬼)---------------------------------------------------------*/
    int [] fail_person;
    int c=0;
    public String check_catched_devil(String own_minor , final String other_minor, String game_room_id, int fail_person_length)
    {
        fail_person = new int[fail_person_length];

        final String url = temp_url.check_catched_devil(own_minor,other_minor,game_room_id);//取得url
        // Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    //Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    if(ans[0].equals("y"))
                    {
                        fail_person[c] = Integer.parseInt(other_minor);
                        set_fail_person(fail_person);
                        c++;
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        return "y";
    }

    public void set_fail_person(int [] fail_person){
        this.fail_person = fail_person;
    }

    public int[] get_fail_person() {
        return fail_person;
    }

    /* --------------------------------------確認捕捉人類(魔鬼)(結束)---------------------------------------------------*/

    /* -----------------------------------------確認破驛站資訊(人類)---------------------------------------------------------*/
    public void check_station_info(String game_station_id,String activity_id,String type_id)
    {
        final String url = temp_url.check_station_info(game_station_id,activity_id,type_id);//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("res",responseData);

                    final String[] ans = responseData.split(",");//分解字串
                    //判斷玩家是否已準備，開啟藍牙
                    if(ans[0].equals("y"))
                    {
                        set_game_level_id(ans[1]);//關卡 id
                        set_game_class_id(ans[2]);//遊戲類別 id
                        set_game_class_name(ans[3]);//遊戲類別名稱
                        set_game_level_name(ans[4]);//關卡名稱
                        set_game_content(ans[5]);//關卡說明
                        set_game_count(ans[6]);//破譯人數
                    }

                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }
    /* --------------------------------------確認破驛站資訊(人類)(結束)---------------------------------------------------*/

    /* -----------------------------------------遊戲關卡資訊(人類)---------------------------------------------------------*/
    public String check_level_info()
    {
        final String url = temp_url.check_level_info(get_game_level_id());//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    //Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    //判斷玩家是否已準備，開啟藍牙
                    if(ans[0].equals("y"))
                    {
                        set_game_level_record_id(ans[2]);
                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
        return "y";
    }
    /* --------------------------------------確認破驛站資訊(人類)(結束)---------------------------------------------------*/

    /* -----------------------------------------確定是否破譯成功(人類)---------------------------------------------------------*/
    public void check_solve_station(final String game_level_record_id, String game_station_id, String game_room_id , String game_class_id, String type_id)
    {
        final String url = temp_url.check_solve_station(game_level_record_id,game_station_id,game_room_id,game_class_id,type_id);//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    //Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    //判斷玩家是否已準備，開啟藍牙
                    if(ans[0].equals("y"))
                    {

                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }
    /* --------------------------------------確定是否破譯成功(人類)(結束)---------------------------------------------------*/

    /* -----------------------------------------判斷玩家是否在遊戲驛站範圍內(人類)---------------------------------------------------------*/
    public void game_check(String type_id, String username, String game_check , String game_room_id)
    {
        final String url = temp_url.game_check(type_id,username,game_check,game_room_id);//取得url
        Log.e("url",url);
        Thread okHttpExecuteThread = new Thread() {//產生執行緒物件 遇到有很需要花時間的動作 通常都會用執行緒
            @Override
            public void run() {
                try {//動到io(網路有關)一定要用try與執行緒
                    final String responseData = client.urlpost(url);//連線後端 取得資料
                    Log.e("res",responseData);
                    final String[] ans = responseData.split(",");//分解字串

                    //判斷玩家是否已準備，開啟藍牙
                    if(ans[0].equals("n"))
                    {

                    }
                } catch (Exception e) {//怕孩子沒抓到錯誤 父親擴大抓
                    Log.e(" TAG ", "error");//偵錯用( 一行一行)
                }
            }
        };
        okHttpExecuteThread.start();
    }
    /* --------------------------------------判斷玩家是否在遊戲驛站範圍內(人類)(結束)---------------------------------------------------*/

    String game_level_id; //關卡 id
    String game_class_id;//遊戲類別 id
    String game_class_name;//遊戲類別名稱
    String game_level_name;//關卡名稱
    String game_content;//關卡說明
    String game_count;//破譯人數
    String game_level_record_id;//遊戲ID

    public void get_context(Context context){
        this.scan_context =context;
    }

    //關卡 id
    public void set_game_level_id(String game_level_id){
        this.game_level_id = game_level_id;
    }

    public String get_game_level_id() {
        return game_level_id;
    }

    //遊戲類別 id
    public void set_game_class_id(String game_class_id){
        this.game_class_id = game_class_id;
    }

    public String get_game_class_id() {
        return game_class_id;
    }

    //遊戲類別名稱
    public void set_game_class_name(String game_class_name){
        this.game_class_name = game_class_name;
    }

    public String get_game_class_name() {
        return game_class_name;
    }

    //關卡名稱
    public void set_game_level_name(String game_level_name){
        this.game_level_name = game_level_name;
    }

    public String get_game_level_name() {
        return game_level_name;
    }

    //關卡說明
    public void set_game_content(String game_content){
        this.game_content = game_content;
    }

    public String get_game_content() {
        return game_content;
    }

    //破譯人數
    public void set_game_count(String game_count){
        this.game_count = game_count;
    }

    public String get_game_count() {
        return game_count;
    }

    //遊戲關卡ID
    public void set_game_level_record_id(String game_level_record_id){
        this.game_level_record_id = game_level_record_id;
    }

    public String get_game_level_record_id() {
        return game_level_record_id;
    }


}
