package nl.robenanita.googlemapstest.markers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 17-9-2015.
 */
public class PlaneMarker {
    public PlaneMarker(GoogleMap map, LatLng position, Float heading)
    {
        this.map = map;
        this.position = position;
        this.heading = heading;
        createPlaneMarker();
    }

    private void createPlaneMarker()
    {
        plane = map.addMarker(new MarkerOptions()
                .position(position)
                .title("Plane Position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blackaircrafticonsmall))
                .rotation(heading)
                .anchor(0.5f, 0.5f)
                .flat(true));

    }

    private GoogleMap map;
    private Marker plane;
    private LatLng position;
    private Float heading;

    public void setPosition(LatLng position)
    {
        this.position = position;
        plane.setPosition(this.position);
    }

    public void setRotation(float heading)
    {
        this.heading = heading;
        plane.setRotation(this.heading);
    }
}
