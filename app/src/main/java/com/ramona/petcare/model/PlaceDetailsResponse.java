package com.ramona.petcare.model;

import com.google.gson.annotations.SerializedName;

public class PlaceDetailsResponse {

    @SerializedName("result")
    private PlaceDetails result = null;

    @SerializedName("status")
    public String status;

    public PlaceDetails getResult() {
        return result;
    }
}
