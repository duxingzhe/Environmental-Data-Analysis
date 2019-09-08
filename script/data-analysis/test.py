# -*- coding: utf-8 -*-
# 导入库

import matplotlib.pyplot as plt
import matplotlib.font_manager as fm  # 字体管理器
import pymysql  # 连接数据库

# 创建数据库

conn = pymysql.connect(host='localhost', port=3306, db='environment_record', user='root', passwd='', charset='utf8')
cur = conn.cursor()

# 查询原数据

select = "SELECT time_point, aqi FROM month_data WHERE cityname='广州'"

cur.execute(select)
all_data = cur.fetchall()

# 准备数据

aqiArray = []

# 准备标签

labels = ['优', '良', '轻度', '中度', '重度', '严重污染']

for timepoint, aqi in all_data:
    print(timepoint + "   " + aqi)

for aqi in all_data:
    aqiArray.append(int(aqi[1]))

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

# 将横、纵坐标轴标准化处理,保证饼图是一个正圆,否则为椭圆

plt.axes(aspect='equal')

# 控制X轴和Y轴的范围(用于控制饼图的圆心、半径)

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

# 不显示X轴、Y轴的刻度值

plt.xticks(())

plt.yticks(())

# 添加图形标题

plt.title('广州市2013年12月——2019年08月空气质量指数占比情况', loc='left', fontproperties=my_font)

# 显示图形

plt.show()
