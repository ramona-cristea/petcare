package com.ramona.petcare.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpeningHours {
    @SerializedName("open_now")
    public boolean openNow;

    @SerializedName("weekday_text")
    public List<String> weekdayText = null;
}
