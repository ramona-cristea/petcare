package com.ramona.petcare;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.ramona.petcare.model.PlaceResult;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mContents;

    CustomInfoWindowAdapter(LayoutInflater inflater) {
        mContents = inflater.inflate(R.layout.custom_info_contents, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mContents);
        return mContents;
    }

    private void render(Marker marker, View view) {
        if (marker.getTag() instanceof PlaceResult){
            PlaceResult place = (PlaceResult) marker.getTag();
            if(place != null) {
                ((ImageView) view.findViewById(R.id.badge)).setImageResource(R.drawable.vector_vet_office_info_window);
                String title = marker.getTitle();
                TextView titleUi = (view.findViewById(R.id.title));
                titleUi.setText(title);

                TextView snippetUi = (view.findViewById(R.id.snippet));
                snippetUi.setText(place.placeDetails.address);

                if(place.placeDetails != null && place.placeDetails.openingHours != null) {
                    TextView openNowUi = (view.findViewById(R.id.open_now));
                    openNowUi.setText(openNowUi.getResources().getString(place.placeDetails.openingHours.openNow ? R.string.is_open : R.string.is_closed));
                    openNowUi.setTextColor(openNowUi.getResources().getColor(place.placeDetails.openingHours.openNow ? R.color.green_color : R.color.colorAccent));
                }
            }
        }
    }
}
