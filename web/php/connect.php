<?php

echo "<title>环境数据分析前端</title>";

// Javascript
echo "<script type=\"text/javascript\" src=\"./js/change_color.js\"></script>";

//css
echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/table_color.css\"></style>";

//使用面向对象进行数据库的连接，在创建对象的时候就自动的连接数据

$mySQLi = new MySQLi('localhost','root','','environment_record',3306);
//判断数据库是否连接

if($mySQLi -> connect_errno){
    echo "连接失败，请检查数据库模块";
}
else {
    echo "连接成功，读取数据中...";

    echo "\n\n";

    echo "<h3 align=\"center\">环境数据记录</h3>";

    echo "\n\n";

    // 4、设置字符集
    mysqli_set_charset($mySQLi, 'utf8');
    // 5、准备SQL语句
    $sql = 'select * from environment_record.recorder';
    // 6、执行SQL语句
    $result = mysqli_query($mySQLi, $sql);
    if ($result && mysqli_num_rows($result)) {
        // 7、处理数据
        //转成数组，且返回第一条数据,当不是一个对象时候退出
        echo "<table align=\"center\" class=\"altrowstable\" id=\"alternatecolorAll\">
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
        echo "SQL语句执行出错，请检查数据库或语句。";
    }

    echo "\n\n";

    echo "<h3 align=\"center\">输入数据</h3>";

    echo "\n\n";

    echo "<form action='insert.php' method='post'>
    <table align=\"center\">
        <tr>
            <td> 时间: </td>
            <td> <input type='text' name='time'> </td>
            <td> 省份: </td>
            <td> <input type='text' name='province'></td>
        </tr>
        <tr>
            <td> 城市: </td>
            <td> <input type='text' name='city'> </td>
            <td> 天气: </td>
            <td> <input type='text' name='weather'> </td>
        </tr>
        <tr>
            <td> 最高温度: </td>
            <td> <input type='text' name='highest_temperature'> </td>
            <td> 最低温度: </td>
            <td> <input type='text' name='lowest_temperature'> </td>
        </tr>
        <tr>
            <td> 空气质量: </td>
            <td> <input type='text' name='air_quality'> </td>
            <td> PM2.5: </td>
            <td> <input type='text' name='PM25'> </td>
        </tr>
        <tr>
            <td> SO2: </td>
            <td> <input type='text' name='SO2'> </td>
        </tr>
        <tr>
            <td> NO2: </td>
            <td> <input type='text' name='NO2'> </td>
            <td> CO: </td>
            <td> <input type='text' name='CO'> </td>
        </tr>
        <tr>
            <td> 预警类型: </td>
            <td> <input type='text' name='warning_type'> </td>
            <td> 预警等级: </td>
            <td> <input type='text' name='warning_level'> </td>
        </tr>
        <tr>
            <td><input type='submit' value='提交' name='sub'></td>
        </tr>

    </table>

</form>";
    echo "<img src=\"PieChartForAPIInHeyuan.php\">";

    echo "<img src=\"LineChartForTemperatureInHeyuan.php\">";
}

?>
