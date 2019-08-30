<?php
Class Chart{
    private $image; // 定义图像
    private $title; // 定义标题
    private $ydata; // 定义Y轴数据
    private $xdata; // 定义X轴数据
    private $color; // 定义条形图颜色
    private $bgcolor; // 定义图片背景颜色
    private $width; // 定义图片的宽
    private $height; // 定义图片的长

    /*
     * 构造函数
     * String title 图片标题
     * Array xdata 索引数组，X轴数据
     * Array ydata 索引数组，数字数组,Y轴数据
     */
    function __construct($title,$xdata,$ydata) {
        $this->title = $title;
        $this->xdata = $xdata;
        $this->ydata = $ydata;
        $this->color = array('#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572');
    }

    /*
     * 公有方法，设置条形图的颜色
     * Array color 颜色数组,元素取值为'#058DC7'这种形式
     */
    function setBarColor($color){
        $this->color = $color;
    }

    /*
     * 绘制饼图
     */
    function mkPieChart() {

        $sum = array_sum($this->ydata); // 获取ydata所有元素之和
        $start = 0; // 弧的开始角度
        $end = 0; // 弧的结束角度
        $pieWidth =  300; // 椭圆的长轴
        $pieHeight = 220; // 椭圆的短轴
        $space = 40; // 椭圆与小矩形的间距
        $margin = 20; // 图片的边距
        $recWidth = 20; // 小矩形的宽
        $recHeight = 15; // 小矩形的高
        $titleHeight = 50; // 标题区域的高
        // 图片自适应宽与高
        $this->width = $pieWidth + $this->arrayLengthMax($this->xdata)*10*4/3 + $space + $recWidth +$margin;
        $this->height =  (($pieHeight > count($this->xdata)*25 ) ? $pieHeight : count($this->xdata)*25) + $titleHeight;
        // 椭圆中心的坐标
        $cx = $pieWidth/2+$margin;
        $cy = $pieHeight/2+$titleHeight;

        $this->image = imagecreatetruecolor($this->width ,$this->height); // 准备画布
        $this->bgcolor = imagecolorallocate($this->image,255,255,255); // 图片的背景颜色
        imagefill($this->image,0,0,$this->bgcolor); // 填充背景

        // 设置条形图的颜色
        $color = array();
        foreach($this->color as $col) {
            $col = substr($col,1,strlen($col)-1);
            $red = hexdec(substr($col,0,2));
            $green = hexdec(substr($col,2,2));
            $blue = hexdec(substr($col,4,2));
            $color[] = imagecolorallocate($this->image ,$red, $green, $blue);
        }

        // 设置线段的颜色、字体的颜色、字体的路径
        $lineColor = imagecolorallocate($this->image ,0xcc,0xcc,0xcc);
        $fontColor = imagecolorallocate($this->image, 0x95,0x8f,0x8f);
        $fontPath = 'F:\Project\Environment\web\php\font\simsun.ttc';

        // 绘制扇形弧
        foreach($this->ydata as $key => $val) {
            if($val>0) {
                $end += 360 * $val / $sum;
                imagefilledarc($this->image, $cx, $cy, $pieWidth, $pieHeight, $start, $end, $color[$key % count($this->color)], IMG_ARC_PIE);
                $start = $end;
            }
        }

        // 绘制小矩形及之后文字说明
        $x1 = $pieWidth+$space;
        $y1 = $titleHeight;
        foreach($this->ydata as $key => $val) {
            imagefilledrectangle($this->image,$x1,$y1,$x1+$recWidth,$y1+$recHeight,$color[$key%count($this->color)]);
            imagettftext($this->image,10,0,$x1+$recWidth+5,$y1+$recHeight-2,$fontColor,$fontPath,mb_convert_encoding($this->xdata[$key],'html-entities','UTF-8'));
            $y1 += $recHeight + 10;
        }

        // 绘画标题
        $titleStart = ($this->width - 5.5*strlen($this->title))/2;
        imagettftext($this->image,11,0,$titleStart,20,$fontColor,$fontPath,$this->title);

        // 输出图片
        header("Content-Type:image/png");
        imagepng($this->image);
    }

    /*
     * 私有方法，求数组中元素长度最大的值
     * Array arr 字符串数组，必须是汉字
     */
    private function arrayLengthMax($arr) {
        $length = 0;
        foreach($arr as $val) {
            $length = strlen($val) > $length ? strlen($val) : $length;
        }
        return $length/3;
    }

    // 析构函数
    function __destruct(){
        imagedestroy($this->image);
    }
}

?>