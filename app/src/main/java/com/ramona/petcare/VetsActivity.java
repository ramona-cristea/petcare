package com.ramona.petcare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.ramona.petcare.api.PlacesApiResponseCodes;
import com.ramona.petcare.model.PlaceDetailsResponse;
import com.ramona.petcare.model.PlaceResult;
import com.ramona.petcare.model.PlacesApiResponse;
import com.ramona.petcare.model.PlacesResponseViewModel;

import java.util.List;

public class VetsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = VetsActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;


    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleMap mMap;

    private SettingsClient mSettingsClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private PlacesResponseViewModel mPlacesViewModel;
    private ProgressBar mLoadingIndicator;
    private Marker mCurrentClickedMarker;
    private String markerPlaceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vets);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.find_a_vet);
        }

        mLoadingIndicator = findViewById(R.id.progress_bar);
        MapView mapView = findViewById(R.id.map);
        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        mSettingsClient = LocationServices.getSettingsClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        mPlacesViewModel = ViewModelProviders.of(this).get(PlacesResponseViewModel.class);
        mPlacesViewModel.getPlacesWrapper().observe(this, this::handleResponse);
        mPlacesViewModel.getPlaceDetails().observe(this, this::handlePlaceDetailsResponse);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                String location = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
                mLoadingIndicator.setVisibility(View.VISIBLE);
                mPlacesViewModel.loadPlacesForLocation(location);
                stopLocationUpdates();
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, Looper.myLooper());

                })
                .addOnFailureListener(this, exception -> {
                    int statusCode = ((ApiException) exception).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                ResolvableApiException rae = (ResolvableApiException) exception;
                                rae.startResolutionForResult(VetsActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = getString(R.string.settings_error);
                            Log.e(TAG, errorMessage);
                            Toast.makeText(VetsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(checkPermissions()) {
            mMap.setMyLocationEnabled(true);
        }
        CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(getLayoutInflater());
        mMap.setInfoWindowAdapter(customInfoWindowAdapter);

        mMap.setOnMarkerClickListener(marker -> {
            mCurrentClickedMarker = marker;
            PlaceResult placeResult = (PlaceResult) marker.getTag();
            if(placeResult != null) {
                mPlacesViewModel.loadPlaceDetails(placeResult.placeId);
                return true;
            }
            return false;
        });

        mMap.setOnInfoWindowClickListener(marker -> {
            PlaceResult place = (PlaceResult) marker.getTag();
            if(place != null) {

                if(place.placeDetails != null && !TextUtils.isEmpty(place.placeDetails.formattedPhoneNumber)) {
                    Uri number = Uri.parse("tel:" + place.placeDetails.formattedPhoneNumber);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }
            }
        });

        mMap.setOnInfoWindowCloseListener(marker ->
                mCurrentClickedMarker = null);

        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackBar(R.string.permission_rationale,
                    android.R.string.ok, view -> ActivityCompat.requestPermissions(VetsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSIONS_REQUEST_CODE));
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(VetsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                if(mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
                startLocationUpdates();
            } else {
                // Permission denied.
                showSnackBar(R.string.permission_denied_explanation,
                        R.string.settings, view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }
    }

    private void showSnackBar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void handleResponse(PlacesApiResponse placesResponse) {
        mLoadingIndicator.setVisibility(View.GONE);
        if(placesResponse == null || placesResponse.status.equals(PlacesApiResponseCodes.RESPONSE_UNKNOWN_ERROR)) {
            Toast.makeText(this, getString(R.string.load_places_error), Toast.LENGTH_SHORT).show();
        } else if(placesResponse.status.equals(PlacesApiResponseCodes.RESPONSE_ZERO_RESULTS)) {
            Toast.makeText(this, getString(R.string.zero_places_error), Toast.LENGTH_SHORT).show();
        } else if (placesResponse.status.equals(PlacesApiResponseCodes.RESPONSE_OK)) {
            if(mMap != null) {
                addResultsOnMap(placesResponse.getResults());
                if(!TextUtils.isEmpty(markerPlaceId)) {
                    mPlacesViewModel.loadPlaceDetails(markerPlaceId);
                    markerPlaceId = "";
                }
            }
        }
    }

    private void handlePlaceDetailsResponse(PlaceDetailsResponse response) {
        mLoadingIndicator.setVisibility(View.GONE);

        if(response == null || response.status.equals(PlacesApiResponseCodes.RESPONSE_UNKNOWN_ERROR)) {
            Toast.makeText(this, getString(R.string.load_places_error), Toast.LENGTH_SHORT).show();
        } else if (response.status.equals(PlacesApiResponseCodes.RESPONSE_OK)) {
            if(mCurrentClickedMarker != null) {
                PlaceResult place = (PlaceResult) mCurrentClickedMarker.getTag();
                if(place != null) {
                    place.placeDetails = response.getResult();
                    mCurrentClickedMarker.setTag(place);
                    showInfoWindowForCurrentMarker();
                }
            }
        }
    }

    private void addResultsOnMap(List<PlaceResult> results) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        BitmapDescriptor markerIcon = vectorToBitmap(R.drawable.vector_vet_office);

        for(PlaceResult place : results) {
            LatLng position = new LatLng(place.geometry.location.lat, place.geometry.location.lng);
            MarkerOptions markerOptions = new MarkerOptions().position(position).title(place.name).snippet(place.name)
                    .icon(markerIcon);
            Marker marker = mMap.addMarker(markerOptions);
            if(!TextUtils.isEmpty(markerPlaceId) && markerPlaceId.equals(place.placeId)) {
                mCurrentClickedMarker = marker;
            }
            marker.setTag(place);
            builder.include(position);
        }

        LatLng userPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        builder.include(userPosition);
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cameraUpdate);
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mCurrentClickedMarker != null) {
            PlaceResult place = (PlaceResult) mCurrentClickedMarker.getTag();
            if(place != null) {
                outState.putString("clicked_marker", place.placeId);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        markerPlaceId = savedInstanceState.getString("clicked_marker");
    }

    private void showInfoWindowForCurrentMarker() {
        int zoom = 13;
        LatLng latlng = new LatLng(mCurrentClickedMarker.getPosition().latitude + (double) 90 / Math.pow(2, zoom),
                mCurrentClickedMarker.getPosition().longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        mCurrentClickedMarker.showInfoWindow();
    }
}
