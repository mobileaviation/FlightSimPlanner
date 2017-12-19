package nl.robenanita.googlemapstest.Weather;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.database.AirportDataSource;

/**
 * Created by Rob Verhoef on 7-5-2014.
 */
public class Taf {
    public Taf(Context context)
    {
        this.context = context;
        elevation_m = 0;
        forecast = new ArrayList<forecast_class>();
    }

    private String TAG = "GooglemapsTest";
    private Context context;
    public String station_id;
    public void setStation_id(String station_id)
    {
        this.station_id = station_id;
        AirportDataSource airportDataSource = new AirportDataSource(context);
        airportDataSource.open(-1);
        airport = airportDataSource.GetAirportByIDENT(station_id);
        airportDataSource.close();
    }
    public Airport airport;

    public String raw_text;
    public String issue_time;
    public Date GetIssueTime()
    {
        // 2015-06-11T11:13:00Z
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        try {
            return format.parse(issue_time);
        } catch (ParseException e) {
            Log.e(TAG, "Taf: Date parse error");
            return new Date();
        }
    }

    public String bulletin_time;
    public String valid_time_from;
    public String valid_time_to;
    public float latitude;
    public float longitude;
    public float elevation_m;
    public String remarks;

    public float distance_to_org_m;
    public void caculateDistance(LatLng orgLocation)
    {
        Location l = new Location("org");
        l.setLatitude(orgLocation.latitude);
        l.setLongitude(orgLocation.longitude);
        Location r = new Location("new");
        r.setLongitude(longitude);
        r.setLatitude(latitude);
        distance_to_org_m = l.distanceTo(r);
    }

    public ArrayList<forecast_class> forecast;
    public forecast_class getNewForecastClass() {return new forecast_class();}
    public class forecast_class
    {
        public forecast_class()
        {
            sky_condition = new ArrayList<sky_condition_class>();
            turbulence_condition = new ArrayList<turbulence_condition_class>();
            icing_condition = new ArrayList<icing_condition_class>();
            temperature = new ArrayList<temperature_class>();
        }

        public String fcst_time_from;
        public String fcst_time_to;
        public String change_indicator;
        public Integer probability;
        public Integer wind_dir_degrees;
        public Integer wind_speed_kt;
        public Integer wind_gust_kt;
        public Integer wind_shear_hgt_ft_agl;
        public Integer wind_shear_dir_degrees;
        public Integer wind_shear_speed_kt;
        public float visibility_statute_mi;
        public float altim_in_hg;
        public Integer vert_vis_ft;
        public String wx_string;
        public String not_decoded;


        public void AddSkyCondition(String cloud_base_ft_agl, String sky_cover)
        {
            sky_condition_class sc = new sky_condition_class();
            sc.cloud_base_ft_agl = Integer.parseInt((cloud_base_ft_agl==null)? "0": cloud_base_ft_agl);
            sc.sky_cover = sky_cover;
            sky_condition.add(sc);
        }
        public ArrayList<sky_condition_class> sky_condition;
        public class sky_condition_class
        {
            public Integer cloud_base_ft_agl;
            public String sky_cover;
            public String cloud_type;
        }

        public ArrayList<turbulence_condition_class> turbulence_condition;
        public class turbulence_condition_class
        {
            public String turbulence_intensity;
            public Integer turbulence_min_alt_ft_agl;
            public Integer turbulence_max_alt_ft_agl;
        }

        public ArrayList<icing_condition_class> icing_condition;
        public class icing_condition_class
        {
            public String icing_intensity;
            public Integer icing_min_alt_ft_agl;
            public Integer icing_max_alt_ft_agl;
        }

        public ArrayList<temperature_class> temperature;
        public class temperature_class
        {
            public String valid_time;
            public float sfc_temp_c;
            public float max_temp_c;
            public float min_temp_c;
        }

    }

}
