<?php

echo "<title>环境数据分析前端</title>";

// Javascript
echo "<script type=\"text/javascript\">
        function altRows(id){
            if(document.getElementsByTagName){

                var table = document.getElementById(id);
                var rows = table.getElementsByTagName(\"tr\");

                for(i = 0; i < rows.length; i++){
                    if(i % 2 == 0){
                        rows[i].className = \"evenrowcolor\";
                    }else{
                        rows[i].className = \"oddrowcolor\";
                    }
                }
            }
        }

        window.onload=function(){
            altRows('alternatecolor');
        }
    </script>";

//css
echo "<style type=\"text/css\">
        table.altrowstable {
            font-family: verdana,arial,sans-serif;
            font-size:11px;
            color:#333333;
            border-width: 1px;
            border-color: #a9c6c9;
            border-collapse: collapse;
        }
        table.altrowstable th {
            border-width: 1px;
            padding: 8px;
            border-style: solid;
            border-color: #a9c6c9;
        }
        table.altrowstable td {
            border-width: 1px;
            padding: 8px;
            border-style: solid;
            border-color: #a9c6c9;
        }
        .oddrowcolor{
            background-color:#d4e3e5;
        }
        .evenrowcolor{
            background-color:#c3dde0;
        }
    </style>";

//使用面向对象进行数据库的连接，在创建对象的时候就自动的连接数据

$mySQLi = new MySQLi('localhost','root','','environment_record',3306);
//判断数据库是否连接

if($mySQLi -> connect_errno){
    echo "Error";
}
else
{
    echo "Success";

    // 4、设置字符集
    mysqli_set_charset($mySQLi,'utf8');
    // 5、准备SQL语句
    $sql = 'select * from environment_record.recorder';
    // 6、执行SQL语句
    $result = mysqli_query($mySQLi,$sql);
    if ($result && mysqli_num_rows($result)) {
        // 7、处理数据
        //转成数组，且返回第一条数据,当不是一个对象时候退出
        echo "<table class=\"altrowstable\" id=\"alternatecolor\">
            <tr align=\"center\" >
            <th>时间</th>
            <th>省份</th>
            <th>城市</th>
            <th>天气</th>
            <th>最高温度</th>
            <th>最低温度</th>
            <th>空气质量</th>
            <th>PM2.5</th>
            <th>二氧化硫</th>
            <th>二氧化氮</th>
            <th>一氧化碳</th>
            <th>预警类型</th>
            <th>预警等级</th>
            </tr>";
        while ($row = mysqli_fetch_array($result)) {
            echo "<tr align=\"center\" >";
            echo "<td>" . $row['time'] . "</td>";
            echo "<td>" . $row['province'] . "</td>";
            echo "<td>" . $row['city'] . "</td>";
            echo "<td>" . $row['weather'] . "</td>";
            echo "<td>" . $row['highest_temperature'] . "</td>";
            echo "<td>" . $row['lowest_temperature'] . "</td>";
            echo "<td>" . $row['air_quality'] . "</td>";
            echo "<td>" . $row['PM25'] . "</td>";
            echo "<td>" . $row['SO2'] . "</td>";
            echo "<td>" . $row['NO2'] . "</td>";
            echo "<td>" . $row['CO'] . "</td>";
            echo "<td>" . $row['warning_type'] . "</td>";
            echo "<td>" . $row['warning_level'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {

    }
    // 8、释放资源、关闭连接
    if($result){
        mysqli_free_result($result);
    }
    mysqli_close($mySQLi);
}
    ?>