package com.example.lab1_sabinregmi_c0856358_android;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

public class Location implements Serializable {

    private LatLng latLng;

    public Location(LatLng latLng) {
        this.latLng = latLng;

    }
    public LatLng getLatLng() {
        return latLng;
    }
}