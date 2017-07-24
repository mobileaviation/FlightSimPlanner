package nl.robenanita.googlemapstest.Airspaces;

import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.vividsolutions.jts.geom.Coordinate;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.Helpers;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public class Airspaces extends ArrayList<Airspace> {
    public Airspaces(Context context)
    {
        this.context = context;
    }

    private Context context;

    public void Add(Airspace airspace) {
        this.add(airspace);
    }


    public void readFromDatabase(Cursor cursor)
    {
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Airspace airspace = new Airspace();
            airspace.AssignFromCursor(cursor);
            Add(airspace);
            cursor.moveToNext();
        }
    }

    public void createAirspacesLayer(GoogleMap mapView, String Name)
    {
        //String mapname = "Airspaces"+Name;

        for (Airspace airspace: this) {
            PolygonOptions polygonOptions = new PolygonOptions();

            for (Coordinate coordinate : airspace.coordinates){
                polygonOptions.add(new LatLng(coordinate.y, coordinate.x));
            }

            polygonOptions.fillColor(airspace.Category.getFillColor());
            polygonOptions.strokeColor(airspace.Category.getStrokeColor());
            polygonOptions.strokeWidth(Helpers.convertDpToPixel(airspace.Category.getStrokeWidth(), context));

            mapView.addPolygon(polygonOptions);
        }
    }

    public String getAirpspacesInfoString()
    {
        String info;

        info = "Airspaces Information................\n";

        for (Airspace airspace : this)
        {
            info = info + "Name:" + airspace.Name + " Class:" +
                    airspace.Category.toString() +
                    "  From:" + airspace.AltLimit_Bottom.toString() + "" + airspace.AltLimit_Bottom_Unit.toString() +
                    "  To:" + airspace.AltLimit_Top.toString() + "" + airspace.AltLimit_Top_Unit.toString() + "\n";
        }

        return info;
    }
}
