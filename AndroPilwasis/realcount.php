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
    $result['golput'] = null;
    $result['count_selected'] = null;

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
      $sql_select_all_kandidat = "SELECT num_id FROM kandidat ORDER BY num_id";
      $sql_total_users = "SELECT count(1) AS countusers FROM user_pilkasis";
      $sql_deactivated_count = "SELECT count(1) AS countusers FROM user_pilkasis WHERE activated LIKE '0'";
      $sql_not_selected_count = "SELECT count(1) AS countusers FROM user_pilkasis WHERE activated NOT LIKE '0' AND pilihan_id LIKE '0'";


      $query_total_users = mysqli_query($con, $sql_total_users);
      $result_total_users = mysqli_fetch_array($query_total_users);
      $totalUser = $result_total_users['countusers'];

      $query_deactivated_count = mysqli_query($con, $sql_deactivated_count);
      $result_deactivated_count = mysqli_fetch_array($query_deactivated_count);
      $deactivatedCount = $result_deactivated_count['countusers'];

      $query_not_selected_count = mysqli_query($con, $sql_not_selected_count);
      $result_not_selected_count = mysqli_fetch_array($query_not_selected_count);
      $notSelectedCount = $result_not_selected_count['countusers'];

      $query_select_all_kandidat = mysqli_query($con, $sql_select_all_kandidat);
      $kandidatArray = array();
      while($row = mysqli_fetch_array($query_select_all_kandidat)) {
        $numId = $row["num_id"];
        $temp_sql = "SELECT count(1) AS realcount FROM user_pilkasis WHERE activated LIKE '1' AND pilihan_id LIKE '$numId'";
        $temp_query = mysqli_query($con, $temp_sql);
        $temp_result = mysqli_fetch_array($temp_query);
        $temp_percent = $temp_result['realcount'] * 100 / $totalUser;
        array_push($kandidatArray, array(
          'calon_id' => $numId,
          'total_percent' => $temp_percent,
          'total_selected' => $temp_result['realcount']
        ));
      }
      $result['count_selected'] = $kandidatArray;

      $deactivatedCount_percent = $deactivatedCount * 100 / $totalUser;
      $notSelected_percent = $notSelectedCount * 100 / $totalUser;
      $result['golput'] = array(
        'not_selected' => $notSelectedCount,
        'deactivated' => $deactivatedCount,
        'not_selected_percent' => $notSelected_percent,
        'deactivated_percent' => $deactivatedCount_percent,
      );
      $result['total_user'] = $totalUser;
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
  echo json_encode(array('realcount' => $result));
  mysqli_close($con);
  }
 ?>
