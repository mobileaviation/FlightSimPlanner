package nl.robenanita.googlemapstest.Classes;

/**
 * Created by Rob Verhoef on 30-7-2017.
 */

public class PlanePosition {
    public PlanePosition(double lat, double lon, double height, double heading) {
        Latitude = lat;
        Longitude = lon;
        Height = height;
        Heading = heading;
    }

    public final double Latitude;
    public final double Longitude;
    public final double Height;
    public final double Heading;


}
