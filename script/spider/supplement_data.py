import pymysql
import warnings
from pypinyin import lazy_pinyin
from utils import *

warnings.filterwarnings("ignore")

if __name__ == '__main__':

    city=''

    city1 = ''.join(lazy_pinyin(city[:]))
    print(city1)
    url = 'http://www.tianqihoubao.com/lishi/' + city1 + '/month/201801.html'

    result_list = get_temperature(url, city)

    # 一年一年存储
    conn = pymysql.connect(host='localhost', user='root', passwd='', db='environment_record', port=3306,
                           charset='utf8')
    cursor = conn.cursor()
    cursor.execute('INSERT INTO weather(city, date, weather, wind, min, max) VALUES(%s, %s, %s, %s, %s, %s)',
                       list)
    conn.commit()
    conn.close()
