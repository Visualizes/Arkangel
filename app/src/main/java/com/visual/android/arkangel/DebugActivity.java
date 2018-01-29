package com.visual.android.arkangel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by RamiK on 1/21/2018.
 */

public class DebugActivity extends AppCompatActivity {

    private DatabaseReference mUserReferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);


        mUserReferences = FirebaseDatabase.getInstance().getReference("users");

        Button mArriveHome = findViewById(R.id.arrive_home);
        Button mArriveDest = findViewById(R.id.arrive_dest);
        Button mLeftHome = findViewById(R.id.left_home);
        Button mLeftDest = findViewById(R.id.left_dest);
        Button mDerailment = findViewById(R.id.derailment);

        mArriveHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyAngel("Rami Khadder has arrived at Rachel Carson College.", false);
            }
        });

        mArriveDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyAngel("Rami Khadder has arrived at Oakes Cafe.", false);
            }
        });

        mLeftHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyAngel("Rami Khadder has departed from Rachel Carson College.", false);
            }
        });

        mLeftDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyAngel("Rami Khadder has departed from Oakes Cafe.", false);
            }
        });

        mDerailment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyAngel("Arkangel has detected a derailment in Rami Khadder\'s path", true);
            }
        });
    }

    private void notifyAngel(String message, boolean isSomethingWrong) {
        mUserReferences.child("BMEkBEZ1nMUKvpAJG8w6NoeqCm73")
                .child("angel-paths")
                .child("81496bf2")
                .child("message")
                .setValue(message);

        mUserReferences.child("BMEkBEZ1nMUKvpAJG8w6NoeqCm73")
                .child("angel-paths")
                .child("81496bf2")
                .child("notify")
                .setValue((isSomethingWrong ? "error" : true));
    }
}
