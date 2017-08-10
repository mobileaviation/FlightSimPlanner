package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import nl.robenanita.googlemapstest.Charts.AirportChart;
import nl.robenanita.googlemapstest.Charts.AirportCharts;
import nl.robenanita.googlemapstest.flightplan.FlightPlan;

/**
 * Created by Rob Verhoef on 3-8-2017.
 */

public class AirportChartsDataSource {
    private SQLiteDatabase database;
    private UserDBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public AirportChartsDataSource(Context context) {
        dbHelper = new UserDBHelper(context);
    }

    public void open(){
        try {
            database = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createAirportChartValues(AirportChart airportChart)
    {
        ContentValues values = new ContentValues();
        values.put(UserDBHelper.C_airport_id, airportChart.airport_id);
        values.put(UserDBHelper.C_url, airportChart.url);
        values.put(UserDBHelper.C_thumbnail_url, airportChart.thumbnail_url);
        values.put(UserDBHelper.C_version, airportChart.version);
        values.put(UserDBHelper.C_created_date, (airportChart.created_date==null)? -1 : airportChart.created_date.getTime());
        values.put(UserDBHelper.C_active, (airportChart.active)? 1 : 0);
        values.put(UserDBHelper.C_display_name, airportChart.display_name);
        values.put(UserDBHelper.C_reference_name, airportChart.reference_name);
        values.put(UserDBHelper.C_file_prefix, airportChart.file_prefix);
        values.put(UserDBHelper.C_file_suffix, airportChart.file_suffix);
        values.put(UserDBHelper.C_latitude1_deg, airportChart.latitude_1_deg);
        values.put(UserDBHelper.C_longitude1_deg, airportChart.longitude_1_deg);
        values.put(UserDBHelper.C_latitude2_deg, airportChart.latitude_2_deg);
        values.put(UserDBHelper.C_longitude2_deg, airportChart.longitude_2_deg);
        return values;
    }

    private AirportChart cursorToAirportChart(Cursor cursor)
    {
        AirportChart airportChart = new AirportChart();
        airportChart.id = cursor.getInt(cursor.getColumnIndex("_id"));
        airportChart.airport_id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_airport_id));
        airportChart.url = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_url));
        airportChart.thumbnail_url = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_thumbnail_url));
        airportChart.version = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_version));
        long t = (cursor.isNull(cursor.getColumnIndex(UserDBHelper.C_created_date))) ? 0 :
                cursor.getLong(cursor.getColumnIndex(UserDBHelper.C_created_date));
        if (t>0) {
            airportChart.created_date = new Date();
            airportChart.created_date.setTime(t);
        }
        Integer active = cursor.getInt(cursor.getColumnIndex(UserDBHelper.C_active));
        airportChart.active = (active==1)? true : false;

        airportChart.display_name = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_display_name));
        airportChart.reference_name = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_reference_name));
        airportChart.file_prefix = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_file_prefix));
        airportChart.file_suffix = cursor.getString(cursor.getColumnIndex(UserDBHelper.C_file_suffix));

        airportChart.latitude_1_deg = cursor.getDouble(cursor.getColumnIndex(UserDBHelper.C_latitude1_deg));
        airportChart.latitude_2_deg = cursor.getDouble(cursor.getColumnIndex(UserDBHelper.C_latitude2_deg));
        airportChart.longitude_1_deg = cursor.getDouble(cursor.getColumnIndex(UserDBHelper.C_longitude1_deg));
        airportChart.longitude_2_deg = cursor.getDouble(cursor.getColumnIndex(UserDBHelper.C_longitude2_deg));

        return  airportChart;
    }

    public Boolean ChartExists(AirportChart airportChart)
    {
        Integer count = 0;
        String query = "SELECT COUNT(*) C FROM " + UserDBHelper.AIRPORTCHARTS_TABLE_NAME +
                " WHERE " + UserDBHelper.C_reference_name  + "='" + airportChart.reference_name + "';";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("C"));
        }
        return (count>0);
    }

    public Integer GetChartCount()
    {
        Integer count = 0;
        String query = "SELECT COUNT(*) C FROM " + UserDBHelper.AIRPORTCHARTS_TABLE_NAME +";";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("C"));
        }
        return count;
    }

    public void InsertChart(AirportChart airportChart)
    {
        if (!ChartExists(airportChart))
        {
            ContentValues values = createAirportChartValues(airportChart);

            long insertId = database.insert(UserDBHelper.AIRPORTCHARTS_TABLE_NAME, null,
                    values);
        }
    }

    public AirportCharts GetAllCharts()
    {
        AirportCharts airportCharts = new AirportCharts();
        String query = "SELECT * FROM " + UserDBHelper.AIRPORTCHARTS_TABLE_NAME  + ";";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            AirportChart airportChart = cursorToAirportChart(cursor);
            airportCharts.add(airportChart);
            cursor.moveToNext();
        }

        return airportCharts;
    }

}
