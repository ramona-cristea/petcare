package com.ramona.petcare.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.ramona.petcare.api.PlacesRepository;
import com.ramona.petcare.api.PlacesRepositoryImpl;

public class PlacesResponseViewModel extends ViewModel {
    private final MediatorLiveData<PlacesApiResponse> mPlacesWrapper;
    private final MediatorLiveData<PlaceDetailsResponse> mPlaceDetailsWrapper;

    private final PlacesRepository mPlacesRepository;

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
