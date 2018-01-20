package com.visual.android.arkangel;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by RamiK on 1/20/2018.
 */

public class WalkerActivity extends AppCompatActivity {

    final int HOME_AUTOCOMPLETE_REQUEST_CODE = 1;
    final int DEST_AUTOCOMPLETE_REQUEST_CODE = 2;
    private EditText mHomeEdit;
    private EditText mDestEdit;
    private String uniqueID;
    private Location home = null;
    private Location destination = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walker);

        Button mCancelButton = findViewById(R.id.cancel_button);
        Button mSaveButton = findViewById(R.id.save_button);

        mHomeEdit = findViewById(R.id.home_edit);
        mHomeEdit.setInputType(InputType.TYPE_NULL);

        mDestEdit = findViewById(R.id.destination_edit);
        mDestEdit.setInputType(InputType.TYPE_NULL);

        uniqueID = UUID.randomUUID().toString().split("-")[0];
        TextView mCode = findViewById(R.id.code);
        mCode.setText(uniqueID);

        mHomeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceAutoComplete(HOME_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        mHomeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    openPlaceAutoComplete(HOME_AUTOCOMPLETE_REQUEST_CODE);
                }
            }
        });

        mDestEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceAutoComplete(DEST_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        mDestEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    openPlaceAutoComplete(DEST_AUTOCOMPLETE_REQUEST_CODE);
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalkerActivity.this, HomeActivity.class));
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (home != null && destination != null) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    List<String> ids = new ArrayList<>();
//                    ids.add(uniqueID);
//                    DatabaseReference myRef = database.getReference("paths")
//                            .child(uniqueID);
//                    myRef.child("home").setValue(home);
//                    myRef.child("destination").setValue(destination);
//                    myRef.child("walker").setValue(user.getUid());
//                    myRef = database.getReference("users").child(user.getUid()).child("paths").child(uniqueID);
//                    myRef.setValue("walker");

                    DatabaseReference myRef = database.getReference("users")
                            .child(user.getUid()).child("walker-paths").child(uniqueID);
                    myRef.child("home").setValue(home);
                    myRef.child("destination").setValue(destination);

                    myRef = database.getReference("paths").child(uniqueID);
                    myRef.setValue(0);

                    startActivity(new Intent(WalkerActivity.this, HomeActivity.class));
                }

            }
        });
    }

    private void openPlaceAutoComplete(int requestCode) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(WalkerActivity.this);
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            System.out.println(place);
            System.out.println("Place: " + place.getName());
            switch (requestCode) {
                case HOME_AUTOCOMPLETE_REQUEST_CODE:
                    mHomeEdit.setText(place.getName());
                    home = new Location(
                            place.getId(),
                            place.getName().toString(),
                            place.getAddress().toString(),
                            place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    break;
                case DEST_AUTOCOMPLETE_REQUEST_CODE:
                    mDestEdit.setText(place.getName());
                    destination = new Location(
                            place.getId(),
                            place.getName().toString(),
                            place.getAddress().toString(),
                            place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    break;
            }

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.
            System.out.println(status.getStatusMessage());

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }
}
