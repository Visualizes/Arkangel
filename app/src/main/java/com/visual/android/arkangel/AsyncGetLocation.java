package com.visual.android.arkangel;

import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by RamiK on 1/20/2018.
 */

public abstract class AsyncGetLocation extends AsyncTask<LocationManager, Void, android.location.Location> {

    @Override
    protected android.location.Location doInBackground(LocationManager... locationManagers) {

        Criteria criteria = new Criteria();
        android.location.Location location = null;
        try {
            location = locationManagers[0].getLastKnownLocation(locationManagers[0]
                    .getBestProvider(criteria, false));
        } catch (SecurityException e) {
            e.printStackTrace();
        }


        if (!Utility.firstRecursiveIteration) {
            try {
                Thread.sleep(300000); // 5 minutes
            } catch (InterruptedException e) {
                // TODO: Auto-generated stub
                e.printStackTrace();
            }
        }

        Utility.firstRecursiveIteration = false;

        return location;
    }

}
