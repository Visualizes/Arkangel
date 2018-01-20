package com.visual.android.arkangel;

import android.location.LocationManager;

/**
 * Created by RamiK on 1/20/2018.
 */

public class RecursiveLocationTracker extends AsyncGetLocation {

    private LocationManager locationManager;

    public RecursiveLocationTracker(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    @Override
    protected void onPostExecute(android.location.Location currentLocation) {
        super.onPostExecute(currentLocation);

        double currentLat = currentLocation.getLatitude();
        double currentLng = currentLocation.getLongitude();
        double currentAccuracy = currentLocation.getAccuracy();


        for (Path path : Utility.paths) {
            Location dest = path.getDestination();
            double destLat = dest.getLat();
            double destLng = dest.getLng();

            float[] results = new float[5];



            // The computed distance is stored in results[0].
            // If results has length 2 or greater, the initial bearing is stored in results[1].
            // If results has length 3 or greater, the final bearing is stored in results[2]
            android.location.Location.distanceBetween(destLat, destLng, currentLat, currentLng, results);


            if (results[0] < 20 + currentAccuracy) {

            } else {

            }
        }

        Utility.recursiveLocationTracker = new RecursiveLocationTracker(locationManager);
        Utility.recursiveLocationTracker.execute(locationManager);

    }
}
