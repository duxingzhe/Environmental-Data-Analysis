<?php

require_once("./Class/LineChart.php");

$mySQLi = new MySQLi('localhost', 'root', '', 'environment_record', 3306);
//判断数据库是否连接

if ($mySQLi->connect_errno) {

} else {

    // 空气污染指数 优、良、轻度、中度、重度、严重污染
    $good = 0;
    $moderate = 0;
    $unhealthyForSensitive = 0;
    $unhealthy = 0;
    $veryUnhealthy = 0;
    $hazardous = 0;

    $title = "空气污染指数";

    // 设置字符集
    mysqli_set_charset($mySQLi, 'utf8');

    // 准备SQL语句
    $sql = 'select time, highest_temperature, lowest_temperature from environment_record.recorder';

    // 执行SQL语句
    $result = mysqli_query($mySQLi, $sql);


    $xdata=array();

    $yLowestData=array();
    $yHighestData=array();

    if ($result && mysqli_num_rows($result)) {

        while ($row = mysqli_fetch_array($result)) {
            $xdata[] = $row['time'];
            $yHighestData[] = $row['highest_temperature'];
            $yLowestData[] = $row['lowest_temperature'];
        }

    } else {

    }

    $ydata=array($yHighestData,$yLowestData);

    $seriesName = array("最高温度","最低温度");
    $title = "温度变化图";
    $Img = new LineChart($title,$xdata,$ydata,$seriesName);
    $Img->paintLineChart();
}
    ?>
