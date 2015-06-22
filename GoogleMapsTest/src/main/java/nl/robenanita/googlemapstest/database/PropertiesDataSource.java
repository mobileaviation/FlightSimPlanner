package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.AirportType;
import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.Property;
import nl.robenanita.googlemapstest.Runway;

/**
 * Created by Rob Verhoef on 23-4-14.
 */
public class PropertiesDataSource {
    private SQLiteDatabase database;
    private UserDBHelper userDbHelper;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Context c;

    private MarkerProperties markerProperties;

    public PropertiesDataSource(Context context) {
        c = context;
        userDbHelper = new UserDBHelper(context);
    }

    public void open(boolean user){
        try {
            //..dbHelper.createDataBase();
            if (user) {
                database = userDbHelper.getWritableDatabase();
                markerProperties = new MarkerProperties();
                fillMarkerProperties();
            } else
            {
                database = dbHelper.getWritableDatabase();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close(boolean user) {
        if (user) userDbHelper.close();
        else dbHelper.close();
    }

//    public void checkVersion()
//    {
//        DBVersion = getProperty("DB_VERSION");
//        this.close();
//        if (DBVersion != null)
//        {
//            Integer v = Integer.parseInt(DBVersion.value1);
//            dbHelper.deleteDatabase(v);
//        }
//    }

    private ContentValues createPropertyContentValues(Property property)
    {
        ContentValues values = new ContentValues();
        values.put("_id", property._id);
        values.put(UserDBHelper.C_name, property.name);
        values.put("value1", property.value1);
        values.put("value2", property.value2);
        return values;
    }

    private String[] allColumns = {
            "_id",
            UserDBHelper.C_name,
            "value1",
            "value2"
    };

    private Property cursorToProperty(Cursor cursor)
    {
        Property property = new Property();
        property._id = cursor.getInt(cursor.getColumnIndex("_id"));
        property.name = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_name));
        property.value1 = cursor.getString(cursor.getColumnIndex("value1"));
        property.value2 = cursor.getString(cursor.getColumnIndex("value2"));

        return property;
    }

    public void updateProperty(Property property)
    {
        String query = "UPDATE " + UserDBHelper.PROPERTIES_TABLE_NAME +
                " SET value1='" + property.value1 + "', value2='" + property.value2.replace("'", "\"") + "'"
                + " WHERE name='" + property.name + "';";

        database.execSQL(query);
    }

    public Property IpAddress;
    public Property InitPosition;
    public Property InitZoom;
    public Property DBVersion;
    public Property InitFlightplan;

    public Airport InitAirport;
    private Property initAirport;
    public Runway InitRunway;
    private Property connectionType;
    public NavigationActivity.ConnectionType getConnectionType()
    { return (connectionType.value2.equals("sim")) ? NavigationActivity.ConnectionType.sim
            : NavigationActivity.ConnectionType.gps; }
    public void updateConnectionType(NavigationActivity.ConnectionType type)
    {
        if (type == NavigationActivity.ConnectionType.gps)
            connectionType.value2 = "gps";
        else connectionType.value2 = "sim";
        updateProperty(connectionType);
    }
    private Property instrumentsVisible;
    public Boolean getInstrumentsVisible()
    { return (instrumentsVisible.value2.equals("1")); }
    public void updateInstrumentVisible(Boolean visible)
    {
        if (visible) instrumentsVisible.value2 = "1"; else instrumentsVisible.value2 = "0";
        updateProperty(instrumentsVisible);
    }

    public void FillProperties()
    {
        IpAddress = getProperty("SERVER");
        InitPosition = getProperty("INIT_POSITION");
        InitZoom = getProperty("INIT_ZOOM");
        DBVersion = getProperty("DB_VERSION");
        InitFlightplan = getProperty("INIT_FLIGHTPLAN_ID");
        initAirport = getProperty("INIT_AIRPORT");
        InitAirport = getInitAirport();
        connectionType = getProperty("LOCATION");
        instrumentsVisible = getProperty("INSTRUMENTS");
    }

    public void UpdateInitAirport()
    {
        initAirport.value1 = Integer.toString(InitAirport.id);
        initAirport.value2 = "";
        if (InitRunway.id>0)
            initAirport.value2 = Integer.toString(InitRunway.id) + "," + InitRunway.active;
        updateProperty(initAirport);
    }

    private Airport getInitAirport()
    {
        Airport a = null;

        AirportDataSource airportDataSource = new AirportDataSource(c);
        airportDataSource.open(-1);
        a = airportDataSource.GetAirportByID(Integer.parseInt(initAirport.value1));

        RunwaysDataSource runwaysDataSource = new RunwaysDataSource(c);
        runwaysDataSource.open();

        a.runways = runwaysDataSource.loadRunwaysByAirport(a);

        runwaysDataSource.close();
        airportDataSource.close();

        Log.i(TAG, "Initial Airport: " + a.name);
        if (a.runways!=null)
            Log.i(TAG, "Found " + a.runways.size() + " runways");

        String r[] = initAirport.value2.split(",");
        if (r.length>0)
        {
            for (Runway ru: a.runways)
            {
                if (ru.id == Integer.parseInt(r[0]))
                {
                    InitRunway = ru;
                    InitRunway.active = r[1];
                    if (InitRunway.le_latitude_deg==0) InitRunway.le_latitude_deg = a.latitude_deg;
                    if (InitRunway.he_latitude_deg==0) InitRunway.he_latitude_deg = a.latitude_deg;
                    if (InitRunway.le_longitude_deg==0) InitRunway.le_longitude_deg = a.longitude_deg;
                    if (InitRunway.he_longitude_deg==0) InitRunway.he_longitude_deg = a.longitude_deg;
                }
            }
        }

        if (InitRunway != null) Log.i(TAG, "Initial Runway: " + InitRunway.he_ident + " - " + InitRunway.le_ident);
        else
        {
            Log.i(TAG, "No Initial runway found!, so create temp InitRunway");
            InitRunway = new Runway();
            InitRunway.id = -1;
            InitRunway.le_latitude_deg = a.latitude_deg;
            InitRunway.le_longitude_deg = a.longitude_deg;
            InitRunway.le_heading_degT = 0;

        }

        return a;
    }

    public Property GetProperty(String name)
    {
        return getProperty(name);
    }

    private Property getProperty(String name)
    {
        String query = "SELECT * FROM " + UserDBHelper.PROPERTIES_TABLE_NAME
                + " WHERE name='" + name + "';";

        Cursor cursor = database.rawQuery(query, null);

        Property p = null;
        if (cursor.moveToFirst())
        {
            p = cursorToProperty(cursor);
        }

        if (p != null) Log.i(TAG, "Property: " + p.name + " with value1: " + p.value1 + " and value2: " + p.value2);
        return p;
    }

    public void InsertProperty(Property p)
    {
        String query = "INSERT INTO " + UserDBHelper.PROPERTIES_TABLE_NAME
                + " (" + UserDBHelper.C_name + ","
                + UserDBHelper.C_value1 + ","
                + UserDBHelper.C_value2 + ") VALUES("
                + "'" + p.name + "', "
                + "'" + p.value1 + "', "
                + "'" + p.value2.replace("'", "\"") + "');";

        database.execSQL(query);
    }

    public boolean checkNoAdvertisements()
    {
        Property p = getProperty("ADVERTISEMENTS");
        if (p != null)
        {
            return p.value1.equals("LICENCE");
        }
        else return false;

    }

    public Property getMapSetup(String map)
    {
        Property p = getProperty(map);
        return p;
    }

    public void setNoAdvertisements()
    {
        Property p;

        p = getProperty("ADVERTISEMENTS");
        if (p == null) {
            p = new Property();
            p.name = "ADVERTISEMENTS";
            p.value1 = "LICENCE";
            p.value2 = "";

            String q = "INSERT INTO " + UserDBHelper.PROPERTIES_TABLE_NAME +
                    " (" +
                    UserDBHelper.C_name + ", " + UserDBHelper.C_value1 + ", " + UserDBHelper.C_value2 +
                    ") VALUES('" + p.name + "','" + p.value1 + "','" + p.value2 + "')";

            database.execSQL(q);
            //database.insert(UserDBHelper.PROPERTIES_TABLE_NAME, null, createPropertyContentValues(p));
        }
        else
        {
            p.value1 = "LICENCE";
            p.value2 = "";
            updateProperty(p);
        }
    }

    public MarkerProperties getMarkersProperties() {
        Property marker = getProperty("MARKERS");
        markerProperties.FromXml(marker.value2);
        return markerProperties;
    }

    private void fillMarkerProperties()
    {
        Property p = getProperty("MARKERS");
        if (p.value2.equals("test")) {
            MarkerProperties.MarkerProperty m = markerProperties.getNewMarkerProperty();
            m.airportType = AirportType.large_airport;
            m.visible = true;
            m.VisibleAbove = 0f;
            markerProperties.properties.add(m);

            m = markerProperties.getNewMarkerProperty();
            m.airportType = AirportType.medium_airport;
            m.visible = true;
            m.VisibleAbove = 7f;
            markerProperties.properties.add(m);

            m = markerProperties.getNewMarkerProperty();
            m.airportType = AirportType.small_airport;
            m.visible = true;
            m.VisibleAbove = 9f;
            markerProperties.properties.add(m);

            m = markerProperties.getNewMarkerProperty();
            m.airportType = AirportType.heliport;
            m.visible = false;
            m.VisibleAbove = 10f;
            markerProperties.properties.add(m);

            m = markerProperties.getNewMarkerProperty();
            m.airportType = AirportType.seaplane_base;
            m.visible = true;
            m.VisibleAbove = 10f;
            markerProperties.properties.add(m);

            m = markerProperties.getNewMarkerProperty();
            m.airportType = AirportType.balloonport;
            m.visible = true;
            m.VisibleAbove = 10f;
            markerProperties.properties.add(m);

            storePropertiesInDB(markerProperties);
        }
    }

    public void storePropertiesInDB(MarkerProperties markerProperties)
    {
        Property p1 = new Property();
        p1.name = "MARKERS";
        p1.value1 = "visible";
        try {
            p1.value2 = markerProperties.ToXml();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateProperty(p1);

        Log.i(TAG, "Marker Property XML: " + p1.value2);
    }

}
