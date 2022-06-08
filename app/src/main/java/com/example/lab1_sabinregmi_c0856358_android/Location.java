package com.example.lab1_sabinregmi_c0856358_android;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

public class Location implements Serializable {

    private double latitude;
    private double longitude;

    public Location(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }
    public LatLng getLatLng() {
         return new LatLng(latitude, longitude);
    }
}