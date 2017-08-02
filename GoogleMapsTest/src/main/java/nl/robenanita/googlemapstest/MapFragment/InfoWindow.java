package nl.robenanita.googlemapstest.MapFragment;

import android.app.Fragment;
import android.graphics.Point;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 2-8-2017.
 */

public class InfoWindow {

    public InfoWindow(LatLng position, GoogleMap googleMap, Fragment parentfragent, Fragment fragment)
    {
        this.position = position;
        this.googleMap = googleMap;
        this.parentFragment = parentfragent;
        this.infoWindowContentLayout = (LinearLayout)parentfragent.getView().findViewById(R.id.fspInfoWindowContentLayout);
        this.infoWindowContent = (LinearLayout)parentfragent.getView().findViewById(R.id.fspInfoWindowContent);
        this.fragment = fragment;
        setupWindow();
    }

    private void setupWindow()
    {
        parentFragment.getFragmentManager().beginTransaction().add(infoWindowContent.getId(), fragment).commit();
        infoWindowContentLayout.setVisibility(View.VISIBLE);
        setPosition();
    }

    private void setPosition()
    {
        Projection projection = googleMap.getProjection();
        Point screenpoint = projection.toScreenLocation(position);
        FrameLayout.LayoutParams  layoutParams = (FrameLayout.LayoutParams) infoWindowContentLayout.getLayoutParams();
        Integer width = layoutParams.width;
        Integer height = layoutParams.height;
        layoutParams.setMargins(screenpoint.x - (width / 2), screenpoint.y - height, 0, 0);
        infoWindowContentLayout.setLayoutParams(layoutParams);
    }

    public void MapPositionChanged()
    {
        setPosition();
    }

    public void RemoveInfoWindow()
    {
        infoWindowContentLayout.setVisibility(View.GONE);
    }

    private final LatLng position;
    private final GoogleMap googleMap;
    private final LinearLayout infoWindowContentLayout;
    private LinearLayout infoWindowContent;
    private Fragment fragment;
    private Fragment parentFragment;
}
