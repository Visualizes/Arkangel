package com.visual.android.arkangel;

import com.google.android.gms.location.places.Place;

/**
 * Created by RamiK on 1/20/2018.
 */

public class Path {
    private Place home;
    private Place destination;

    public Path(Place home, Place destination) {
        this.home = home;
        this.destination = destination;
    }


}
