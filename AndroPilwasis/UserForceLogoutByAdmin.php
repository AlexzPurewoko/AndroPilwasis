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
  $result = array();
    $result["isAuthSuccess"] = "true";
    $logout_sql = "UPDATE user_pilkasis SET has_running='0' WHERE username LIKE '$user_name'";
    $logout_run = mysqli_query($con, $logout_sql);
    if($logout_run){
      $result['message'] = "All Succeded";
      $result['isSuccess'] = "true";
    } else {
      $result['message'] = "Cannot logout!";
      $result['isSuccess'] = "false";
    }
  echo json_encode(array('logout' => $result));
  mysqli_close($con);
  }
 ?>
