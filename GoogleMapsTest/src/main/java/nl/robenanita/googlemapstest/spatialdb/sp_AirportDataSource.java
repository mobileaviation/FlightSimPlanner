package nl.robenanita.googlemapstest.spatialdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.Map;

import jsqlite.Database;
import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.Runway;
import nl.robenanita.googlemapstest.database.DBHelper;
import nl.robenanita.googlemapstest.database.Helpers;
import nl.robenanita.googlemapstest.database.MarkerProperties;

/**
 * Created by Rob Verhoef on 19-3-2015.
 */
public class sp_AirportDataSource {
    // Database fields
    private Database database;
    private SpatialHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Integer pid;

    private String[] allColumns = {
            SpatialHelper.C_id,
            SpatialHelper.C_airport_ident,
            SpatialHelper.C_continent,
            SpatialHelper.C_elevation_ft,
            SpatialHelper.C_gps_code,
            SpatialHelper.C_home_link,
            SpatialHelper.C_iata_code,
            SpatialHelper.C_iso_country,
            SpatialHelper.C_iso_region,
            SpatialHelper.C_keywords,
            SpatialHelper.C_latitude_deg,
            SpatialHelper.C_local_code,
            SpatialHelper.C_longitude_deg,
            SpatialHelper.C_municipality,
            SpatialHelper.C_name,
            SpatialHelper.C_scheduled_service,
            SpatialHelper.C_type,
            SpatialHelper.C_wikipedia_link,
            SpatialHelper.C_Version,
            SpatialHelper.C_Modified,
            SpatialHelper.C_heading
    };



    public sp_AirportDataSource(Context context) {
        dbHelper = new SpatialHelper(context);
    }

    public void open(Integer pid) {
        this.pid = pid;
        database = dbHelper.openDatabase();
    }

    public void close() {
        dbHelper.closeDatabase();
    }


    private ContentValues createContentValues(Airport airport)
    {
        ContentValues values = new ContentValues();
        values.put(DBHelper.C_id, airport.id);
        values.put(DBHelper.C_airport_ident, airport.ident);
        values.put(DBHelper.C_continent, airport.continent);
        values.put(DBHelper.C_elevation_ft, airport.elevation_ft);
        values.put(DBHelper.C_gps_code, airport.gps_code);
        values.put(DBHelper.C_home_link, airport.home_link);
        values.put(DBHelper.C_iata_code, airport.iata_code);
        values.put(DBHelper.C_iso_country, airport.iso_country);
        values.put(DBHelper.C_iso_region, airport.iso_region);
        values.put(DBHelper.C_keywords, airport.keywords);
        values.put(DBHelper.C_latitude_deg, airport.latitude_deg);
        values.put(DBHelper.C_local_code, airport.local_code);
        values.put(DBHelper.C_longitude_deg, airport.longitude_deg);
        values.put(DBHelper.C_municipality, airport.municipality);
        values.put(DBHelper.C_name, airport.name);
        values.put(DBHelper.C_scheduled_service, airport.scheduled_service);
        values.put(DBHelper.C_type, airport.type.toString());
        values.put(DBHelper.C_wikipedia_link, airport.wikipedia_link);
        values.put(DBHelper.C_Version, airport.version);
        values.put(DBHelper.C_Modified, Helpers.getDateTime(airport.modified));
        values.put(DBHelper.C_heading, airport.heading);
        return values;
    }


    public void setProgramID(Integer uniqueId)
    {
        String u = "UPDATE " + DBHelper.AIRPORT_TABLE_NAME + " SET " + DBHelper.C_pid +
                "=" + Integer.toString(uniqueId);
        //database.execSQL(u);
    }

    public Map<Integer, Airport> SearchAirportNameCode(String searchTerm) {
        //ArrayList<Airport> airports = new ArrayList<Airport>();
        Map<Integer, Airport> airports = new HashMap<Integer, Airport>();

        String query = "SELECT * FROM " + DBHelper.AIRPORT_TABLE_NAME +
                " WHERE " + DBHelper.C_name  + " LIKE '%" + searchTerm + "%' OR "
                + DBHelper.C_gps_code + " LIKE '%" + searchTerm + "%' "
                + " ORDER BY " + DBHelper.C_name + " LIMIT 100;";
        Cursor cursor = null;// = database.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Airport airport = cursorToAirport(cursor);
            airports.put(airport.id, airport);
            //airports.add(airport);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return airports;
    }

    public Airport GetAirportByID(Integer id) {
        String query = "SELECT * FROM " + DBHelper.AIRPORT_TABLE_NAME +
                " WHERE id=" + Integer.toString(id) + ";";

        Cursor cursor = null;// database.rawQuery(query, null);
        Airport airport = null;

        if (cursor.moveToFirst())
        {
            airport = cursorToAirport(cursor);
        }

        return airport;
    }

    public Airport GetAirportByIDENT(String ident) {
        String query = "SELECT * FROM " + DBHelper.AIRPORT_TABLE_NAME +
                " WHERE ident='" + ident + "';";

        Cursor cursor = null; // database.rawQuery(query, null);
        Airport airport = null;

        if (cursor.moveToFirst())
        {
            airport = cursorToAirport(cursor);
        }

        return airport;
    }



    public Map<Integer, Airport> getAirportsByCoordinateAndZoomLevel(LatLngBounds boundary, Float zoomLevel,
                                                                     Map<Integer, Airport> curAirports ,
                                                                     MarkerProperties markerProperties)
    {
        Map<Integer, Airport> airports;
        if (curAirports==null) airports = new HashMap<Integer, Airport>();
        else
            airports = curAirports;

        String where = markerProperties.getWhereByZoomLevel(zoomLevel);

        String latBetween = "";
        latBetween = "latitude_deg BETWEEN " + Double.toString(boundary.southwest.latitude)
                + " AND " + Double.toString(boundary.northeast.latitude);

        String lonBetween = "";
        lonBetween = "longitude_deg BETWEEN " + Double.toString(boundary.southwest.longitude)
                + " AND " + Double.toString(boundary.northeast.longitude);

        where = where + " AND " + latBetween + " AND " + lonBetween;

        String query = "SELECT A." + dbHelper.C_id + ","
                + "A." + dbHelper.C_ident + ","
                + "A." + dbHelper.C_name + ","
                + "A." + dbHelper.C_latitude_deg + ","
                + "A." + dbHelper.C_longitude_deg + ","
                + "A." + dbHelper.C_type + ","
                + "A." + DBHelper.C_mapLocation_ID + ","
                + "A._id,"
                + "R." + DBHelper.C_le_heading + ","
                + "R." + DBHelper.C_le_ident + ","
                + "R." + DBHelper.C_le_latitude + ","
                + "R." + DBHelper.C_le_longitude + ","
                + "R." + DBHelper.C_he_ident + ","
                + "R." + DBHelper.C_he_latitude + ","
                + "R." + DBHelper.C_he_longitude
                + " FROM " + DBHelper.AIRPORT_TABLE_NAME + " A " +
                "LEFT JOIN " + DBHelper.RUNWAY_TABLE_NAME + " R ON R." + DBHelper.C_airport_ref + " = A." + DBHelper.C_id + " " + where
                + " AND " + DBHelper.C_pid + "<>" + pid;

        Log.i(TAG, "Airports Query: " + query);

        Cursor cursor = null;// database.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Airport airport = cursorToPartAirport(cursor);
            if (!airports.containsKey(airport.id)) {
                airports.put(airport.id, airport);
                // Add the first runway if present else add dummy runway
                airport.runways.add(cursorToRunway(cursor));
            }
            else
            {
                Airport a = airports.get(airport.id);
                a.runways.add(cursorToRunway(cursor));
            }


            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        String pidQuery = "UPDATE " + DBHelper.AIRPORT_TABLE_NAME + " SET " + DBHelper.C_pid + " = " + pid + " " + where;
        //database.execSQL(pidQuery);

        return airports;
    }



    private Airport cursorToPartAirport(Cursor cursor) {
        Airport airport = new Airport();
        airport.ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_ident));
        airport.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));
        airport.id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_id));
        airport.MapLocation_ID = cursor.getInt(cursor.getColumnIndex(DBHelper.C_mapLocation_ID));
        airport.latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_latitude_deg));
        airport.longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_longitude_deg));
        airport.heading = (cursor.isNull(cursor.getColumnIndex(DBHelper.C_le_heading))) ? 0 : cursor.getDouble(cursor.getColumnIndex(DBHelper.C_le_heading));
        airport.type = Airport.ParseAirportType(cursor.getString(cursor.getColumnIndex(DBHelper.C_type)));
        return airport;
    }

    private Runway cursorToRunway(Cursor cursor)
    {
        Runway runway = new Runway();

        if (!cursor.isNull(cursor.getColumnIndex(DBHelper.C_le_heading))) {
            //runway.airport_ref = cursor.getInt(cursor.getColumnIndex(DBHelper.C_airport_ref));
            //runway.airport_ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_airport_ident));
            //runway.length_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_length));
            //runway.width_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_width));
            //runway.surface = cursor.getString(cursor.getColumnIndex(DBHelper.C_surface));
            //runway.id = cursor.getInt(cursor.getColumnIndex("id"));
            runway.le_ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_le_ident));
            runway.le_latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_le_latitude));
            runway.le_longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_le_longitude));
            //runway.le_elevation_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_le_elevation));
            runway.le_heading_degT = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_le_heading));
            //runway.le_displaced_threshold_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_le_displaced_threshold));
            runway.he_ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_he_ident));
            runway.he_latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_he_latitude));
            runway.he_longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_he_longitude));
            //runway.he_elevation_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_he_elevation));
            //runway.he_heading_degT = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_he_heading));
            //runway.he_displaced_threshold_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_he_displaced_threshold));
        }
        else
        {
            runway.le_heading_degT = -1;
        }

        return runway;
    }

    private Airport cursorToAirport(Cursor cursor) {
        Airport airport = new Airport();
        airport.ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_ident));
        airport.continent = cursor.getString(cursor.getColumnIndex(DBHelper.C_continent));
        airport.elevation_ft = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_elevation_ft));
        airport.gps_code = cursor.getString(cursor.getColumnIndex(DBHelper.C_gps_code));
        airport.home_link = cursor.getString(cursor.getColumnIndex(DBHelper.C_home_link));
        airport.iata_code = cursor.getString(cursor.getColumnIndex(DBHelper.C_iata_code));
        airport.id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_id));
        airport.iso_country = cursor.getString(cursor.getColumnIndex(DBHelper.C_iso_country));
        airport.iso_region = cursor.getString(cursor.getColumnIndex(DBHelper.C_iso_region));
        airport.keywords = cursor.getString(cursor.getColumnIndex(DBHelper.C_keywords));
        airport.latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_latitude_deg));
        airport.local_code = cursor.getString(cursor.getColumnIndex(DBHelper.C_local_code));
        airport.longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_longitude_deg));
        airport.municipality = cursor.getString(cursor.getColumnIndex(DBHelper.C_municipality));
        airport.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));
        airport.scheduled_service = cursor.getString(cursor.getColumnIndex(DBHelper.C_scheduled_service));
        airport.type = Airport.ParseAirportType(cursor.getString(cursor.getColumnIndex(DBHelper.C_type)));
        airport.wikipedia_link = cursor.getString(cursor.getColumnIndex(DBHelper.C_wikipedia_link));
        airport.version = (cursor.isNull(cursor.getColumnIndex(DBHelper.C_Version))) ? 0 : cursor.getInt(cursor.getColumnIndex(DBHelper.C_Version));
        //airport.modified = Helpers.readDataTime(cursor.getString(cursor.getColumnIndex(DBHelper.C_Modified)));
        airport.heading = (cursor.isNull(cursor.getColumnIndex(DBHelper.C_heading))) ? 0 : cursor.getDouble(cursor.getColumnIndex(DBHelper.C_heading));


        return airport;
    }
}
