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
        plt.figure(figsize=(20, 8), dpi=80)

        x1 = time_point
        y1 = so2
        plt.plot(x1, y1, color='#009966')

        y2 = no2
        plt.plot(x1, y2, color='#FFDE33')

        y3 = co
        plt.plot(x1, y3, color='#FF9933')

        # 坐标轴标签
        plt.xlabel("时间", fontproperties=my_font)
        plt.ylabel("μg/m3(CO为mg/m3)", fontproperties=my_font)

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