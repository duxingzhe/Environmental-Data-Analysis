import numpy as np
import skimage.io
import matplotlib.pyplot as plt
import cartopy.crs as ccrs

# Image has shape: (y: 296, x: 640, RGBA: 4)
atw80d = skimage.io.imread('./640px-Around_the_World_in_Eighty_Days_map.png')

yellowish = ((atw80d[:, :, 0] > 200) &  # Lots of Red.
             (atw80d[:, :, 1] > 200) &  # Lots of Green.
             (atw80d[:, :, 2] < 100) &  # Not lots of Blue.
             (atw80d[:, :, 3] > 250))   # Not transparent.

ind_y, ind_x = np.where(yellowish)
print('Number of yellow-ish pixels: ', len(ind_x))
print('x indexes: ', ind_x)
print('y indexes: ', ind_y)

def pixels_to_data(extent, shape, i, j):
    """Converts from coordinates of the array to data coordinates."""
    xmin, xmax, ymin, ymax = extent
    x_range = xmax - xmin
    y_range = ymax - ymin

    pix_width = x_range / shape[1]
    pix_height = y_range / shape[0]

    # For y handle the fact that the image's pixels
    # start at the top (in mpl that is what origin='upper' means).
    j = shape[0] - j

    return (xmin + pix_width * (i + 0.5),
            ymin + pix_height * (j + 0.5))

# Use the extents computed in an earlier exercise.
extent = [-13636707, 17044670, -6308712, 8565930]

# Note: Data coords in Robinson.
xs, ys = pixels_to_data(
    extent, atw80d.shape,
    ind_x, ind_y)

print('Last 3 x coordinates (Robinson): ', xs[-3:])
print('Last 3 y coordinates (Robinson): ', ys[-3:])

plt.figure()

rob = ccrs.Robinson(central_longitude=11.25)
ax = plt.axes(projection=rob)

ax.gridlines(color='gray', linestyle='--')
ax.coastlines()
ax.imshow(atw80d, extent=extent,
          transform=rob, origin='upper')
ax.set_global()

plt.plot(xs, ys, transform=rob,
         linestyle='none', marker='o',
         markeredgecolor='k', color='yellow')
plt.show()