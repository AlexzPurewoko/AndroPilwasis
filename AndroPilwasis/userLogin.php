/**
 * Copyright @2019 by Alexzander Purwoko Widiantoro <purwoko908@gmail.com>
 * @author APWDevs
 *
 * Licensed under GNU GPLv3
 *
 * This module is provided by "AS IS" and if you want to take
 * a copy or modifying this module, you must include this @author
 * Thanks! Happy Coding!
 */

<?php
  require_once('db_conn.php');

  if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $user_name = $_POST['username'];
    $passwd = $_POST['password'];
    $sameDevice = $_POST['sameDevice'];
    $result = array();

    // check if the user is registered, but no password checking
    $sql_oauth = "SELECT count(1) AS cond FROM user_pilkasis WHERE username LIKE '$user_name'";
    $result_query_auth = mysqli_query($con, $sql_oauth);
    $auth_arr = mysqli_fetch_array($result_query_auth);

    if($auth_arr['cond'] == 1){
      $result["isUserRegistered"] = "true";
      $sql_passwd_check = "SELECT count(1) AS pwd FROM user_pilkasis WHERE username LIKE '$user_name' AND user_password LIKE '$passwd'";
      $result_passwd_check = mysqli_query($con, $sql_passwd_check);
      $pwd_arr = mysqli_fetch_array($result_passwd_check);
      if($pwd_arr['pwd'] == 0){
        $result['data'] = null;
        $result['isSuccess'] = "false";
        $result['message'] = "Invalid Password!";
        $result['passwordVerification'] = 'false';
      }
      else {
        $result['passwordVerification'] = 'true';
        // GET the user, and checks, did used in another devices?
        $sql_haslogged_check = "SELECT * FROM user_pilkasis WHERE username LIKE '$user_name' AND user_password LIKE '$passwd'";
        $result_haslogged_check = mysqli_query($con, $sql_haslogged_check);
        $arr_haslogged_check = mysqli_fetch_array($result_haslogged_check);

        $result_data = array();
        if($arr_haslogged_check['has_running'] == 0 || $sameDevice == 1){

          $login_sql = "UPDATE user_pilkasis SET has_running='1' WHERE username LIKE '$user_name' AND user_password LIKE '$passwd'";
          $login_run = mysqli_query($con, $login_sql);

          $result_data['nama'] = $arr_haslogged_check['nama'];
          $result_data['kelas'] = $arr_haslogged_check['kelas'];
          $result_data['username'] = $arr_haslogged_check['username'];
          $result_data['password'] = $arr_haslogged_check['user_password'];
          $result_data['activated'] = $arr_haslogged_check['activated'];
          $result_data['pilihan'] = $arr_haslogged_check['pilihan_id'];
          $result_data['tps_id'] = $arr_haslogged_check['tps_id'];


        // get the tps_name
        $tpsId = $arr_haslogged_check['tps_id'];
          $sql_tps_check = "SELECT tps_name FROM tps WHERE num_id LIKE '$tpsId'";
          $result_tps_check = mysqli_query($con, $sql_tps_check);
          $arr_tps_check = mysqli_fetch_array($result_tps_check);
          $result_data['tps_name'] = $arr_tps_check['tps_name'];

          // is tps_admin?
          $sql_tpsadmin_check = "SELECT count(1) AS tps_admin FROM tps WHERE tps_admin LIKE '$user_name'";
          $result_tpsadmin_check = mysqli_query($con, $sql_tpsadmin_check);
          $tpsadmin_arr = mysqli_fetch_array($result_tpsadmin_check);

          $tps_admin = $tpsadmin_arr['tps_admin'];
          // is allowed users(admin_all)?
          $sql_adminall_check = "SELECT count(1) AS allowed_all FROM allowed_users WHERE username LIKE '$user_name'";
          $result_adminall_check = mysqli_query($con, $sql_adminall_check);
          $adminall_arr = mysqli_fetch_array($result_adminall_check);

          $adminall = $adminall_arr['allowed_all'];

          if($tps_admin == 1){
            $result_data['user_mode'] = 1; // mode tpsadmin
          } elseif($adminall == 1){
            $result_data['user_mode'] = 2; // mode admin all tps lead
          } else {
            $result_data['user_mode'] = 0; // mode normal user
          }
          $result['isSuccess'] = "true";
          $result['message'] = "Verification Successfull";
          $result['data'] = $result_data;
        } else {
          $result['isSuccess'] = "false";
          $result['message'] = "User already logged in another device!";
          $result['data'] = null;
        }
      }
    } else {
      $result['data'] = null;
      $result['isSuccess'] = "false";
      $result['message'] = "User not found!";
      $result["isUserRegistered"] = "false";
    }
    echo json_encode(array('auth' => $result));
    mysqli_close($con);
  }
 ?>
