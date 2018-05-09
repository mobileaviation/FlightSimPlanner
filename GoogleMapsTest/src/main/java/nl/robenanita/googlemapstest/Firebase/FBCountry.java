package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;

public class FBCountry {
    public int id;
    public String code;
    public String name;
    public Integer index;
    public String continent;
    public String wikipedia_link;
    public String keywords;

    public ContentValues getCountryContentValues()
    {
        ContentValues values = new ContentValues();

        values.put(FBDBHelper.C_id, id);
        values.put(FBDBHelper.C_code, code);
        values.put(FBDBHelper.C_name, name);
        values.put(FBDBHelper.C_continent, continent);
        values.put(FBDBHelper.C_wikipedia_link, wikipedia_link);
        values.put(FBDBHelper.C_keywords, keywords);

        return values;
    }
}
