package nl.robenanita.googlemapstest;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * Created by Rob Verhoef on 25-6-2015.
 */
public class Helpers {

    public static LatLng getDutchFormatPosition(String text)
    {
        // Dutch notams
        // PSN 521837N0045613E
        // PSN 5403N00627E
        // PSN 53.18.4N 003.56.8E
        LatLng latLng = null;

        try {
            if (text.contains("PSN")) {
                String[] l = text.substring(text.indexOf("PSN") + 4, text.length()).split("[EW]");
                String loc = text.substring(text.indexOf("PSN") + 4, text.indexOf("PSN") + 4 + l[0].length()+1);
                loc = loc.replace(".", "");
                loc = loc.replace(" ", "");
                String[] ll = loc.split("[NS]");
                if (ll.length>1)
                {
                    String latstr = ll[0].replaceAll("[\\D]", "");
                    String lonstr = ll[1].replaceAll("[\\D]", "");

                    if (latstr.length()==4) latstr = latstr + "00";
                    if (latstr.length()==5) latstr = latstr.substring(0, 4) + "00";

                    if (lonstr.length()==5) lonstr = lonstr + "00";
                    if (lonstr.length()==5) lonstr = lonstr.substring(0, 5) + "00";

                    Double lat = Double.valueOf(latstr.substring(0,2)) +
                            (Double.valueOf(latstr.substring(2,4)) / 60) +
                            (Double.valueOf(latstr.substring(4,6)) / 3600)
                                    * ((loc.contains("S")) ? -1 : 1);
                    Double lon = Double.valueOf(lonstr.substring(0,3)) +
                            (Double.valueOf(lonstr.substring(3,5)) / 60) +
                            (Double.valueOf(lonstr.substring(5,7)) / 3600)
                                    * ((loc.contains("W")) ? -1 : 1);
                    latLng = new LatLng(lat,lon);

                }
            }
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            return null;
        }

        return latLng;
    }

    public static LatLng parseOpenAirLocation(String location)
    {
        // replace DP, DB, V X=
        String l = location.replace("DP", "");
        l = l.replace("DB", "");
        l = l.replace("V X=", "");
        l = l.trim();

        // 53:40:00 N 006:30:00 E
        String[] loc = l.split(" ");

        LatLng latLng = null;
        String lat[] = loc[0].split(":");
        Double _lat = Double.valueOf(lat[0]) +
                (Double.valueOf(lat[1]) / 60) +
                (Double.valueOf(lat[2]) / 3600)
                        * ((loc[1].equals("S")) ? -1 : 1);
        String lon[] = loc[2].split(":");

        Double _lon = Double.valueOf(lon[0]) +
                (Double.valueOf(lon[1]) / 60) +
                (Double.valueOf(lon[2]) / 3600)
                        * ((loc[1].equals("W")) ? -1 : 1);
        latLng = new LatLng(_lat,_lon);

        return latLng;
    }

    public static Location getLocation(LatLng latLng)
    {
        Location location = new Location("");
        location.setLongitude(latLng.longitude);
        location.setLatitude(latLng.latitude);
        return location;
    }

    public static Coordinate getCoordinate(LatLng latLng)
    {
        return new Coordinate(latLng.longitude, latLng.latitude);
    }
}