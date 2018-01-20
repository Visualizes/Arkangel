package com.visual.android.arkangel;

import com.google.android.gms.location.places.Place;

/**
 * Created by RamiK on 1/20/2018.
 */

// Duplicate of Google's Place object, except with an empty constructor so it works with FireBase

public class Location {

    private String id;
    private String name;
    private String address;
    private double lat;
    private double lng;

    public Location() {}

    public Location(String id, String name, String address, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
