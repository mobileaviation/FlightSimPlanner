package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

import nl.robenanita.googlemapstest.LocationTracking;

/**
 * Created by Rob Verhoef on 21-5-2014.
 */
public class LocationTrackingDataSource {
    private SQLiteDatabase database;
    private UserDBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public LocationTrackingDataSource(Context context) {
        dbHelper = new UserDBHelper(context);
    }

    public void open() {
        try
        {
            database = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public Long insertNewLocationTracking(LocationTracking locationTracking)
    {
        Long id = database.insert(UserDBHelper.TRACKS_TABLE_NAME, null, createTrackContentValues(locationTracking));
        return id;
    }

    public void setTrackPoint(LocationTracking locationTracking, Location location)
    {
        database.insert(UserDBHelper.TRACKPOINTS_TABLE_NAME, null,
                createTrackPointsContentValues(locationTracking, location));
    }

    private ContentValues createTrackContentValues(LocationTracking locationTracking) {
        ContentValues values = new ContentValues();
        values.put("name", locationTracking.Name);
        values.put("time", (new Date()).getTime());
        return values;
    }

    private ContentValues createTrackPointsContentValues(LocationTracking locationTracking, Location location) {
        ContentValues values = new ContentValues();
        values.put("name", "");
        values.put("time", (new Date()).getTime());
        values.put("track_id", locationTracking.Id);
        values.put("latitude_deg", location.getLatitude());
        values.put("longitude_deg", location.getLongitude());
        values.put("time", location.getTime());
        // Convert ms to knots
        values.put("ground_speed_kt", location.getSpeed()*1.9438444924574f);
        values.put ("altitude_ft", (location.hasAltitude()) ? location.getAltitude(): 0d);
        values.put(UserDBHelper.C_true_heading_deg, (location.hasBearing()) ? location.getBearing(): 0d);
        return values;
    }

    public void runQuery()
    {
//        String q = "UPDATE " + DBHelper.TRACKPOINTS_TABLE_NAME +
//                " SET ground_speed_kt = ground_speed_kt * 1.9438444924574;";
        String q = "DELETE FROM " + UserDBHelper.TRACKPOINTS_TABLE_NAME + " WHERE track_id<8;";
        database.execSQL(q);
    }

    public ArrayList<Track> getTracks()
    {
        //HashMap<Date, Track> trackHashMap = new HashMap<Date, Track>();

        ArrayList<Track> tracks = new ArrayList<Track>();
        Cursor c = database.rawQuery("SELECT * FROM " + UserDBHelper.TRACKS_TABLE_NAME +
                " WHERE _id in (SELECT track_id FROM " + UserDBHelper.TRACKPOINTS_TABLE_NAME+ " GROUP BY track_id)" +
                " ORDER BY _id DESC;", null);
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            Track t = cursorToTrack(c);
            Date d = new Date();



            //trackHashMap.put(t.date, t);
            tracks.add(t);
            c.moveToNext();
        }

        return tracks;
    }

    public ArrayList<TrackPoint> getTrackPoints(Integer track_id)
    {
        ArrayList<TrackPoint> tp = new ArrayList<TrackPoint>();

        String q = "SELECT * FROM " + UserDBHelper.TRACKPOINTS_TABLE_NAME + " WHERE "
                + UserDBHelper.C_trackid + "=" + track_id.toString() + " ORDER BY _id;";

        Cursor c = database.rawQuery(q, null);

        c.moveToFirst();
        while (!c.isAfterLast())
        {
            TrackPoint t = cursorToTrackPoint(c);
            // meters to feet
            // t.altitude = t.altitude * 3.2808399f;
            tp.add(t);
            c.moveToNext();
        }

        return tp;
    }

    private Track cursorToTrack(Cursor cursor) {
        Track track = new Track();
        track.id = cursor.getInt(cursor.getColumnIndex("_id"));
        track.name = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_name));
        track.date.setTime(cursor.getLong(cursor.getColumnIndex(UserDBHelper.C_time)));
        return track;
    }



    public class Track
    {
        public Track()
        {
            date = new Date();
        }
        public Integer id;
        public String name;
        public Date date;
    }

    private TrackPoint cursorToTrackPoint(Cursor cursor)
    {
        TrackPoint t = new TrackPoint();
        t.id = cursor.getInt(cursor.getColumnIndex("_id"));
        t.name = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_name));
        t.time.setTime(cursor.getLong(cursor.getColumnIndex(UserDBHelper.C_time)));
        t.latitude = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_latitude_deg));
        t.longitude = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_longitude_deg));
        t.ground_speed = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_ground_speed_kt));
        t.altitude = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_altitude_ft));
        t.true_heading = cursor.getFloat(cursor.getColumnIndex(UserDBHelper.C_true_heading_deg));
        return t;
    }

    public class TrackPoint
    {
        public TrackPoint() {
            time = new Date();
        }

        public Integer id;
        public String name;
        public Float longitude;
        public Float latitude;
        public Date time;
        public Float ground_speed;
        public Float altitude;
        public Float true_heading;
    }
}
