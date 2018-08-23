package com.ramona.petcare.model;

import com.google.gson.annotations.SerializedName;

public class PlaceDetails {

    @SerializedName("formatted_phone_number")
    public String formattedPhoneNumber;

    @SerializedName("name")
    public String name;

    @SerializedName("formatted_address")
    public String address;

    @SerializedName("opening_hours")
    public OpeningHours openingHours;

    @SerializedName("rating")
    public double rating;
}
