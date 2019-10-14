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

fig = plt.figure(figsize=(16, 9))
ax = plt.axes(projection = proj)
ax.add_feature(feature.ShapelyFeature(mp, shpProj), facecolor = TPROBCOLORS,
               edgecolor = TPROBCOLORS)
ax.add_feature(feature.NaturalEarthFeature(
        category='cultural',
        name='admin_1_states_provinces_lines',
        scale='50m',
        facecolor='none'))
ax.add_feature(feature.NaturalEarthFeature(
        category='physical',
        name='lakes',
        scale='50m',
        facecolor='none'))
ax.coastlines('50m')
ax.add_feature(feature.BORDERS)
ax.set_extent(EXTENT, ccrs.PlateCarree())
ax.set_title('120414 1630Z Day 1 Tornado Probabilities')

# rectangular colorfill patches for legend
tor2 = mpatches.Rectangle((0,0), 1, 1, ec = 'none', fc = TPROBCOLORS[5], lw=2)
tor5 = mpatches.Rectangle((0,0), 1, 1, ec = 'none', fc = TPROBCOLORS[4], lw=2)
tor10 = mpatches.Rectangle((0,0), 1, 1, ec = 'none', fc = TPROBCOLORS[3], lw=2)
tor15 = mpatches.Rectangle((0,0), 1, 1, ec = 'none', fc = TPROBCOLORS[2], lw=2)
tor30 = mpatches.Rectangle((0,0), 1, 1, ec = 'none', fc = TPROBCOLORS[1], lw=2)
tor45 = mpatches.Rectangle((0,0), 1, 1, ec = 'none', fc = TPROBCOLORS[0], lw=2)

rects = [tor2, tor5, tor10, tor15, tor30, tor45]

leg = ax.legend(rects, TPRLABELS, loc = 3)

plt.show()