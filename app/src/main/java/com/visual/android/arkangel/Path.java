package com.visual.android.arkangel;

/**
 * Created by RamiK on 1/20/2018.
 */

public class Path {

    private String id;
    private Location home;
    private Location destination;
    private boolean madeItToDestination = false;
    private boolean madeItHome = false;
    private String[] angelIDs;
    private String walkerID;

    public Path(String id, Location home, Location destination) {
        this.id = id;
        this.home = home;
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public Location getHome() {
        return home;
    }

    public Location getDestination() {
        return destination;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }
}
