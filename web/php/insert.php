<?php

$mySQLi = new MySQLi('localhost','root','','environment_record',3306);
//判断数据库是否连接

if($mySQLi -> connect_errno){
    echo "连接失败，请检查数据库模块";
}
else {
    $sql = "INSERT INTO environment_record.recorder
(`time`,`province`,`city`,`weather`,`highest_temperature`,`lowest_temperature`,`air_quality`,`PM25`,`SO2`,`NO2`,`CO`,`warning_type`,`warning_level`)
VALUES
('$_POST[time]','$_POST[province]','$_POST[city]','$_POST[weather]','$_POST[highest_temperature]','$_POST[lowest_temperature]','$_POST[air_quality]', '$_POST[PM25]','$_POST[SO2]','$_POST[NO2]','$_POST[CO]','$_POST[warning_type]','$_POST[warning_level]');";

    echo "$sql\n";

    $result = mysqli_query($mySQLi, $sql);
    if ($result) {
        echo "SQL语句执行成功，添加了一份数据。";
    } else {
        echo "SQL语句执行出错，请检查数据库或语句。";
    }
    // 8、释放资源、关闭连接
    if ($result) {
        mysqli_free_result($result);
    }
    mysqli_close($mySQLi);
}
    ?>