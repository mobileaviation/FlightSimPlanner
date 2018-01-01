package nl.robenanita.googlemapstest.MapFragment;

import android.app.Fragment;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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

    private String TAG = "InfoWindow";

    public InfoWindow(LatLng position, GoogleMap googleMap, Fragment parentfragent, Fragment fragment)
    {
        this.position = position;
        this.googleMap = googleMap;
        this.parentFragment = parentfragent;
        this.infoWindowContentLayout = (LinearLayout)parentfragent.getView().findViewById(R.id.fspInfoWindowContentLayout);

        this.infoWindowContent = (LinearLayout)parentfragent.getView().findViewById(R.id.fspInfoWindowContent);
        this.fragment = fragment;

        setupWindow();
        infoWindowContentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setPosition();
            }
        });
    }

    private void setupWindow()
    {
        parentFragment.getFragmentManager().beginTransaction().replace(infoWindowContent.getId(), fragment).commit();
        infoWindowContentLayout.setVisibility(View.VISIBLE);
        //setPosition();
    }

    private void afterContentLoaded()
    {
        //width = infoWindowContentLayout.getWidth();
        //height = infoWindowContentLayout.getHeight();
        //Log.i(TAG, "Window Width: " + width + " Window Height: " + height);
    }


    private Integer width = 0;
    private Integer height = 0;
    private void setPosition()
    {
        Projection projection = googleMap.getProjection();
        Point screenpoint = projection.toScreenLocation(position);
        infoWindowContentLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        width = infoWindowContentLayout.getMeasuredWidth();
        height = infoWindowContentLayout.getMeasuredHeight();
        Integer x = screenpoint.x - (width / 2);
        Integer y = screenpoint.y - height;

//        Log.i(TAG, "Window X: " + x + " Window Y: " + y + " ScreenPoint X: " + screenpoint.x + " Screenpoint Y: " + screenpoint.y
//         + " Window Width: " + width + " Window Height: " + height);

        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(width, height);
        p.setMargins(x, y, 0, 0);
        infoWindowContentLayout.setLayoutParams(p);
    }

    public void MapPositionChanged()
    {
        afterContentLoaded();
        setPosition();
    }

    public void RemoveInfoWindow()
    {
        infoWindowContentLayout.setVisibility(View.INVISIBLE);
    }

    private final LatLng position;
    private final GoogleMap googleMap;
    private final LinearLayout infoWindowContentLayout;
    private LinearLayout infoWindowContent;
    private Fragment fragment;
    private Fragment parentFragment;
}
