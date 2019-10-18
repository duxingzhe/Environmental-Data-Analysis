import cartopy.crs as ccrs
from cartopy.examples.waves import sample_data
import matplotlib.pyplot as plt
from cartopy.mpl.patch import path_to_geos
import shapely.ops
import json
import shapely.geometry as sgeom
import numpy as np
import skimage.io

lons, lats, data = sample_data()

plt.figure()
ax = plt.axes(projection=ccrs.PlateCarree())
cs = plt.contourf(
    lons, lats, data, 5,  # Choose approximately 5 sensible levels.
    transform=ccrs.PlateCarree())
ax.coastlines()
plt.colorbar(orientation='horizontal')
plt.show()

paths=cs.collections[4].get_paths()

geoms=[]
for path in paths:
    geoms.extend(path_to_geos(path))

polygon=shapely.ops.unray_union(geoms)

with open('contour.geojson', 'w') as fh:
    json.dump(sgeom.mapping(polygon), fh)

atw80d = skimage.io.imread('640px-Around_the_World_in_Eighty_Days_map.png')

yellowish=((atw80d[:, :, 0]>200)&
           (atw80d[:, :, 1]>200)&
           (atw80d[:, :, 2]<100)&
           (atw80d[:, :, 3]>250))

ind_y, ind_x=np.where(yellowish)

def pixels_to_data(extent, shape, i, j):
    xmin,xmax,ymin,ymax=extent
    x_range=xmax-xmin
    y_range=ymax-ymin

    pix_width=x_range/shape[1]
    pix_height=y_range/shape[0]

    j=shape[0]-j
    return (xmin+pix_width*(i+0.5),
            ymin+pix_height*(i+0.5))

extent=[-13636707, 17044670, -6308712, 8565930]

xs, ys=pixels_to_data(
    extent, atw80d.shape, ind_x, ind_y
)

plt.figure()

rob=ccrs.Robinson(central_longitude=11.25)
ax=plt.axes(project=rob)

ax.gridlines()
ax.costalines()
ax.imshow(atw80d, extent=extent, transform=rob, origin='upper')
ax.set_global()

plt.plot(xs, ys, transform=rob,
         linestyle='none', marker='o',
         markeredgecolor='k', color='yellow')
plt.show()
