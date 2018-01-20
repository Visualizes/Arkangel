package com.visual.android.arkangel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.location.places.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RamiK on 1/20/2018.
 */

public class HomeActivity extends AppCompatActivity {

    private List<Path> paths;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        System.out.println("ON CREATE ACTIVITY HOME");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(HomeActivity.this, ChoiceActivity.class));
            }
        });

        paths = new ArrayList<>();
//        paths.add(new Path(new Location("1", "2", "Test", 4, 2),
//                new Location("1", "2", "Test2", 4, 5)));
        final ListView listHome = findViewById(R.id.list_home);
        final HomeAdapter homeAdapter = new HomeAdapter(this, paths);
        listHome.setAdapter(homeAdapter);

        homeAdapter.notifyDataSetChanged();

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                paths.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    System.out.println(child);
                    System.out.println(child.child("home"));
                    paths.add(new Path(child.child("home").getValue(Location.class),
                            (child.child("destination").getValue(Location.class))));
                }
                homeAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mPostReference.addValueEventListener(postListener);

    }
}
