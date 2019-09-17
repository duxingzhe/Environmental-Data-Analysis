import pymysql
import warnings
from pypinyin import lazy_pinyin
from utils import *

warnings.filterwarnings("ignore")

if __name__ == '__main__':

    cities = get_all_cities()

    # cities = ['澳门']

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
