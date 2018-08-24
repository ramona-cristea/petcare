package com.ramona.petcare.api;

import com.ramona.petcare.model.PlaceDetailsResponse;
import com.ramona.petcare.model.PlacesApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface PlacesService {
    @GET("maps/api/place/nearbysearch/json?")
    Call<PlacesApiResponse> getPlaces(@Query("location") String location, @Query("radius") int radius, @Query("type") String type, @Query("key") String apiKey);

    @GET("maps/api/place/details/json?")
    Call<PlaceDetailsResponse> getPlaceDetails(@Query("placeid") String placeId, @Query("fields") String fields, @Query("key") String apiKey);
}
