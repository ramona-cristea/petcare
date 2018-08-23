package com.ramona.petcare.model;

import com.google.gson.annotations.SerializedName;

public class PlaceLocation {

    @SerializedName("lat")
    public double lat;

    @SerializedName("lng")
    public double lng;
}
