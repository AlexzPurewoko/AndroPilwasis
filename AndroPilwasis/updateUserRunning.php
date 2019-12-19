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
    $user_state = $_POST['user_status'];
    $logout_sql = "UPDATE user_pilkasis SET has_running='$user_state' WHERE username LIKE '$user_name' AND user_password LIKE '$passwd'";
    $logout_run = mysqli_query($con, $logout_sql);
    if($logout_run){

      $result['message'] = "All Succeded";
      $result['isSuccess'] = "true";
    } else {
      $result['message'] = "Cannot update Users!";
      $result['isSuccess'] = "false";
    }
  } else {
    $result['message'] = "User is not found!";
    $result["isAuthSuccess"] = "false";
  }
  echo json_encode(array('updateState' => $result));
  mysqli_close($con);
  }
 ?>
