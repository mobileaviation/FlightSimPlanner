package nl.robenanita.googlemapstest.Airspaces;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import nl.robenanita.googlemapstest.database.AirspacesDB;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public class LoadAirspacesAsync extends AsyncTask {
    public GoogleMap mapView;
    public String databaseName;
    public Context context;
    private Airspaces airspaces;
    private String country;

    @Override
    protected Object doInBackground(Object[] params) {
        AirspacesDB airspacesDB = new AirspacesDB(context);
        airspacesDB.Open(databaseName);
        airspaces = new Airspaces(context);
        country = databaseName.split("_")[0];
        airspaces.readFromDatabase(airspacesDB.GetAirspaces(country));
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        airspaces.createAirspacesLayer(mapView, country);
        super.onPostExecute(o);
    }
}
