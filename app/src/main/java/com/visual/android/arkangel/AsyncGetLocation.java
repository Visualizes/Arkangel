package com.visual.android.arkangel;

import android.location.*;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

/**
 * Created by RamiK on 1/20/2018.
 */

public abstract class AsyncGetLocation extends AsyncTask<FusedLocationProviderClient, Void, android.location.Location> {

    @Override
    protected android.location.Location doInBackground(FusedLocationProviderClient... fusedLocationProviderClients) {

        if (!Utility.firstRecursiveIteration) {
            try {// 300000
                Thread.sleep(10000); // 5 minutes
            } catch (InterruptedException e) {
                // TODO: Auto-generated stub
                e.printStackTrace();
            }
        }

        return null;
    }


}
