from mpl_toolkits.axisartist.parasite_axes import HostAxes, ParasiteAxes
import matplotlib.pyplot as plt
import matplotlib.font_manager as fm  # 字体管理器
import numpy as np
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
        x2 = [i for i in range(0, 29)]
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

    draw_cities(southern_cities)
    draw_cities(northern_cities)