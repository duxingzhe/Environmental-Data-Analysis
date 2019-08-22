<?php
$image=imagecreatetruecolor(220,220); //制作画布
//是指图像中所需的颜色，相当画画是准备的染料
$white=imagecolorallocate($image,0xff,0xff,0xff);
$darkgray=imagecolorallocate($image,0xc0,0xc0,0xc0);
$gray=imagecolorallocate($image,0x90,0x90,0x90);
$navy=imagecolorallocate($image,0x00,0x00,0x80);
$darknavy=imagecolorallocate($image,0x00,0x00,0x50);
$red=imagecolorallocate($image,0xff,0x00,0x00);
$darkred=imagecolorallocate($image,0x90,0x00,0x00);
imagefill($image,0,0,$white);

imagefilledarc($image,100,110,200,200,-160,40,$darknavy,IMG_ARC_PIE);
imagefilledarc($image,100,110,200,200,40,75,$darkgray,IMG_ARC_PIE);
imagefilledarc($image,100,110,200,200,75,200,$darkred,IMG_ARC_PIE);

imagestring($image,10,35,135,'34.7%',$white);
imagestring($image,10,85,65,'55.5%',$white);
header('Content-type:image/png');
imagepng($image);
imagedestroy($image);
?>