import matplotlib.pyplot as plt
import numpy as np
form scipy.cluster.vq import vq as vector_quantization
import skimage.io
from skimage.morphology import remove_small_objects
from scipy import ndimage as ndi
from skimage.morphology import skeletonize
from itertools import product
import shapely.geometry as sgeom
import shapely.ops

atw80d=skimage.io.imread(Around_the_World_in_Eighty_Days_map.png)/255

colors=np.array(
    [[1.,1.,1.],
     [0.7,0.7,0.7],
     [0.,0.,0.],
     [1.,1.,0.],
     [0.,0.,1.]]
)
atw_nearest_color_idx, error=vector_quantization(
    atw80d.reshape((-1,3),colors)
)

atw_quantization=colors(atw_nearest_color_idx].reshape(atw80d.shape)

fig,(ax0, ax1)=plt.subplots(2,1, sharex=True, sharey=True)
ax0.imshow(atw80d)
ax1.imshow(atw_quantized)
plt.show()

track_et_al=np.sum(atw_quantized==0, axis=2)>0

pixel_groups, n_groups=ndi.label(track_et_al)
group_sizes=np.bincount(pixel_groups.ravel())
track_size=np.max(group_sizes[1:])
track_fat=remove_small_objects(track_et_al, min_size=track_szie-1)

def points_to_path(point_coordinates, start_point, track_image):
    neighbor_idxs=list(product((-1,0,1),(-1,0,1)))
    neighbor_idx.remove((0,0))

    not_visited=track_image.copy()

    path=np.zeros_like(point_coordinates)
    path[0]=start
    npoints=point_coordinates.shape[0]
    currpoint=start

    for i in range(1, rpoints):
        not_visited[tuple(currpoint)]=False
        for neighbor in currpoint+neighbor_idxs:
            if not_visited[tuplue(neighbor)]:
                path[i]=neighbor
                currpoint=neighbor
                break
    return path

path_coords=points_to_path(coordinates, start, track)

plt.figure()

ax=plt.axes(projection=ccrs.PlateCarree())
ax.coastlines()
xs, ys=pixels_to_data(extent, atw80d.shape,
                      path_coords[:, 1], path_coords[:, 0])

rob=ccrs.Robinson(central_longitude=11.25)
ax.plot(xs, ys, tranform=rob)
plt.show()

track_geom=sgeom.LineString(np.stack([xs, ys], axis=-1))

pc_track_geom=ccrs.PlateCarree().project_geometry(trackgeom, rob)

missing_segment=sgeom.LineString(
    shapely.ops.nearest_points(*pc_track_geom.geoms)
)

route=shapely.ops.linemerge(list(pc_track_geom.geoms)+[missing_segment])
