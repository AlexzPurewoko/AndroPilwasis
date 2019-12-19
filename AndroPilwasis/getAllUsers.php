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
  require_once('auth_session.php');
  $result = array();

  $result['tps'] = null;
  $result['user'] = null;
  if($auth_condition){
    $result["isAuthSuccess"] = "true";
    $user_mode = $_POST['userMode'];

    // VERIFY ARE THIS ACCOUNT IS INTENDED FPR admin_tps or admin_all
    $validated = false;
    if($user_mode == 1){
      $sql_tpsadmin_check = "SELECT count(1) AS tps_admin FROM tps WHERE tps_admin LIKE '$user_name'";
      $result_tpsadmin_check = mysqli_query($con, $sql_tpsadmin_check);
      $tpsadmin_arr = mysqli_fetch_array($result_tpsadmin_check);

      $tps_admin = $tpsadmin_arr['tps_admin'];
      $validated = ($tps_admin == 1);
    } elseif($user_mode == 2){
      $sql_adminall_check = "SELECT count(1) AS allowed_all FROM allowed_users WHERE username LIKE '$user_name'";
      $result_adminall_check = mysqli_query($con, $sql_adminall_check);
      $adminall_arr = mysqli_fetch_array($result_adminall_check);
      $adminall = $adminall_arr['allowed_all'];
      $validated = ($adminall == 1);
    }

    $sql_getcount_users = "";
    $sql_getcount_deactivated_users = "";
    $sql_get_users = "";
    $sql_gettps = "";
    $tps = 0;
    $array_tps = array();
    if($validated && $user_mode == 1){
      $verify_tps_sql = "SELECT num_id,tps_name FROM tps WHERE tps_admin LIKE '$user_name'";
      $result_verify_tps_sql = mysqli_query($con, $verify_tps_sql);
      $verify_tps_arr = mysqli_fetch_array($result_verify_tps_sql);
      array_push($array_tps, array(
          'id' => $verify_tps_arr['num_id'],
          'name' => $verify_tps_arr['tps_name']
      ));
      $tps = $verify_tps_arr['num_id'];

      $sql_getcount_users = "SELECT count(1) AS countusers FROM user_pilkasis WHERE tps_id LIKE '$tps'";
      $sql_getcount_deactivated_users = "SELECT count(1) AS countusers FROM user_pilkasis WHERE tps_id LIKE '$tps' AND activated LIKE '0'";
      $sql_get_users = "SELECT nama,username,kelas,tps_id,activated FROM user_pilkasis WHERE tps_id LIKE '$tps'";
    } elseif($validated && $user_mode == 2){
      $sql_getcount_users = "SELECT count(1) AS countusers FROM user_pilkasis";
      $sql_getcount_deactivated_users = "SELECT count(1) AS countusers FROM user_pilkasis WHERE activated LIKE '0'";
      $sql_get_users = "SELECT nama,username,kelas,tps_id,activated FROM user_pilkasis";


      $sql_gettps = "SELECT * FROM tps";
      $sql_gettps_query = mysqli_query($con, $sql_gettps);
      while($row = mysqli_fetch_array($sql_gettps_query)){
        $arr = array(
          'id' => $row['num_id'],
          'name' => $row['tps_name']
        );
        array_push($array_tps, $arr);
      }
    }

    if($validated){
      $query_getcount_users = mysqli_query($con, $sql_getcount_users);
      $result_getcount_users = mysqli_fetch_array($query_getcount_users);
      $result['all_user_count'] = $result_getcount_users['countusers'];
      $query_getcount_deactivated_users = mysqli_query($con, $sql_getcount_deactivated_users);
      $result_getcount_deactivated_users = mysqli_fetch_array($query_getcount_deactivated_users);
      $result['all_deactivated_user_count'] = $result_getcount_deactivated_users['countusers'];
      $query_get_users = mysqli_query($con, $sql_get_users);
      $array_users = array();
      while($row = mysqli_fetch_array($query_get_users)){
        $arr = array(
          'username' => $row['username'],
          'nama' => $row['nama'],
          'kelas' => $row['kelas'],
          'tps_id' => $row['tps_id'],
          'activated' => $row['activated']
        );
        array_push($array_users, $arr);
      }

      // put into base result array
      $result['tps'] = $array_tps;
      $result['user'] = $array_users;
      $result['isSuccess'] = "true";
      $result['message'] = "All Succeded";
    } else {
      $result['isSuccess'] = "false";
      $result['message'] = "You is not recognized as Administrator";
    }
  } else {
    $result["isAuthSuccess"] = "false";
    $result['isSuccess'] = "false";
    $result['message'] = "Authentication Failed!";
  }
  echo json_encode(array('all_user' => $result));
  mysqli_close($con);
  }
 ?>
