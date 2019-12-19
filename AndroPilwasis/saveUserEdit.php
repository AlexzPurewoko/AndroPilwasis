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
  if($auth_condition){
    $result["isAuthSuccess"] = "true";
    $user_mode = $_POST['userMode'];
    $users = explode(",", $_POST['list_user']);
    $usersCondition = explode(",", $_POST['list_cond']);

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

    if($validated){
      $size = count($users);
      $x = 0;
      while($x < $size){
        $temp_sql = "UPDATE user_pilkasis SET activated='".$usersCondition[$x]."' WHERE username LIKE '".$users[$x]."'";
        $temp_query = mysqli_query($con, $temp_sql);
        $x++;
      }
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
  echo json_encode(array('userChanges' => $result));
  mysqli_close($con);
  }
 ?>
