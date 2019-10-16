import cartopy.crs as ccrs
from cartopy.examples.waves import sample_data
import matplotlib.pyplot as plt

lons, lats, data= sample_data()

plt.figure()
ax=plt.axes(projection=ccrs.PlateCarree())
cs=plt.contourf(
    lons, lats, data, 5, transform=ccrs.PlateCarree()
)

ax.coastlines()
plt.colorbar(orientation='horizontal')
plt.show()
