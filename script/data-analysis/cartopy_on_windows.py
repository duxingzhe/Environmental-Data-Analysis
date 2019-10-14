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

fig=plt.figure(figsize=(16,9))
ax=plt.axes(projection=ccrs.Orthographic(central_latitude=25, central_longitude=265))
cbax=ax.pcolormesh(lon, lat, np.ma.masked_less(h5AbsV/1e-5,10),
                   transform=ccrs.PlateCarree(),
                   cmap=cm.plasma, vmin=10, vmax=80)
ax.contour(lon, lat, h5Ht, levels=np.arange(4960, 5960, 40),
           colors='k', transform=ccrs.PlateCarree(),
           linewidths=1.25)
ax.add_feature(feature.NaturalEarthFeature(
    category='cultural',
    name='admin_1_states_provinces_lines',
    scale='50m',
    facecolor='none'
))
ax.add_feature(feature.NaturalEarthFeature(
    category='physical',
    name='lakes',
    scale='50m',
    facecolor='none'
))
ax.coastlines('50m')
ax.add_feature(feature.BORDERS)
ax.gridlines()
ax.set_global()
plt.title('500 mb Height [m] and 500 mb Absolute Vorticity [s$^{-1}$]')
plt.suptitle('RUC Analysis 120414 2100Z')
cb=plt.colorbar(cbax, ax=ax, label='$\zeta_{ABS}$ [s$^{-1}$]')
cb.set_ticks(np.arange(10, 90, 10))
plt.show()