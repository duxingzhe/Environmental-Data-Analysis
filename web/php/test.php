<?php

require_once('./api/Response.php');
$arr = array(
    'id' => 1,
    'name'=>'singwa'

);
Response::json(200,"数据返回成功",$arr);

?>