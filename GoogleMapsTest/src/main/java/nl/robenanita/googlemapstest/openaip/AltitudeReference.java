package nl.robenanita.googlemapstest.openaip;

/**
 * Created by Rob Verhoef on 29-9-2015.
 */
public enum AltitudeReference {
    GND,        // Ground
    MSL,        // Main sea level
    STD         // Standard atmosphere
    ;

    @Override
    public String toString() {
        return super.toString();
    }

    public AltitudeReference parse(String reference)
    {
        if (reference.equals("GND")) return this.GND;
        if (reference.equals("MSL")) return this.MSL;
        if (reference.equals("STD")) return this.STD;
        return STD;
    }

}
