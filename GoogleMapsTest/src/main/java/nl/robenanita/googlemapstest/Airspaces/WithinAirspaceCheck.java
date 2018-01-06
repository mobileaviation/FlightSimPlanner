package nl.robenanita.googlemapstest.Airspaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.database.AirspacesDataSource;
import nl.robenanita.googlemapstest.database.DBFilesHelper;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public class WithinAirspaceCheck extends AsyncTask {

    public WithinAirspaceCheck(Context context, Coordinate checkPoint)
    {
        this.context = context;
        foundAirspaces = new Airspaces(this.context);
        GeometryFactory factory = new GeometryFactory();
        point = factory.createPoint(checkPoint);
    }

    private Airspaces foundAirspaces;
    private Context context;
    private Point point;
    private String TAG = "WithinAirspaceCheck";

    @Override
    protected Object doInBackground(Object[] params) {

        Airspaces as = new Airspaces(context);
        AirspacesDataSource airspacesDB = new AirspacesDataSource(context);
        airspacesDB.Open("all-airspaces.db");
        as.readFromDatabase(airspacesDB.GetAirspacesByCoordinate(point.getCoordinate()));

        for (Airspace airspace : as)
        {
            if (airspace.getGeometry().contains(point)) {
                foundAirspaces.add(airspace);
                publishProgress(airspace);
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        Airspace airspace = (Airspace)values[0];
        if (onFoundAirspace != null) onFoundAirspace.OnFoundAirspace(airspace);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object o) {
        if (onFoundAirspace != null) onFoundAirspace.OnFoundAllAirspaces(foundAirspaces);
        super.onPostExecute(o);
    }

    private OnFoundAirspace onFoundAirspace;
    public void SetOnFoundAirspace(OnFoundAirspace d) { onFoundAirspace = d; }
    public interface OnFoundAirspace
    {
        public void OnFoundAirspace(Airspace airspace);
        public void OnFoundAllAirspaces(Airspaces airspaces);
    }
}