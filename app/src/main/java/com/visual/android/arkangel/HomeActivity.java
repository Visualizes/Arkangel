package com.visual.android.arkangel;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by RamiK on 1/20/2018.
 */

public class HomeActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private List<Path> paths;
    private LocationManager locationManager;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        System.out.println("ON CREATE ACTIVITY HOME");

        if (checkLocationPermission()) {
//            locationManager = (LocationManager)
//                    getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            android.location.Location l = locationManager.getLastKnownLocation(locationManager
//                    .getBestProvider(criteria, false));
//            System.out.println("naaa");
//            System.out.println("test: " + l.getLatitude());
//            System.out.println("bad");
            // Construct a GeoDataClient.
            mGeoDataClient = Places.getGeoDataClient(this, null);

            // Construct a PlaceDetectionClient.
            mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

            // Construct a FusedLocationProviderClient.
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        } else {
            System.out.println("BRUH IS THIS NULL?");
            locationManager = null;
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(HomeActivity.this, ChoiceActivity.class));
            }
        });

        Button mSignOutButton = findViewById(R.id.sign_out);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(HomeActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                            }
                        });
            }
        });

        paths = new ArrayList<>();
//        paths.add(new Path(new Location("1", "2", "Test", 4, 2),
//                new Location("1", "2", "Test2", 4, 5)));
        final ListView listHome = findViewById(R.id.list_home);
        final HomeAdapter homeAdapter = new HomeAdapter(this, paths);
        listHome.setAdapter(homeAdapter);

        homeAdapter.notifyDataSetChanged();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mUserWalkerReference = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid()).child("walker-paths");

        DatabaseReference mPathsReference = FirebaseDatabase.getInstance().getReference("paths");
        ValueEventListener singleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                paths.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    paths.add(new Path(child.getKey(), child.child("home").getValue(Location.class),
                            (child.child("destination").getValue(Location.class))));

                }
                Utility.paths = paths;
                homeAdapter.notifyDataSetChanged();


                FusedLocationProviderClient mFusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(HomeActivity.this);

                if (Utility.recursiveLocationTracker == null && paths.size() > 0) {
                    Utility.recursiveLocationTracker = new RecursiveLocationTracker(mFusedLocationProviderClient, HomeActivity.this);
                    Utility.recursiveLocationTracker.execute();
                } else {
                    System.out.println();
                    System.out.println("TASK NOT STARTED");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        DatabaseReference mUserAngelReference = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid()).child("angel-paths");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
                outerLoop:
                for (DataSnapshot path : dataSnapshot.getChildren()) {
                    System.out.println("=====================================================");
                    String message = null;
                    System.out.println(path);
                    for (DataSnapshot data : path.getChildren()) {
                        System.out.println(data.getValue());
                        if (message == null) {
                            message = data.getValue().toString();
                            continue;
                        }
                        if (data.getValue().toString().toLowerCase().equals("false")) {
                            continue outerLoop;
                        }
                        openNotification(message, path.getKey().toString(),
                                data.getValue().toString().toLowerCase().equals("error"));
                    }
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
        mUserAngelReference.addValueEventListener(postListener);

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Title")
                        .setMessage("essage")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{
                                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(1);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        System.exit(1);
                    }
                })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    System.exit(1);
                }
                return;
            }
        }
    }

    private void openNotification(String message, String key, boolean isSomethingWrong) {
        System.out.println("OPEN NOTIFICATION");
        System.out.println(message);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Arkangel")
                        .setContentText(message);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUserAngelReference = FirebaseDatabase.getInstance()
                .getReference("users").child(user.getUid()).child("angel-paths")
                .child(key);

        mUserAngelReference.child("notify").setValue(false);
        mUserAngelReference.child("message").setValue("");

    }
}
