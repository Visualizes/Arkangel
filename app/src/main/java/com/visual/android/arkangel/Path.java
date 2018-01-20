package com.visual.android.arkangel;

/**
 * Created by RamiK on 1/20/2018.
 */

public class Path {

    private Location home;
    private Location destination;
    private boolean madeItToDestination = false;
    private boolean madeItHome = false;
    private String[] angelIDs;
    private String walkerID;

    public Path(Location home, Location destination) {
        this.home = home;
        this.destination = destination;
    }

    public Location getHome() {
        return home;
    }

    public Location getDestination() {
        return destination;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }
}
