# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
from mpl_toolkits.basemap import Basemap
from matplotlib.patches import Polygon
from matplotlib.colors import rgb2hex
import numpy as np
import pandas as pd

plt.figure(figsize=(16, 8))
m = Basemap(
    llcrnrlon=77,
    llcrnrlat=14,
    urcrnrlon=140,
    urcrnrlat=51,
    projection='lcc',
    lat_1=33,
    lat_2=45,
    lon_0=100
)
m.drawcountries(linewidth=1.5)
m.drawcoastlines()

m.readshapefile('gadm36_CHN_shp/gadm36_CHN_1', 'states', drawbounds=True)

df = pd.read_csv('pop.csv')
new_index_list = []
for i in df["地区"]:
    i = i.replace(" " ,"")
    new_index_list.append(i)
new_index = {"region": new_index_list}
new_index = pd.DataFrame(new_index)
df = pd.concat([df ,new_index], axis=1)
df = df.drop(["地区"], axis=1)
df.set_index("region", inplace=True)

provinces = m.states_info
statenames =[]
colors = {}
cmap = plt.cm.YlOrRd
vmax = 100000000
vmin = 3000000

for each_province in provinces:
    province_name = each_province['NL_NAME_1']
    p = province_name.split('|')
    if len(p) > 1:
        s = p[1]
    else:
        s = p[0]
    s = s[:2]
    if s == '黑龍':
        s = '黑龙江'
    if s == '内蒙':
        s = '内蒙古'
    statenames.append(s)
    pop = df['人口数'][s]
    colors[s] = cmap(np.sqrt((pop - vmin) / (vmax - vmin)))[:3]

ax = plt.gca()
for nshape, seg in enumerate(m.states):
    color = rgb2hex(colors[statenames[nshape]])
    poly = Polygon(seg, facecolor=color, edgecolor=color)
    ax.add_patch(poly)

plt.show()