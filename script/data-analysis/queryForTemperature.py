import random
from matplotlib import pyplot as plt # 环境中有和pyplot冲突的会报 ImportError: cannot import name 'pyplot
import matplotlib

x = range(2,26,2)
y = [15,13,14,5,17,20,25,26,24,22,18,15]
# 设置图片大小
plt.figure(figsize=(20,8),dpi=80) # figsize设置图片大小，dpi设置清晰度
plt.plot(x,y)
# 设置x轴的刻度
plt.xticks(x)
# 设置y轴的刻度
plt.yticks(range(min(y),max(y)+1)) # 最后一位取不到，所以要加1
#保存
#plt.savefig("./t1.png")
plt.show()




# 绘制气温变化
# matplotlib默认显示英文，要显示中文要自己设置如下
matplotlib.rc("font",family='KaiTi',weight="bold")

x1 = range(0,120)
y1 = [random.randint(20,35) for i in range(120)]
plt.figure(figsize=(20,8),dpi=80)
plt.plot(x1,y1)

# 调整x轴的刻度
_x1 = list(x1)
_xtick_labels = ["10点{}分".format(i) for  i in range(60)]
_xtick_labels += ["11点{}分".format(i) for  i in range(60)]
# 只有列表才能取步长，range是不能取步长的
plt.xticks(list(_x1)[::3],_xtick_labels[::3],rotation=45) # 设置字符串作为x轴，要有数字和字符一一对应
plt.xlabel("时间")
plt.ylabel("温度 单位（。c）")
plt.title("10点到12点每分钟的气温变化情况")

plt.show()