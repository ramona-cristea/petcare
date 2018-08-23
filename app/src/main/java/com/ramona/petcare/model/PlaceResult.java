package com.ramona.petcare.model;

import com.google.gson.annotations.SerializedName;

public class PlaceResult {

    @SerializedName("geometry")
    public Geometry geometry;

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("place_id")
    public String placeId;

    public PlaceDetails placeDetails;
}
