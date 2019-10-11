from mpl_toolkits.axisartist.parasite_axes import HostAxes, ParasiteAxes
import matplotlib.pyplot as plt
import matplotlib.font_manager as fm  # 字体管理器
from utils import *
import pymysql  # 连接数据库

conn = pymysql.connect(host='localhost', port=3306, db='environment_record', user='root', passwd='', charset='utf8')
cur = conn.cursor()

plt.rcParams['font.sans-serif'] =['SimSun']
plt.rcParams['axes.unicode_minus'] = False

def draw_city(city):
    select = "SELECT so2, no2, co, time_point FROM day_data WHERE cityname='" + city + "'"+"AND time_point Between '2019-02-01' AND '2019-03-01'"

    cur.execute(select)
    all_data = cur.fetchall()

    # 防止查不到数据

    if len(all_data) != 0:

        # 准备数据

        so2 = []
        no2 = []
        co = []
        time_point = []

        # 准备标签

        for data in all_data:
            so2.append(int(data[0]))
            no2.append(int(data[1]))
            co.append(float(data[2]))
            time_point.append(str(data[3]))

        # 准备字体

        my_font = fm.FontProperties(fname="F:\\Project\\Environment\\web\\php\\font\\simsun.ttc")
        fontcn = {'family': 'SimSun', 'size': 10}

        # 绘制气温变化
        fig = plt.figure(figsize=(40, 8), dpi=80)

        ax_so2 = HostAxes(fig, [0, 0, 0.9, 0.9])  # 用[left, bottom, weight, height]的方式定义axes，0 <= l,b,w,h <= 1

        # parasite addtional axes, share x
        ax_no2 = ParasiteAxes(ax_so2, sharex=ax_so2)
        ax_co = ParasiteAxes(ax_so2, sharex=ax_so2)

        ax_so2.parasites.append(ax_no2)
        ax_so2.parasites.append(ax_co)

        # invisible right axis of ax_so2
        ax_so2.axis['right'].set_visible(False)
        ax_so2.axis['top'].set_visible(False)
        ax_no2.axis['right'].set_visible(True)
        ax_no2.axis['right'].major_ticklabels.set_visible(True)
        ax_no2.axis['right'].label.set_visible(True)

        # set label for axis
        ax_so2.set_ylabel("so2 μg/m3", fontdict=fontcn)
        ax_so2.set_xlabel("时间", fontdict=fontcn)
        ax_no2.set_ylabel("no2 μg/m3", fontdict=fontcn)
        ax_co.set_ylabel("CO mg/m3", fontdict=fontcn)

        co_axisline = ax_co.get_grid_helper().new_fixed_axis

        ax_co.axis['right2'] = co_axisline(loc='right', axes=ax_co, offset=(40, 0))

        fig.add_axes(ax_so2)

        x1 = time_point
        x2 = [i for i in range(0, len(x1))]
        y1 = so2
        y2 = no2
        y3 = co

        ax_so2.plot(x1, y1, label='so2', color='#009966')
        ax_no2.plot(x2, y2, label='no2', color='#FFDE33')
        ax_co.plot(x2, y3, label='co', color='#FF9933')

        ax_so2.legend()

        ax_no2.axis['right'].label.set_color('#FFDE33')
        ax_co.axis['right2'].label.set_color('#FF9933')

        ax_no2.axis['right'].major_ticks.set_color('#FFDE33')
        ax_co.axis['right2'].major_ticks.set_color('#FF9933')

        ax_no2.axis['right'].major_ticklabels.set_color('#FFDE33')
        ax_co.axis['right2'].major_ticklabels.set_color('#FF9933')

        ax_no2.axis['right'].line.set_color('#FFDE33')
        ax_co.axis['right2'].line.set_color('#FF9933')

        # 添加图形标题

        plt.title(city + '日二氧化硫、二氧化氮、一氧化碳浓度变化情况', loc='center', fontproperties=my_font)

        # 保存图片

        plt.savefig("./../../article/image/pollution/" + city + ".png", bbox_inches='tight')

        # 显示图形
        # show ann img
        plt.cla()  # clear fig to show ann img
        saved_img = plt.imread("./../../article/image/pollution/" + city + ".png")

        # keep the origin image size
        dpi = 80.0
        height, width, depth = saved_img.shape
        plt.figure(figsize=(width / dpi, height / dpi))
        plt.axis('off')

        plt.imshow(saved_img)
        plt.show()

def draw_cities(cities):

    for city in cities:

        draw_city(city)

if __name__ == '__main__':

    draw_cities(southern_cities)
    draw_cities(northern_cities)
