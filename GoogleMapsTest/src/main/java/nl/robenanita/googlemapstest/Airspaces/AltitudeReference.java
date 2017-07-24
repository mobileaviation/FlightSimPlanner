package nl.robenanita.googlemapstest.Airspaces;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public enum AltitudeReference {
    GND,        // Ground
    MSL,        // Main sea level
    STD,        // Standard atmosphere
    AGL         // Above Ground level
    ;

    @Override
    public String toString() {
        return super.toString();
    }
}
