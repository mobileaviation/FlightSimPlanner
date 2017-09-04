package nl.robenanita.googlemapstest;

import android.app.LoaderManager;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Rob Verhoef on 25-6-2015.
 */
public class Helpers {

    public static LatLng midPoint(LatLng point1 ,LatLng point2){

        double dLon = Math.toRadians(point2.longitude - point1.longitude);
        //convert to radians
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double lon1 = Math.toRadians(point1.longitude);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }

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
        location = location.toUpperCase();
        // replace DP, DB, V X=
        String l = location.replace("DP", "");
        l = l.replace("DB", "");
        l = l.replace("V X=", "");
        l = l.trim();

        // 53:40:00 N 006:30:00 E
        String[] loc = l.split("[NS]");

        LatLng latLng = null;
        String lat[] = loc[0].split(":");
        Double _lat = (Double.valueOf(lat[0]) +
                (Double.valueOf(lat[1]) / 60) +
                (Double.valueOf(Helpers.findRegex("[0-9]+\\w",lat[2])) / 3600))
                        * ((l.indexOf("S")>3) ? -1d : 1d);
        loc[1] = loc[1].replaceAll("[EW]", "");
        String lon[] = loc[1].split(":");

        Double _lon = (Double.valueOf(lon[0]) +
                (Double.valueOf(lon[1]) / 60) +
                (Double.valueOf(Helpers.findRegex("[0-9]+\\w",lon[2])) / 3600))
                        * ((l.indexOf("W")>3) ? -1d : 1d);
        latLng = new LatLng(_lat, _lon);

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

    public static String findRegex(String pattern, String input)
    {
        try {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(input);
            matcher.find();
            return matcher.group();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static Float dp(Float px, Context context)
    {
        return context.getResources().getDisplayMetrics().density * px;
    }

    public static boolean CheckInternetAvailability() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    (new URL("http://clients3.google.com/generate_204")
                            .openConnection());
            urlConnection.setRequestProperty("User-Agent", "Android");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1500);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 204 &&
                    urlConnection.getContentLength() == 0) {
                Log.d("Network Checker", "Successfully connected to internet");
                return true;
            }
            else
            {
                Log.d("Network Checker", "clients3.google.com not accessible");
                return false;
            }
        } catch (Exception e) {
            Log.e("Network Checker", "Error checking internet connection", e);
            return false;
        }
    }
}
