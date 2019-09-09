# -*- coding: utf-8 -*-
# 导入库

import matplotlib.pyplot as plt
import matplotlib.font_manager as fm  # 字体管理器
import pymysql  # 连接数据库

# 创建数据库

conn = pymysql.connect(host='localhost', port=3306, db='environment_record', user='root', passwd='', charset='utf8')
cur = conn.cursor()


def draw(cities):
    for city in cities:
        # 查询原数据

        select = "SELECT aqi FROM month_data WHERE cityname='"+city+"'"

        cur.execute(select)
        all_data = cur.fetchall()

        # 防止查不到数据

        if len(all_data) != 0:

            # 准备数据

            aqiArray = []

            # 准备标签

            labels = ['优', '良', '轻度', '中度', '重度', '严重污染']

            for aqi in all_data:
                aqiArray.append(int(aqi[0]))

            aqiLen = len(aqiArray)

            good = 0
            moderate = 0
            unhealthyForSensitive = 0
            unhealthy = 0
            veryUnhealthy = 0
            hazardous = 0

            # 获得空气质量登记统计数据

            for i in range(0, aqiLen):
                if aqiArray[i] < 50:
                    good += 1
                elif aqiArray[i] < 100:
                    moderate += 1
                elif aqiArray[i] < 150:
                    unhealthyForSensitive += 1
                elif aqiArray[i] < 200:
                    unhealthy += 1
                elif aqiArray[i] < 300:
                    veryUnhealthy += 1
                else:
                    hazardous += 1

            aqi_level = [good, moderate, unhealthyForSensitive, unhealthy, veryUnhealthy, hazardous]

            aqi_percentage = []

            for i in range(0, 6):
                aqi_percentage.append(aqi_level[i] / aqiLen)

            # 准备字体

            my_font = fm.FontProperties(fname="F:\\Project\\Environment\\web\\php\\font\\simsun.ttc")

            # 将排列在第4位的语言(Python)分离出来

            explode = [0, 0, 0, 0, 0, 0]

            # 使用自定义颜色

            colors = ['#009966', '#FFDE33', '#FF9933', '#CC0033', '#660099', '#7E0023']

            # 将横', '纵坐标轴标准化处理,保证饼图是一个正圆,否则为椭圆

            plt.axes(aspect='equal')

            # 控制X轴和Y轴的范围(用于控制饼图的圆心', '半径)

            plt.xlim(0, 10)

            plt.ylim(0, 10)

            # 不显示边框

            plt.gca().spines['right'].set_color('none')

            plt.gca().spines['top'].set_color('none')

            plt.gca().spines['left'].set_color('none')

            plt.gca().spines['bottom'].set_color('none')

            # 绘制饼图

            plt.pie(x=aqi_percentage,  # 绘制数据
                    labels=labels,  # 添加编程语言标签
                    explode=explode,  # 突出显示Python

                    colors=colors,  # 设置自定义填充色

                    autopct='%.3f%%',  # 设置百分比的格式,保留3位小数

                    pctdistance=0.5,  # 设置百分比标签和圆心的距离

                    labeldistance=0.8,  # 设置标签和圆心的距离

                    startangle=180,  # 设置饼图的初始角度

                    center=(4, 4),  # 设置饼图的圆心(相当于X轴和Y轴的范围)

                    radius=4.0,  # 设置饼图的半径(相当于X轴和Y轴的范围)

                    counterclock=False,  # 是否为逆时针方向,False表示顺时针方向

                    wedgeprops={'linewidth': 1, 'edgecolor': 'green'},  # 设置饼图内外边界的属性值

                    textprops={'fontsize': 12, 'color': 'black', 'fontproperties': my_font},  # 设置文本标签的属性值

                    frame=1)  # 是否显示饼图的圆圈,1为显示

            # 不显示X轴', 'Y轴的刻度值

            plt.xticks(())

            plt.yticks(())

            # 添加图形标题

            plt.title(city+'月空气质量指数占比情况', loc='left', fontproperties=my_font)

            #保存图片

            plt.savefig("./../../article/image/aqi/" + city + ".png")
            
            # 显示图形

            plt.show()


# 南北方城市列表

southern_cities = ['广州', '深圳', '珠海', '东莞', '惠州', '汕头', '湛江', '潮州', '阳江', '韶关', '河源', '佛山', '海丰', '梅州',
                   '茂名', '南宁', '桂林', '梧州', '柳州', '百色', '河池', '贺州', '钦州', '北海', '防城港', '海口', '三亚', '福州',
                   '厦门', '泉州', '南平', '漳州', '三明；昆明', '大理', '曲靖', '楚雄', '昭通', '保山', '版纳', '思茅', '文山',
                   '贵阳', '六盘水', '铜仁', '凯里', '安顺', '遵义','长沙', '湘潭', '株洲', '常德', '岳阳', '吉首', '冷水江',
                   '南昌', '九江', '赣州', '吉安', '鹰潭', '景德镇', '萍乡','杭州', '宁波', '湖州', '嘉兴', '绍兴', '舟山', '温州',
                   '台州', '金华', '丽水', '成都', '绵阳', '南充', '自贡', '峨眉山', '宜宾', '泸州', '康定', '阿坝', '西昌', '攀枝花',
                   '重庆', '涪陵', '万州', '武汉', '宜昌', '襄樊', '鄂州', '荆州', '黄石', '孝感', '沙市', '咸宁','合肥', '芜湖',
                   '安庆', '黄山', '贵池', '铜陵', '宣城', '马鞍山', '巢湖', '滁州','南京', '镇江', '扬州', '泰州', '常州', '无锡',
                   '苏州', '南通', '盐城']

northern_cities = ['北京', '天津', '石家庄', '承德', '张家口', '唐山', '秦皇岛', '保定', '廊坊', '沧州', '衡水', '邢台', '邯郸',
                   '安阳', '开封', '洛阳', '郑州', '南阳', '三门峡', '登封', '信阳', '商丘', '大同', '太原', '榆次', '忻州', '朔州',
                   '长治', '大连', '沈阳', '锦州', '凌源', '鞍山', '阜新', '吉林', '长春', '伊春', '白城', '丹东', '哈尔滨', '齐齐哈尔',
                   '满洲里', '漠河', '黑河', '七台河', '牡丹江', '鹤岗', '旅顺', '二连浩特', '包头', '呼和浩特', '锡林浩特', '乌兰浩特',
                   '集宁', '德州', '济南', '泰安', '聊城', '泰州', '徐州', '青岛', '烟台', '潍坊', '淄博', '延安', '西安', '宝鸡',
                   '榆林', '汉中', '咸阳', '离石', '介休', '银川', '石嘴山', '兰州', '天水', '酒泉', '张掖', '嘉峪关', '玉门', '西宁',
                   '格尔木', '乌鲁木齐', '喀什', '善僐', '伊犁', '石河子', '吐鲁番']

if __name__ == '__main__':
    draw(southern_cities)
    draw(northern_cities)

