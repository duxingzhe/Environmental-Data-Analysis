<?php

require_once './api/response.php';
require_once './api/DB.php';

$key= urldecode($_GET['key']);

if($key=='e900e40bc91d3f9f7f0a99fed68a2e96')
{
    $cityname= urldecode($_GET['cityname']);

    if(!is_string($cityname)){
        return @Response::show(401,'数据不合法');
    }

    $sql = 'select * from environment_record.month_data where cityname = \''. $cityname.'\' ';

    echo "$sql";

#捕获异常
    try{
        $connect = DB::getInstance()->connect($sql);
    }catch(Exception $e){
        return Response::show(403,'数据库连接失败');
    }

    $result = mysqli_query($connect,$sql);
    $values = array();
    if ($result && mysqli_num_rows($result)) {
        while ($val = mysqli_fetch_array($result)) {
            $values[] = $val; //二维数组
        }
    }

    if($values){
        return Response::show(200,'首页数据获取成功',$values);
    }else{
        return Response::show(400,'首页数据获取失败',$values);
    }
}
else
{
    echo '非法访问';
}

?>