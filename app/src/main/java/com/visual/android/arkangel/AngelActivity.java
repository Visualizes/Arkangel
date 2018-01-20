package com.visual.android.arkangel;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by RamiK on 1/20/2018.
 */

public class AngelActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angel);

        final EditText mCode = findViewById(R.id.code);
        Button mOkayButton = findViewById(R.id.okay_button);
        Button mCancelButton = findViewById(R.id.cancel_button);

        mOkayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mCode.getText().toString().equals("")) {
                    String code = mCode.getText().toString();
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    final DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference("users")
                            .child(user.getUid()).child("angel-paths").child(code);
                    final DatabaseReference mPathsReference = FirebaseDatabase.getInstance().getReference("paths")
                            .child(code);
                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                mPathsReference.child(user.getUid()).setValue(0);
                                mUserReference.child("notifyDestination").setValue(false);
                                mUserReference.child("notifyHome").setValue(false);
                                startActivity(new Intent(AngelActivity.this, HomeActivity.class));
                            } else {
                                System.out.println("DOES NOT EXIST");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    };
                    mPathsReference.addListenerForSingleValueEvent(postListener);
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AngelActivity.this, HomeActivity.class));
            }
        });
    }
}
