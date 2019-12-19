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
  $result['data'] = null;
  if($auth_condition){
    $result["isAuthSuccess"] = "true";
    $tps_id = $_POST['tps_id'];

    $sql_get_users = "SELECT nama,username,kelas,tps_id,activated FROM user_pilkasis WHERE tps_id LIKE '$tps_id'";
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

    $result['data'] = $array_users;
    $result['isSuccess'] = "true";
    $result['message'] = "All Succeded";
  } else {
    $result["isAuthSuccess"] = "false";
    $result['isSuccess'] = "false";
    $result['message'] = "Authentication Failed!";
  }
  echo json_encode(array('users' => $result));
  mysqli_close($con);
  }
 ?>
