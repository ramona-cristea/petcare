package com.ramona.petcare.api;

import android.arch.lifecycle.LiveData;

import com.ramona.petcare.model.PlaceDetailsResponse;
import com.ramona.petcare.model.PlacesApiResponse;

public interface PlacesRepository {

    LiveData<PlacesApiResponse> getPlaces(String location);

    LiveData<PlaceDetailsResponse> getPlaceDetails(String placeId);
}
