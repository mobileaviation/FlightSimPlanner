package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;

public class FBFir {
    public Integer id;
    public Integer index;
    public String name;
    public String ident;
    public String position;
    public String polygon;

    public ContentValues getFirContentValues()
    {
        ContentValues values = new ContentValues();

        values.put(FBDBHelper.C_id, id);
        values.put(FBDBHelper.C_name, name);
        values.put(FBDBHelper.C_ident, ident);
        values.put(FBDBHelper.C_position, position);
        values.put(FBDBHelper.C_polygon, polygon);

        return values;
    }

}
