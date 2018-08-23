package com.ramona.petcare.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesApiResponse {

    @SerializedName("results")
    private List<PlaceResult> results = null;

    @SerializedName("status")
    public String status;

    public List<PlaceResult> getResults() {
        return results;
    }
}
