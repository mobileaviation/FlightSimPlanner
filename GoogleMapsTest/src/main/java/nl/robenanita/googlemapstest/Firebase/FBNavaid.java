package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;

import nl.robenanita.googlemapstest.NavaidType;

public class FBNavaid {
    public Integer id;
    public Integer index;
    public String filename;
    public String ident;
    public String name;
    public NavaidType type;
    public double frequency_khz;
    public double latitude_deg;
    public double longitude_deg;
    public Integer elevation_ft;
    public String iso_country;
    public double dme_frequency_khz;
    public double dme_channel;
    public double dme_latitude_deg;
    public double dme_longitude_deg;
    public Integer dme_elevation_ft;
    public double slaved_variation_deg;
    public double magnetic_variation_deg;
    public String usageType;
    public String power;
    public String associated_airport;
    public Integer associated_airport_id;

    public void setType(String typeString)
    {
        NavaidType a = NavaidType.VOR;
        if (typeString.equals("DME")) a = NavaidType.DME;
        if (typeString.equals("NDB")) a = NavaidType.NDB;
        if (typeString.equals("NDB-DME")) a = NavaidType.NDB_DME;
        if (typeString.equals("TACAN")) a = NavaidType.TACAN;
        if (typeString.equals("VOR-DME")) a = NavaidType.VOR_DME;
        if (typeString.equals("VORTAC")) a = NavaidType.VORTAC;

        type = a;
    }

    public ContentValues getNavaidContentValue()
    {
        ContentValues values = new ContentValues();

        values.put(FBDBHelper.C_id, id);
        values.put(FBDBHelper.C_filename, filename);
        values.put(FBDBHelper.C_name, name);
        values.put(FBDBHelper.C_type, type.toString());
        values.put(FBDBHelper.C_frequency, frequency_khz);
        values.put(FBDBHelper.C_latitude_deg, latitude_deg);
        values.put(FBDBHelper.C_longitude_deg, longitude_deg);
        values.put(FBDBHelper.C_elevation_ft, elevation_ft);
        values.put(FBDBHelper.C_iso_country, iso_country);
        values.put(FBDBHelper.C_dme_frequency_khz, dme_frequency_khz);
        values.put(FBDBHelper.C_dme_channel, dme_channel);
        values.put(FBDBHelper.C_dme_latitude_deg, dme_latitude_deg);
        values.put(FBDBHelper.C_dme_longitude_deg, dme_longitude_deg);
        values.put(FBDBHelper.C_dme_elevation_ft, dme_elevation_ft);
        values.put(FBDBHelper.C_slaved_variation_deg, slaved_variation_deg);
        values.put(FBDBHelper.C_magnetic_variation_deg, magnetic_variation_deg);
        values.put(FBDBHelper.C_usageType, usageType);
        values.put(FBDBHelper.C_power, power);
        values.put(FBDBHelper.C_associated_airport, associated_airport);
        values.put(FBDBHelper.C_associated_airport_id, associated_airport_id);

        return values;
    }
}
