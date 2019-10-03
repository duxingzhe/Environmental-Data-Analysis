import cartopy.crs as ccrs
import shapely.geometry as sgeom
import matplotlib.pyplot as plt

osgb=ccrs.OSGB()
geod=ccrs.Geodetic()

easting=291813.424
northing=92098.387

lon, lat=geod.transform_point(
    x=easting, y=northing, src_crs=osgb
)

print(lon, lat)

new_york=[-74.0060, 40.7128]
honolulu=[-157.8583, 21.3069]

line=sgeom.LineString([new_york, honolulu])
pc=ccrs.PlateCarree()

lines = pc.project_geometry(line, geod)

plt.figure()
ax = plt.axes(projection=pc)
ax.add_geometries(
    [lines], pc,
    edgecolor='blue', facecolor='none', lw=2)
ax.coastlines()
plt.show()