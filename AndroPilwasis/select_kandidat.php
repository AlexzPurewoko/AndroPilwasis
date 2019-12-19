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
  $pilih_id = $_POST['id_calon'];
  require_once('auth_session.php');
  $result = array();
  if($auth_condition){
  $result["isAuthSuccess"] = "true";
    // Are user already selected their Candidates, if yes this can't be continued
    $check_user_selection = "SELECT pilihan_id FROM user_pilkasis WHERE username LIKE '$user_name' AND user_password LIKE '$passwd'";
    $check_selection_query = mysqli_query($con, $check_user_selection);
    $check_selection_result = mysqli_fetch_assoc($check_selection_query);
    if($check_selection_result['pilihan_id'] == 0){
      // Check if selected id is any on kandidat tables
      $check_id_sql = "SELECT COUNT(1) AS occurences FROM kandidat WHERE num_id LIKE '$pilih_id'";
      $check_id_query = mysqli_query($con, $check_id_sql);
      $check_array = mysqli_fetch_assoc($check_id_query);

      if($check_array['occurences'] == 1){
        $select_kandidat_sql = "UPDATE user_pilkasis SET pilihan_id='$pilih_id' WHERE username LIKE '$user_name' AND user_password LIKE '$passwd'";
        $query_update = mysqli_query($con, $select_kandidat_sql);
        if($query_update){
          $result["isSuccess"] = "true";
          $result['message'] = "Terimakasih sudah memilih!";
        } else {
          $result["isSuccess"] = "false";
          $result['message'] = "Mohon maaf!, ada kesalahan saat menyimpan data ke server";
        }
      } else {
        $result["isSuccess"] = "false";
        $result['message'] = "Mohon maaf!, Anda memilih kandidat yang salah!. Tidak ditemukan kandidat dengan id='$pilih_id' ";
      }
    } else {
        $result["isSuccess"] = "false";
        $result['message'] = "Mohon maaf!, anda sudah memilih. Data yang anda pilih saat ini tidak akan tersimpan.\n Contact Administrator untuk lebih jelasnya!";
    }


  } else {
    $result["isAuthSuccess"] = "false";
  }
  echo json_encode(array('selectKandidat' => $result));
}
  mysqli_close($con);
 ?>
