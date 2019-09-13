from bs4 import BeautifulSoup
import requests
import pymysql
import warnings
# import pinyin
# from pinyin import PinYin
from pypinyin import pinyin, lazy_pinyin
import pypinyin

warnings.filterwarnings("ignore")

def get_temperature(url, city):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,  like Gecko) Chrome/63.0.3239.132 Safari/537.36'}  # 设置头文件信息
    response = requests.get(url, headers=headers).content  # 提交requests get 请求
    soup = BeautifulSoup(response, "lxml")  # 用Beautifulsoup 进行解析

    conmid2 = soup.findAll('div', class_='wdetail')
    # conmid2 = conmid.findAll('div',  class_='wdetail')

    for info in conmid2:
        tr_list = info.find_all('tr')[1:]  # 使用切片取到第三个tr标签
        for index, tr in enumerate(tr_list):  # enumerate可以返回元素的位置及内容
            td_list = tr.find_all('td')
            # if index == 0:

            date = td_list[0].text.strip().replace("\n", "")  # 取每个标签的text信息，并使用replace()函数将换行符删除
            weather = td_list[1].text.strip().replace("\n", "").split("/")[0].strip()
            min = td_list[2].text.strip().replace("\n", "").split("/")[0].strip()
            max = td_list[2].text.strip().replace("\n", "").split("/")[1].strip()
            wind = td_list[3].text.strip().replace("\n", "").split("/")[0].strip()

            # else:
            #     city_name = td_list[0].text.replace('\n',  '')
            #     weather = td_list[4].text.replace('\n',  '')
            #     wind = td_list[5].text.replace('\n',  '')
            #     max = td_list[3].text.replace('\n',  '')
            #     min = td_list[6].text.replace('\n',  '')

            print(city, date, weather, wind, max, min)
            conn = pymysql.connect(host='localhost', user='root', passwd='', db='environment_record', port=3306,
                                   charset='utf8')
            cursor = conn.cursor()
            cursor.execute('INSERT INTO weather(city, date, weather, wind, min, max) VALUES(%s, %s, %s, %s, %s, %s)',
                           (city, date, weather, wind, min, max))
            conn.commit()
            conn.close()

if __name__ == '__main__':

    cities = ['北京', '天津', '石家庄', '唐山', '秦皇岛', '邯郸', '邢台', '保定', '张家口', '承德', '沧州', '廊坊', '衡水', '太原', '大同',
              '阳泉', '长治', '晋城', '朔州', '晋中', '运城', '忻州', '临汾', '吕梁',
              '呼和浩特', '包头', '乌海', '赤峰', '通辽', '鄂尔多斯', '呼伦贝尔', '巴彦淖尔', '乌兰察布', '兴安盟', '锡林郭勒', '阿拉善盟',
              '沈阳', '大连', '鞍山', '抚顺', '本溪', '丹东', '锦州', '营口', '阜新', '辽阳', '盘锦', '昌图', '朝阳', '葫芦岛',
              '长春', '吉林', '四平', '辽源', '通化', '白山', '松原', '白城', '延边',
              '哈尔滨', '齐齐哈尔', '鸡西', '鹤岗', '双鸭山', '大庆', '伊春', '佳木斯', '七台河', '牡丹江', '黑河', '绥化', '大兴安岭',
              '上海', '南京', '无锡', '徐州', '常州', '苏州', '南通', '连云港', '淮安', '盐城', '扬州', '镇江', '泰州', '宿迁',
              '杭州', '宁波', '温州', '嘉兴', '湖州', '绍兴', '金华', '衢州', '舟山', '台州', '丽水',
              '合肥', '芜湖', '蚌埠', '淮南', '马鞍山', '淮北', '铜陵', '安庆', '黄山', '滁州', '阜阳', '宿州', '巢湖', '六安', '亳州', '池州', '宣城',
              '福州', '厦门', '莆田', '三明', '泉州', '漳州', '南平', '龙岩', '宁德', '南昌', '景德镇', '萍乡', '九江', '新余', '鹰潭', '赣州', '吉安', '宜春',
              '抚州', '上饶',
              '济南', '青岛', '淄博', '枣庄', '东营', '烟台', '潍坊', '济宁', '泰安', '威海', '日照', '莱芜', '临沂', '德州', '聊城', '滨州', '菏泽',
              '郑州',
              '开封', '洛阳', '平顶山', '安阳', '鹤壁', '新乡', '焦作', '濮阳', '许昌', '漯河', '三门峡', '南阳', '商丘', '信阳', '周口',
              '驻马店', '武汉', '黄石', '十堰', '宜昌', '襄阳', '鄂州', '荆门', '孝感', '荆州', '黄冈', '咸宁', '随州', '恩施', '仙桃', '潜江', '天门',
              '神农架',
              '长沙', '株洲', '湘潭', '衡阳', '邵阳', '岳阳', '常德', '张家界', '益阳', '郴州', '永州', '怀化', '娄底', '湘西',
              '广州', '韶关', '深圳', '珠海', '汕头', '佛山', '江门', '湛江', '茂名', '肇庆', '惠州', '梅州', '汕尾', '河源', '阳江', '清远', '东莞',
              '中山', '潮州', '揭阳', '云浮',
              '南宁', '柳州', '桂林', '梧州', '北海', '防城港', '钦州', '贵港', '玉林', '百色', '贺州', '河池', '来宾', '崇左',
              '海口', '三亚', '五指山', '琼海', '儋州', '文昌', '万宁', '东方', '定安', '屯昌', '澄迈', '临高', '白沙', '昌江', '陵水', '西沙群岛',
              '南沙群岛',
              '重庆', '成都', '自贡', '攀枝花', '泸州', '德阳', '绵阳', '广元', '遂宁', '内江', '乐山', '南充', '眉山', '宜宾', '广安', '达州', '雅安',
              '巴中', '资阳', '阿坝', '甘孜', '凉山',
              '贵阳', '六盘水', '遵义', '安顺', '铜仁', '黔西南', '毕节', '黔东南', '黔南',
              '昆明', '曲靖', '玉溪', '保山', '昭通', '丽江', '普洱', '临沧', '楚雄', '红河', '文山', '西双版纳', '大理', '德宏', '怒江', '迪庆',
              '拉萨', '昌都', '山南', '日喀则', '那曲', '阿里', '林芝',
              '西安', '铜川', '宝鸡', '咸阳', '渭南', '延安', '汉中', '榆林', '安康', '商洛',
              '兰州', '嘉峪关', '金昌', '白银', '天水', '武威', '张掖', '平凉', '酒泉', '庆阳', '定西', '陇南', '临夏', '甘南',
              '西宁', '海东', '海北', '黄南', '海南', '果洛', '玉树', '海西',
              '宁夏银川', '石嘴山', '吴忠', '固原', '中卫',
              '乌鲁木齐', '克拉玛依', '吐鲁番', '哈密', '昌吉', '博州', '巴州', '阿克苏', '克州', '喀什', '和田', '伊犁', '塔城', '阿勒泰', '石河子', '五家渠',
              '台北', '台中', '高雄', '香港', '澳门']

    for city in cities:
        city1 = ''.join(lazy_pinyin(city[:]))
        print(city1)
        urls = []
        for year in range(2011, 2019):
            for month in range(1, 12):
                if 1 <= month <= 9:
                    month = "0" + str(month)
                    urls.append('http://www.tianqihoubao.com/lishi/' + city1 + '/month/' + str(year) + month + '.html')

        for url in urls:
            get_temperature(url, city)
