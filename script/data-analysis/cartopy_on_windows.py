import numpy as np
import cartopy.geodesic

geod = cartopy.geodesic.Geodesic()

places = {'London': {'lon': -0.1278, 'lat': 51.5074},
          'Suez': {'lon': 32.5498, 'lat': 29.9668},
          'Bombay': {'lon': 72.8777, 'lat': 19.0760},
          'Calcutta': {'lon': 88.3639, 'lat': 22.5726},
          'Hong Kong': {'lon': 114.1095, 'lat': 22.3964},
          'Yokohama': {'lon': 139.6380, 'lat': 35.4437},
          'San Fransisco': {'lon': -122.4194, 'lat': 37.7749},
          'New York City': {'lon': -74.0060, 'lat': 40.7128},
         }

destinations=['London', 'Suez', 'Bombay', 'Calcutta', 'Hong Kong', 'Yokohama',
              'San Fransisco', 'New York City', 'London']

waypoints=[(places[place]['lon'], places[place]['lat']) for place in destinations]

# Solve the "inverse" Geodetic problem to compute the distance
# between two points. This solution is more accurate than
# the traditional Vincenty formulation.
distances, azi_0, azi_1 = np.array(geod.inverse(waypoints[:-1], waypoints[1:]).T)

print("Approximate distance of Fogg's proposed route: {:.0f} km".format(distances.sum()/1000))

print('Average speed required over 80 days:{:.1f} km/h '
      .format(distances.sum() / 1000/(80 * 24)))
