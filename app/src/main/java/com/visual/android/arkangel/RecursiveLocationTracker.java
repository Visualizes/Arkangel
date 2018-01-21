package com.visual.android.arkangel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
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
    private static boolean userIsOnWayHome = false;
    private static boolean userIsAtDest = false;
    private static boolean userIsOnWayDest = false;
    private static double bearing;
    private static int strikes = 0;
    private static boolean strikesFlagged = false;
    private SharedPreferences sharedPref;

    public RecursiveLocationTracker(FusedLocationProviderClient mFusedLocationProviderClient,
                                    Activity context) {
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
        this.context = new WeakReference<>(context);
        sharedPref = context.getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    protected void onPostExecute(final android.location.Location currentLocation) {
        super.onPostExecute(currentLocation);

        System.out.println("ON POST EXECUTE");

        final String activeID = sharedPref.getString("active-path", null);
        System.out.println("id: " + activeID);
        if (activeID == null) {
            Utility.firstRecursiveIteration = false;
            recursivelyExecuteScript();
            return;
        }

        try {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(context.get(), new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(@NonNull android.location.Location location) {
                            if (location != null) {
                                DatabaseReference mDebugReference =
                                        FirebaseDatabase.getInstance().getReference("debug");


                                double currentLat = location.getLatitude();
                                double currentLng = location.getLongitude();
                                double currentAccuracy = location.getAccuracy();

                                for (final Path path : Utility.paths) {
                                    if (!path.getId().equals(activeID)) {
                                        continue;
                                    }

                                    Location dest = path.getDestination();
                                    System.out.println(dest.getName());
                                    double destLat = dest.getLat();
                                    double destLng = dest.getLng();

                                    System.out.println("Dest Lat: " + destLat);
                                    System.out.println("Dest Lng: " + destLng);

                                    final float[] destResults = new float[5];

                                    Location home = path.getHome();
                                    double homeLat = home.getLat();
                                    double homeLng = home.getLng();

                                    System.out.println("Home Lat: " + homeLat);
                                    System.out.println("Home Lng: " + homeLng);

                                    final float[] homeResults = new float[5];

                                    // The computed distance is stored in results[0].
                                    // If results has length 2 or greater, the initial bearing is stored in results[1].
                                    // If results has length 3 or greater, the final bearing is stored in results[2]
                                    android.location.Location.distanceBetween(destLat, destLng, currentLat, currentLng, destResults);
                                    android.location.Location.distanceBetween(homeLat, homeLng, currentLat, currentLng, homeResults);

//                                    ValueEventListener postListener = new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                                System.out.println(data.getKey());
//                                                System.out.println(data.getValue());
//                                                if (data.getKey().equals("0")) {
//                                                    homeResults[0] = Float.parseFloat(data.getValue().toString());
//                                                } else {
//                                                    destResults[0] = Float.parseFloat(data.getValue().toString());
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//                                            // Getting Post failed, log a message
//                                            Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
//                                            // ...
//                                        }
//                                    };
//
//                                    mDebugReference.child("distances").addListenerForSingleValueEvent(postListener);

                                    double radius = 100 + currentAccuracy;
//                                    System.out.println("Radius: " + radius);
//                                    System.out.println("Home: " + homeResults[0]);
//                                    System.out.println("Dest: " + destResults[0]);


                                    if (destResults[0] < radius && !RecursiveLocationTracker.userIsAtDest) {
                                        // if user enters dest radius
                                        System.out.println("User entered DEST");
                                        RecursiveLocationTracker.userIsOnWayDest = false; // they're not on the way to dest since they're already there
                                        RecursiveLocationTracker.userIsAtDest = true; // they're at dest
                                        RecursiveLocationTracker.activeLocation = path.getDestination();
                                        RecursiveLocationTracker.activePath = path;
                                        notifyAngel(path, "arrived at ", false);
                                    } else if (destResults[0] > radius && RecursiveLocationTracker.userIsAtDest) {
                                        // if user leaves dest radius
                                        System.out.println("User left DEST");
                                        RecursiveLocationTracker.userIsAtDest = false; // they're no longer at dest
                                        RecursiveLocationTracker.userIsOnWayHome = true; // they're on the way home
                                        RecursiveLocationTracker.activeLocation = path.getDestination();
                                        RecursiveLocationTracker.activePath = path;
                                        notifyAngel(path, "departed ", false);
                                    } else if (homeResults[0] < radius && !RecursiveLocationTracker.userIsAtHome) {
                                        // if user enters home radius
                                        System.out.println("User entered HOME");
                                        RecursiveLocationTracker.userIsOnWayHome = false; // they're not on the way home since they're already there
                                        RecursiveLocationTracker.userIsAtHome = true; // they're home
                                        RecursiveLocationTracker.activeLocation = path.getHome();
                                        RecursiveLocationTracker.activePath = path;
                                        notifyAngel(path, "arrived at ", false);

                                    } else if (homeResults[0] > radius && RecursiveLocationTracker.userIsAtHome) {
                                        // if user leaves home
                                        System.out.println("User left HOME");
                                        RecursiveLocationTracker.userIsAtHome = false; // they're no longer home
                                        RecursiveLocationTracker.userIsOnWayDest = true; // they're on the way to dest
                                        RecursiveLocationTracker.activeLocation = path.getHome();
                                        RecursiveLocationTracker.activePath = path;
                                        notifyAngel(path, "departed ", false);
                                    } else {
                                        System.out.println("NOT IN");
                                    }

                                    final Toast mToastToShow = Toast.makeText(context.get(), "Radius: " + radius +
                                                    "\nDistance to Home: " + homeResults[0] +
                                                    "\nDistance to Dest: " + destResults[0] +
                                                    "\nCurrent Lat: " + currentLat +
                                                    "\nCurrent Lng: " + currentLng +
                                                    "\nIs user at dest? " + RecursiveLocationTracker.userIsAtDest +
                                                    "\nIs user at home? " + RecursiveLocationTracker.userIsAtHome +
                                                    "\nIs user on the way home? " + RecursiveLocationTracker.userIsOnWayHome +
                                                    "\nIs user on the way dest? " + RecursiveLocationTracker.userIsOnWayDest +
                                                    "\nStrikes: " + RecursiveLocationTracker.strikes,
                                            Toast.LENGTH_LONG);

                                    // Set the countdown to display the toast
                                    CountDownTimer toastCountDown;
                                    toastCountDown = new CountDownTimer(8000, 1000 /*Tick duration*/) {
                                        public void onTick(long millisUntilFinished) {
                                            mToastToShow.show();
                                        }
                                        public void onFinish() {
                                            mToastToShow.cancel();
                                        }
                                    };

                                    // Show the toast and starts the countdown
                                    mToastToShow.show();
                                    toastCountDown.start();

                                }

//                                if (RecursiveLocationTracker.activePath != null) {
//                                    Location home = RecursiveLocationTracker.activePath.getHome();
//                                    Location dest = RecursiveLocationTracker.activePath.getDestination();
//
//                                    if (userIsOnWayDest && !strikesFlagged) {
//                                        double bearing = bearing(home.getLat(), home.getLng(), dest.getLat(), dest.getLng());
//                                        double userBearing = bearing(currentLat, currentLng, dest.getLat(), dest.getLng());
//                                        calculateStrikes(bearing, userBearing);
//                                    }
//
//                                    if (userIsOnWayHome && !strikesFlagged) {
//                                        double bearing = bearing(dest.getLat(), dest.getLng(), home.getLat(), home.getLng());
//                                        double userBearing = bearing(currentLat, currentLng, home.getLat(), home.getLng());
//                                        calculateStrikes(bearing, userBearing);
//                                    }
//                                }

                                Utility.firstRecursiveIteration = false;
                                recursivelyExecuteScript();

                            } else {
                                System.out.println("NULL");
                                recursivelyExecuteScript();
                                // current location is null
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    private void recursivelyExecuteScript() {
        Utility.recursiveLocationTracker = new RecursiveLocationTracker(mFusedLocationProviderClient, context.get());
        Utility.recursiveLocationTracker.execute();

    }

    private void calculateStrikes(double bearing, double userBearing) {
        double bearingCap = bearing + 90;
        double bearingMin = bearing - 90;
        if (bearingCap > 0) {
            bearingCap -= 360;
        }
        if (bearingMin < 0) {
            bearingMin += 360;
        }
        if (userBearing > bearingCap || userBearing < bearingMin) {
            RecursiveLocationTracker.strikes++;
            if (RecursiveLocationTracker.strikes == 3) {
                RecursiveLocationTracker.strikesFlagged = true;
                notifyAngel(activePath, null, true);
            }
        } else {
            RecursiveLocationTracker.strikes--;
            if (RecursiveLocationTracker.strikes < 0) {
                RecursiveLocationTracker.strikes = 0;
            }
        }
    }

    private double bearing(double lat1, double lon1, double lat2, double lon2) {
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(lon2 - lon1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    private void notifyAngel(final Path path, final String arriveDepart, final boolean isSomethingWrong) {
        if (Utility.firstRecursiveIteration) {
//            return;
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mUserWalkerReference = FirebaseDatabase.getInstance().getReference("paths")
                .child(path.getId());
        final DatabaseReference mUserReferences = FirebaseDatabase.getInstance().getReference("users");
        ValueEventListener singleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    String message;
                    if (!isSomethingWrong) {
                        message = user.getDisplayName() + " has " + arriveDepart +
                                RecursiveLocationTracker.activeLocation.getName();
                    } else {
                        message = "Arkangel has detected an abnormally in the direction " + user.getDisplayName() +
                                "is taking.";
                    }

                    mUserReferences.child(key)
                            .child("angel-paths")
                            .child(path.getId())
                            .child("message")
                            .setValue(message);

                    mUserReferences.child(key)
                            .child("angel-paths")
                            .child(path.getId())
                            .child("notify")
                            .setValue((isSomethingWrong ? "error" : true));
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
    }


}
