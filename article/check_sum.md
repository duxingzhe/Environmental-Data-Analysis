1、 先将需要计算checksum数据中的checksum设为0； 
2、 计算checksum的数据按2byte划分开来，每2byte组成一个16bit的值，如果最后有单个byte的数据，补一个byte的0组成2byte； 
3、 将所有的16bit值累加到一个32bit的值中； 
4、 将32bit值的高16bit与低16bit相加到一个新的32bit值中，若新的32bit值大于0Xffff, 
再将新值的高16bit与低16bit相加； 
5、 将上一步计算所得的16bit值按位取反，即得到checksum值，存入数据的checksum字段即可。

抓个IP数据包，取IP数据报报头部分(20B)，数据如下：

45 00 00 30 80 4c 40 00 80 06 b5 2e d3 43 11 7b cb 51 15 3d

下面我来计算一下校验和：

(1)将校验和字段置为0：

将b5 2e置为00 00,即变成: 
45 00 00 30 80 4c 40 00 80 06 00 00 d3 43 11 7b cb 51 15 3d

(2)求和

4500+0030+804c+4000+8006+0000+d343+117b+cb51+153d=34ace
将将进位(3)加到低16位(4ace)上:0003+4ace=4ad1

(3)取反码

 将4ad1取反得:checksum=b52e
 
接收IP数据报检验IP校验和

(1)对首部中每个16 bit进行二进制反码求和;
(2)将(1)中得到的和再取反码 ,看是否为0.

接收到的IP数据报首部:

45 00 00 30 80 4c 40 00 80 06 b5 2e d3 43 11 7b cb 51 15 3d 
 
下面来验证下:

(1)反码求和

4500+0030+804c+4000+8006+b52e+d343+117b+cb51+153d=3fffc
0003+fffc=ffff

(2)取反码:~ffff=0 正确