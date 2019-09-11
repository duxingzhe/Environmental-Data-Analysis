<?php

class Response {

    const JSON = "json";
    /**
     * 按综合方式输出通信数据
     * @param integer $code 状态码
     * @param string $message 提示信息
     * @param array $data 数据
     * @param string $type 数据类型
     * return string
     */
    public static function show($code, $message = '', $data = array(), $type = self::JSON) {
        if(!is_numeric($code)) {
            return '';
        }

        $type = isset($_GET['format']) ? $_GET['format'] : self::JSON;

        $result = array(
            'code' => $code,
            'message' => $message,
            'data' => $data,
        );

        if($type == 'json') {
            self::json($code, $message, $data);
            exit;
        } elseif($type == 'array') { //适合调试代码
            var_dump($result);
        }
    }
    /**
     * 按json方式输出通信数据
     * @param integer $code 状态码
     * @param string $message 提示信息
     * @param array $data 数据
     * return string
     */
    public static function json($code, $message = '', $data = array()) {

        if(!is_numeric($code)) {
            return '';
        }

        $result = array(
            'code' => $code,
            'message' => $message,
            'data' => $data
        );

        echo self::decodeUnicode(json_encode($result));
        exit;
    }

    function decodeUnicode($str){
        return preg_replace_callback('/\\\\u([0-9a-f]{4})/i',
            function($matches) {
                return mb_convert_encoding(pack("H*", $matches[1]), "UTF-8", "UCS-2BE");
            }, $str);
    }

}

?>
