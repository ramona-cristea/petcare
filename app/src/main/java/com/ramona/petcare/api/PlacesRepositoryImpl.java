package com.ramona.petcare.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ramona.petcare.BuildConfig;
import com.ramona.petcare.model.PlaceDetailsResponse;
import com.ramona.petcare.model.PlacesApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesRepositoryImpl implements PlacesRepository {

    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static final String KEY = BuildConfig.ApiKey;
    private static final String TYPE = "veterinary_care";
    private static final String FIELDS = "name,rating,formatted_address,opening_hours,formatted_phone_number";
    private static final int RADIUS = 6500;

    private PlacesService mPlacesService;

    public PlacesRepositoryImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mPlacesService = retrofit.create(PlacesService.class);
    }


    @Override
    public LiveData<PlacesApiResponse> getPlaces(String location) {
        final MutableLiveData<PlacesApiResponse> placesLiveData = new MutableLiveData<>();
        Call<PlacesApiResponse> request = mPlacesService.getPlaces(location, RADIUS, TYPE, KEY);
        request.enqueue(new Callback<PlacesApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacesApiResponse> call, @NonNull Response<PlacesApiResponse> response) {
                placesLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<PlacesApiResponse> call, @NonNull Throwable error) {
                placesLiveData.setValue(null);
            }
        });

        return placesLiveData;
    }

    @Override
    public LiveData<PlaceDetailsResponse> getPlaceDetails(String placeId) {
        final MutableLiveData<PlaceDetailsResponse> placeDetailsLiveData = new MutableLiveData<>();
        Call<PlaceDetailsResponse> request = mPlacesService.getPlaceDetails(placeId, FIELDS, KEY);
        request.enqueue(new Callback<PlaceDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetailsResponse> call, @NonNull Response<PlaceDetailsResponse> response) {
                placeDetailsLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetailsResponse> call, @NonNull Throwable error) {
                placeDetailsLiveData.setValue(null);
            }
        });

        return placeDetailsLiveData;
    }
}
