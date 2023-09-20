package com.example.model.util;

import android.app.ProgressDialog;
import android.content.Context;

public class connection_util {
    /*
    String user_mail;//使用者登入帳號
    user_mail = file_util.Read_loginInfo_account_Value(platform_product_exchange.this);//取得登入會員信箱
     */
    private String url = "http://192.192.140.199";
    public String get_url(){return url;}

    private String url_img = "http://192.192.140.199/~D10516216/";
    public String get_url_img(){return url_img;}

    //---------------------------------------------------------------------------------------------------
    //TODO 會員相關
    public String check_login(String input_mail_id,String input_password){//會員登入
        return get_url() + "/~D10516216/phone/login/check_login.php?" +
                "username=" + input_mail_id+
                "&password=" + input_password;
    }

    public String check_register(String input_mail_id, String input_password, String input_re_password, String input_user_name ){//會員註冊
        return get_url() + "/~D10516216/phone/register/check_register.php?" +
                "username="+ input_mail_id +
                "&password="+ input_password +
                "&check_password="+ input_re_password +
                "&name="+ input_user_name ; //給參數
    }

    public String check_email(String input_mail_id,String input_code,String input_verify_flag){//確認會員有沒有驗證過信箱
        return get_url() + "/~D10516216/phone/register/check_email.php?" +
                "username="+ input_mail_id +
                "&randid="+ input_code + //給參數
                "&verify_flag="+ input_verify_flag ; //0:註冊驗證 1:忘記密碼驗證

    }

    public String send_verification(String input_mail_id){//會員發送驗證碼
        return get_url() + "/~D10516216/phone/register/send_verification.php?" +
                "username=" + input_mail_id;
    }

    public String check_forgot_pwd(String input_mail_id){//會員忘記密碼
        return get_url() + "/~D10516216/phone/password/check_forgot_pwd.php?"+
                "username=" + input_mail_id  ; //給參數
    }

    public String check_change_pwd(String input_mail_id,String input_password,String input_re_password){//更改密碼
        return get_url() + "/~D10516216/phone/password/check_change_pwd.php?"+
                "username=" + input_mail_id + //給參數
                "&new_password="+ input_password +
                "&check_password="+ input_re_password ;
    }

    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //TODO 會員中心
    public String get_member_center (String username){//取得會員中心的用戶資訊
        return get_url() + "/~D10516216/phone/member/member_center.php?"+
                "username=" + username ;
    }

    public String get_member_admin (String username){//取得會員資料的用戶資訊
        return get_url() + "/~D10516216/phone/member/member_admin.php?"+
                "username=" + username ;
    }

    public String check_member_pwd(String username,String check_password){//會員中心更改密碼前 需確認舊密碼輸入是否正確 (確認會員密碼)
        return get_url() + "/~D10516216/phone/member/check_member_pwd.php?"+
                "username=" + username +
                "&check_password=" + check_password;
    }

    public String member_modify(String username,String name,String real_name,String birthday,String photo,String phone,String address){//會員修改資料(無更改圖片)
        return get_url() + "/~D10516216/phone/member/member_modify.php?"+
                "username=" + username +
                "&real_name=" + real_name +
                "&name=" + name +
                "&birthday=" + birthday +
                "&photo=" + photo +
                "&phone=" + phone +
                "&address=" + address;
    }

    public String member_modify(String username,String name,String real_name,String birthday,String phone,String address){//會員修改資料(有更改圖片)
        return get_url() + "/~D10516216/phone/member/member_modify.php?"+
                "username=" + username +
                "&real_name=" + real_name +
                "&name=" + name +
                "&birthday=" + birthday+
                "&phone=" + phone +
                "&address=" + address;
    }
    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //TODO 活動相關
    public String get_activity_info(String type_id){//初步讀取活動的大概資訊
        return get_url() + "/~D10516216/phone/activity/activity_info.php?"+
                "type_id=" + type_id ;
    }

    public String get_activity_info_detail(String activity_id){//讀取活動的詳細資訊
        return get_url() + "/~D10516216/phone/activity/activity_info_detail.php?"+
                "activity_id=" + activity_id ;
    }

    public String activity_insert (String username){//讀取發起活動需要的相關欄位 活動種類、活動地點
        return get_url() + "/~D10516216/phone/activity/activity_insert.php?"+
                "username=" + username;
    }

    public String activity_insert_session (String activity_cid,String activity_start_date,String activity_end_date,String activity_start_time,String activity_end_time){//讀取發起活動需要的相關欄位 活動時段(遊戲活動)
        return get_url() + "/~D10516216/phone/activity/activity_insert_session.php?" +
                "activity_cid=" + activity_cid +
                "&activity_start_date=" + activity_start_date +
                "&activity_end_date=" + activity_end_date +
                "&activity_start_time=" + activity_start_time +
                "&activity_end_time=" + activity_end_time;
    }

    public String activity_insert_session (String activity_cid,String activity_start_date,String activity_end_date){//讀取發起活動需要的相關欄位 活動時段(一般活動)
        return get_url() + "/~D10516216/phone/activity/activity_insert_session.php?" +
                "activity_cid=" + activity_cid +
                "&activity_start_date=" + activity_start_date +
                "&activity_end_date=" + activity_end_date;
    }

    //發起活動(一般)
    public String check_activity_insert(String activity_name,
                                        String activity_content,
                                        String park_id,
                                        String activity_cid,
                                        String activity_array,
                                        String activity_unit1,
                                        String activity_unit2,
                                        String photo,
                                        String ticket,
                                        String ticket_no_open,
                                        String ticket_date,
                                        String ticket_array,
                                        String tag1,
                                        String tag2,
                                        String tag3,
                                        String activity_init){//發起活動
        return get_url() + "/~D10516216/phone/activity/check_activity_insert.php?"+
                "activity_name=" + activity_name +
                "&activity_content=" + activity_content +
                "&park_id=" + park_id +
                "&activity_cid=" + activity_cid +
                "&activity_array=" + activity_array +
                "&activity_unit1=" + activity_unit1 +
                "&activity_unit2=" + activity_unit2 +
                "&photo=" + photo +
                "&ticket=" + ticket +
                "&ticket_no_open=" + ticket_no_open +
                "&ticket_date=" + ticket_date +
                "&ticket_array=" + ticket_array +
                "&tag1=" + tag1 +
                "&tag2=" + tag2 +
                "&tag3=" + tag3 +
                "&activity_init=" + activity_init ;
    }

    public String get_activity_admin_info (String username , String type_id){//活動管理清單
        return get_url() + "/~D10516216/phone/activity/activity_admin_info.php?" +
                "username=" + username +
                "&type_id=" + type_id;
    }

    public String get_activity_admin_info_detail(String activity_id){//讀取活動管理的詳細資訊
        return get_url() + "/~D10516216/phone/activity/activity_admin_info_detail.php?"+
                "activity_id=" + activity_id ;
    }

    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //TODO 票券相關
    public String get_ticket_info (String username,String type_id){//取得票券的詳細資訊
        return get_url() + "/~D10516216/phone/ticket/ticket_info.php?"+
                "username=" + username +
                "&type_id=" + type_id ;
    }

    public String get_activity_buy_ticket (String username,String activity_id){//取得活動購買票券相關資料
        return get_url() + "/~D10516216/phone/activity/activity_buy_ticket.php?"+
                "username=" + username +
                "&activity_id=" + activity_id ;
    }

    public String get_activity_buy_ticket_detail (){//取得活動購買票券詳細資料
        return get_url() + "/~D10516216/phone/activity/activity_buy_ticket_detail.php";
    }

    public String check_buy_ticket (String username,String activity_id,String activity_start_time,String activity_end_time,String ticket_id,String pay_id){//活動票券確認購買
        return get_url() + "/~D10516216/phone/activity/check_buy_ticket.php?"+
                "username=" + username +
                "&activity_id=" + activity_id +
                "&activity_start_time=" + activity_start_time +
                "&activity_end_time=" + activity_end_time +
                "&ticket_id=" + ticket_id +
                "&pay_id=" + pay_id ;
    }

    public String get_ticket_info_detail(String activity_aid){//票券詳細資訊
        return get_url() + "/~D10516216/phone/ticket/ticket_info_detail.php?activity_aid=" + activity_aid;
    }
    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //TODO 主題園區相關
    public String get_park_offer(String park_id){//取得園區折扣資訊
        return get_url() + "/~D10516216/phone/park/park_offer.php?" +
                "park_id=" + park_id;
    }

    public String get_park_activity(String park_id){//取得園區活動資訊
        return get_url() + "/~D10516216/phone/park/park_activity.php?" +
                "park_id=" + park_id;
    }

    public String get_park_list(String search_id){//取得園區清單
        return get_url() + "/~D10516216/phone/park/park_list.php?" +
                "search_id=" + search_id;
    }

    public String get_park_map(String park_id){//取得園區地圖
        return get_url() + "/~D10516216/phone/park/park_map.php?" +
                "park_id=" + park_id;
    }

    public String get_park_guide(String park_id , String guide_id){//取得園區地圖
        return get_url() + "/~D10516216/phone/park/park_guide.php?" +
                "park_id=" + park_id +
                "&guide_id=" + guide_id;
    }

    public String get_parkcoin(String park_id , String guide_id,String username){//取得園區地圖
        return get_url() + "/~D10516216/phone/park/park_get_parkcoin.php?" +
                "park_id=" + park_id +
                "&username=" + username +
                "&guide_id=" + guide_id;
    }

    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //TODO 好友資訊相關
    public String get_member_friends(String username){//取得好友列表
        return get_url() + "/~D10516216/phone/member/member_friends.php?username=" + username;
    }

    public String get_check_friend_detail(String username,String friend_username ){//取得好友簡短資訊
        return get_url() + "/~D10516216/phone/member/check_friend_detail.php?" +
                "username=" + username +
                "&friend_username=" + friend_username;
    }

    public String get_member_friend_detail(String friend_username ){//取得好友詳細資訊
        return get_url() + "/~D10516216/phone/member/member_friend_detail.php?" +
                "friend_username=" + friend_username;
    }

    public String check_friend(String username,String friend_username,String type_id ){//加入&刪除好友　 (0:加入好友 9:刪除好友)
        return get_url() + "/~D10516216/phone/member/check_friend.php?" +
                "username=" + username +
                "&friend_username=" + friend_username +
                "&type_id=" + type_id;
    }
    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------
    //TODO 平台商城相關
    public String get_store_info(){//取得商城資訊String park
        return get_url() + "/~D10516216/phone/store/store_info.php";
    }

    public String get_store_info_detail(String store_id){//取得商品詳細資訊
        return get_url() + "/~D10516216/phone/store/store_info_detail.php?"+
                "store_id=" + store_id;
    }

    public String store_car_add(String store_id,String username){//購物車 新增商品
        return get_url() + "/~D10516216/phone/store/store_car.php?"+
                "type_id=" + 2 +
                "&store_id=" + store_id +
                "&username=" + username ;
    }

    public String store_car_alter(String store_id,String username,String store_number){//購物車 修改數量
        return get_url() + "/~D10516216/phone/store/store_car.php?"+
                "type_id=" + 5 +
                "&store_id=" + store_id +
                "&username=" + username +
                "&store_number=" + store_number ;
    }

    public String store_car_delete(String store_id,String username){//購物車 刪除商品
        return get_url() + "/~D10516216/phone/store/store_car.php?"+
                "type_id=" + 9 +
                "&store_id=" + store_id +
                "&username=" + username ;
    }

    public String get_store_car_detail(String username){//取得購物車詳細資訊
        return get_url() + "/~D10516216/phone/store/store_car_detail.php?"+
                "username=" + username;
    }

    public String get_check_purchase_store_detail(String user_mail){//取得付費方式
        return get_url() + "/~D10516216/phone/store/check_purchase_store_detail.php?"+
                "username=" + user_mail;
    }

    public String get_store_exchange_info(String username){//取得商品兌換清單
        return get_url() + "/~D10516216/phone/store/store_exchange_info.php?" +
                "username=" + username;
    }

    public String get_store_exchange_info_detail(String store_exchange_id){//取得商品兌換詳細清單
        return get_url() + "/~D10516216/phone/store/store_exchange_info_detail.php?" +
                "store_exchange_id=" + store_exchange_id;
    }

    public String check_exchange_store(String username,String store_exchage_code){//確認商品兌換
        return get_url() + "/~D10516216/phone/store/check_exchange_store.php?" +
                "username=" + username +
                "&store_exchange_code=" + store_exchage_code;
    }

    public String check_purchase_store(String username,String store_json,String pay_id,String type_id,String name,String phone,String address,String remark){//確認商品購買
        return get_url() + "/~D10516216/phone/store/check_purchase_store.php?"+
                "username=" + username +
                "&store_array=" + store_json +
                "&pay_id=" + pay_id +
                "&type_id=" + type_id +
                "&name=" + name +
                "&phone=" + phone +
                "&address=" + address +
                "&remark=" + remark ;
    }
    //---------------------------------------------------------------------------------------------------

    //TODO 廣告相關
    public String get_ad_info(String ad_cid){//確認商品兌換
        return get_url() + "/~D10516216/phone/ad/ad_info.php?" +
                "ad_cid=" + ad_cid;
    }

    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------

    //TODO 通知消息相關
    public String get_ad_recive_list(String username){//取得最新消息清單
        return get_url() + "/~D10516216/phone/ad/ad_recive_list.php?" +
                "username=" + username;
    }

    public String check_ad_recive(String ad_location_id,String ad_content , String username){//新增最新資訊 ad_location_id=0(全部人都可以看見) 1(個別帳號才能看見)
        return get_url() + "/~D10516216/phone/ad/ad_recive.php?" +
                "ad_location_id=" + ad_location_id +
                "&username=" + username +
                "&ad_content=" + ad_content;
    }

    //---------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------

    //TODO 遊戲相關
    public String check_enter_game(String attend_verify,String username){//掃描QRcode
        return get_url() + "/~D10516216/phone/game/check_enter_game.php?" +
                "attend_verify=" + attend_verify +
                "&username=" + username;
    }

    public String check_activity_attend(String attend_verify){//加入活動
        return get_url() + "/~D10516216/phone/activity/activity_attend.php?" +
                "attend_verify=" + attend_verify;
    }
    //---------------------------------------------------------------------------------------------------
    // Beacon
    //手環配對
    public String beacon_watch_info(String input_mail_id,String Beacon_Minor,String activity_id){
        return get_url() + "/~D10516216/phone/member/member_wristband.php?"+
                "username=" + input_mail_id +
                "&wristband_id=" + Beacon_Minor+
                "&activity_id=" + activity_id ;
    }
    //解除手環配對
    public String unlink_beacon_watch(String input_mail_id,String Beacon_Minor,String activity_id){
        return get_url() + "/~D10516216/phone/member/member_wristband_release.php?"+
                "username=" + input_mail_id +
                "&wristband_id=" + Beacon_Minor+
                "&activity_id=" + activity_id;
    }

    //遊戲等待室資訊
    public String game_room_info(String game_room_id){
        return get_url() + "/~D10516216/phone/game/game_room.php?"+
                "game_room_id=" + game_room_id ;
    }

    //取得自己的角色職業
    public String get_own_role(String input_mail_id){
        return get_url() + "/~D10516216/phone/game/game_role_assignment_player.php?"+
                "username=" + input_mail_id ;
    }

    //確認遊戲準備
    public String check_game_attend(String input_mail_id,String activity_id,String type_id){
        return get_url() + "/~D10516216/phone/game/check_game_attend.php?"+
                "username=" + input_mail_id +
                "&activity_id=" + activity_id+
                "&type_id=" + type_id ;
    }

    //設定遊戲基本資訊
    public String setting_basic_game_info(String game_room_id){
        return get_url() + "/~D10516216/phone/game/check_game_info.php?"+
                "game_room_id=" + game_room_id ;
    }

    //確認遊戲開始(系統管理員)
    public String system_check_game_start(String username,String activity_id,String game_room_id){
        return get_url() + "/~D10516216/phone/game/check_game_start.php?"+
                "username=" + username +
                "&activity_id=" + activity_id+
                "&game_room_id=" + game_room_id ;
    }

    //確認遊戲開始
    public String waiting_started(String game_room_id,String activity_id){
        return get_url() + "/~D10516216/phone/game/check_game_started.php?"+
                "game_room_id=" + game_room_id+
                "&activity_id=" + activity_id;
    }

    //取得每位玩家的遊戲角色分配資訊
    public String user_role(String activity_id){
        return get_url() + "/~D10516216/phone/game/game_role_assignment.php?"+
                "activity_id=" + activity_id ;
    }

    //取得驛站資訊
    public String get_station(String activity_id){
        return get_url() + "/~D10516216/phone/game/check_game_station.php?"+
                "activity_id=" + activity_id ;
    }

    //偵測到人類(魔鬼)
    public String check_catch_person(String beacon_id_self , String beacon_id_other){
        return get_url() + "/~D10516216/phone/game/check_catch_person.php?"+
                "beacon_id_self=" + beacon_id_self +
                "&beacon_id_other=" + beacon_id_other ;
    }

    //捕捉到人類(魔鬼)
    public String check_catched_devil(String beacon_id_self , String beacon_id_other,String game_room_id){
        return get_url() + "/~D10516216/phone/game/check_catched_devil.php?"+
                "beacon_id_self=" + beacon_id_self +
                "&beacon_id_other=" + beacon_id_other+
                "&game_room_id=" + game_room_id ;
    }

    //確定被捕捉(魔鬼)
    public String check_catched(String beacon_id_self){
        return get_url() + "/~D10516216/phone/game/check_catched.php?"+
                "beacon_id_self=" + beacon_id_self;
    }

    //確認破譯站資訊(人類)
    public String check_station_info(String game_station_id , String activity_id,String type_id){
        return get_url() + "/~D10516216/phone/game/check_station_info.php?"+
                "game_station_id=" + game_station_id +
                "&activity_id=" + activity_id+
                "&type_id=" + type_id ;
    }

    //遊戲關卡資訊(人類)
    public String check_level_info(String game_level_id){
        return get_url() + "/~D10516216/phone/game/check_level_info.php?"+
                "game_level_id=" + game_level_id;
    }

    //確認解釋破譯站(人類)
    public String check_solve_station(String game_level_record_id , String game_station_id,String game_room_id,String game_class_id,String type_id){
        return get_url() + "/~D10516216/phone/game/check_solve_station.php?"+
                "game_level_record_id=" + game_level_record_id +
                "&game_station_id=" + game_station_id+
                "&game_room_id=" + game_room_id +
                "&game_class_id=" + game_class_id +
                "&type_id=" + type_id ;
    }

    //判斷玩家是否在遊戲驛站範圍內
    public String game_check(String type_id , String username,String game_check,String game_room_id){
        return get_url() + "/~D10516216/phone/game/game_check.php?"+
                "type_id=" + type_id +
                "&username=" + username+
                "&game_check=" + game_check +
                "&game_room_id=" + game_room_id ;
    }

    //確認遊戲結果
    public String check_game_result(String game_room_id , String activity_id){
        return get_url() + "/~D10516216/phone/game/check_game_result.php?"+
                "game_room_id=" + game_room_id +
                "&activity_id=" + activity_id ;
    }

    //---------------------------------------------------------------------------------------------------
    //End
}
