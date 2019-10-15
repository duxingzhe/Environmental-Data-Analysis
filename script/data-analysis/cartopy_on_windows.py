import cartopy.crs as ccrs
from cartopy import feature
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import cm
import fiona
from shapely.geometry import shape, MultiPolygon
import matplotlib.patches as mpatches
from metpy.calc import get_wind_components
from metpy.plots import StationPlot

MSTOKTS=1.94384

TPROBCOLORS=['purple', 'magenta', 'red', 'yellow', 'brown', 'green']
TPRLABELS=['2%', '5%', '10%', '15%', '30%', '45%']
EXTENT=[-105, -90, 30, 45]
STN_EXTENT=[-105, -90, 30, 45]

def KTOF(T):
    return T*(9/5.)-459.67

ruc=np.load('ruc2anl_130_20120414_2100_000.npz')
stn_data=np.load('20120414_mesowest_stn.npz')
sat=np.load('20120414_g13_vis.npz')

lccProjParams = { 'central_latitude'   : 25.0, # same as lat_0 in proj4 string
                  'central_longitude'  : 265.0, # same as lon_0
                  'standard_parallels' : (25.0, 25.0) # same as (lat_1, lat_2)
}

sfcT=ruc['TSFC']
sfcP=ruc['PSFC']
sfcTf=KTOF(sfcT)
sfcTd=ruc['TDSFC']
sfcTdf=KTOF(sfcTd)
MSLP=ruc['MSLP']
sfcU=ruc['USFC']
sfcV=ruc['VSFC']

h5T=ruc['T500']
h5Ht=ruc['HGHT500']
h5U=ruc['U500']
h5V=ruc['V500']
h5AbsV=ruc['ABSV500']

lat=ruc['LAT']
lon=ruc['LON']

stn_lon=stn_data['longitude']
stn_lat=stn_data['latitude']
stn_u=stn_data['eastward_wind']
stn_v=stn_data['northward_wind']
stn_Td=stn_data['dew_point']
stn_T=stn_data['air_temperature']
stn_slp=stn_data['slp']

sat_gvar=sat['gvar10']
sat_lat=sat['lat']
sat_lon=sat['lon']

proj=ccrs.LambertConformal(**lccProjParams)

shp=fiona.open('day1otlk_20120414_1630_torn.shp', 'r')
shpProj = ccrs.LambertConformal(central_latitude = shp.crs['lat_0'],
                                central_longitude = shp.crs['lon_0'],
                                standard_parallels = (shp.crs['lat_1'], shp.crs['lat_2']))
mp=MultiPolygon([shape(polygon['geometry']) for polygon in shp])

fig = plt.figure(figsize=(20, 13))
ax = fig.add_subplot(1,1,1, projection=proj)
ax.coastlines(resolution='50m', zorder=2, color='black')
ax.add_feature(feature.NaturalEarthFeature(category='cultural',
                                           name='admin_1_states_provinces_lines',
                                           scale='50m', facecolor='none'))
ax.add_feature(feature.NaturalEarthFeature(category='physical',
                                           name='lakes',
                                           scale='50m', facecolor='none'))
ax.add_feature(feature.BORDERS, linewidth='2', edgecolor='black')

stationplot=StationPlot(ax, stn_lon, stn_lat, transform=ccrs.PlateCarree(), fontsize=12)

stationplot.plot_parameter('NW', stn_T, color='red')
stationplot.plot_parameter('SW', stn_Td, color='lightgreen')
stationplot.plot_barb(stn_u, stn_v, color='lightgray')

ax.pcolormesh(sat_lon, sat_lat, sat_gvar,
              transform=ccrs.PlateCarree(),
              cmap=cm.gray, vmin=0, vmax=1023, zorder=0)

ax.set_extent(EXTENT, ccrs.PlateCarree())
plt.title('GOES-13 VIS and Surface Stations -- 2012-04-14 2100 UTC')
plt.show()
