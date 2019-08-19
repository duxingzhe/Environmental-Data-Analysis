<?php
//使用面向对象进行数据库的连接，在创建对象的时候就自动的连接数据

$mySQLi = new MySQLi('localhost','root','','environment_record',3306);
//判断数据库是否连接

if($mySQLi -> connect_errno){
    echo "Error";
}
else
{
    echo "Success";
}

// 4、设置字符集
mysqli_set_charset($mySQLi,'utf8');
// 5、准备SQL语句
$sql = 'select * from environment_record.recorder';
// 6、执行SQL语句
$result = mysqli_query($mySQLi,$sql);
if ($result && mysqli_num_rows($result)) {
    // 7、处理数据
    while ($row = mysqli_fetch_array($result,MYSQLI_NUM)) {
        var_dump($row);
    }
} else {

}
// 8、释放资源、关闭连接
if($result){
    mysqli_free_result($result);
}
mysqli_close($mySQLi);
    ?>