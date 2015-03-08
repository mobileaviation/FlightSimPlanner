package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rob Verhoef on 27-5-2014.
 */
public class WBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wbairnav.db";
    private static final int DATABASE_VERSION = 1;

    public static final String AIRCRAFT_TABLE_NAME = "tbl_Aircraft";
    public static final String AIRCRAFTWEIGHTBALANCE_TABLE_NAME = "tbl_AircraftWeightBalance";

    public static final String C_name = "brand";
    public static final String C_type = "type";
    public static final String C_typename = "typename";
    public static final String C_version = "version";
    public static final String C_zero_fuel_weight_lbs = "zero_fuel_weight_lbs";
    public static final String C_arm_in = "arm_in";
    public static final String C_no_off_passengers = "no_off_passengers";
    public static final String C_no_off_bagages = "no_off_bagages";
    public static final String C_max_takeoff_weight_normal_lbs = "max_takeoff_weight_normal_lbs";
    public static final String C_max_takeoff_weight_utility_lbs = "max_takeoff_weight_utility_lbs";
    public static final String C_extra_ramp_weight_lbs = "extra_ramp_weight_lbs";
    public static final String C_max_landing_weight_normal_lbs = "max_landing_weight_normal_lbs";
    public static final String C_max_landing_weight_utility_lbs = "max_landing_weight_utility_lbs";
    public static final String C_max_bagage_weight_normal_lbs = "max_bagage_weight_normal_lbs";
    public static final String C_max_bagage_weight_utility_lbs = "max_bagage_weight_utility_lbs";
    public static final String C_cg_env_arm_left_in = "cg_env_arm_left_in";
    public static final String C_cg_env_arm_right_in = "cg_env_arm_right_in";
    public static final String C_cg_env_utility_arm_left_in = "cg_env_utility_arm_left_in";
    public static final String C_cg_env_normal_arm_left_in = "cg_env_normal_arm_left_in";
    public static final String C_usable_fuel_capacity_usg = "usable_fuel_capacity_usg";
    public static final String C_cruise_fuel_capacity_usg = "cruise_fuel_capacity_usg";

    public static final String C_registration = "registration";

    public WBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
