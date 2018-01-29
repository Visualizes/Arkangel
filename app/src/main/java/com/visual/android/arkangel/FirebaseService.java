package com.visual.android.arkangel;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by RamiK on 1/21/2018.
 */

public class FirebaseService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("STARTED");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mUserAngelReference = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid()).child("angel-paths");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("DETECTED CHANGE");
                System.out.println(dataSnapshot.getValue());
                for (DataSnapshot path : dataSnapshot.getChildren()) {
                    System.out.println(path);
                    for (DataSnapshot data : path.getChildren()) {
                        System.out.println(data.getValue());
                        String message = data.getValue().toString();
                        if (!message.equals("")) {
                            openNotification(message, path.getKey().toString(),
                                    data.getValue().toString().toLowerCase().equals("error"));
                        }
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

        mUserAngelReference.addValueEventListener(postListener);

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void openNotification(String message, String key, boolean isSomethingWrong) {
        System.out.println("OPEN NOTIFICATION");
        System.out.println(message);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
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

//        mUserAngelReference.child("notify").setValue(false);
        mUserAngelReference.child("message").setValue("");

    }
}