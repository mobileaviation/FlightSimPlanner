package nl.robenanita.googlemapstest.MapFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Navaid;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.flightplan.WaypointType;

/**
 * Created by Rob Verhoef on 29-8-2017.
 */

public class SelectableWaypoint {
    public WaypointType type;
    public Object object;
    public LatLng position;
    public String name;
    public String ident;

    public String GetName()
    {
        switch (type)
        {
            case Airport:
                return ((Airport)object).name;
            case navaid:
                return ((Navaid)object).name;
            case userwaypoint:
                return name;
        }
        return "";
    }

    public String GetIdent()
    {
        switch (type)
        {
            case Airport:
                return ((Airport)object).ident;
            case navaid:
                return ((Navaid)object).ident;
            case userwaypoint:
                return ident;
        }
        return "";
    }

    public LatLng GetLatLng()
    {
        LatLng loc = new LatLng(0,0);
        switch (type)
        {
            case Airport:
                return ((Airport)object).getLatLng();
            case navaid:
                return ((Navaid)object).getLatLng();
            case userwaypoint:
                return position;
        }
        return loc;
    }

    public Bitmap GetIcon(Context context)
    {
        switch (type)
        {
            case Airport:
                Airport a = ((Airport)object);
                return a.GetSmallIcon((float)a.heading, a.ident, context );
            case navaid:
                Navaid n = ((Navaid)object);
                return n.GetSmallIcon(context);
            case userwaypoint:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.waypoint);
        }
        return null;
    }
}
