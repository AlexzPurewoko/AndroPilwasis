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
  define('HOST', 'localhost');
  define('USER', 'root');
  define('PASS', 'Purwo21112001');
  define('PilkasisDB', 'AndroPilwasis_final');

  $con = mysqli_connect(HOST, USER, PASS, PilkasisDB) or die('Cannot connect');

 ?>
