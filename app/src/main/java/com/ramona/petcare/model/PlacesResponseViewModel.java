package com.ramona.petcare.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.ramona.petcare.api.PlacesRepository;
import com.ramona.petcare.api.PlacesRepositoryImpl;

public class PlacesResponseViewModel extends ViewModel{
    private MediatorLiveData<PlacesApiResponse> mPlacesWrapper;
    private MediatorLiveData<PlaceDetailsResponse> mPlaceDetailsWrapper;

    private PlacesRepository mPlacesRepository;

    public PlacesResponseViewModel() {
        mPlacesWrapper = new MediatorLiveData<>();
        mPlaceDetailsWrapper = new MediatorLiveData<>();
        mPlacesRepository = new PlacesRepositoryImpl();
    }

    @NonNull
    public LiveData<PlacesApiResponse> getPlacesWrapper() {
        return mPlacesWrapper;
    }

    @NonNull
    public LiveData<PlaceDetailsResponse> getPlaceDetails() {
        return mPlaceDetailsWrapper;
    }

    public void loadPlacesForLocation(@NonNull String location) {
        mPlacesWrapper.addSource(
                mPlacesRepository.getPlaces(location),
                placesWrapper -> mPlacesWrapper.setValue(placesWrapper)
        );
    }

    public void loadPlaceDetails(String placeId){
        mPlaceDetailsWrapper.addSource(
                mPlacesRepository.getPlaceDetails(placeId),
                placeDetailsWrapper -> mPlaceDetailsWrapper.setValue(placeDetailsWrapper)
        );
    }
}
