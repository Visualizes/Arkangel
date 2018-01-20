package com.visual.android.arkangel;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;

/**
 * Created by RamiK on 1/20/2018.
 */

public class RecursiveLocationTracker extends AsyncGetLocation {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private WeakReference<Activity> context;
    private static Path activePath;
    private static Location activeLocation;
    private static boolean userIsAtHome = false;
    private static boolean userIsAtDest = false;

    public RecursiveLocationTracker(FusedLocationProviderClient mFusedLocationProviderClient,
                                    Activity context) {
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
        this.context = new WeakReference<>(context);
    }

    @Override
    protected void onPostExecute(final android.location.Location currentLocation) {
        super.onPostExecute(currentLocation);

        System.out.println("ON POST EXECUTE");
        try {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(context.get(), new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(@NonNull android.location.Location location) {
                            if (location != null) {
                                double currentLat = location.getLatitude();
                                double currentLng = location.getLongitude();
                                double currentAccuracy = location.getAccuracy();

                                for (final Path path : Utility.paths) {
                                    Location dest = path.getDestination();
                                    System.out.println(dest.getName());
                                    double destLat = dest.getLat();
                                    double destLng = dest.getLng();

                                    float[] destResults = new float[5];

                                    Location home = path.getHome();
                                    double homeLat = home.getLat();
                                    double homeLng = home.getLng();

                                    float[] homeResults = new float[5];

                                    // The computed distance is stored in results[0].
                                    // If results has length 2 or greater, the initial bearing is stored in results[1].
                                    // If results has length 3 or greater, the final bearing is stored in results[2]
                                    android.location.Location.distanceBetween(destLat, destLng, currentLat, currentLng, destResults);
                                    android.location.Location.distanceBetween(homeLat, homeLng, currentLat, currentLng, homeResults);

                                    if (destResults[0] < (200 + currentAccuracy) || homeResults[0] < (200 + currentAccuracy)) {
                                        RecursiveLocationTracker.activePath = path;
                                        if (destResults[0] < (200 + currentAccuracy)) {
                                            RecursiveLocationTracker.userIsAtDest = true;
                                            RecursiveLocationTracker.activeLocation = path.getDestination();
                                        } else {
                                            RecursiveLocationTracker.userIsAtHome = true;
                                            RecursiveLocationTracker.activeLocation = path.getHome();
                                        }

                                        System.out.println("IS IN!!");
                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        DatabaseReference mUserWalkerReference = FirebaseDatabase.getInstance().getReference("paths")
                                                .child(path.getId());
                                        final DatabaseReference mUserReferences = FirebaseDatabase.getInstance().getReference("users");
                                        ValueEventListener singleListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                    String key = data.getKey();
                                                    mUserReferences.child(key)
                                                            .child("angel-paths")
                                                            .child(path.getId())
                                                            .child("notify")
                                                            .setValue(true);

                                                    mUserReferences.child(key)
                                                            .child("angel-paths")
                                                            .child(path.getId())
                                                            .child("message")
                                                            .setValue(user.getDisplayName() + " has arrived at " +
                                                            RecursiveLocationTracker.activeLocation.getName());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Getting Post failed, log a message
                                                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                                                // ...
                                            }
                                        };

                                        mUserWalkerReference.addListenerForSingleValueEvent(singleListener);

                                    } else {
                                        System.out.println("NOT IN");


                                    }
                                }

                                Utility.recursiveLocationTracker = new RecursiveLocationTracker(mFusedLocationProviderClient, context.get());
                                Utility.recursiveLocationTracker.execute();

                            } else {
                                System.out.println("NULL");
                                Utility.recursiveLocationTracker = new RecursiveLocationTracker(mFusedLocationProviderClient, context.get());
                                Utility.recursiveLocationTracker.execute();
                                // current location is null
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }




    }

    private void getDeviceLocation() {

    }


}
