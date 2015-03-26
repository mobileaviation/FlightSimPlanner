package nl.robenanita.googlemapstest.Weather;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 26-3-2015.
 */
public class Station {
    public Station(){
        siteTypes = new ArrayList<SiteType>();
    }

    public String station_id;
    public Integer wmo_id;
    public Double latitude;
    public Double longitude;
    public Double elevation_m;
    public String site;
    public String state;
    private ArrayList<SiteType> siteTypes;
    public void AddSiteType(String type)
    {
        if (type.equals("METAR")) siteTypes.add(SiteType.METAR);
        if (type.equals("TAF")) siteTypes.add(SiteType.TAF);
    }

    public enum SiteType
    {
        METAR,
        TAF
    }

}
