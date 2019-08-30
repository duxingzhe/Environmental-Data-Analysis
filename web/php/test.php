<?php

require_once("chart.php");
require_once("connect.php");

$xdata = array('测试一','测试二','测试三','测试四','测试五','测试六','测试七','测试八','测试九');
$ydata = array(89,90,90,23,35,45,56,23,56);
$title = "test";
$Img = new Chart($title,$xdata,$ydata);
$Img->mkPieChart();

?>