package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.Runway;
import nl.robenanita.googlemapstest.Weather.Station;

/**
 * Created by Rob Verhoef on 18-1-14.
 */
public class AirportDataSource {
    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Integer pid;

    private String[] allColumns = {
            DBHelper.C_id,
            DBHelper.C_airport_ident,
            DBHelper.C_continent,
            DBHelper.C_elevation_ft,
            DBHelper.C_gps_code,
            DBHelper.C_home_link,
            DBHelper.C_iata_code,
            DBHelper.C_iso_country,
            DBHelper.C_iso_region,
            DBHelper.C_keywords,
            DBHelper.C_latitude_deg,
            DBHelper.C_local_code,
            DBHelper.C_longitude_deg,
            DBHelper.C_municipality,
            DBHelper.C_name,
            DBHelper.C_scheduled_service,
            DBHelper.C_type,
            DBHelper.C_wikipedia_link,
            DBHelper.C_Version,
            DBHelper.C_Modified,
            DBHelper.C_heading
    };

    public AirportDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open(Integer pid) {
        this.pid = pid;
        try
        {
            dbHelper.createDataBase();
            database = dbHelper.openDataBase();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void close() {
        dbHelper.close();
    }

//    public boolean insertRunways(ArrayList<Runway> runways)
//    {
//        if (runways.size()>0)
//        {
//            database.beginTransaction();
//            try
//            {
//                Integer airport_id = runways.get(0).airport_ref;
//
//                String[] args = {airport_id.toString()};
//                database.delete(DBHelper.RUNWAY_TABLE_NAME, DBHelper.C_airport_id + " =?", args);
//
//                for (Runway runway : runways)
//                {
//                    ContentValues values = getRunwayValues(runway);
//                    database.insert(DBHelper.RUNWAY_TABLE_NAME, null, values);
//                }
//
//                database.setTransactionSuccessful();
//            }
//            catch (Exception ee)
//            {
//                database.endTransaction();
//                Log.i(TAG, "Database error: " + ee.getMessage());
//                return false;
//            }
//
//            database.endTransaction();
//        }
//
//        return true;
//    }

    public boolean insertAirports(Map<Integer, Airport> airports)
    {
        String deleteQuery = "DELETE FROM " + DBHelper.AIRPORT_TABLE_NAME + " WHERE "
            + DBHelper.C_id + " in (";
        for (Map.Entry<Integer, Airport> airp : airports.entrySet())
        {
            Airport a = airp.getValue();
            deleteQuery = deleteQuery + Integer.toString(a.id) + ",";
        }
        deleteQuery = deleteQuery.substring(0, deleteQuery.length()-1) + ");";
        Log.d(TAG, "DELETE Query: " + deleteQuery);
        try {
            database.beginTransaction();
            //database.rawQuery(DBHelper.AIRPORT_TABLE_LOCATION_DROP_INDEX, null);
            //database.rawQuery(DBHelper.AIRPORT_TABLE_DROP_INDEX, null);
            database.rawQuery(deleteQuery, null);
        }
        catch (Exception ee)
        {
            database.endTransaction();
            return false;
        }
        finally {
            database.setTransactionSuccessful();
            database.endTransaction();
        }


        database.beginTransaction();
        try
        {
            for (Map.Entry<Integer, Airport> airp : airports.entrySet())
            {
                Airport a = airp.getValue();
                ContentValues values = createContentValues(a);
                database.insert(DBHelper.AIRPORT_TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
        }
        catch (Exception ee)
        {
            database.endTransaction();
            Log.i(TAG, "Database error: " + ee.getMessage());
            return false;
        }

        database.endTransaction();

        try {
            //database.rawQuery(DBHelper.AIRPORT_TABLE_LOCATION_INDEX, null);
            //database.rawQuery(DBHelper.AIRPORT_TABLE_INDEX, null);
        }
        catch (Exception ee)
        {
            Log.i(TAG, "Database error: " + ee.getMessage());
            return false;
        }

        return true;
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

    public Airport createAirport(Airport airport) {
        ContentValues values = createContentValues(airport);

        long insertId = database.insert(DBHelper.AIRPORT_TABLE_NAME, null,
                values);

        Cursor cursor = database.query(DBHelper.AIRPORT_TABLE_NAME,
                allColumns, "_id = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();

        Airport newAirport = cursorToAirport(cursor);
        cursor.close();
        return newAirport;
    }

    public void setProgramID(Integer uniqueId)
    {
        String u = "UPDATE " + DBHelper.AIRPORT_TABLE_NAME + " SET " + DBHelper.C_pid +
                "=" + Integer.toString(uniqueId);
        Log.i(TAG , "Set program pid: " + u);
        database.execSQL(u);
    }

    public Map<Integer, Airport> SearchAirportNameCode(String searchTerm) {
        //ArrayList<Airport> airports = new ArrayList<Airport>();
        Map<Integer, Airport> airports = new HashMap<Integer, Airport>();

        String query = "SELECT * FROM " + DBHelper.AIRPORT_TABLE_NAME +
                " WHERE " + DBHelper.C_name  + " LIKE '%" + searchTerm + "%' OR "
                + DBHelper.C_gps_code + " LIKE '%" + searchTerm + "%' "
                + " ORDER BY " + DBHelper.C_name + " LIMIT 100;";
        Cursor cursor = database.rawQuery(query, null);

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

        Cursor cursor = database.rawQuery(query, null);
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

        Cursor cursor = database.rawQuery(query, null);
        Airport airport = null;

        if (cursor.moveToFirst())
        {
            airport = cursorToAirport(cursor);
        }

        return airport;
    }

    public Integer GetAirportsCount()
    {
        Integer count = 0;
        String query = "SELECT COUNT(*) c FROM " + DBHelper.AIRPORT_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("c"));
        }
        return count;
    }

    public ArrayList<Integer> getMapLocationIDsByBoundary(LatLngBounds boundary)
    {
        ArrayList<Integer> mapLocationIDs = new ArrayList<Integer>();

        String q = "SELECT " + DBHelper.C_mapLocation_ID + " FROM " + DBHelper.AIRPORT_TABLE_NAME + " ";

        String latBetween = "";
        latBetween = "latitude_deg BETWEEN " + Double.toString(boundary.southwest.latitude)
                + " AND " + Double.toString(boundary.northeast.latitude);

        String lonBetween = "";
        lonBetween = "longitude_deg BETWEEN " + Double.toString(boundary.southwest.longitude)
                + " AND " + Double.toString(boundary.northeast.longitude);

        q = q + " WHERE " + latBetween + " AND " + lonBetween + " ";

        q = q + "GROUP BY " + DBHelper.C_mapLocation_ID;

        Log.i(TAG, "MapLocationID Query: " + q);

        Cursor cursor = database.rawQuery(q, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            mapLocationIDs.add(cursor.getInt(cursor.getColumnIndex(DBHelper.C_mapLocation_ID)));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return mapLocationIDs;
    }

    public Map<Integer, ArrayList<Airport>> getAirportsByMapLocationID(Map<Integer, ArrayList<Airport>> curAirports, ArrayList<Integer> mapLocationIDs)
    {
        Map<Integer, ArrayList<Airport>> airports;
        if (curAirports==null) airports = new HashMap<Integer, ArrayList<Airport>>();
        else
            airports = curAirports;

        String IDs = "";
        for (Integer ID : mapLocationIDs)
        {
            if(!airports.containsKey(ID))
            {
                IDs = IDs + ID.toString() + ",";
            }
        }

        if (IDs.length()>0) {
            IDs = IDs.substring(0, IDs.length() - 1);

            String query = "SELECT A." + dbHelper.C_id + ","
                    + "A." + dbHelper.C_ident + ","
                    + "A." + dbHelper.C_name + ","
                    + "A." + dbHelper.C_latitude_deg + ","
                    + "A." + dbHelper.C_longitude_deg + ","
                    + "A." + dbHelper.C_type + ","
                    + "A." + DBHelper.C_mapLocation_ID + ","
                    + "A._id,"
                    + "R." + DBHelper.C_le_heading + " as heading FROM " + DBHelper.AIRPORT_TABLE_NAME + " A " +
                    "LEFT JOIN " + DBHelper.RUNWAY_TABLE_NAME + " R ON R." + DBHelper.C_airport_ref + " = A." + DBHelper.C_id + " "
                    + "WHERE type!='closed' AND " + DBHelper.C_mapLocation_ID + " in (" + IDs + ")" +
                    " ORDER BY " + DBHelper.C_mapLocation_ID;

            Log.i(TAG, "Airports Query: " + query);

            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            Integer id = -1;
            ArrayList<Airport> a = null;
            while (!cursor.isAfterLast()) {
                Airport airport = cursorToPartAirport(cursor);


                if (!airport.MapLocation_ID.equals(id))
                {
                    if (a != null) airports.put(id, a);
                    id = airport.MapLocation_ID;
                    a = new ArrayList<Airport>();

                    Log.i(TAG, "Add new arraylist for MapLocationID: " + Integer.toString(id));
                }

                a.add(airport);

                cursor.moveToNext();
            }
            airports.put(id, a);
            // make sure to close the cursor
            cursor.close();
        }

        return airports;
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
                //+ "A." + DBHelper.C_mapLocation_ID + ","
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
                + " AND (" + DBHelper.C_pid + "<>" + pid + " or " +  DBHelper.C_pid + " is null);";

        Log.i(TAG, "Airports Query: " + query);

        Cursor cursor = database.rawQuery(query, null);

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
        database.execSQL(pidQuery);

        return airports;
    }

    private void UpdateProgramID(String query)
    {

    }

    public ArrayList<Airport> getAirportsByBoundary(LatLngBounds boundary)
    {
        ArrayList<Airport> _airports = new ArrayList<>();
        String latBetween = "";
        latBetween = "latitude_deg BETWEEN " + Double.toString(boundary.southwest.latitude)
                + " AND " + Double.toString(boundary.northeast.latitude);

        String lonBetween = "";
        lonBetween = "longitude_deg BETWEEN " + Double.toString(boundary.southwest.longitude)
                + " AND " + Double.toString(boundary.northeast.longitude);

        String where = " WHERE " + latBetween + " AND " + lonBetween;

        String query = "SELECT A." + dbHelper.C_id + ","
                + "A." + dbHelper.C_ident + ","
                + "A." + dbHelper.C_name + ","
                + "A." + dbHelper.C_latitude_deg + ","
                + "A." + dbHelper.C_longitude_deg + ","
                + "A." + dbHelper.C_type + ","
                + "A._id"
                + " FROM " + DBHelper.AIRPORT_TABLE_NAME + " A " +
                " " + where
                + ";";

        Log.i(TAG, "Airports Query: " + query);

        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Airport airport = cursorToPartAirport(cursor);
            _airports.add(airport);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return _airports;
    }

    public ArrayList<Station> getAirportsInBuffer(Geometry buffer)
    {
        ArrayList<Station> airports = new ArrayList<Station>();
        // First create the envelope
        Geometry e = buffer.getEnvelope();
        Coordinate[] corners = e.getCoordinates();
        Coordinate c1 = corners[0];
        Coordinate c2 = corners[2];

        String select = "SELECT " + DBHelper.C_ident +
                "," + DBHelper.C_latitude_deg +
                "," + DBHelper.C_longitude_deg +
                " FROM " + DBHelper.AIRPORT_TABLE_NAME + " WHERE ";
        String latBetween = "";
        latBetween = "latitude_deg BETWEEN " + Double.toString(c1.y)
                + " AND " + Double.toString(c2.y);

        String lonBetween = "";
        lonBetween = "longitude_deg BETWEEN " + Double.toString(c1.x)
                + " AND " + Double.toString(c2.x);

        String type = "type in ('large_airport','medium_airport','small_airport')";

        select = select + latBetween + " AND " + lonBetween + " AND " + type;

        Log.d(TAG, select);

        Cursor cursor = database.rawQuery(select, null);

        cursor.moveToFirst();
        PreparedGeometry targetPrep
                = PreparedGeometryFactory.prepare(buffer);
        while(!cursor.isAfterLast())
        {
            Station s = new Station();
            s.station_id = cursor.getString(cursor.getColumnIndex(DBHelper.C_ident));
            s.latitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_latitude_deg));
            s.longitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_longitude_deg));
            Geometry g1 = new GeometryFactory().createPoint(new Coordinate(s.longitude, s.latitude));
            if (targetPrep.contains(g1)) airports.add(s);
            cursor.moveToNext();
        }

        return airports;
    }

    public Map<Integer, Airport> getAllAirports() {
        //ArrayList<Airport> airports = new ArrayList<Airport>();
        Map<Integer, Airport> airports = new HashMap<Integer, Airport>();

        Cursor cursor = database.query(DBHelper.AIRPORT_TABLE_NAME,
                allColumns, null, null, null, null, null);

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

    private Airport cursorToPartAirport(Cursor cursor) {
        Airport airport = new Airport();
        airport.ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_ident));
        airport.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));
        airport.id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_id));
        //airport.MapLocation_ID = cursor.getInt(cursor.getColumnIndex(DBHelper.C_mapLocation_ID));
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
