package nl.robenanita.googlemapstest.Weather;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.database.AirportDataSource;

/**
 * Created by Rob Verhoef on 2-5-2014.
 */
public class Metar {
    public Metar()
    {
        temp_c = 0;
        altim_in_hg = 0;
        visibility_statute_mi = 0;
        elevation_m = 0;
    }
    //private Context c;

    public String raw_text;
    public String station_id;
    public void setStation_id(String station_id)
    {
        this.station_id = station_id;
        AirportDataSource airportDataSource = new AirportDataSource(null);
        airportDataSource.open(-1);
        airport = airportDataSource.GetAirportByIDENT(station_id);
        airportDataSource.close();
    }
    public Airport airport;
    public String observation_time;
    public float latitude;
    public float longitude;
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

    public float temp_c;
    public float dewpoint_c;
    public Integer wind_dir_degrees;
    public Integer wind_speed_kt;
    public Integer wind_gust_kt;
    public float visibility_statute_mi;
    public float altim_in_hg;
    public float sea_level_pressure_mb;
    public String quality_control_flags;
    public String wx_string;

    public ArrayList<sky_condition_class> sky_condition;
    public void AddSkyCondition(String cloud_base_ft_agl, String sky_cover)
    {
        if (sky_condition == null) sky_condition = new ArrayList<sky_condition_class>();
        sky_condition_class sc = new sky_condition_class();
        sc.cloud_base_ft_agl = Integer.parseInt((cloud_base_ft_agl==null)? "0": cloud_base_ft_agl);
        sc.sky_cover = sky_cover;
        sky_condition.add(sc);
    }
    public class sky_condition_class
    {
        public Integer cloud_base_ft_agl;
        public String sky_cover;
    }

    public String flight_category;
    public float three_hr_pressure_tendency_mb;
    public float maxT_c;
    public float minT_c;
    public float maxT24hr_c;
    public float minT24hr_c;
    public float precip_in;
    public float pcp3hr_in;
    public float pcp6hr_in;
    public float pcp24hr_in;
    public float snow_in;
    public Integer vert_vis_ft;
    public String metar_type;
    public float elevation_m;

}
