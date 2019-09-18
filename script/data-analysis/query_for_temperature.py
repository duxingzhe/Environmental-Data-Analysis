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
    select = "SELECT max, min, date FROM weather WHERE city='" + city + "'"+" AND date Between '2018年02月01日' AND '2018年02月28日'"

    cur.execute(select)
    all_data = cur.fetchall()

    # 防止查不到数据

    if len(all_data) != 0:

        # 准备数据

        max = []
        min = []
        time_point = []

        # 准备标签

        for data in all_data:
            max_temperature = str(data[0])
            max.append(int(max_temperature[0:len(max_temperature)-1]))

            min_temperature = str(data[1])
            min.append(int(min_temperature[0:len(min_temperature)-1]))

            time_point.append(str(data[2]))

        # 准备字体

        my_font = fm.FontProperties(fname="F:\\Project\\Environment\\web\\php\\font\\simsun.ttc")
        fontcn = {'family': 'SimSun', 'size': 10}

        # 绘制气温变化
        fig = plt.figure(figsize=(40, 8), dpi=80)

        ax_max = HostAxes(fig, [0, 0, 0.9, 0.9])  # 用[left, bottom, weight, height]的方式定义axes，0 <= l,b,w,h <= 1

        # parasite addtional axes, share x
        ax_min = ParasiteAxes(ax_max, sharex=ax_max)

        ax_max.parasites.append(ax_min)

        # invisible right axis of ax_so2
        ax_max.axis['right'].set_visible(False)
        ax_max.axis['top'].set_visible(False)
        ax_min.axis['right'].set_visible(True)
        ax_min.axis['right'].major_ticklabels.set_visible(True)
        ax_min.axis['right'].label.set_visible(True)

        # set label for axis
        ax_max.set_ylabel("摄氏度（℃）", fontdict=fontcn)
        ax_max.set_xlabel("时间", fontdict=fontcn)
        ax_min.set_ylabel("摄氏度（℃）", fontdict=fontcn)

        fig.add_axes(ax_max)

        x1 = time_point
        x2 = [i for i in range(0, len(time_point))]
        y1 = max
        y2 = min

        ax_max.plot(x1, y1, label='最高温度', color='#009966')
        ax_min.plot(x2, y2, label='最低温度', color='#FFDE33')

        ax_max.legend()

        ax_min.axis['right'].label.set_color('#FFDE33')

        ax_min.axis['right'].major_ticks.set_color('#FFDE33')

        ax_min.axis['right'].major_ticklabels.set_color('#FFDE33')

        ax_min.axis['right'].line.set_color('#FFDE33')

        # 添加图形标题

        plt.title(city + '最高温度和最低温度变化情况', loc='center', fontproperties=my_font)

        # 保存图片

        plt.savefig("./../../article/image/temperature/" + city + ".png", bbox_inches='tight')

        # 显示图形
        # show ann img
        plt.cla()  # clear fig to show ann img
        saved_img = plt.imread("./../../article/image/temperature/" + city + ".png")

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
