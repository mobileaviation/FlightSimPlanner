package nl.robenanita.googlemapstest;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rob Verhoef on 25-6-2015.
 */
public class Helpers {

    public static LatLng getDutchFormatPosition(String text)
    {
        // Dutch notams
        // PSN 521837N0045613E
        LatLng latLng = null;

        try {
            if (text.contains("PSN")) {
                String l = text.substring(text.indexOf("PSN") + 4, text.indexOf("PSN") + 19);
                String[] ll = l.split("[NS]");
                if (ll.length>1)
                {
                    String latstr = ll[0].replace("[\\D]", "");
                    String lonstr = ll[1].replace("[\\D]", "");

                    Double lat = Double.valueOf(latstr.substring(0,2)) +
                            (Double.valueOf(latstr.substring(2,4)) / 60) +
                            (Double.valueOf(latstr.substring(4,6)) / 3600)
                                    * ((l.contains("S")) ? -1 : 1);
                    Double lon = Double.valueOf(lonstr.substring(0,3)) +
                            (Double.valueOf(lonstr.substring(3,5)) / 60) +
                            (Double.valueOf(lonstr.substring(5,7)) / 3600)
                                    * ((l.contains("W")) ? -1 : 1);
                    latLng = new LatLng(lat,lon);

                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return latLng;
    }
}
