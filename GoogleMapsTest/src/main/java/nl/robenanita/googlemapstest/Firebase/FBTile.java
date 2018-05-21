package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;

import java.util.Date;

import nl.robenanita.googlemapstest.MBTiles.MBTileType;

public class FBTile {
    public Long index;
    public String name;
    public String region;
    public MBTileType type;
    public String mbtileslink;
    public String xmllink;
    public String version;
    public Long startValidity;
    public Long endValidity;

    public ContentValues getTileContentValues()
    {
        ContentValues values = new ContentValues();

        values.put(FBDBHelper.C_name, name);
        values.put(FBDBHelper.C_region, region);
        values.put(FBDBHelper.C_type, type.toString());
        values.put(FBDBHelper.C_mbtileslink, mbtileslink);
        values.put(FBDBHelper.C_xmllink, xmllink);
        values.put(FBDBHelper.C_version, version);
        values.put(FBDBHelper.C_startValidity, startValidity);
        values.put(FBDBHelper.C_endValidity, endValidity);

        return values;
    }
}
