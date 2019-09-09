import threading
from queue import Queue
from proxy import get_proxy
# 获取一个城市所有的历史数据  by lczCrack  qq1124241615
# 加密参数
from utils import *


# 爬虫的线程对象
class AQIThread(threading.Thread):
    def __init__(self, threadID, city_queue):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.city_queue = city_queue
        self.singal = threading.Event()
        self.singal.set()

    def run(self):
        while not self.city_queue.empty():
            ci = self.city_queue.get()
            result = get_all_info_by_city(city=ci)
            print(result)
            # insert_month_db(result)
            insert_db(result)
            # write_excel(result, ci)

    def pause(self):
        self.log_ctrl.AppendText("pause\n")
        self.singal.clear()

    def restart(self):
        self.log_ctrl.AppendText("continue\n")
        self.singal.set()


if __name__ == '__main__':
    city_queue = Queue()

    # cities = get_city()

    cities = ['齐齐哈尔', '七台河', '黔西南州', '清远', '庆阳', '钦州', '衢州', '泉州',
              '琼中', '荣成', '日喀则', '乳山', '日照', '韶关', '寿光', '上海', '绥化', '石河子', '石家庄', '商洛', '三明', '三门峡', '山南', '遂宁', '四平', '商丘',
              '宿迁', '上饶', '汕头', '汕尾', '绍兴', '三亚', '邵阳', '沈阳', '十堰', '松原', '双鸭山', '深圳', '朔州', '宿州', '随州', '苏州', '石嘴山', '泰安',
              '塔城地区', '太仓', '铜川', '屯昌', '通化', '天津', '铁岭', '通辽', '铜陵', '吐鲁番地区', '铜仁地区', '唐山', '天水', '太原', '台州', '泰州', '文昌', '文登', '潍坊',
              '瓦房店', '威海', '乌海', '芜湖', '武汉', '吴江', '乌兰察布', '乌鲁木齐', '渭南', '万宁', '文山州', '武威', '无锡', '温州', '吴忠', '梧州', '五指山',
              '西安', '兴安盟', '许昌', '宣城', '襄阳', '孝感', '迪庆州', '锡林郭勒盟', '厦门', '西宁', '咸宁', '湘潭', '邢台', '新乡', '咸阳', '新余', '信阳', '忻州',
              '徐州', '雅安', '延安', '延边州', '宜宾', '盐城', '宜昌', '宜春', '银川', '运城', '伊春', '云浮', '阳江', '营口', '榆林', '玉林', '伊犁哈萨克州', '阳泉',
              '玉树州', '烟台', '鹰潭', '义乌', '宜兴', '玉溪', '益阳', '岳阳', '扬州', '永州', '淄博', '自贡', '珠海', '湛江', '镇江', '诸暨', '张家港', '张家界',
              '张家口', '周口', '驻马店', '章丘', '肇庆', '中山', '舟山', '昭通', '中卫', '张掖', '招远', '资阳', '遵义', '枣庄', '漳州', '郑州', '株洲']

    for i in cities:
        city_queue.put(i)

    threads = [AQIThread(i, city_queue) for i in range(num_of_threads)]
    for i in range(num_of_threads):
        threads[i].start()
