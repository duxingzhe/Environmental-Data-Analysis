import pymysql
import warnings
from pypinyin import lazy_pinyin
from utils import *

warnings.filterwarnings("ignore")

if __name__ == '__main__':

    cities = ['惠州', '梅州', '汕尾', '河源', '阳江', '清远', '东莞', '中山', '潮州', '揭阳', '云浮',
              '南宁', '柳州', '桂林', '梧州', '北海', '防城港', '钦州', '贵港', '玉林', '百色', '贺州', '河池', '来宾', '崇左',
              '海口', '三亚', '五指山', '琼海', '儋州', '文昌', '万宁', '东方', '定安', '屯昌', '澄迈', '临高', '白沙', '昌江', '陵水', '西沙群岛', '南沙群岛',
              '重庆', '成都', '自贡', '攀枝花', '泸州', '德阳', '绵阳', '广元', '遂宁', '内江', '乐山', '南充', '眉山', '宜宾', '广安', '达州', '雅安',
              '巴中', '资阳', '阿坝', '甘孜', '凉山', '贵阳', '六盘水', '遵义', '安顺', '铜仁', '黔西南', '毕节', '黔东南', '黔南',
              '昆明', '曲靖', '玉溪', '保山', '昭通', '丽江', '普洱', '临沧', '楚雄', '红河', '文山', '西双版纳', '大理', '德宏', '怒江', '迪庆',
              '拉萨', '昌都', '山南', '日喀则', '那曲', '阿里', '林芝', '西安', '铜川', '宝鸡', '咸阳', '渭南', '延安', '汉中', '榆林', '安康', '商洛',
              '兰州', '嘉峪关', '金昌', '白银', '天水', '武威', '张掖', '平凉', '酒泉', '庆阳', '定西', '陇南', '临夏', '甘南',
              '西宁', '海东', '海北', '黄南', '海南', '果洛', '玉树', '海西', '宁夏银川', '石嘴山', '吴忠', '固原', '中卫',
              '乌鲁木齐', '克拉玛依', '吐鲁番', '哈密', '昌吉', '博州', '巴州', '阿克苏', '克州', '喀什', '和田', '伊犁', '塔城', '阿勒泰', '石河子', '五家渠',
              '台北', '台中', '高雄', '香港', '澳门']

    for city in cities:

        city1 = ''.join(lazy_pinyin(city[:]))

        city1 = pinyin_correction(city1)

        print(city1)

        # 2011——2019年
        now_y, now_m, now_day = datetime.datetime.now().strftime('%Y-%m-%d').split('-')
        for year in range(2011, int(now_y)+1):

            urls = []

            # 1月——12月
            for month in range(1, 13):
                if 1 <= month <= 9:
                    month = "0" + str(month)

                if year == int(now_y) and int(month) > int(now_m):
                    break

                urls.append('http://www.tianqihoubao.com/lishi/' + city1 + '/month/' + str(year) + str(month) + '.html')

            for url in urls:

                result_list = get_temperature(url, city)

                # 一个月存储一次
                conn = pymysql.connect(host='localhost', user='root', passwd='', db='environment_record', port=3306,
                                       charset='utf8')
                cursor = conn.cursor()
                cursor.executemany('INSERT INTO weather(city, date, weather, wind, min, max) VALUES(%s, %s, %s, %s, %s, %s)',
                                   result_list)
                conn.commit()
                conn.close()
