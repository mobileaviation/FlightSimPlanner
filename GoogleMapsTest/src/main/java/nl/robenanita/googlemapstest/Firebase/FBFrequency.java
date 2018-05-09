package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;

public class FBFrequency {
    public FBFrequency() {}

    public Integer id;
    public Integer airport_ref;
    public String airport_ident;
    public String type;
    public String description;
    public Double frequency_mhz;

    public ContentValues getFrequencyValues() {
        ContentValues values = new ContentValues();

        values.put(FBDBHelper.C_id, id);
        values.put(FBDBHelper.C_airport_ref, airport_ref);
        values.put(FBDBHelper.C_airport_ident, airport_ident);
        values.put(FBDBHelper.C_type, type);
        values.put(FBDBHelper.C_description, description);
        values.put(FBDBHelper.C_frequency_mhz, frequency_mhz);

        return values;
    }

}
