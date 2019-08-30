<?php

require_once("PieChart.php");

$mySQLi = new MySQLi('localhost','root','','environment_record',3306);
//判断数据库是否连接

if($mySQLi -> connect_errno){

}
else {

    // 空气污染指数 优、良、轻度、中度、重度、严重污染
    $good=0;
    $moderate=0;
    $unhealthyForSensitive=0;
    $unhealthy=0;
    $veryUnhealthy=0;
    $hazardous=0;

    $title = "空气污染指数";

    // 设置字符集
    mysqli_set_charset($mySQLi, 'utf8');

    // 准备SQL语句
    $sql = 'select * from environment_record.recorder';

    // 执行SQL语句
    $result = mysqli_query($mySQLi, $sql);

    if ($result && mysqli_num_rows($result)) {

        while ($row = mysqli_fetch_array($result)) {

            if($row['air_quality']<50){
                $good++;
            }else if($row['air_quality']<100){
                $moderate++;
            }else if($row['air_quality']<150){
                $unhealthyForSensitive++;
            }else if($row['air_quality']<200){
                $unhealthy++;
            }else if($row['air_quality']<300){
                $veryUnhealthy++;
            }else{
                $hazardous++;
            }
        }

    } else {

    }

    $xdata=array("优", "良", "轻度", "中度", "重度","严重污染");
    $ydata[] = $good;
    $ydata[] = $moderate;
    $ydata[] = $unhealthyForSensitive;
    $ydata[] = $unhealthy;
    $ydata[] = $veryUnhealthy;
    $ydata[] = $hazardous;

    $Img = new PieChart($title, $xdata, $ydata);
    $Img->mkPieChart();
}

?>