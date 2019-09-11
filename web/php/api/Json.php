<?php


class Json extends Api {
    public function response($code, $message = '', $data = array()) {
        if(!(is_numeric($code))) {
            return '';
        }

        $result = array(
            'code' => $code,
            'message' => $message,
            'data' => $data
        );

        echo json_encode($result);
        exit;
    }
}
