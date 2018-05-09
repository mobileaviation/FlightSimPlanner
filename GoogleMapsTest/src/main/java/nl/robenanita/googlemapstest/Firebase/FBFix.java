package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;

public class FBFix {
    public Integer index;
    public String name;
    public String ident;
    public Double latitude_deg;
    public Double longitude_deg;

    public ContentValues getFixContentValues()
    {
        ContentValues values = new ContentValues();

        values.put(FBDBHelper.C_name, ident);
        values.put(FBDBHelper.C_ident, ident);
        values.put(FBDBHelper.C_longitude_deg, longitude_deg);
        values.put(FBDBHelper.C_latitude_deg, latitude_deg);

        return values;
    }
}
