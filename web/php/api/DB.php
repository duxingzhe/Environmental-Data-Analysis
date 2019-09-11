<?php


class DB
{
    static private $_instance;    //非public的类的实例的静态成员变量
    static private $_connectSource;    //连接数据库返回的资源句柄
    private $_dbConfig = array(
        'host'=>'127.0.0.1',
        'username'=>'root',
        'pwd'=>'',
        'port'=>'3306',
        'database'=>'environment_record'
    );

    private function __construct(){    //非public 的构造函数
    }

    static public function getInstance(){    //访问实例的公共静态方法
        if(!self::$_instance instanceof self){
            self::$_instance = new self();
        }
        return self::$_instance;
    }

    public function connect($sql){
        if(!self::$_connectSource){
            //连接mysql服务
            self::$_connectSource = mysqli_connect($this->_dbConfig['host'],$this->_dbConfig['username'],$this->_dbConfig['pwd'],
                $this->_dbConfig['database'],$this->_dbConfig['port']);
            if(!self::$_connectSource){
                //抛出异常
                echo "连接失败，请检查数据库模块";
            }
            //设置字符集
            mysqli_set_charset(self::$_connectSource, 'utf8');

            mysqli_query(self::$_connectSource, $sql);
        }
        return self::$_connectSource; //返回资源
    }
}