package nl.robenanita.googlemapstest;

import com.google.android.gms.maps.model.LatLng;
//import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Rob Verhoef on 1-2-14.
 */

public class AirportMarker // implements ClusterItem
{
    private final LatLng mPosition;

    public AirportMarker(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    //@Override
    public LatLng getPosition() {
        return mPosition;
    }
}


