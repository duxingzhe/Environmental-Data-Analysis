from mpl_toolkits.axisartist.parasite_axes import HostAxes, ParasiteAxes
import matplotlib.pyplot as plt
import matplotlib.font_manager as fm  # 字体管理器
import pymysql  # 连接数据库

conn = pymysql.connect(host='localhost', port=3306, db='environment_record', user='root', passwd='', charset='utf8')
cur = conn.cursor()

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

        # 绘制气温变化
        fig = plt.figure(figsize=(20, 8), dpi=80)

        ax = fig.add_subplot(111)

        x1 = time_point
        y1 = so2
        lns1 = ax.plot(x1, y1, label='so2', color='#009966')

        y2 = no2
        lns2 = ax.plot(x1, y2, label='no2', color='#FFDE33')

        ax2 = ax.twinx()  # 添加一条Y轴
        y3 = co
        lns3 = ax2.plot(x1, y3, label='co', color='#FF9933')

        # 坐标轴标签

        ax.set_xlabel("时间", fontproperties=my_font)
        ax.set_ylabel("μg/m3", fontproperties=my_font)
        ax2.set_ylabel("CO为mg/m3", fontproperties=my_font)

        # added these three lines
        lns = lns1 + lns2 + lns3
        labs = [l.get_label() for l in lns]
        ax.legend(lns, labs, loc=0)

        # 添加图形标题

        plt.title(city + '日二氧化硫、二氧化氮、一氧化碳浓度变化情况', loc='left', fontproperties=my_font)

        # 保存图片

        plt.savefig("./../../article/image/pollution/" + city + ".png")

        # 显示图形

        plt.show()

def draw_cities(cities):

    for city in cities:

        draw_city(city)

if __name__ == '__main__':

    draw_cities(['北京'])
    # draw_cities(northern_cities)