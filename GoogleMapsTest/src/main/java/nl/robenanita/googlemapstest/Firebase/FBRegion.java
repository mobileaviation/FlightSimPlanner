package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;

public class FBRegion {
    public Integer id;
    public Integer index;
    public String code;
    public String local_code;
    public String name;
    public String continent;
    public String iso_country;
    public String wikipedia_link;
    public String keywords;

    public ContentValues getRegionContentValues()
    {
        ContentValues values = new ContentValues();

        values.put(FBDBHelper.C_id, id);
        values.put(FBDBHelper.C_code, code);
        values.put(FBDBHelper.C_name, name);
        values.put(FBDBHelper.C_iso_country, name);
        values.put(FBDBHelper.C_local_code, name);
        values.put(FBDBHelper.C_continent, continent);
        values.put(FBDBHelper.C_wikipedia_link, wikipedia_link);
        values.put(FBDBHelper.C_keywords, keywords);

        return values;
    }
}
